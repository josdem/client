package com.all.client.view.chat;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.view.chat.ChatViewManager.ChatFrameFactory;
import com.all.core.common.model.ApplicationActions;
import com.all.i18n.Messages;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.stats.usage.UserActions;

public class TestChatViewManager {

	@InjectMocks
	private ChatViewManager chatViewManager = new ChatViewManager();
	@Mock
	private Messages messages;
	@Mock
	private ChatFrameFactory chatFrameFactory;
	@Mock
	private ChatFrame chatFrame;
	@Captor
	private ArgumentCaptor<WindowAdapter> windowAdapterCaptor;
	@Mock
	private ViewEngine viewEngine;

	private ContactInfo contact;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		contact = new ContactInfo();
		contact.setChatType(ChatType.FACEBOOK);
	}

	@After
	public void tearDown() {
		chatViewManager.shutdown();
	}

	@Test
	public void shouldShowChatForContactAndRequestFocus() throws Exception {
		when(chatFrameFactory.createChatWindow(contact)).thenReturn(chatFrame);

		chatViewManager.showChat(contact);

		verify(chatFrame).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		verify(chatFrame).setMessages(messages);
		verify(chatFrame).setVisible(true);
		verify(chatFrame).setState(Frame.ICONIFIED);
		verify(chatFrame).setState(Frame.NORMAL);
		verify(chatFrame).requestFocus();
	}

	@Test
	public void shouldRemoveChatFrameWhenClosed() throws Exception {
		shouldShowChatForContactAndRequestFocus();
		verify(chatFrameFactory).createChatWindow(contact);
		verify(chatFrame).addWindowListener(windowAdapterCaptor.capture());
		WindowAdapter windowAdapter = windowAdapterCaptor.getValue();
		WindowEvent e = mock(WindowEvent.class);
		when(e.getComponent()).thenReturn(chatFrame);

		windowAdapter.windowClosed(e);

		verify(chatFrame).removeMessages(messages);
		verify(chatFrame).removeWindowListener(windowAdapter);
	}

	@Test
	public void shouldAddMessageReceivedToChatWindow() throws Exception {
		when(chatFrameFactory.createChatWindow(contact)).thenReturn(chatFrame);
		ChatMessage chatMessage = mock(ChatMessage.class);
		when(chatMessage.getSender()).thenReturn(contact);
		chatViewManager.onMessage(chatMessage);

		verify(chatFrame).addMessage(chatMessage);
	}

	@Test
	public void shouldNotifyStatusToChatFrame() throws Exception {
		contact.setChatStatus(ChatStatus.ONLINE);
		shouldShowChatForContactAndRequestFocus();
		chatViewManager.onContactUpdated(contact);

		verify(chatFrame).notifyContactUpdated(contact);
	}

	@Test
	public void shouldNotStealFocusWhenMessageReceivedAndChatFrameIsNotActive() throws Exception {
		shouldShowChatForContactAndRequestFocus();
		when(chatFrame.isActive()).thenReturn(true);
		ContactInfo contact2 = mock(ContactInfo.class);
		ChatFrame chatFrame2 = mock(ChatFrame.class);
		when(chatFrameFactory.createChatWindow(contact2)).thenReturn(chatFrame2);
		ContactInfo user = mock(ContactInfo.class);
		ChatMessage newMessage = new ChatMessage(contact2, user, "message");

		chatViewManager.onMessage(newMessage);

		verify(chatFrame2).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		verify(chatFrame2).setMessages(messages);
		verify(chatFrame2).setVisible(true);
		verify(chatFrame2).setState(Frame.ICONIFIED);
		verify(chatFrame2, never()).setState(Frame.NORMAL);
		verify(chatFrame2, never()).requestFocus();
	}

	@Test
	public void shouldCreateChatFrame() throws Exception {
		ChatFrameFactory factory = chatViewManager.new ChatFrameFactory();

		ChatFrame chatFrame = factory.createChatWindow(contact);
		assertNotNull(chatFrame);
		assertTrue(chatFrame instanceof ChatFacebookFrame);
		contact.setChatType(ChatType.ALL);
		chatFrame = factory.createChatWindow(contact);
		assertNotNull(chatFrame);
		verify(viewEngine).sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.AllNetwork.MC_CHAT);
		assertTrue(chatFrame instanceof ChatAllFrame);

	}
}
