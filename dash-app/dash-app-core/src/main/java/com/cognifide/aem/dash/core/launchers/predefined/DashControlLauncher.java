package com.cognifide.aem.dash.core.launchers.predefined;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.finder.Search;
import com.cognifide.aem.dash.core.launchers.Launcher;
import com.cognifide.aem.dash.core.launchers.LauncherProgress;
import com.cognifide.aem.dash.core.utils.Dash;
import com.cognifide.aem.dash.core.utils.TextUtils;

@Service(Launcher.class)
@Component(label = Dash.PREFIX + " " + DashControlLauncher.LABEL, description = DashControlLauncher.DESCRIPTION, policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class DashControlLauncher implements Launcher {

	private static final Logger LOG = LoggerFactory.getLogger(DashControlLauncher.class);

	public static final String LABEL = "Launcher Dash Control";

	public static final String DESCRIPTION = "Controls itself. Options: actions to be performed delimited by '|'. Available: clear cache";

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private Search search;

	@Override
	public void launch(LauncherProgress progress) throws Exception {
		for (Map.Entry<String, String> entry : progress.getOptions().entrySet()) {
			final String action = (TextUtils.isInteger(entry.getKey()) ? entry.getValue() : entry.getKey())
					.toLowerCase();

			if ("clear cache".contains(action)) {
				search.reset();
				progress.step("Cache cleared successfully.");
			}
		}

		if (progress.getSteps().size() == 0) {
			progress.step("No action performed.");
		}
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
}
