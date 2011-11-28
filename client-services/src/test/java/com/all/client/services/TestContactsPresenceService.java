package com.all.client.services;

import static com.all.shared.messages.MessEngineConstants.CONTACT_LIST_STATUS_REQUEST_TYPE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import com.all.chat.ChatType;
import com.all.client.util.AllMessageMatcher;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestContactsPresenceService {

	@UnderTest
	ContactsPresenceService service;
	@Mock
	MessEngine messEngine;
	@Mock
	ContactCacheService contactCacheService;
	@Mock
	User user;

	@SuppressWarnings("deprecation")
	@Test
	public void shouldRequestContactsStatusForNonPendingContacts() throws Exception {
		List<ContactInfo> contacts = new ArrayList<ContactInfo>();
		ContactInfo contact = new ContactInfo();
		String email="email@all.com";
		contact.setEmail(email);
		contact.setId(1L);
		contacts.add(contact );
		
		List<String> expectedContactList = new ArrayList<String>();
		expectedContactList.add(email);
		
		when(contactCacheService.getContactsByType(ChatType.ALL)).thenReturn(contacts );
		service.start();
		verify(messEngine).send((Message<?>) Matchers.argThat(new AllMessageMatcher(CONTACT_LIST_STATUS_REQUEST_TYPE, expectedContactList)));
	}
}
