package com.cognifide.aem.dash.core.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class with methods helping in query creation.
 *
 * @author dominik.kornas@cognifide.com
 */
public final class QueryUtil {

	private static final Pattern QUOTE = Pattern.compile("'");

	private static final Pattern WHITESPACE = Pattern.compile("\\s");

	private static final Pattern WHITESPACES = Pattern.compile("[\\s]+");

	// WE DON'T escape asterisks and question marks!!! In order to do it, add '\\*' and '\\?' to pattern.
	private static final Pattern LUCENE_ESCAPES = Pattern
			.compile("(?=(&&)|(\\|\\|)|[\\+\\!\\(\\)\\{\\}\\[\\]\\^\\~\\:\\/])");

	private static final Pattern FULL_LUCENE_ESCAPES = Pattern
			.compile("(?=(&&)|(\\|\\|)|[\\+\\!\\(\\)\\{\\}\\[\\]\\^\\~\\:\\/\\\"\\*\\?\\\\\\-])");

	private static final Pattern JCR_ESCAPES = Pattern.compile("(?=[\\\"\\'\\-])");

	private QueryUtil() {
	}

	/**
	 * @param unparsedQuery The unmodified query string typed by user.
	 * @return Normalized query string, trimmed and with subsequent whitespaces replaced by a single space
	 * character. In addition all occurrences of the <code>'</code> character are removed.
	 */
	public static String normalizeQuery(final String unparsedQuery) {
		if (StringUtils.isBlank(unparsedQuery)) {
			return unparsedQuery;
		}
		final String noQuotes = QUOTE.matcher(unparsedQuery).replaceAll("");
		return StringUtils.trimToEmpty(WHITESPACES.matcher(noQuotes).replaceAll(" "));
	}

	public static boolean isSentence(final Object value) {
		return value instanceof String && WHITESPACE.matcher((String) value).find();
	}

	/**
	 * Escapes Lucene special characters as documented in <a href=
	 * "http://lucene.apache.org/core/4_8_1/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#Escaping_Special_Characters"
	 * >Escaping_Special_Characters</a>. IF strict escaping is chose, escapes all special characters as
	 * mentioned in the linked coument. Otherwise following characters are not escaped: <code>*</code>,
	 * <code>?</code>, <code>"</code>, <code>\</code> and <code>-</code>. This give the choice to decide if
	 * the application support all query syntax features (if no escaping is applied), only basic subset (if
	 * strictEscaping is falsde) or none of them (if strictEscaping is true).
	 */
	public static String escapeLuceneSpecialCharacters(final String unescaped, final boolean strictEscaping) {
		if (strictEscaping) {
			return escape(unescaped, FULL_LUCENE_ESCAPES);
		} else {
			return escape(unescaped, LUCENE_ESCAPES);
		}
	}

	/**
	 * Escapes special JCR characters which are highlighted in <a
	 * href="http://www.day.com/specs/jcr/1.0/8.5.4.5_CONTAINS.html">JSR-170 p. 8.5.4.5</a>. These are:
	 * <code>"</code>, <code>'</code> and <code>-</code>.
	 */
	public static String escapeJcrSqlSpecialCharacters(final String unescaped) {
		return escape(unescaped, JCR_ESCAPES);
	}

	private static String escape(final String unescaped, final Pattern pattern) {
		if (StringUtils.isBlank(unescaped)) {
			return unescaped;
		}
		final String[] splitted = pattern.split(unescaped);

		final StringBuilder builder = new StringBuilder();
		for (String s : splitted) {
			if (pattern.matcher(s).find()) {
				builder.append("\\" + s);
			} else {
				builder.append(s);
			}
		}

		return builder.toString();
	}
}