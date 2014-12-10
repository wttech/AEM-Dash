package com.cognifide.aem.dash.core.utils;

import java.util.Map;

public class TextUtils {

	/**
	 * Inject variables into text
	 */
	public static String injectVars(String template, Map<String, String> vars) {
		String injected = template;
		for (Map.Entry<String, String> entry : vars.entrySet()) {
			injected = injected.replace(String.format("${%s}", entry.getKey()), entry.getValue());
		}

		return injected;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}
}
