package com.all.client.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatCredentials;
import com.all.chat.ChatService;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.Message;
import com.all.chat.exceptions.ChatException;
import com.all.chat.impl.ChatUserImpl;
import com.all.chat.impl.MessageImpl;
import com.all.client.services.ContactCacheService;
import com.all.client.services.UserPreferenceService;
import com.all.core.actions.ContactMessage;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

public class TestChatController {

	@InjectMocks
	private ChatController chatController = new ChatController();
	@Mock
	private ContactCacheService contactCacheService;
	@Mock
	private ChatService chatService;
	@Mock
	private ChatCredentials chatCredentials;
	@Mock
	private UserPreferenceService userPreferenceService;
	@Mock
	private ControlEngine controlEngine;
	@Captor
	private ArgumentCaptor<ChatServiceListener> listenerCaptor;
	@Captor
	private ArgumentCaptor<ContactInfo> contactInfoCaptor;
	@Captor
	private ArgumentCaptor<ChatMessage> chatMessageCaptor;
	
	private ChatServiceListener serviceListener;


	private String chatId = "chatId";
	
	private String chatName = "chatName";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		chatController.initialize();
		verify(chatService).addListener(listenerCaptor.capture());
		serviceListener = listenerCaptor.getValue();
	}

	@After
	public void tearDown() {
		chatController.shutdown();
		verify(chatService).removeListener(serviceListener);
	}

	@Test
	public void shouldLogin() throws Exception {
		when(chatCredentials.getType()).thenReturn(ChatType.FACEBOOK);
		ChatUser chatUser = new ChatUserImpl(chatId, chatName, ChatStatus.ONLINE, ChatType.FACEBOOK);
		when(chatService.login(chatCredentials)).thenReturn(chatUser);

		assertTrue(chatController.login(chatCredentials));
		verify(userPreferenceService).setFacebookChatStatus(any(Boolean.class));
	}

	@Test
	public void shouldLogout() throws Exception {
		shouldLogin();
		chatController.logout(ChatType.FACEBOOK);
		verify(chatService).logout(contactInfoCaptor.capture());
		ContactInfo contactInfo = contactInfoCaptor.getValue();
		assertEquals(chatId, contactInfo.getChatId());
		assertEquals(chatName, contactInfo.getChatName());
		assertEquals(ChatStatus.ONLINE, contactInfo.getChatStatus());
		assertEquals(ChatType.FACEBOOK, contactInfo.getChatType());
	}
	
	@Test
	public void shouldNotLogoutIfNotLoginCorrecltyFirst() throws Exception {
		chatController.logout(ChatType.FACEBOOK);
		verify(chatService, never()).logout(any(ChatUser.class));
	}

	@Test
	public void shouldNotLoginTwiceForSameAccount() throws Exception {
		shouldLogin();
		reset(chatService);
		assertTrue(chatController.login(chatCredentials));
		verify(chatService, never()).login(chatCredentials);
	}

	@Test
	public void shouldNotLoginIfException() throws Exception {
		ChatCredentials chatCredentials = mock(ChatCredentials.class);
		when(chatService.login(chatCredentials)).thenThrow(new ChatException("Some exception."));

		assertFalse(chatController.login(chatCredentials));
	}

	@Test
	public void shouldLoginOnApplicationStarted() throws Exception {
		User user = new User();
		user.setEmail("user@all.com");
		user.setPassword("pwd");
		chatController.start(new AllMessage<User>(MessEngineConstants.USER_SESSION_STARTED_TYPE, user));

		verify(chatService).login(any(ChatCredentials.class));
	}

	@Test
	public void shouldNotifyListenersWhenMessageReceived() throws Exception {
		ChatUser recipient = new ChatUserImpl("chatId1", "chatName1", ChatStatus.ONLINE, ChatType.FACEBOOK);
		ChatUser sender = new ChatUserImpl("chatId2", "chatName2", ChatStatus.ONLINE, ChatType.FACEBOOK);
		Message message = new MessageImpl(recipient, sender, "message", ChatType.FACEBOOK);

		serviceListener.onMessage(message);

		verify(controlEngine).fireValueEvent(eq(Events.Chat.INCOMING_MESSAGE), chatMessageCaptor.capture());
		ChatMessage chatMessage = chatMessageCaptor.getValue();
		assertEquals(new ContactInfo(recipient), chatMessage.getSender());
		assertEquals(new ContactInfo(sender), chatMessage.getRecipient());
		assertEquals(message.getMessage(), chatMessage.getMessage());
		assertEquals(message.getChatType(), chatMessage.getChatType());
	}

	@Test
	public void shouldAddContactsOnContactsRetrieved() throws Exception {
		Collection<ChatUser> chatUsers = new ArrayList<ChatUser>();
		ChatUser contact = new ChatUserImpl("chatId1", "chatName1", ChatStatus.ONLINE, ChatType.FACEBOOK);
		chatUsers.add(contact);
		Set<ContactInfo> contacts = new HashSet<ContactInfo>();
		contacts.add(new ContactInfo(contact));

		serviceListener.onChatContactsRetrieved(chatUsers);

		verify(contactCacheService).addContacts(contacts);
	}

	@Test
	public void shouldUpdateContactStatusOnEvent() throws Exception {
		ChatUser contact = new ChatUserImpl("chatId1", "chatName1", ChatStatus.ONLINE, ChatType.FACEBOOK);

		serviceListener.onChatContactPresenceChanged(contact);

		verify(contactCacheService).updateStatus(new ContactInfo(contact));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSendMessageToContact() throws Exception {
		String message = "message";
		ContactInfo contact = new ContactInfo();
		ChatUser recipient = new ChatUserImpl("chatId1", "chatName1", ChatStatus.ONLINE, ChatType.FACEBOOK);
		ChatUser sender = new ChatUserImpl("chatId2", "chatName2", ChatStatus.ONLINE, ChatType.FACEBOOK);
		Message messageImpl = new MessageImpl(recipient, sender, "message", ChatType.FACEBOOK);
		when(chatService.sendMessage(message, contact)).thenReturn(messageImpl);

		ChatMessage chatMessage = chatController.sendTo(new ContactMessage(contact, message));
		assertNotNull(chatMessage);
		assertEquals(new ContactInfo(recipient), chatMessage.getSender());
		assertEquals(new ContactInfo(sender), chatMessage.getRecipient());
		assertEquals(messageImpl.getMessage(), chatMessage.getMessage());
		assertEquals(messageImpl.getChatType(), chatMessage.getChatType());
	}
	
	@Test
	public void shouldFireFacebookLoginDialog() throws Exception {
		when(controlEngine.get(Model.UserPreference.FACEBOOK_CHAT_STATUS)).thenReturn(false);
		
		chatController.isSelectionChatType(ChatType.FACEBOOK);
		
		verify(controlEngine).fireEvent(Events.Chat.DISPLAY_FACEBOOK_AUTHORIZATION_DIALOG);
	}
	
	@Test
	public void shouldNotFireFacebookLoginDialog() throws Exception {
		when(controlEngine.get(Model.UserPreference.FACEBOOK_CHAT_STATUS)).thenReturn(true);
		
		chatController.isSelectionChatType(ChatType.FACEBOOK);
		
		verify(contactCacheService).setSelectedChatType(ChatType.FACEBOOK);
	}
}
