package com.all.client.view.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;

public class TestChatTextArea {
	private static final String REMOTE_COLOR = "E6E6E6";
	private static final String SENDER_EMAIL = "B@all.com";
	
	private ChatTextArea chatTextArea = new ChatTextArea();
	private SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
	private ChatMessage message;
	private ContactInfo sender;

	@Before
	public void setup(){
		message = mock(ChatMessage.class);
		sender = mock(ContactInfo.class);
		when(message.getSender()).thenReturn(sender);
		when(message.getTime()).thenReturn(new Date());
	}
	
	@Test
	public void shouldAddRemoteMessage() throws Exception {
		chatTextArea.addColor(SENDER_EMAIL, REMOTE_COLOR);
		
		when(sender.getEmail()).thenReturn(SENDER_EMAIL);
		when(sender.getNickName()).thenReturn("hilda");
		when(message.getMessage()).thenReturn("Hola");
        
		chatTextArea.addMessage(message);
		final String expected = "<br/><table width=\"100%\" border=0 cellpadding=0 cellspacing=0><tr  BGCOLOR=\"#E6E6E6\"><td valign=\"middle\" align=\"left\"><font style='color: #646464; font-size: 11pt '>hilda</font></td><td align=\"right\" width=\"46px\" height=\"15%\"><font style='color: #646464; font-size: 10pt '>" + formatter.format(message.getTime())+"</font></td></tr></table><table width=\"100%\" border=0 cellpadding=0 cellspacing=0><tr><td valign=\"middle\" align=\"left\" width=\"100\" height=\"15%\"><font style='color: #464646; font-size: 13pt '>Hola</font></td></tr></table>";
        assertEquals(expected, chatTextArea.text.toString());
	}
	
	@Test
	public void shouldGetMailImageResource() throws Exception {
		when(message.getMessage()).thenReturn("<RECEIVECONTENT>");
		chatTextArea.addMessage(message);
		String result = chatTextArea.text.toString();
		assertTrue(result.contains("mail.gif"));
	}
}
