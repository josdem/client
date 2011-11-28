package com.all.client.services;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.MessageListener;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.testing.MockInyectRunner;
import com.all.testing.Stub;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestContactMessageService {

	@UnderTest
	private ContactMessageService service;

	@Stub
	private MessEngine messEngine = new StubMessEngine();
	private StubMessEngine testEngine = (StubMessEngine) messEngine;

	private Long userId = 1L;

	private Set<ContactInfo> contacts = getContactSet();

	@SuppressWarnings("unused")
	@Mock
	private MessageListener<AllMessage<ContactInfo>> messageListener;

	@SuppressWarnings("deprecation")
	private Set<ContactInfo> getContactSet() {
		Set<ContactInfo> result = new HashSet<ContactInfo>();
		ContactInfo contactInfoA = new ContactInfo();
		contactInfoA.setId(1L);
		ContactInfo contactInfoB = new ContactInfo();
		contactInfoB.setId(2L);
		result.add(contactInfoA);
		result.add(contactInfoB);
		return result;
	}

	@Test
	public void shouldDeleteContacts() throws Exception {
		service.deleteContacts(userId, contacts);

		Message<?> messageSent = testEngine.getMessage(0);
		assertEquals(service.getIdsAsString(contacts), messageSent.getBody());
		assertEquals(userId.toString(), messageSent.getProperty(MessEngineConstants.SENDER_ID));
	}

	@Test
	public void shouldDeletePendingEmails() throws Exception {
		service.deletePendingEmails(contacts);

		assertEquals(service.getIdsAsString(contacts), testEngine.getMessage(0).getBody());
	}

}
