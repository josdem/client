package com.all.client.view.alerts;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.alert.FriendshipRequestAlert;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactRequest;
import com.all.shared.model.Gender;

public class TestAlertViewFactory {

	@InjectMocks
	private AlertViewFactory alertFactory = new AlertViewFactory();
	@Mock
	private Messages messages;
	@Mock
	public DialogFactory dialogFactory;

	private ContactInfo sender;
	private ContactInfo receiver;
	private Long id = 1L;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(messages.getMessage(anyString(), anyString())).thenReturn("");
		when(messages.getMessage(anyString())).thenReturn("");
		sender = new ContactInfo();
		sender.setGender(Gender.MALE);
		receiver = new ContactInfo();
	}

	@Test
	public void shouldReturnFriendshipRequestAlertView() throws Exception {
		ContactRequest request = new ContactRequest(sender, receiver);
		request.setId(id);
		FriendshipRequestAlert alert = new FriendshipRequestAlert(request);
		assertTrue(alertFactory.getAlertView(alert) instanceof FriendshipRequestAlertView);
	}

}
