package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.ALERTS_RESPONSE_TYPE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.all.appControl.control.ControlEngine;
import com.all.client.UnitTestCase;
import com.all.client.services.ContactCacheService;
import com.all.client.services.UserProfileClientService;
import com.all.core.common.model.ApplicationModel;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.shared.alert.Alert;
import com.all.shared.alert.FriendshipResponseAlert;
import com.all.shared.alert.MusicContentAlert;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

public class TestAlertController extends UnitTestCase {

	@InjectMocks
	private AlertController alertController = new AlertController();
	@Mock
	private Alert alert;
	@Mock
	private Set<Alert> currentAlerts;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private MessEngine messEngine;
	@Mock
	private ContactController contactController;
	@Mock
	private UserProfileClientService profileService;
	@Mock
	private User user;
	@Mock
	private Future<Message<?>> future;
	@Mock
	private FriendshipResponseAlert friendshipResponseAlert;
	@Mock
	private ContactInfo sender;
	@SuppressWarnings("unused")
	@Mock
	private ContactCacheService contactCacheService;

	@Before
	public void setup() {
		when(alert.getTypedClass()).thenReturn(Alert.class);
	}

	@Test
	public void shouldDeleteAlert() throws Exception {
		String alertId = "alertId";
		when(alert.getId()).thenReturn(alertId);
		when(alert.getType()).thenReturn(MusicContentAlert.TYPE);
		when(currentAlerts.remove(alert)).thenReturn(true);

		alertController.delete(alert);

		verify(messEngine).send(any(AllMessage.class));
		// verify(controlEngine).set(eq(Model.CURRENT_ALERTS),
		// any(Collection.class), eq(Events.Application.CURRENT_ALERTS_CHANGED));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldRequestAlerts() throws Exception {
		when(controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(true);
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
		String userId = "user@all.com";
		when(user.getEmail()).thenReturn(userId);
		when(messEngine.request(isA(AllMessage.class), eq(MessEngineConstants.ALERTS_RESPONSE_TYPE), anyLong()))
				.thenReturn(future);
		Collection<Alert> alerts = new TreeSet<Alert>();
		alerts.add(alert);
		Message responseMessage = new AllMessage(ALERTS_RESPONSE_TYPE, (Serializable) alerts);
		when(future.get()).thenReturn(responseMessage);
		when(currentAlerts.addAll(any(Collection.class))).thenReturn(true);

		alertController.requestAlerts();

		verify(messEngine).request(isA(AllMessage.class), eq(MessEngineConstants.ALERTS_RESPONSE_TYPE), anyLong());
		// verify(controlEngine).set(eq(Model.CURRENT_ALERTS),
		// any(Collection.class), eq(Events.Application.CURRENT_ALERTS_CHANGED));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddContactWhenFriendshipResponseAlert() throws Exception {
		when(friendshipResponseAlert.getSender()).thenReturn(sender);
		when(friendshipResponseAlert.getTypedClass()).thenReturn(Alert.class);

		AllMessage alertMessage = new AllMessage(ALERTS_RESPONSE_TYPE, friendshipResponseAlert);
		alertController.onAlertMessageReceived(alertMessage);

		Thread.sleep(100);
		verify(profileService).requestAvatar(sender);
		verify(currentAlerts).add(friendshipResponseAlert);
		verify(contactController).createContact(sender);
	}

}
