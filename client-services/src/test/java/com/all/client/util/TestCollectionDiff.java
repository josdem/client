package com.all.client.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestCollectionDiff {
	private List<String> listA = new ArrayList<String>();
	private List<String> listB = new ArrayList<String>();

	@Before
	public void init() {
		listA.add("A");
		listA.add("B");
		listA.add("C");
		listA.add("D");
		listA.add("E");
		listB.add("C");
		listB.add("D");
		listB.add("E");
		listB.add("F");
		listB.add("G");
	}

	@Test
	public void shouldCompareTwoListAndGetDifferences() throws Exception {
		CollectionDiff<String> colDiff = new CollectionDiff<String>(listA, listB);
		assertEquals(2, colDiff.getRemoved().size());
		assertEquals(2, colDiff.getAdded().size());
		assertTrue(colDiff.getAdded().contains("F"));
		assertTrue(colDiff.getAdded().contains("G"));
		assertTrue(colDiff.getRemoved().contains("B"));
		assertTrue(colDiff.getRemoved().contains("A"));
	}
}
