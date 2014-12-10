package com.cognifide.aem.dash.core.finder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PhraseTest {

	@Test
	public void shouldCreate() {
		final Phrase phrase = new Phrase("welcome");

		assertEquals("welcome", phrase.getRaw());
		assertEquals("", phrase.getHint());
		assertEquals("welcome", phrase.getValue());
	}

	@Test
	public void shouldCreateWithWhitespaces() {
		final Phrase phrase = new Phrase(" welcome ");

		assertEquals(" welcome ", phrase.getRaw());
		assertEquals("", phrase.getHint());
		assertEquals("welcome", phrase.getValue());
	}

	@Test
	public void shouldCreateWithHintAtStart() {
		final Phrase phrase = new Phrase("^:siteadmin | welcome");

		assertEquals("^:siteadmin | welcome", phrase.getRaw());
		assertEquals("siteadmin", phrase.getHint());
		assertEquals("welcome", phrase.getValue());
	}

	@Test
	public void shouldCreateWithHintAtEnd() {
		final Phrase phrase = new Phrase("welcome | ^:siteadmin");

		assertEquals("welcome | ^:siteadmin", phrase.getRaw());
		assertEquals("siteadmin", phrase.getHint());
		assertEquals("welcome", phrase.getValue());
	}

	@Test
	public void shouldCreateWithHintAndOptions() {
		final Phrase phrase = new Phrase("^:launcher | remove paths | /tmp | /var/classes");

		assertEquals("^:launcher | remove paths | /tmp | /var/classes", phrase.getRaw());
		assertEquals("launcher", phrase.getHint());
		assertEquals("remove paths", phrase.getValue());
		assertEquals(2, phrase.getOptions().size());
		assertEquals("/tmp", phrase.getOptions().get("0"));
		assertEquals("/var/classes", phrase.getOptions().get("1"));
	}

	@Test
	public void shouldCreateWithHintInMiddleAndOptions() {
		final Phrase phrase = new Phrase("remove paths | ^:launcher | /tmp | /var/classes");

		assertEquals("remove paths | ^:launcher | /tmp | /var/classes", phrase.getRaw());
		assertEquals("launcher", phrase.getHint());
		assertEquals("remove paths", phrase.getValue());
		assertEquals(2, phrase.getOptions().size());
		assertEquals("/tmp", phrase.getOptions().get("0"));
		assertEquals("/var/classes", phrase.getOptions().get("1"));
	}
}
