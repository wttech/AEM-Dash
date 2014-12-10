package com.cognifide.aem.dash.core.finder;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SearchResult {

	private final String provider;

	private transient int rank;

	private final String path;

	private final String label;

	private String description;

	private Map<String, Object> context;

	private List<String> phrases;

	private Boolean playable;

	public SearchResult(String provider, int rank, String label, String path) {
		this.provider = provider;
		this.rank = rank;
		this.label = label;
		this.path = path;
		this.context = Maps.newHashMap();

		this.phrases = Lists.newArrayList();
		this.phrases.add(label);
		this.playable = true;
	}

	public SearchResult(String provider, int rank, String label, String path, String description) {
		this(provider, rank, label, path);
		this.description = description;
	}

	public SearchResult(String provider, int rank, String label, String path, String description,
			Map<String, Object> context) {
		this(provider, rank, label, path, description);
		this.context = context;
	}

	public String getProvider() {
		return provider;
	}

	public int getRank() {
		return rank;
	}

	public String getPath() {
		return path;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public Boolean getPlayable() {
		return playable;
	}

	public void setPlayable(Boolean playable) {
		this.playable = playable;
	}

	/**
	 * Update result ranking as hint for sorting
	 */
	public void updateRank(int delta) {
		this.rank += delta;
	}

	/**
	 * Get phrases which should be compared to queries phrase
	 */
	public List<String> getPhrases() {
		return phrases;
	}
}
