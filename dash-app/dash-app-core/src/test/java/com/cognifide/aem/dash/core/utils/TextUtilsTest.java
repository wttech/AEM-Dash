package com.cognifide.aem.dash.core.utils;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

public class TextUtilsTest {

	@Test
	public void shouldInjectVars() {
		final Map<String, String> vars = Maps.newHashMap();

		vars.put("name", "Foo");
		vars.put("surname", "Bar");

		assertEquals("Hello Foo Bar!", TextUtils.injectVars("Hello ${name} ${surname}!", vars));
	}
}
