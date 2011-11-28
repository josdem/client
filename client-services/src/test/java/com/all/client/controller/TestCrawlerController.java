package com.all.client.controller;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.client.services.CrawlerClientService;
import com.all.shared.command.LoginCommand;
import com.all.shared.external.email.EmailDomain;
import com.all.shared.messages.CrawlerRequest;
import com.all.shared.messages.CrawlerResponse;
import com.all.shared.messages.EmailContact;
import com.all.shared.model.ContactInfo;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestCrawlerController {

	@UnderTest
	CrawlerController controller;

	@Mock
	CrawlerClientService service;
	@Mock
	ViewEngine viewEngine;
	@Mock
	CrawlerResponse response;
	@Mock
	private EmailContact emailContact;
	@Mock
	private ContactInfo contact;

	private List<EmailContact> emailContacts;
	private List<ContactInfo> contactInfo;

	@Before
	public void setup() throws Exception {
		emailContacts = new ArrayList<EmailContact>();
		contactInfo = new ArrayList<ContactInfo>();
		contactInfo.add(contact);
		when(emailContact.getRegisteredContacts()).thenReturn(contactInfo);
		emailContacts.add(emailContact);
	}

	@Test
	public void shouldImportContacts() throws Exception {
		HashMap<EmailDomain, List<LoginCommand>> emailAccounts = new HashMap<EmailDomain, List<LoginCommand>>();
		when(response.getEmailContacts()).thenReturn(emailContacts);
		when(service.requestImportContacts(isA(CrawlerRequest.class))).thenReturn(response);
		controller.importContacts(emailAccounts);

		verify(service).requestImportContacts(isA(CrawlerRequest.class));
	}
}
