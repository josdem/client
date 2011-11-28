package com.all.client.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestSizeComparator {
	@Test
	public void shouldCompare() throws Exception {
		SizeComparator sizeComparator = new SizeComparator();
		assertEquals(0, sizeComparator.compare("1 MB", "1 MB"));
		assertEquals(1, sizeComparator.compare("2 MB", "1 MB"));
		assertEquals(-1, sizeComparator.compare("1 MB", "10 MB"));
		assertEquals(1, sizeComparator.compare("2 MB", "103 KB"));
	}
}
