package com.all.client.view.components;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestAlignedRenderer {
	@Test
	public void shouldCreateANewRenderer() throws Exception {
		AllignedRenderer cellRenderer = new AllignedRenderer(AllignedRenderer.CENTER);
		assertNotNull(cellRenderer);
	}
}
