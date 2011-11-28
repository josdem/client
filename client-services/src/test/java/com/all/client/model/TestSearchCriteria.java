package com.all.client.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSearchCriteria {

	@Test
	public void shouldExistOnly5CriteriaForSearching() throws Exception {
		assertEquals(5, SearchCriteria.values().length);
	}
}
