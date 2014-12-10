package com.cognifide.aem.dash.core.finder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.cognifide.aem.dash.core.utils.ServiceUtils;
import com.google.common.collect.Lists;

public class SearchModel {

	private Search search;

	private List<Provider> providers;

	public static SearchModel fromRequest(SlingHttpServletRequest request) {
		return new SearchModel(ServiceUtils.getService(request, Search.class));
	}

	public SearchModel(Search search) {
		this.search = search;
	}

	public Search getSearch() {
		return search;
	}

	public List<Provider> getProviders() {
		if (providers != null) {
			return providers;
		}

		providers = Lists.newArrayList(search.getProviders());
		Collections.sort(providers, new Comparator<Provider>() {
			@Override
			public int compare(Provider p1, Provider p2) {
				return p1.getLabel().compareToIgnoreCase(p2.getLabel());
			}
		});

		return providers;
	}

	public String getNavbarClass() {
		final String theme = StringUtils.trimToEmpty(search.getTheme());

		return theme.equalsIgnoreCase("light") ? "" : "navbar-inverse";
	}
}
