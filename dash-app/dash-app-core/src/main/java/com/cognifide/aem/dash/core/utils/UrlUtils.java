package com.cognifide.aem.dash.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.jackrabbit.util.Base64;

public final class UrlUtils {

	private UrlUtils() {
		// hidden constructor
	}

	public static String getText(String url) throws IOException {
		return getText(url, null, null);
	}

	public static String getText(String url, String username, String password) throws IOException {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();

		if (username != null && password != null) {
			String credentials = username + ":" + password;
			connection.setRequestProperty("Authorization",
					String.format("Basic %s", Base64.encode(credentials)));
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);

		in.close();

		return response.toString();
	}
}
