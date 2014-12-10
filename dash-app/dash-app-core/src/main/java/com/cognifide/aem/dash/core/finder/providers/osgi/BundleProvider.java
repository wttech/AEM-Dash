package com.cognifide.aem.dash.core.finder.providers.osgi;

import static com.cognifide.aem.dash.core.finder.SearchUtils.composeDescription;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.finder.Phrase;
import com.cognifide.aem.dash.core.finder.Provider;
import com.cognifide.aem.dash.core.finder.SearchResult;
import com.cognifide.aem.dash.core.finder.SearchUtils;
import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Component(label = Dash.PREFIX + " " + BundleProvider.LABEL, description = "Provides links which targets to bundle status", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Service(Provider.class)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class BundleProvider implements Provider {

	private static final Logger LOG = LoggerFactory.getLogger(BundleProvider.class);

	public static final String LABEL = "Finder OSGi Bundle Provider";

	private static final String PATH = "/system/console/bundles/%d";

	private BundleContext bundleContext;

	private static final boolean ENABLED_DEFAULT = true;

	private boolean enabled;

	@Property(boolValue = ENABLED_DEFAULT, label = "Enabled")
	public static final String ENABLED_PROP = "enabled";

	private static final int RESULT_RANK_DEFAULT = 350;

	private int resultRank;

	@Property(intValue = RESULT_RANK_DEFAULT, label = "Result rank", description = "Ranking of provided results (for sorting, lower better)")
	public static final String RESULT_RANK_PROP = "resultRank";

	@Activate
	private void activate(ComponentContext ctx) {
		this.enabled = PropertiesUtil.toBoolean(ctx.getProperties().get(ENABLED_PROP), ENABLED_DEFAULT);
		this.bundleContext = ctx.getBundleContext();
		this.resultRank = PropertiesUtil.toInteger(ctx.getProperties().get(RESULT_RANK_PROP),
				RESULT_RANK_DEFAULT);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SearchResult> byPhrase(Phrase phrase) {
		final List<SearchResult> results = Lists.newArrayList();
		if (!enabled || StringUtils.isBlank(phrase.getValue())) {
			return results;
		}

		final String provider = SearchUtils.parseProviderFromLabel(LABEL);

		for (Bundle bundle : bundleContext.getBundles()) {
			final Dictionary<String, String> headers = bundle.getHeaders();
			final String bundleName = headers.get(Constants.BUNDLE_NAME);
			final String symbolicName = bundle.getSymbolicName();

			final List<String> texts = Arrays.asList(bundleName, symbolicName,
					String.valueOf(bundle.getBundleId()));

			if (!SearchUtils.containsPhrase(phrase.getValue(), texts)) {
				continue;
			}

			final String name = StringUtils.isNotBlank(bundleName) ? bundleName : symbolicName;
			final String path = String.format(PATH, bundle.getBundleId());

			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("State", mapBundleState(bundle.getState()));
			params.put("Description", headers.get(Constants.BUNDLE_DESCRIPTION));

			final String description = composeDescription(params);

			final SearchResult result = new SearchResult(provider, resultRank, name, path, description);
			final String lastPathPart = StringUtils.substringAfterLast(path, "/");
			if (StringUtils.isNotBlank(lastPathPart)) {
				result.getPhrases().add(lastPathPart);
			}

			result.setPlayable(false);
			results.add(result);
		}

		return results;
	}

	private String mapBundleState(int state) {
		switch (state) {
			case Bundle.ACTIVE:
				return "active";
			case Bundle.INSTALLED:
				return "installed";
			case Bundle.RESOLVED:
				return "resolved";
			case Bundle.UNINSTALLED:
				return "uninstalled";
			default:
				return "unknown";
		}
	}

	@Override
	public void reset() {
		// nothing to do
	}

	@Override
	public String getLabel() {
		return LABEL;
	}
}
