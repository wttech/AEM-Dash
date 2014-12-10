package com.cognifide.aem.dash.core.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class SearchUtils {

	private static final String WORD_DELIMITER = " ";

	private static final int PHRASE_MIN_WORD_LENGTH_DEFAULT = 3;

	private SearchUtils() {
		// cannot be constructed
	}

	/**
	 * Determine whether specified result ranking should be boosted
	 */
	public static boolean isResultRankBoosted(SearchResult result, String text) {
		for (String phrase : result.getPhrases()) {
			final String p = phrase.toLowerCase();
			final String t = text.toLowerCase();

			if (p.startsWith(t) || p.endsWith(t)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Calculate search result score basing on input phrase (comparator function)
	 */
	public static int compareToPhrase(SearchResult sr1, SearchResult sr2, String text, int rankBoost) {
		final String phrase = text.toLowerCase();

		int r1 = sr1.getRank();
		int r2 = sr2.getRank();

		// Rank boost if one of phrases starts or ends with
		if (isResultRankBoosted(sr1, text)) {
			r1 -= rankBoost;
		}
		if (isResultRankBoosted(sr2, text)) {
			r2 -= rankBoost;
		}

		// Differences in rankings
		if (r1 != r2) {
			return r1 - r2;
		}

		// Substrings detection
		final String s1 = sr1.getLabel().toLowerCase();
		final String s2 = sr2.getLabel().toLowerCase();

		if ((s1.contains(text) && s2.contains(text)) || (!s1.contains(text) && !s2.contains(text))) {
			final int d1 = StringUtils.getLevenshteinDistance(phrase, s1);
			final int d2 = StringUtils.getLevenshteinDistance(phrase, s2);

			return d1 - d2;
		}

		if (s1.contains(text)) {
			return -1;
		}

		return 1;
	}

	/**
	 * Check whether phrase contains specified text (with multiple word support)
	 */
	public static boolean containsPhrase(final String phrase, final String text, int minWordLength) {
		for (String phraseWord : splitWords(phrase)) {
			for (String textWord : splitWords(text)) {
				final String p = StringUtils.trimToEmpty(phraseWord);
				final String t = StringUtils.trimToEmpty(textWord);

				if ((!p.isEmpty() && !t.isEmpty()) && (t.length() >= minWordLength)
						&& (p.length() >= minWordLength) && (p.contains(t) || t.contains(p))) {
					return true;
				}
			}
		}

		return false;
	}

	private static String[] splitWords(String phrase) {
		return StringUtils.trimToEmpty(phrase).toLowerCase().split(WORD_DELIMITER);
	}

	/**
	 * Check whether phrase contains one of specified texts (with multiple word support)
	 */
	public static boolean containsPhrase(final String phrase, List<String> texts, int minWordLength) {
		for (String text : texts) {
			if (containsPhrase(phrase, text, minWordLength)) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsPhrase(final String phrase, List<String> texts) {
		return containsPhrase(phrase, texts, PHRASE_MIN_WORD_LENGTH_DEFAULT);
	}

	/**
	 * Compose human readable parameter list as description
	 */
	public static String composeDescription(Map<String, Object> params) {
		List<String> lines = Lists.newArrayList();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			lines.add(String.format("%s: %s", entry.getKey(), entry.getValue()));
		}

		return StringUtils.join(lines, ", ");
	}

	/**
	 * Get provider label
	 */
	public static String parseProviderFromLabel(String label) {
		List<String> words = new ArrayList<String>(Arrays.asList(StringUtils.split(label, WORD_DELIMITER)));
		words.remove(0);
		words.remove(words.size() - 1);

		return StringUtils.join(words, " ");
	}
}
