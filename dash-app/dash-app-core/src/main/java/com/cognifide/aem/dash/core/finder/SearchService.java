package com.cognifide.aem.dash.core.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Service(Search.class)
@Component(label = Dash.PREFIX + " " + SearchService.LABEL, description = "Search everywhere service", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class SearchService implements Search {

	private static final Logger LOG = LoggerFactory.getLogger(Search.class);

	public static final String LABEL = "Finder Search";

	private static final int PROVIDER_THREAD_POOL_SIZE = 8;

	public static final String WARM_UP_PHRASE = "dash";

	private ExecutorService executor;

	@Reference(referenceInterface = Provider.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE)
	private List<Provider> providers = Lists.newArrayList();

	private static final String STARTUP_PATH_DEFAULT = "/etc/dash/board.html";

	private String startupPath;

	@Property(value = STARTUP_PATH_DEFAULT, label = "Startup path", description = "Default page opened in frame at startup")
	public static final String STARTUP_PATH_PROP = "startupPath";

	private static final String THEME_DEFAULT = "dark";

	private String theme;

	@Property(value = THEME_DEFAULT, label = "Color theme", description = "Available: dark, light")
	public static final String THEME_PROP = "theme";

	private static final int PHRASE_CACHE_SIZE_DEFAULT = 60;

	@Property(intValue = PHRASE_CACHE_SIZE_DEFAULT, label = "Phrase cache size", description = "Maximum number of phrase results stored temporarily in cache")
	public static final String PHRASE_CACHE_SIZE_PROP = "phraseCacheSize";

	private int phraseCacheSize;

	private Map<Integer, SearchProgress> phraseCache;

	private static final int RANK_BOOST_DEFAULT = 1000;

	@Property(intValue = RANK_BOOST_DEFAULT, label = "Rank boost", description = "Value subtracted from search result ranking if its label starts or ends with phrase")
	public static final String RANK_BOOST_PROP = "rankBoost";

	private int rankBoost;

	private static final int PROVIDER_TIMEOUT_DEFAULT = 10;

	@Property(intValue = PROVIDER_TIMEOUT_DEFAULT, label = "Provider timeout", description = "Maximum time for querying single enabled provider (in seconds)")
	public static final String PROVIDER_TIMEOUT_PROP = "providerTimeout";

	private int providerTimeout;

	private static final int RESULT_LIMIT_DEFAULT = 60;

	private int resultLimit;

	@Property(intValue = RESULT_LIMIT_DEFAULT, label = "Result limit", description = "Global maximum number of queried results")
	public static final String RESULT_LIMIT_PROP = "resultLimit";

	@Activate
	private void activate(ComponentContext ctx) {
		this.executor = Executors.newFixedThreadPool(PROVIDER_THREAD_POOL_SIZE);

		this.startupPath = PropertiesUtil.toString(ctx.getProperties().get(STARTUP_PATH_PROP),
				STARTUP_PATH_DEFAULT);
		this.theme = PropertiesUtil.toString(ctx.getProperties().get(THEME_PROP), THEME_DEFAULT);
		this.phraseCacheSize = PropertiesUtil.toInteger(ctx.getProperties().get(PHRASE_CACHE_SIZE_PROP),
				PHRASE_CACHE_SIZE_DEFAULT);
		this.phraseCache = Collections.synchronizedMap(new LinkedHashMap<Integer, SearchProgress>() {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Integer, SearchProgress> entry) {
				return size() > phraseCacheSize;
			}
		});
		this.rankBoost = PropertiesUtil.toInteger(ctx.getProperties().get(RANK_BOOST_PROP),
				RANK_BOOST_DEFAULT);
		this.providerTimeout = PropertiesUtil.toInteger(ctx.getProperties().get(PROVIDER_TIMEOUT_PROP),
				PROVIDER_TIMEOUT_DEFAULT);
		this.resultLimit = PropertiesUtil.toInteger(ctx.getProperties().get(RESULT_LIMIT_PROP),
				RESULT_LIMIT_DEFAULT);

		reset();
	}

	@Deactivate
	private void deactive() {
		this.executor.shutdownNow();
	}

	@Override
	public SearchProgress byPhrase(final String text) {
		final List<Callable<List<SearchResult>>> tasks = Lists.newArrayList();
		final Phrase phrase = new Phrase(text);
		final List<Provider> providers = findProviders(phrase);

		for (final Provider provider : providers) {
			tasks.add(new Callable<List<SearchResult>>() {
				@Override
				public List<SearchResult> call() throws Exception {
					return provider.byPhrase(phrase);
				}
			});
		}

		return fetchResults(providers, phrase, tasks);
	}

	private List<Provider> findProviders(Phrase phrase) {
		final ArrayList<Provider> providers = Lists.newArrayList();
		final String hint = phrase.getHint().toLowerCase();

		if (hint.isEmpty()) {
			providers.addAll(this.providers);
		} else {
			for (Provider provider : this.providers) {
				final String label = provider.getLabel().toLowerCase();
				if (label.contains(hint)) {
					providers.add(provider);
				}
			}
		}

		return providers;
	}

	private SearchProgress fetchResults(List<Provider> providers, Phrase phrase,
			List<Callable<List<SearchResult>>> tasks) {
		final int key = composeCacheKey(providers, phrase);
		final String text = phrase.getValue();

		SearchProgress progress;
		if (phraseCache.containsKey(key)) {
			progress = phraseCache.get(key);
		} else {
			progress = new SearchProgress(phrase);
			progress.start();

			try {
				// Pool each provider in parallel
				final List<Future<List<SearchResult>>> futures = Lists.newArrayList();
				for (Callable<List<SearchResult>> task : tasks) {
					futures.add(executor.submit(task));
				}

				// Grab each response (or service time out)
				for (Future<List<SearchResult>> future : futures) {
					final Provider provider = providers.get(futures.indexOf(future));
					final String providerName = provider != null ? provider.getLabel() : "<unknown>";

					try {
						progress.getResults().addAll(future.get(providerTimeout, TimeUnit.SECONDS));
					} catch (ExecutionException e) {
						final String message = String.format("%s execution error: %s", providerName,
								e.getMessage());

						LOG.error(message, e);
						progress.step(message);
					} catch (TimeoutException e) {
						final String message = String.format("%s exceeds time limit: %d second(s)",
								providerName, providerTimeout);

						LOG.error(message);
						progress.step(message);
					}
				}
			} catch (InterruptedException e) {
				final String message = "Cannot search by phrase (probably lack of resources)";

				LOG.error(message, e);
				progress.step(message);
			}

			progress.groupResultsByPath(text, rankBoost);
			progress.orderResultsByRank(text, rankBoost);
			progress.adjustResultsQuantity(resultLimit);

			progress.stop();

			phraseCache.put(key, progress);
		}

		return progress;
	}

	private int composeCacheKey(List<Provider> providers, Phrase phrase) {
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(phrase.getValue());
		for (Provider provider : providers) {
			builder.append(provider.getLabel());
		}

		return builder.toHashCode();
	}

	@Override
	public void reset() {
		// Clear cache of core and provider internals
		phraseCache.clear();
		for (Provider provider : providers) {
			provider.reset();
		}

		// Some providers at first search need data source initialization which can tak some more time
		byPhrase(WARM_UP_PHRASE);
	}

	@Override
	public List<Provider> getProviders() {
		return providers;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProvider(Class<T> providerClass) {
		for (Provider provider : providers) {
			if (provider.getClass().equals(providerClass)) {
				return (T) provider;
			}
		}

		return null;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public String getStartupPath() {
		return startupPath;
	}

	@Override
	public String getTheme() {
		return theme;
	}

	protected void bindProviders(Provider provider) {
		LOG.debug("Registering new provider: {}", ((Object) provider).getClass());
		providers.add(provider);

		provider.reset();
	}

	protected void unbindProviders(Provider provider) {
		LOG.debug("Unregistering existing provider: {}", ((Object) provider).getClass());
		providers.remove(provider);
	}
}
