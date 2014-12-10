package com.cognifide.aem.dash.core.playgrounds;

import org.apache.sling.api.SlingHttpServletRequest;

import com.cognifide.aem.dash.core.utils.ServiceUtils;

public class PlaygroundModel {

	private PlaygroundManager manager;

	public static PlaygroundModel fromRequest(SlingHttpServletRequest request) {
		return new PlaygroundModel(ServiceUtils.getService(request, PlaygroundManager.class));
	}

	public PlaygroundModel(PlaygroundManager manager) {
		this.manager = manager;
	}

	public PlaygroundManager getManager() {
		return manager;
	}
}
