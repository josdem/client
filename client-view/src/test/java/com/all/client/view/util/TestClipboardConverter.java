package com.all.client.view.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.all.client.view.util.ClipboardConverter;

public class TestClipboardConverter {
	@Test
	public void shouldGetModel() throws Exception {
		assertNotNull(ClipboardConverter.getModelCollection());
	}
}
