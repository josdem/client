package com.all.client.view.chat;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TestEmoticonHandler {
	@Test
	public void shouldGetMailIcon() throws Exception {
		String result = EmoticonHandler.getResourceAsImageTag("mail.gif");
		assertTrue(result.contains("mail.gif"));
	}
}
