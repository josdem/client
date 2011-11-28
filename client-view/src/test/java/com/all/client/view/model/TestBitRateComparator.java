package com.all.client.view.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.all.client.view.model.BitRateComparator;


public class TestBitRateComparator {
	@Test
	public void shouldCompare() throws Exception {
		BitRateComparator bitRateComparator = new BitRateComparator();
		assertEquals(0, bitRateComparator.compare("128 kbps","128 kbps"));
		assertEquals(-1, bitRateComparator.compare("128 kbps","196 kbps"));
		assertEquals(1, bitRateComparator.compare("128 kbps","64 kbps"));
		assertEquals(0, bitRateComparator.compare("128 kbps","~128 kbps"));
	}
	
}
