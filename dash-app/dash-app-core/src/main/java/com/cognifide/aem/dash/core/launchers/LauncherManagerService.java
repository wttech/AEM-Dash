package com.cognifide.aem.dash.core.launchers;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Service(LauncherManager.class)
@Component(label = Dash.PREFIX + " " + LauncherManagerService.LABEL, description = "Launcher management centre", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class LauncherManagerService implements LauncherManager {

	private static final Logger LOG = LoggerFactory.getLogger(LauncherManagerService.class);

	public static final String LABEL = "Launcher Manager";

	@Reference(referenceInterface = Launcher.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE)
	private List<Launcher> launchers = Lists.newArrayList();

	@Override
	public List<Launcher> getLaunchers() {
		return launchers;
	}

	@Override
	public LauncherProgress launch(Launcher launcher, Map<String, String> options) {
		LauncherProgress progress = new LauncherProgress(launcher, options);
		progress.start();
		try {
			launcher.launch(progress);
		} catch (Exception e) {
			LOG.error("Launcher error", e);
			progress.setException(e);
		} finally {
			progress.stop();
		}

		return progress;
	}

	@Override
	public Launcher getLauncher(String name) {
		final String filteredName = StringUtils.trimToEmpty(name);

		for (Launcher launcher : launchers) {
			final String clazz = ((Object) launcher).getClass().getName();
			if (clazz.equalsIgnoreCase(filteredName)) {
				return launcher;
			}
		}

		return null;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	protected void bindLaunchers(Launcher launcher) {
		LOG.debug("Registering new launcher: {}", ((Object) launcher).getClass());
		launchers.add(launcher);
	}

	protected void unbindLaunchers(Launcher launcher) {
		LOG.debug("Unregistering exising launcher: {}", ((Object) launcher).getClass());
		launchers.remove(launcher);
	}
}
