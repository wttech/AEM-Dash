package com.cognifide.aem.dash.core.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

import com.cognifide.aem.dash.core.finder.Search;
import com.cognifide.aem.dash.core.finder.SearchResult;
import com.cognifide.aem.dash.core.finder.providers.StaticProvider;
import com.cognifide.aem.dash.core.playgrounds.Playground;
import com.cognifide.aem.dash.core.playgrounds.PlaygroundManager;
import com.cognifide.aem.dash.core.utils.ServiceUtils;
import com.google.common.collect.Maps;

public class BoardModel {

	private Search search;

	private PlaygroundManager playgroundManager;

	private Map<String, List<SearchResult>> entries;

	public static BoardModel fromRequest(SlingHttpServletRequest request) {
		return new BoardModel(ServiceUtils.getService(request, Search.class), ServiceUtils.getService(
				request, PlaygroundManager.class));
	}

	public BoardModel(Search search, PlaygroundManager manager) {
		this.search = search;
		this.playgroundManager = manager;
	}

	public List<Playground> getPlaygrounds() {
		return playgroundManager.getPlaygrounds();
	}

	public Map<String, List<SearchResult>> getEntries() {
		if (entries != null) {
			return entries;
		}

		entries = Maps.newTreeMap();

		StaticProvider provider = search.getProvider(StaticProvider.class);
		if (provider != null) {
			for (SearchResult entry : provider.getEntries()) {
				final String letter = entry.getLabel().substring(0, 1).toUpperCase();

				if (!entries.containsKey(letter)) {
					entries.put(letter, new ArrayList<SearchResult>());
				}

				entries.get(letter).add(entry);
			}

			for (Map.Entry<String, List<SearchResult>> entry : entries.entrySet()) {
				Collections.sort(entry.getValue(), new Comparator<SearchResult>() {
					@Override
					public int compare(SearchResult o1, SearchResult o2) {
						return o1.getLabel().compareToIgnoreCase(o2.getLabel());
					}
				});
			}
		}

		return entries;
	}
}
