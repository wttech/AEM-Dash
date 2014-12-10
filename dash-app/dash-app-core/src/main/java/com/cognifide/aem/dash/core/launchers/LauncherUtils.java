package com.cognifide.aem.dash.core.launchers;

import org.apache.commons.lang.StringUtils;

public final class LauncherUtils {

	private LauncherUtils() {
		// cannot be constructed
	}

	public static String parseLabel(String label) {
		return StringUtils.replaceOnce(label, "Launcher ", "");
	}
}
