package com.cognifide.aem.dash.core.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletResponse;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Helper functions for JSON. Can be used for unifying servlet responses.
 */
public final class JSONUtils {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final String RESPONSE_ENCODING_DEFAULT = "UTF-8";

	private JSONUtils() {
		// hidden constructor
	}

	public static void writeJson(SlingHttpServletResponse response, final Object obj) throws IOException {
		String json = (obj instanceof String) ? (String) obj : GSON.toJson(obj);

		writeJson(response, json, RESPONSE_ENCODING_DEFAULT);
	}

	public static void writeJson(SlingHttpServletResponse response, String json, String encoding)
			throws IOException {
		response.setCharacterEncoding(encoding);
		response.setContentType("application/json");
		response.getWriter().write(json);
		response.getWriter().flush();
	}

	public static void writeMessage(SlingHttpServletResponse response, String type, String text)
			throws IOException {
		final HashMap<String, Object> context = Maps.newHashMap();

		writeMessage(response, type, text, context);
	}

	public static void writeMessage(SlingHttpServletResponse response, String type, String text,
			Map<String, Object> context) throws IOException {
		final Map<String, Object> map = Maps.newHashMap();

		map.put("type", type);
		map.put("message", text);
		map.putAll(context);

		writeJson(response, map);
	}

}
