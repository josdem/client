package com.all.client.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.command.LoginCommand;
import com.all.shared.external.email.EmailDomain;
import com.all.shared.messages.CrawlerRequest;
import com.all.shared.messages.CrawlerResponse;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.testing.MockInyectRunner;
import com.all.testing.Stub;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestCrawlerClientService {
	@UnderTest
	private CrawlerClientService service;
	@Stub
	private MessEngine messEngine = new StubMessEngine();
	@Mock
	private CyclicBarrier requestLock;
	private CrawlerResponse response = new CrawlerResponse();
	private AllMessage<CrawlerResponse> responseMessage = 
		new AllMessage<CrawlerResponse>(MessEngineConstants.IMPORT_CONTACTS_RESPONSE_TYPE, response);
	@Mock
	private HashMap<EmailDomain, List<LoginCommand>> emailAccounts;
	private StubMessEngine recordedEngine;
	
	@Before
	public void setup(){
		recordedEngine = (StubMessEngine) messEngine;
		messEngine.addMessageListener(MessEngineConstants.IMPORT_CONTACTS_RESPONSE_TYPE, service);
		assertTrue(recordedEngine.getRegisteredTypes().contains(MessEngineConstants.IMPORT_CONTACTS_RESPONSE_TYPE));
	}
	
	@Test
	public void shouldRequestImportContacts() throws Exception {
		recordedEngine.addTypeResponse(MessEngineConstants.IMPORT_CONTACTS_REQUEST_TYPE, responseMessage);	
		CrawlerRequest request = new CrawlerRequest();
		request.setAccounts(emailAccounts);
		CrawlerResponse actualResponse = service.requestImportContacts(request);
		
		assertEquals(2, recordedEngine.sentMessagesCount());
		Message<?> sentMessage = recordedEngine.getMessage(0);
		assertEquals(request, sentMessage.getBody());
		assertEquals(response, actualResponse);
		verify(requestLock).reset();
		verify(requestLock).await(anyInt(), isA(TimeUnit.class));
		verify(requestLock).await();
	}
}
