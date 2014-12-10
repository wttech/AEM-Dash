package com.cognifide.aem.dash.core.finder;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

public class Phrase {

	private static final String ARG_SEPARATOR = "|";

	private static final String OPTION_VALUE_SEPARATOR = ":";

	private static final String HINT_OPTION = "^";

	private String raw;

	private String hint;

	private String value;

	private Map<String, String> options;

	/**
	 * Parse search text with named options support (delimited by '|', pairs name-value delimited by ':').
	 *
	 * Hint can be specified in query at any place (at start or end in the middle). First value which is not a
	 * hint is taken as phrase to search.
	 */
	public Phrase(String phrase) {
		this.raw = phrase;
		this.hint = "";
		this.value = "";
		this.options = Maps.newLinkedHashMap();

		int optionIndex = 0;
		for (String optionRaw : StringUtils.split(StringUtils.trimToEmpty(phrase), ARG_SEPARATOR)) {
			final String option = StringUtils.trimToEmpty(optionRaw);
			final String[] parts = StringUtils.split(option, OPTION_VALUE_SEPARATOR);

			if (parts.length == 2) {
				final String key = StringUtils.trimToEmpty(parts[0]);
				final String value = StringUtils.trimToEmpty(parts[1]);

				if (key.equals(HINT_OPTION)) {
					this.hint = value;
				} else if (StringUtils.isBlank(this.value)) {
					this.value = value;
				} else {
					this.options.put(key, value);
				}
			} else {
				if (StringUtils.isBlank(this.value)) {
					this.value = option;
				} else {
					this.options.put(String.valueOf(optionIndex), option);
					optionIndex++;
				}
			}
		}
	}

	public String getRaw() {
		return raw;
	}

	public String getValue() {
		return value;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public String getHint() {
		return hint;
	}
}
