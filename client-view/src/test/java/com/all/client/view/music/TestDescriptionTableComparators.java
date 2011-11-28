package com.all.client.view.music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TestDescriptionTableComparators {
	@Test
	public void shouldCompareStrings() throws Exception {
		assertEquals(0, DescriptionTableComparators.compareString(null, ""));
		assertTrue(0 > DescriptionTableComparators.compareString("ab", ""));
		assertTrue(0 < DescriptionTableComparators.compareString("cd", "AB"));
	}
}
