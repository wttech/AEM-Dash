package com.cognifide.aem.dash.core.finder.providers;

import java.util.Arrays;
import java.util.List;

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
import com.cognifide.aem.dash.core.launchers.Launcher;
import com.cognifide.aem.dash.core.launchers.LauncherManager;
import com.cognifide.aem.dash.core.launchers.LauncherManagerServlet;
import com.cognifide.aem.dash.core.launchers.LauncherUtils;
import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Component(label = Dash.PREFIX + " " + LauncherProvider.LABEL, description = "Provides links which launches predefined actions", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Service(Provider.class)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class LauncherProvider implements Provider {

	public static final String LABEL = "Finder Launcher Provider";

	private static final Logger LOG = LoggerFactory.getLogger(LauncherProvider.class);

	private static final String PATH = "/bin/dash/launcher/%d";

	private static final boolean ENABLED_DEFAULT = true;

	private boolean enabled;

	@Reference
	private LauncherManager manager;

	@Property(boolValue = ENABLED_DEFAULT, label = "Enabled")
	public static final String ENABLED_PROP = "enabled";

	private static final int RESULT_RANK_DEFAULT = 0;

	private int resultRank;

	@Property(intValue = RESULT_RANK_DEFAULT, label = "Result rank", description = "Ranking of provided results (for sorting, lower better)")
	public static final String RESULT_RANK_PROP = "resultRank";

	@Activate
	private void activate(ComponentContext ctx) {
		this.enabled = PropertiesUtil.toBoolean(ctx.getProperties().get(ENABLED_PROP), ENABLED_DEFAULT);
		this.resultRank = PropertiesUtil.toInteger(ctx.getProperties().get(RESULT_RANK_PROP),
				RESULT_RANK_DEFAULT);
	}

	@Override
	public List<SearchResult> byPhrase(Phrase phrase) {
		final List<SearchResult> results = Lists.newArrayList();
		if (!enabled || StringUtils.isBlank(phrase.getValue())) {
			return results;
		}

		final String provider = SearchUtils.parseProviderFromLabel(LABEL);

		for (Launcher launcher : manager.getLaunchers()) {
			final String path = String.format("%s?action=execute&launcher=%s", LauncherManagerServlet.PATH,
					launcher.getClass().getName());
			final String label = LauncherUtils.parseLabel(launcher.getLabel());
			final String description = launcher.getDescription();

			if (SearchUtils.containsPhrase(phrase.getValue(), Arrays.asList(label, description, path))) {
				final SearchResult sr = new SearchResult(provider, resultRank, label, path, description);

				results.add(sr);
			}
		}

		return results;
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
