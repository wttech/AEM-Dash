package com.cognifide.aem.dash.core.launchers;

import java.util.List;
import java.util.Map;

public interface LauncherManager {

	String getLabel();

	List<Launcher> getLaunchers();

	LauncherProgress launch(Launcher launcher, Map<String, String> options);

	Launcher getLauncher(String name);
}
