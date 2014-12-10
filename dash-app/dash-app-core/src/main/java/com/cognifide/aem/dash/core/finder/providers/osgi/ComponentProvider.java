package com.cognifide.aem.dash.core.finder.providers.osgi;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.finder.Phrase;
import com.cognifide.aem.dash.core.finder.Provider;
import com.cognifide.aem.dash.core.finder.SearchResult;
import com.cognifide.aem.dash.core.finder.SearchUtils;
import com.cognifide.aem.dash.core.playgrounds.Playground;
import com.cognifide.aem.dash.core.playgrounds.PlaygroundManager;
import com.cognifide.aem.dash.core.utils.Dash;
import com.cognifide.aem.dash.core.utils.UrlUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

@Component(label = Dash.PREFIX + " " + ComponentProvider.LABEL, description = "Provides links which targets to component status page", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Service(Provider.class)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class ComponentProvider implements Provider {

	private static final Logger LOG = LoggerFactory.getLogger(ComponentProvider.class);

	private static final Gson GSON = new Gson();

	public static final String LABEL = "Finder OSGi Component Provider";

	private static final String CONSOLE_PATH = "/system/console/components";

	private static final String COMPONENT_PATH = CONSOLE_PATH + "/%s";

	private static final Type CONFIG_TYPE = new TypeToken<Map<String, Object>>() {
	}.getType();

	private static final String CONFIG_URL_PROTOCOL = "http://";

	private static final Pattern CONFIG_PATTERN = Pattern.compile(".*scrData = (.*);.*", Pattern.MULTILINE
			| Pattern.DOTALL);

	private static final String CONFIG_COMPONENTS_PROP = "data";

	private static final String CONFIG_COMPONENT_ID = "id";

	private static final String CONFIG_COMPONENT_NAME = "name";

	private static final String CONFIG_COMPONENT_PID = "pid";

	private static final String CONFIG_COMPONENT_STATE = "state";

	private static final String CONFIG_COMPONENT_STATE_RAW = "stateRaw";

	@Reference
	private PlaygroundManager playgroundManager;

	private static final boolean ENABLED_DEFAULT = true;

	private boolean enabled;

	@Property(boolValue = ENABLED_DEFAULT, label = "Enabled")
	public static final String ENABLED_PROP = "enabled";

	private static final int RESULT_RANK_DEFAULT = 300;

	private int resultRank;

	@Property(intValue = RESULT_RANK_DEFAULT, label = "Result rank", description = "Ranking of provided results (for sorting, lower better)")
	public static final String RESULT_RANK_PROP = "resultRank";

	private Map<String, Object> configData = Maps.newHashMap();

	@Activate
	private void activate(ComponentContext ctx) {
		this.enabled = PropertiesUtil.toBoolean(ctx.getProperties().get(ENABLED_PROP), ENABLED_DEFAULT);
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

		try {
			List<Map<String, String>> configurations = (List<Map<String, String>>) configData
					.get(CONFIG_COMPONENTS_PROP);

			for (Map<String, String> configuration : configurations) {
				final String id = StringUtils.trimToEmpty(configuration.get(CONFIG_COMPONENT_ID));
				final String name = StringUtils.trimToEmpty(configuration.get(CONFIG_COMPONENT_NAME));

				if (!SearchUtils.containsPhrase(phrase.getValue(), Arrays.asList(name, id))) {
					continue;
				}

				final String path = String.format(COMPONENT_PATH, id);
				final Map<String, Object> params = Maps.newHashMap();

				params.put("State", configuration.get(CONFIG_COMPONENT_STATE));
				params.put("Raw state", configuration.get(CONFIG_COMPONENT_STATE_RAW));

				final String description = SearchUtils.composeDescription(params);

				final SearchResult result = new SearchResult(provider, resultRank, name, path, description);
				final String lastComponentPart = StringUtils.substringAfterLast(path, ".");
				if (StringUtils.isNotBlank(lastComponentPart)) {
					result.getPhrases().add(lastComponentPart);
				}

				result.setPlayable(false);
				results.add(result);
			}
		} catch (Exception e) {
			LOG.error("OSGi Configuration Provider - Internal error occurred", e);
		}

		return results;
	}

	@Override
	public void reset() {
		final List<Playground> playgrounds = playgroundManager.getPlaygrounds();
		if (playgrounds.size() == 0) {
			LOG.warn("At least one local playground should be defined to use OSGi Component Provider.");
			return;
		}

		try {
			final Playground playground = playgrounds.get(0);
			final String url = CONFIG_URL_PROTOCOL + playground.getLoginUrl() + CONSOLE_PATH;
			final String html = UrlUtils.getText(url, playground.getUsername(), playground.getPassword());
			final Matcher matcher = CONFIG_PATTERN.matcher(html);

			if (matcher.matches()) {
				JsonReader json = new JsonReader(new StringReader(matcher.group(1)));
				json.setLenient(true);

				configData = GSON.fromJson(json, CONFIG_TYPE);
			} else {
				LOG.warn(String.format("Cannot retrieve config data from HTML, "
						+ "pattern: %s does not match any value on page: %s", CONFIG_PATTERN, CONSOLE_PATH));
			}
		} catch (IOException e) {
			LOG.error("Cannot read OSGi components", e);
		}
	}

	@Override
	public String getLabel() {
		return LABEL;
	}
}
