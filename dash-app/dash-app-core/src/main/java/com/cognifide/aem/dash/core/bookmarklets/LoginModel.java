package com.cognifide.aem.dash.core.bookmarklets;

import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

import com.cognifide.aem.dash.core.playgrounds.Playground;
import com.cognifide.aem.dash.core.playgrounds.PlaygroundManager;
import com.cognifide.aem.dash.core.utils.ServiceUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class LoginModel {

	private static final Gson GSON = new Gson();

	private PlaygroundManager manager;

	public static LoginModel fromRequest(SlingHttpServletRequest request) {
		return new LoginModel(ServiceUtils.getService(request, PlaygroundManager.class));
	}

	public LoginModel(PlaygroundManager manager) {
		this.manager = manager;
	}

	public String getData() {
		final List<Map<String, String>> instances = Lists.newArrayList();

		for (Playground playground : manager.getPlaygrounds()) {
			final Map<String, String> instance = Maps.newHashMap();

			instance.put("url", "http://" + playground.getUrl());
			instance.put("login", playground.getUsername());
			instance.put("password", playground.getPassword());
			instance.put("server", playground.getName());
			instance.put("type", playground.isAuthor() ? "author" : "publish");

			instances.add(instance);
		}

		return GSON.toJson(instances);
	}
}
