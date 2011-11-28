package com.all.client.alerts;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.controller.ContactController;
import com.all.shared.alert.FriendshipRequestAlert;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactRequest;

public class TestFriendshipAlertsDelegate {

	@InjectMocks
	private FriendshipAlertsDelegate delegate = new FriendshipAlertsDelegate();

	@Mock
	private ContactController contactController;
	@Mock
	private ContactRequest request;
	@Mock
	private FriendshipRequestAlert requestAlert;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldRespondFriendshipRequest() throws Exception {
		@SuppressWarnings("deprecation")
		ContactInfo contact = new ContactInfo();
		contact.setId(1l);
		when(requestAlert.getRequest()).thenReturn(request);
		when(request.getRequester()).thenReturn(contact);

		delegate.accept(requestAlert);

		verify(request).accept();
		verify(contactController).respondFriendshipRequest(request);
		verify(contactController).createContact(contact);
	}

}
