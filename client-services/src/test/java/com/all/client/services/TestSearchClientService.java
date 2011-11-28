package com.all.client.services;

import static com.all.shared.messages.MessEngineConstants.SEARCH_CONTACTS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.SEARCH_CONTACTS_RESPONSE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.client.services.reporting.ClientReporter;
import com.all.messengine.MessEngine;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.testing.MockInyectRunner;
import com.all.testing.Stub;
import com.all.testing.UnderTest;


@RunWith(MockInyectRunner.class)
public class TestSearchClientService {

	@UnderTest
	SearchClientService service;
	@Stub
	MessEngine messEngine = new StubMessEngine();
	StubMessEngine testEngine = (StubMessEngine) messEngine;
	@Mock
	CyclicBarrier requestLock;
	@SuppressWarnings("unused")
	@Mock
	private ClientReporter reporter;

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSearchForContacts() throws Exception {
		final String keyword = "some name";
		final ArrayList<ContactInfo> expectedResult = new ArrayList<ContactInfo>();
		final ContactInfo contactInfo = new ContactInfo();
		contactInfo.setName(keyword);
		testEngine.addMessageListener(SEARCH_CONTACTS_RESPONSE_TYPE, service);
		testEngine.addTypeReaction(SEARCH_CONTACTS_REQUEST_TYPE, new Runnable(){
			@Override
			public void run() {
				expectedResult.add(contactInfo);
				testEngine.send(new AllMessage<ArrayList<ContactInfo>>(SEARCH_CONTACTS_RESPONSE_TYPE, expectedResult));
			}
		});
		
		List<ContactInfo> result = service.search(keyword);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(keyword, result.get(0).getName());
		verify(requestLock).reset();
		verify(requestLock).await(anyInt(), isA(TimeUnit.class));
		verify(requestLock).await();

	}
}
