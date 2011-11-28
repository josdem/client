package com.all.client.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.all.messengine.Message;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.alert.AbstractAlert;
import com.all.shared.alert.Alert;
import com.all.shared.alert.MusicContentAlert;
import com.all.shared.json.JsonConverter;
import com.all.shared.messages.FeedsResponse;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.PendingEmail;
import com.all.shared.model.SyncValueObject;

public class TestAllServerAdapter {

	@InjectMocks
	private AllServerAdapter allServerAdapter = new AllServerAdapter();
	@Spy
	private StubMessEngine messEngine = new StubMessEngine();
	@Mock
	private RestTemplate syncTemplate;
	@Mock
	private Properties clientSettings;
	@Mock
	private RestTemplate defaultTemplate;

	private String email = "test@all.com";

	private SyncValueObject request = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
	private SyncValueObject expectedResponse = new SyncValueObject(email, 1, 2, System.currentTimeMillis());
	private AllMessage<SyncValueObject> mergeRequest;
	private AllMessage<SyncValueObject> commitRequest;
	private AllMessage<Long> feedsRequest;
	private AllMessage<Long> localFeedsRequest;
	private AllMessage<HashMap<String, Long>> lastFeedsRequest;
	private Long feedUserIdRequest = 3L;
	private Long feedTimestampRequest = 0L;
	private HashMap<String, Long> map = new HashMap<String, Long>();
	private FeedsResponse expectedFeedResponse = new FeedsResponse();
	private List<HttpMessageConverter<?>> converters;

	@SuppressWarnings("unchecked")
	public TestAllServerAdapter() {
		converters = Mockito.mock(ArrayList.class);
		converters.add(new StringHttpMessageConverter());
		converters.add(new StringHttpMessageConverter());
		syncTemplate = Mockito.mock(RestTemplate.class);
		when(syncTemplate.getMessageConverters()).thenReturn(converters);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		messEngine.setup(allServerAdapter);
		when(clientSettings.getProperty(anyString())).thenReturn("some url");

		allServerAdapter.initialize();

		mergeRequest = new AllMessage<SyncValueObject>(MessEngineConstants.SYNC_LIBRARY_MERGE_REQUEST, request);
		commitRequest = new AllMessage<SyncValueObject>(MessEngineConstants.SYNC_SEND_DELTA_REQUEST, request);
		initFeedRequests();
	}

	private void initFeedRequests() {
		feedsRequest = new AllMessage<Long>(MessEngineConstants.FEEDS_REQUEST, feedUserIdRequest);
		localFeedsRequest = new AllMessage<Long>(MessEngineConstants.FEEDS_LOCAL_REQUEST, feedUserIdRequest);
		map.put("userId", feedUserIdRequest);
		map.put("timestamp", feedTimestampRequest);
		lastFeedsRequest = new AllMessage<HashMap<String, Long>>(MessEngineConstants.LAST_FEED_REQUEST, map);
	}

	@Test
	public void shouldForwardMergeRequestsToLibraryBackend() throws Exception {
		when(syncTemplate.postForObject(anyString(), eq(JsonConverter.toJson(request)), eq(String.class))).thenReturn(
				JsonConverter.toJson(expectedResponse));

		messEngine.send(mergeRequest);

		@SuppressWarnings("unchecked")
		Message<SyncValueObject> response = (Message<SyncValueObject>) messEngine
				.getMessage(MessEngineConstants.SYNC_LIBRARY_MERGE_RESPONSE);
		assertNotNull(response);
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	public void shouldReturnNullResponseIfLibraryBackendFails() throws Exception {
		when(syncTemplate.postForObject(anyString(), eq(JsonConverter.toJson(request)), eq(String.class))).thenThrow(
				new RuntimeException("Some server error"));

		messEngine.send(mergeRequest);

		@SuppressWarnings("unchecked")
		Message<SyncValueObject> response = (Message<SyncValueObject>) messEngine
				.getMessage(MessEngineConstants.SYNC_LIBRARY_MERGE_RESPONSE);
		assertNotNull(response);
		assertNull(response.getBody());
	}

	@Test
	public void shouldForwardCommitRequestToLibraryBackendAndReturnResponseWithoutEvents() throws Exception {
		when(syncTemplate.postForObject(anyString(), eq(JsonConverter.toJson(request)), eq(String.class))).thenReturn(
				JsonConverter.toJson(expectedResponse));
		assertNotNull(request.getEvents());
		assertNotNull(expectedResponse.getEvents());

		messEngine.send(commitRequest);

		@SuppressWarnings("unchecked")
		Message<SyncValueObject> response = (Message<SyncValueObject>) messEngine
				.getMessage(MessEngineConstants.SYNC_SEND_DELTA_RESPONSE);
		assertNotNull(response);
		SyncValueObject actualResponse = response.getBody();
		assertEquals(expectedResponse, actualResponse);
		assertNull(actualResponse.getEvents());
	}

	@Test
	public void shouldReturnSameRequestWithoutEventsIfCannotCommitDeltaToLibraryBackend() throws Exception {
		when(syncTemplate.postForObject(anyString(), eq(JsonConverter.toJson(request)), eq(String.class))).thenThrow(
				new RuntimeException("Some server error"));
		assertNotNull(request.getEvents());

		messEngine.send(commitRequest);

		@SuppressWarnings("unchecked")
		Message<SyncValueObject> response = (Message<SyncValueObject>) messEngine
				.getMessage(MessEngineConstants.SYNC_SEND_DELTA_RESPONSE);
		assertNotNull(response);
		assertEquals(request, response.getBody());
		assertNull(request.getEvents());
	}

	@Test
	public void shouldGetFeeds() throws Exception {
		when(defaultTemplate.getForObject(anyString(), eq(String.class), eq(feedUserIdRequest))).thenReturn(
				JsonConverter.toJson(expectedFeedResponse));

		messEngine.send(feedsRequest);
		@SuppressWarnings("unchecked")
		Message<FeedsResponse> response = (Message<FeedsResponse>) messEngine
				.getMessage(MessEngineConstants.FEEDS_RESPONSE);

		assertNotNull(response);
		assertEquals(expectedFeedResponse.getFeeds(), response.getBody().getFeeds());
		assertEquals(expectedFeedResponse.getOwnerId(), response.getBody().getOwnerId());
		assertEquals(expectedFeedResponse.getCurrentServerTime(), response.getBody().getCurrentServerTime());

	}

	@Test
	public void shouldGetLastFeeds() throws Exception {
		when(defaultTemplate.getForObject(anyString(), eq(String.class), eq(feedUserIdRequest), eq(feedTimestampRequest)))
				.thenReturn(JsonConverter.toJson(expectedFeedResponse));
		messEngine.send(lastFeedsRequest);

		@SuppressWarnings("unchecked")
		Message<FeedsResponse> response = (Message<FeedsResponse>) messEngine
				.getMessage(MessEngineConstants.FEEDS_RESPONSE);
		assertNotNull(response);
		assertEquals(expectedFeedResponse.getFeeds(), response.getBody().getFeeds());
		assertEquals(expectedFeedResponse.getOwnerId(), response.getBody().getOwnerId());
		assertEquals(expectedFeedResponse.getCurrentServerTime(), response.getBody().getCurrentServerTime());
	}

	@Test
	public void shouldGetLocalFeeds() throws Exception {
		when(defaultTemplate.getForObject(anyString(), eq(String.class), eq(feedUserIdRequest))).thenReturn(
				JsonConverter.toJson(expectedFeedResponse));

		messEngine.send(localFeedsRequest);
		@SuppressWarnings("unchecked")
		Message<FeedsResponse> response = (Message<FeedsResponse>) messEngine
				.getMessage(MessEngineConstants.FEEDS_LOCAL_RESPONSE);

		assertNotNull(response);
		assertEquals(expectedFeedResponse.getFeeds(), response.getBody().getFeeds());
		assertEquals(expectedFeedResponse.getOwnerId(), response.getBody().getOwnerId());
		assertEquals(expectedFeedResponse.getCurrentServerTime(), response.getBody().getCurrentServerTime());
	}

	@Test
	public void shouldDeleteAlert() throws Exception {
		final String id = "id";
		Alert alert = new AbstractAlert() {
			@Override
			public String getId() {
				return id;
			}
		};
		messEngine.send(new AllMessage<Alert>(MessEngineConstants.DELETE_ALERT_TYPE, alert));

		verify(defaultTemplate).delete(anyString(), eq(alert.getId()));
	}

	@Test
	public void shouldPutAlert() throws Exception {
		@SuppressWarnings("deprecation")
		MusicContentAlert alert = new MusicContentAlert(new ContactInfo(), new ContactInfo(), new Date(),
				new ModelCollection(), "message");
		messEngine.send(new AllMessage<Alert>(MessEngineConstants.PUT_ALERT_TYPE, alert));

		verify(defaultTemplate).put(anyString(), eq(JsonConverter.toJson(alert)), eq(alert.getId()));

	}

	@Test
	public void shouldGetAlerts() throws Exception {
		@SuppressWarnings("deprecation")
		MusicContentAlert alert = new MusicContentAlert(new ContactInfo(), new ContactInfo(), new Date(),
				new ModelCollection(), "message");
		List<Alert> expectedList = new ArrayList<Alert>();
		expectedList.add(alert);
		String userId = "userID";
		when(defaultTemplate.getForObject(anyString(), eq(String.class), eq(userId))).thenReturn(
				JsonConverter.toJson(expectedList));
		messEngine.send(new AllMessage<String>(MessEngineConstants.ALERTS_REQUEST_TYPE, userId));

		verify(defaultTemplate).getForObject(anyString(), eq(String.class), eq(userId));

		Message<?> responseMessage = messEngine.getMessage(MessEngineConstants.ALERTS_RESPONSE_TYPE);
		assertNotNull(responseMessage);
		assertEquals(expectedList, responseMessage.getBody());
	}

	@Test
	public void shouldDeletePendingEmails() throws Exception {
		Message<?> message = new AllMessage<String>(MessEngineConstants.DELETE_PENDING_EMAILS_TYPE, "delete");
		messEngine.send(message);
		verify(defaultTemplate).delete(anyString(), eq(message.getBody()));
	}

	@Test
	public void shouldUpdateContactProfile() throws Exception {
		@SuppressWarnings("deprecation")
		ContactInfo contactInfo = new ContactInfo();
		contactInfo.setEmail("user@all.com");
		contactInfo.setId(1L);
		Message<?> message = new AllMessage<ContactInfo>(MessEngineConstants.UPDATE_CONTACT_PROFILE_REQUEST, contactInfo);
		when(defaultTemplate.postForObject(anyString(), eq(JsonConverter.toJson(contactInfo)), eq(String.class)))
				.thenReturn(JsonConverter.toJson(contactInfo));
		messEngine.send(message);
		verify(defaultTemplate).postForObject(anyString(), eq(JsonConverter.toJson(contactInfo)), eq(String.class));
		Message<?> response = messEngine.getMessage(MessEngineConstants.UPDATE_CONTACT_PROFILE_RESPONSE);
		assertNotNull(response);
		assertEquals(contactInfo, response.getBody());
	}

	@SuppressWarnings("unchecked")
	@Test
	// TODO Hacer más complejo este test
	public void shouldProcessContactListRequest() throws Exception {
		String email = "user@all.com";
		List<ContactInfo> contactlist = new ArrayList<ContactInfo>();
		AllMessage<String> request = new AllMessage<String>(MessEngineConstants.CONTACT_LIST_REQUEST_TYPE, email);
		when(defaultTemplate.getForObject(anyString(), any(Class.class), eq(request))).thenReturn(contactlist);

		messEngine.send(request);

		Message<List<ContactInfo>> response = (Message<List<ContactInfo>>) messEngine
				.getMessage(MessEngineConstants.CONTACT_LIST_RESPONSE_TYPE);
		assertNotNull(response);
	}

	@Test
	public void shouldCreatePendingEmail() throws Exception {
		final PendingEmail pendingEmail = new PendingEmail(1L, "not@all.com");
		Message<?> message = new AllMessage<PendingEmail>(MessEngineConstants.SEND_EMAIL_TYPE, pendingEmail);
		when(defaultTemplate.postForObject(anyString(), eq(JsonConverter.toJson(message.getBody())), eq(String.class)))
				.thenAnswer(new Answer<String>() {
					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						pendingEmail.setId(100L);
						return JsonConverter.toJson(pendingEmail);
					}
				});
		messEngine.send(message);
		
		assertNotNull(pendingEmail.getId());
		Message<?> response = messEngine.getMessage(MessEngineConstants.PUSH_PENDING_EMAIL_TYPE);
		assertNotNull(response);
		PendingEmail actual = (PendingEmail) response.getBody();
		assertEquals(pendingEmail.getId(), actual.getId());
	}
	
	@Test
	public void shouldProcessDeleteContactsMessage() throws Exception {
		String body = "contactIds";
		String sender = "sender";
		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.DELETE_CONTACTS_TYPE, body);
		message.putProperty(MessEngineConstants.SENDER_ID, sender);

		messEngine.send(message);

		verify(defaultTemplate).delete(anyString(), eq(sender), eq(body));
	}
}
