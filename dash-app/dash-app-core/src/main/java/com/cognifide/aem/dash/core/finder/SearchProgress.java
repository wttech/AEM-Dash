package com.cognifide.aem.dash.core.finder;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Fetched search progress results
 */
public class SearchProgress {

	private Phrase phrase;

	private Date start;

	private Date stop;

	private long duration;

	private String elapsed;

	private List<Object> steps = Lists.newArrayList();

	private List<SearchResult> results = Lists.newArrayList();

	public SearchProgress(Phrase phrase) {
		this.phrase = phrase;
	}

	public void start() {
		this.start = new Date();
	}

	public void stop() {
		this.stop = new Date();
		this.duration = stop.getTime() - start.getTime();
		this.elapsed = new PrettyTime(start).format(stop);
	}

	public Phrase getPhrase() {
		return phrase;
	}

	public Date getStart() {
		return start;
	}

	public Date getStop() {
		return stop;
	}

	public long getDuration() {
		return duration;
	}

	public void step(String message) {
		steps.add(message);
	}

	public List<Object> getSteps() {
		return steps;
	}

	public String getElapsed() {
		return elapsed;
	}

	public List<SearchResult> getResults() {
		return results;
	}

	/**
	 * Order results by its ranking (if equals use result which better matches phrase)
	 */
	public void orderResultsByRank(final String phrase, final int rankBoost) {
		Collections.sort(results, new Comparator<SearchResult>() {
			@Override
			public int compare(SearchResult s1, SearchResult s2) {
				return SearchUtils.compareToPhrase(s1, s2, phrase, rankBoost);
			}
		});
	}

	/**
	 * Removes duplicated paths in search results, leaves results with best scores
	 */
	public void groupResultsByPath(String text, int rankBoost) {
		final Map<String, SearchResult> groupedResults = Maps.newHashMap();

		for (SearchResult result : results) {
			if (!groupedResults.containsKey(result.getPath())) {
				groupedResults.put(result.getPath(), result);
			} else {
				final SearchResult best = groupedResults.get(result.getPath());

				if (SearchUtils.compareToPhrase(result, best, text, rankBoost) < 0) {
					groupedResults.put(result.getPath(), result);
				}
			}
		}

		results.clear();
		results.addAll(groupedResults.values());
	}

	/**
	 * Ensure that we have not too many results
	 */
	public void adjustResultsQuantity(int maxSize) {
		if (results.size() > maxSize) {
			results = results.subList(0, maxSize);
		}
	}
}
