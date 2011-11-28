package com.all.client.view.chat;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;

public class ChatFrameTest extends UnitTestCase {
	
	@Mock
	private ChatFrame chatFrameMock;
	
	@Test
	public void shouldCreateChatFrame() throws Exception {
		assertNotNull(chatFrameMock);
	}

}
