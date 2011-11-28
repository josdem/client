package com.all.core.common.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.appControl.control.ControlEngine;
import com.all.core.common.model.ApplicationModel;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.messengine.impl.StubMessEngine;
import com.all.networking.PeerNetworkingService;
import com.all.shared.alert.Alert;
import com.all.shared.alert.MusicContentAlert;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.PresenceInfo;
import com.all.shared.model.PushMessage;
import com.all.shared.model.UltrapeerNode;
import com.all.shared.model.User;

public class TestUltrapeerProxy {
	private static final String ULTRAPEER_ADDR = "11.22.22.11";

	private StubMessEngine stubEngine = new StubMessEngine();

	@InjectMocks
	private UltrapeerProxy interceptor = new UltrapeerProxy();

	@Mock
	private PeerNetworkingService peerNetworkingService;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private UltrapeerSource ultrapeerSource;

	@Spy
	private MessEngine messEngine = stubEngine;

	@Mock
	private User user;
	private User currentUser;
	private String email;
	private User defaultUser;
	private ContactInfo defaultContact;

	private UltrapeerNode ultrapeerNode = new UltrapeerNode(ULTRAPEER_ADDR);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(true);
		when(ultrapeerSource.get()).thenReturn(ultrapeerNode);
		
		interceptor.initialize();
	}

	@Test
	public void shouldShutdown() throws Exception {
		interceptor.shutdown();
		verify(peerNetworkingService).shutdown();
	}

	@Test//(timeout = 2000)
	public void shouldAutoRespondPresenceRequestForDefaultContacts() throws Exception {
		setDefaultContactsExpectations();

		final AtomicBoolean done = new AtomicBoolean(false);
		messEngine.addMessageListener(MessEngineConstants.CONTACT_STATUS_RESPONSE_TYPE,
				new MessageListener<AllMessage<PresenceInfo>>() {
					@Override
					public void onMessage(AllMessage<PresenceInfo> message) {
						PresenceInfo presenceInfo = message.getBody();
						if (presenceInfo.isOnline()) {
							done.set(true);
						}
					}
				});
		messEngine.send(new AllMessage<String>(MessEngineConstants.CONTACT_STATUS_REQUEST_TYPE, defaultContact.getEmail()));

		verify(peerNetworkingService, never()).send(eq(email), any(AllMessage.class));
		assertTrue(done.get());
	}

	@Test
	public void shouldIgnorePushMessagesForDefaultContacts() throws Exception {
		setDefaultContactsExpectations();
		ChatMessage body = new ChatMessage(
				new ContactInfo(currentUser), defaultContact, "hello");
		AllMessage<ChatMessage> chatMessage = new AllMessage<ChatMessage>(MessEngineConstants.CHAT_MESSAGE_RESPONSE, body);
		PushMessage<ChatMessage> pushMessage = new PushMessage<ChatMessage>(chatMessage, Arrays.asList(defaultContact.getEmail()));
		messEngine.send(pushMessage);

		verify(peerNetworkingService, never()).send(eq(email), any(AllMessage.class));

	}

	@Test
	public void shouldIgnorePutAlertMessagesForDefaultContacts() throws Exception {
		setDefaultContactsExpectations();
		messEngine.send(new AllMessage<Alert>(MessEngineConstants.PUT_ALERT_TYPE, new MusicContentAlert(new ContactInfo(
				currentUser), defaultContact, new Date(), new ModelCollection(), "personal Message")));

		verify(peerNetworkingService, never()).send(eq(email), any(AllMessage.class));
	}

	private void setDefaultContactsExpectations() throws Exception {
		initUserSession();
		initDefaultContacts();
		setValidSessionExpectations();
	}

	private void initDefaultContacts() {
		List<ContactInfo> defaultContacts = new ArrayList<ContactInfo>();
		defaultUser = new User();
		defaultUser.setEmail("default@all.com");
		defaultUser.setFirstName("default");
		defaultUser.setLastName("name");
		defaultContact = new ContactInfo(defaultUser);
		defaultContacts.add(defaultContact);
		messEngine.send(new AllMessage<List<ContactInfo>>(MessEngineConstants.DEFAULT_CONTACTS_RESPONSE_TYPE,
				defaultContacts));
	}

	private void setValidSessionExpectations() {
		when(peerNetworkingService.send(eq(email), any(AllMessage.class))).thenReturn(true);
		when(peerNetworkingService.hasDefaultSession()).thenReturn(true);
	}

	private void initUserSession() {
		currentUser = new User();
		email = "user@all.com";
		currentUser.setEmail(email);
		when(peerNetworkingService.hasDefaultSession()).thenReturn(true);
		when(peerNetworkingService.send(eq(email), any(AllMessage.class))).thenReturn(true);
		interceptor.initSessionForUser(currentUser);
		reset(peerNetworkingService);
	}

	@Test
	public void shouldRetryToSendAMessageThroughTheNetworkIfUltrapeerSessionIsValid() throws Exception {
		when(peerNetworkingService.hasDefaultSession()).thenReturn(true);
		AllMessage<String> message = new AllMessage<String>("type", "body");
		when(peerNetworkingService.send(anyString(), any(AllMessage.class))).thenReturn(false);

		interceptor.onMessage(message);

		verify(peerNetworkingService, times(UltrapeerProxy.MAX_ATTEMPTS)).send(anyString(), any(AllMessage.class));
	}

	@Test
	public void shouldNotLoadDefaultContactsIfOffline() throws Exception {
		when(controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(false);
		when(user.getEmail()).thenReturn("josdem@all.com");

		interceptor.initSessionForUser(user);
		verify(user, never()).getEmail();
	}

}
