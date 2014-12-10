package com.cognifide.aem.dash.core.launchers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.cognifide.aem.dash.core.utils.ServiceUtils;
import com.google.common.collect.Lists;

public class LauncherModel {

	private LauncherManager manager;

	private List<Launcher> launchers;

	public static LauncherModel fromRequest(SlingHttpServletRequest request) {
		return new LauncherModel(ServiceUtils.getService(request, LauncherManager.class));
	}

	public LauncherModel(LauncherManager manager) {
		this.manager = manager;
	}

	public LauncherManager getManager() {
		return manager;
	}

	public List<Launcher> getLaunchers() {
		if (launchers != null) {
			return launchers;
		}

		launchers = Lists.newArrayList(manager.getLaunchers());
		Collections.sort(launchers, new Comparator<Launcher>() {
			@Override
			public int compare(Launcher p1, Launcher p2) {
				return p1.getLabel().compareToIgnoreCase(p2.getLabel());
			}
		});

		return launchers;
	}
}
