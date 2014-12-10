package com.cognifide.aem.dash.core.launchers.predefined;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.launchers.Launcher;
import com.cognifide.aem.dash.core.launchers.LauncherProgress;
import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Service(Launcher.class)
@Component(label = Dash.PREFIX + " " + PathRemoveLauncher.LABEL, description = PathRemoveLauncher.DESCRIPTION, policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class PathRemoveLauncher implements Launcher {

	private static final Logger LOG = LoggerFactory.getLogger(PathRemoveLauncher.class);

	public static final String LABEL = "Launcher Remove Paths";

	public static final String DESCRIPTION = "Removes all configured paths (with temporary content, etc). Options: paths to be removed delimited by '|'.";

	@Reference
	private ResourceResolverFactory resolverFactory;

	private List<String> paths;

	@Property(value = { "/var/classes", "/var/clientlibs" }, label = "Paths", description = "Paths to be removed (with temporary content, etc)", unbounded = PropertyUnbounded.ARRAY)
	public static final String PATHS = "paths";

	@Activate
	protected void activate(ComponentContext ctx) {
		this.paths = Arrays.asList(PropertiesUtil.toStringArray(ctx.getProperties().get(PATHS)));
	}

	@Override
	public void launch(LauncherProgress progress) throws Exception {
		List<String> paths = this.paths;
		if (!progress.getOptions().isEmpty()) {
			paths = Lists.newArrayList(progress.getOptions().values());
		}

		progress.step(String.format("Removing paths (%d)", paths.size()));

		List<String> removed = Lists.newArrayList();
		ResourceResolver resolver = null;
		try {
			resolver = resolverFactory.getAdministrativeResourceResolver(null);
			for (String path : paths) {
				Resource resource = resolver.getResource(path);
				if (resource == null) {
					progress.step(String.format("Path does not exist: %s", path));
				} else {
					resource.adaptTo(Node.class).remove();
					removed.add(path);
				}
			}
			resolver.adaptTo(Session.class).save();
		} finally {
			if (resolver != null) {
				resolver.close();
			}
		}

		if (paths.size() == removed.size()) {
			progress.step(String.format("Paths removed successfully (%d)", removed.size()));
		} else {
			progress.step(String.format("Some paths cannot be removed (%d)", paths.size() - removed.size()));
		}

		progress.getContext().put("removedPaths", removed);
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
