package com.all.client.rest;

import static com.all.shared.json.JsonConverter.toBean;
import static com.all.shared.json.JsonConverter.toJson;
import static com.all.shared.messages.MessEngineConstants.ALERTS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.ALERTS_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.AVATAR_OWNER;
import static com.all.shared.messages.MessEngineConstants.AVATAR_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.DELETE_ALERT_TYPE;
import static com.all.shared.messages.MessEngineConstants.FACEBOOK_RECOMMENDATION;
import static com.all.shared.messages.MessEngineConstants.FRIENDSHIP_REQUEST_RESULT_TYPE;
import static com.all.shared.messages.MessEngineConstants.PUT_ALERT_TYPE;
import static com.all.shared.messages.MessEngineConstants.SENDER_ID;
import static com.all.shared.messages.MessEngineConstants.SYNC_LIBRARY_MERGE_RESPONSE;
import static com.all.shared.messages.MessEngineConstants.SYNC_SEND_DELTA_RESPONSE;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.all.client.controller.beans.FeedsRequest;
import com.all.core.common.messages.ErrorMessage;
import com.all.core.common.messages.ResponseMessage;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.MessageMethod;
import com.all.networking.NetworkingConstants;
import com.all.shared.alert.Alert;
import com.all.shared.json.JsonConverter;
import com.all.shared.messages.ContactRequestResult;
import com.all.shared.messages.CrawlerRequest;
import com.all.shared.messages.CrawlerResponse;
import com.all.shared.messages.FeedsResponse;
import com.all.shared.messages.FriendshipRequestStatus;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Avatar;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactRequest;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.PendingEmail;
import com.all.shared.model.SyncValueObject;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;

@Component
public class AllServerAdapter {
	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	private Properties clientSettings;
	@Autowired
	private MessEngine messEngine;

	private final RestTemplate defaultTemplate = new RestTemplate();

	// Uses another instance for restTemplate since sync requires an special
	// converter
	private final RestTemplate syncTemplate = new RestTemplate();

	private final SyncHttpResponseConverter syncConverter = new SyncHttpResponseConverter();

	@PostConstruct
	public void initialize() {
		initializeSyncTemplate();
		log.info("ALL-SERVER-ADAPTER: INIT COMPLETE:" + clientSettings.getProperty("all.server.url"));
	}

	@MessageMethod(MessEngineConstants.SYNC_SEND_DELTA_REQUEST)
	public void postDelta(SyncValueObject delta) {
		long startTime = System.currentTimeMillis();
		SyncValueObject response = null;
		try {
			response = toBean(syncTemplate.postForObject(getUrl("sync.commit"), toJson(delta), String.class),
					SyncValueObject.class);
			log.info("Library server took " + (System.currentTimeMillis() - startTime) + " to commit library changes.");
		} catch (Exception e) {
			log.error("Unexpected error commiting delta to library server.", e);
			response = delta;
		} finally {
			response.setEvents(null);
			messEngine.send(new AllMessage<SyncValueObject>(SYNC_SEND_DELTA_RESPONSE, response));
		}
	}

	@MessageMethod(MessEngineConstants.SYNC_LIBRARY_MERGE_REQUEST)
	public void postSyncMergeRequest(SyncValueObject request) {
		long startTime = System.currentTimeMillis();
		SyncValueObject response = null;
		try {
			syncConverter.addMergeRequest(request);
			response = toBean(syncTemplate.postForObject(getUrl("sync.merge"), toJson(request), String.class),
					SyncValueObject.class);
			log.info("Library Server took " + (System.currentTimeMillis() - startTime) + " ms to merge library.");
		} catch (Exception e) {
			log.error("Unexpected error merging library.", e);
		} finally {
			messEngine.send(new AllMessage<SyncValueObject>(SYNC_LIBRARY_MERGE_RESPONSE, response));
			syncConverter.removeMergeRequest(request);
		}
	}

	@MessageMethod(MessEngineConstants.FEEDS_REQUEST)
	public void getFeeds(Long userId) {
		String jsonList = defaultTemplate.getForObject(getUrl("feeds.get"), String.class, userId);
		FeedsResponse feedsResponse = JsonConverter.toBean(jsonList, FeedsResponse.class);
		messEngine.send(new AllMessage<FeedsResponse>(MessEngineConstants.FEEDS_RESPONSE, feedsResponse));
	}

	@MessageMethod(MessEngineConstants.FEEDS_LOCAL_REQUEST)
	public FeedsResponse gteLocalFeeds(Long userId) {
		String jsonList = defaultTemplate.getForObject(getUrl("feeds.get"), String.class, userId);
		FeedsResponse feedsResponse = JsonConverter.toBean(jsonList, FeedsResponse.class);
		messEngine.send(new AllMessage<FeedsResponse>(MessEngineConstants.FEEDS_LOCAL_RESPONSE, feedsResponse));
		return feedsResponse;
	}

	@MessageMethod(MessEngineConstants.LAST_FEED_REQUEST)
	public void getLastFeeds(HashMap<String, Long> args) {
		Long userId = args.get("userId");
		String jsonList = defaultTemplate.getForObject(getUrl("feeds.timestamp.get"), String.class, userId,
				args.get("timestamp"));
		FeedsResponse feedsResponse = JsonConverter.toBean(jsonList, FeedsResponse.class);
		messEngine.send(new AllMessage<FeedsResponse>(MessEngineConstants.FEEDS_RESPONSE, feedsResponse));
	}

	@MessageMethod(MessEngineConstants.OLD_FEEDS_REQUEST)
	public FeedsResponse getOldFeeds(FeedsRequest body) {
		String url = getUrl("feeds.old.get");
		String jsonList = defaultTemplate.getForObject(url, String.class, body.getUserId(), body.getLastId());
		FeedsResponse feedsResponse = JsonConverter.toBean(jsonList, FeedsResponse.class);
		messEngine.send(new AllMessage<FeedsResponse>(MessEngineConstants.OLD_FEEDS_RESPONSE, feedsResponse));
		return feedsResponse;
	}

	@MessageMethod(MessEngineConstants.USAGE_STATS_TYPE)
	public void sendStats(AllMessage<List<AllStat>> message) {
		try {
			defaultTemplate.put(getUrl("stats.put"), JsonConverter.toJson(message.getBody()));
			messEngine.send(new ResponseMessage("stats-sent", message));
		} catch (Exception e) {
			messEngine.send(new ErrorMessage(message));
		}
	}

	@MessageMethod(ALERTS_REQUEST_TYPE)
	public List<Alert> getUserAlerts(AllMessage<String> message) {
		String userId = message.getBody();
		log.info("Processing alerts request from " + userId);

		String jsonList = defaultTemplate.getForObject(getUrl("retrieveAlerts"), String.class, userId);
		@SuppressWarnings("unchecked")
		List<Alert> currentAlerts = JsonConverter.toTypedCollection(jsonList, ArrayList.class, Alert.class);
		Message<List<Alert>> responseMessage = new AllMessage<List<Alert>>(ALERTS_RESPONSE_TYPE, currentAlerts);
		responseMessage.putProperty(NetworkingConstants.NETWORKING_SESSION_ID,
				message.getProperty(NetworkingConstants.NETWORKING_SESSION_ID));
		messEngine.send(responseMessage);
		return currentAlerts;
	}

	@MessageMethod(DELETE_ALERT_TYPE)
	public void deleteAlert(Alert alert) {
		defaultTemplate.delete(getUrl("deleteAlert"), alert.getId());
	}

	@MessageMethod(PUT_ALERT_TYPE)
	public void putAlert(Alert alert) {
		String body = JsonConverter.toJson(alert);
		defaultTemplate.put(getUrl("saveAlert"), body, alert.getId());
	}
	
	@MessageMethod(FACEBOOK_RECOMMENDATION)
	public void putAlert(ModelCollection model) {
		String body = JsonConverter.toJson(model);
		Long recommendationId = defaultTemplate.getForObject(getMobileUrl(), Long.class, body);
		log.info("Recommendatio id request : " + recommendationId);
		messEngine.send(new AllMessage<Long>(MessEngineConstants.FACEBOOK_RECOMMENDATION_ID, recommendationId));
	}

	@MessageMethod(MessEngineConstants.FRIENDSHIP_RESPONSE_TYPE)
	public void processFriendshipResponseMessage(ContactRequest request) {
		log.info("Processing a friendship response for request " + request.getId());
		String response = request.isAccepted() ? Boolean.toString(true) : Boolean.toString(false);
		String url = getUrl("contacts.responseUrl");
		defaultTemplate.put(url, response, request.getId());
	}

	@MessageMethod(MessEngineConstants.FRIENDSHIP_REQUEST_TYPE)
	public void processFriendshipRequestMessage(AllMessage<ContactRequest> message) {
		ContactRequest request = message.getBody();
		log.info("Processing a friendship request from " + request.getRequester() + " to " + request.getRequested());
		String url = getUrl("contacts.requestUrl");
		Long reqyesterId = request.getRequester().getId();
		Long requestedId = request.getRequested().getId();
		String result = defaultTemplate.getForObject(url, String.class, reqyesterId, requestedId);
		ContactRequestResult reqRes = new ContactRequestResult(request.getRequested(),
				FriendshipRequestStatus.valueOf(result));
		AllMessage<ContactRequestResult> resultMessage = new AllMessage<ContactRequestResult>(
				FRIENDSHIP_REQUEST_RESULT_TYPE, reqRes);

		messEngine.send(resultMessage);
	}

	@SuppressWarnings("unchecked")
	@MessageMethod(MessEngineConstants.SEARCH_CONTACTS_REQUEST_TYPE)
	public void processSearchRequestMessage(AllMessage<String> message) {
		log.info("Processing a search request message.");
		String json = defaultTemplate.getForObject(getUrl("searchContactsUrl"), String.class, message.getBody());
		sendSearchServiceResponse(JsonConverter.toTypedCollection(json, ArrayList.class, ContactInfo.class));
	}

	@MessageMethod(MessEngineConstants.IMPORT_CONTACTS_REQUEST_TYPE)
	public void processImportContactsRequest(AllMessage<CrawlerRequest> message) {
		log.info("Prcessing a crawler request...");
		String body = JsonConverter.toJson(message.getBody());
		String response = defaultTemplate.postForObject(getUrl("crawlerRequestUrl"), body, String.class);
		CrawlerResponse responseBody = JsonConverter.toBean(response, CrawlerResponse.class);
		AllMessage<CrawlerResponse> responseMessage = new AllMessage<CrawlerResponse>(
				MessEngineConstants.IMPORT_CONTACTS_RESPONSE_TYPE, responseBody);
		messEngine.send(responseMessage);
	}

	@SuppressWarnings("unchecked")
	@MessageMethod(MessEngineConstants.DEFAULT_CONTACTS_REQUEST_TYPE)
	public void postDefaultContacts(String email) {
		log.info("Getting default contacts list...");
		String json = defaultTemplate.getForObject(getUrl("defaultContactsUrl"), String.class);
		sendDefaultContactsResponse(JsonConverter.toTypedCollection(json, ArrayList.class, ContactInfo.class));
	}

	private void sendDefaultContactsResponse(List<ContactInfo> defaultContacts) {
		log.info("DEFAULT_CONTACTS " + defaultContacts);
		AllMessage<List<ContactInfo>> responseMessage = new AllMessage<List<ContactInfo>>(
				MessEngineConstants.DEFAULT_CONTACTS_RESPONSE_TYPE, new ArrayList<ContactInfo>(defaultContacts));
		messEngine.send(responseMessage);
	}

	private void sendSearchServiceResponse(List<ContactInfo> result) {
		log.info("Search Service Contacts : " + result);
		AllMessage<List<ContactInfo>> responseMessage = new AllMessage<List<ContactInfo>>(
				MessEngineConstants.SEARCH_CONTACTS_RESPONSE_TYPE, result);
		messEngine.send(responseMessage);
	}

	@MessageMethod(MessEngineConstants.CONTACT_LIST_REQUEST_TYPE)
	public void processContactListRequest(AllMessage<String> message) {
		log.info("Processing a contact list request from " + message.getBody());
		String json = defaultTemplate.getForObject(getUrl("contactListUrl"), String.class, message.getBody());
		@SuppressWarnings("unchecked")
		List<ContactInfo> contactList = JsonConverter.toTypedCollection(json, ArrayList.class, ContactInfo.class);
		AllMessage<List<ContactInfo>> responseMessage = new AllMessage<List<ContactInfo>>(
				MessEngineConstants.CONTACT_LIST_RESPONSE_TYPE, contactList);
		messEngine.send(responseMessage);
	}

	@MessageMethod(MessEngineConstants.UPDATE_USER_QUOTE_TYPE)
	public void updateUserQoute(final AllMessage<String> message) {
		log.info("Processing a quote update request from " + message.getProperty(SENDER_ID));
		// user/quote/{userId}
		String[] urlVars = urlVars(message.getProperty(SENDER_ID));
		String[][] headers = headers(header(MessEngineConstants.PUSH_TO, message.getProperty(MessEngineConstants.PUSH_TO)));
		send("user.updateQuoteUrl", HttpMethod.PUT, message.getBody(), urlVars, headers);
	}

	@MessageMethod(MessEngineConstants.UPDATE_USER_AVATAR_TYPE)
	public void updateAvatar(AllMessage<Avatar> message) {
		log.info("Processing an avatar update request");
		// user/avatar
		String[][] headers = headers(header(MessEngineConstants.PUSH_TO, message.getProperty(MessEngineConstants.PUSH_TO)));
		send("user.updateAvatarUrl", HttpMethod.PUT, message.getBody(), null, headers);
	}

	@MessageMethod(MessEngineConstants.UPDATE_USER_PROFILE_TYPE)
	public void updateUserProfile(AllMessage<User> message) {
		log.info("Processing an update profile request.");
		// user/profile/
		String[] urlVars = urlVars(message.getProperty(SENDER_ID));
		String[][] headers = headers(header(MessEngineConstants.PUSH_TO, message.getProperty(MessEngineConstants.PUSH_TO)));
		send("user.updateProfileUrl", HttpMethod.PUT, message.getBody(), urlVars, headers);
	}

	@MessageMethod(MessEngineConstants.AVATAR_REQUEST_TYPE)
	public void requestAvatar(AllMessage<String> message) {
		log.info("Processing an avatar request for " + message.getProperty(AVATAR_OWNER));
		// user/avatar/{userId}
		String avatarStr = defaultTemplate.getForObject(getUrl("user.getAvatarUrl"), String.class, message.getBody());
		Avatar avatar = JsonConverter.toBean(avatarStr, Avatar.class);

		AllMessage<Avatar> responseMessage = new AllMessage<Avatar>(AVATAR_RESPONSE_TYPE, avatar);
		responseMessage.putProperty(AVATAR_OWNER, message.getProperty(AVATAR_OWNER));
		log.debug("RESPONDING AVATAR REQUEST FOR " + message.getProperty(AVATAR_OWNER));
		messEngine.send(responseMessage);
	}

	@MessageMethod(MessEngineConstants.DELETE_PENDING_EMAILS_TYPE)
	public void deletePendingEmails(String params) {
		log.info("Processing a delete pending email request.");
		defaultTemplate.delete(getUrl("deletePendingEmailsUrl"), params);
	}

	@MessageMethod(MessEngineConstants.UPDATE_CONTACT_PROFILE_REQUEST)
	public void updateContactProfile(AllMessage<ContactInfo> message) {
		log.info("Processing a " + message.getType() + " for Contact with id " + message.getBody().getId());
		String body = JsonConverter.toJson(message.getBody());
		String contactInfoJson = defaultTemplate.postForObject(getUrl("updateContactProfileUrl"), body, String.class);
		ContactInfo contactInfo = JsonConverter.toBean(contactInfoJson, ContactInfo.class);
		AllMessage<ContactInfo> responseMessage = new AllMessage<ContactInfo>(
				MessEngineConstants.UPDATE_CONTACT_PROFILE_RESPONSE, contactInfo);
		messEngine.send(responseMessage);
	}

	@MessageMethod(MessEngineConstants.DELETE_CONTACTS_TYPE)
	public void processDeleteContactsRequest(AllMessage<String> message) {
		log.info("Processing a delete contact request from " + message.getProperty(MessEngineConstants.SENDER_ID));
		defaultTemplate.delete(getUrl("deleteContactsUrl"),  message.getProperty(MessEngineConstants.SENDER_ID), message.getBody());
	}
	
	@MessageMethod(MessEngineConstants.SEND_EMAIL_TYPE)
	public void createPendingEmail(AllMessage<PendingEmail> message) {
		String jsonResponse = defaultTemplate.postForObject(getUrl("emailUrl"), JsonConverter.toJson(message.getBody()),
				String.class);
		PendingEmail pendingEmail = JsonConverter.toBean(jsonResponse, PendingEmail.class);
		if (pendingEmail.getId() != null) {
			AllMessage<PendingEmail> response = new AllMessage<PendingEmail>(MessEngineConstants.PUSH_PENDING_EMAIL_TYPE,
					pendingEmail);
			messEngine.send(response);
		}
	}
	
	private String getUrl(String urlKey) {
		return clientSettings.getProperty("all.server.url") + clientSettings.getProperty(urlKey);
	}
	
	private String getMobileUrl(){
		return clientSettings.getProperty("all.server.url") + clientSettings.getProperty("widgetRecommendation");
	}
	

	private void initializeSyncTemplate() {
		List<HttpMessageConverter<?>> messageConverters = syncTemplate.getMessageConverters();
		if (messageConverters == null) {
			messageConverters = new ArrayList<HttpMessageConverter<?>>();
		}
		Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
		while (iterator.hasNext()) {
			HttpMessageConverter<?> converter = iterator.next();
			if (converter instanceof StringHttpMessageConverter) {
				iterator.remove();
				break;
			}
		}
		messageConverters.add(syncConverter);
		syncTemplate.setMessageConverters(messageConverters);
	}

	private String send(String urlKey, HttpMethod method, final Object body, final Object[] urlVariables,
			final String[][] headers) {
		return defaultTemplate.execute(getUrl(urlKey), method, new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				if (headers != null) {
					for (String[] header : headers) {
						request.getHeaders().add(header[0], header[1]);
					}
				}
				String bodyStr = null;
				if (body != null) {
					if (body instanceof String) {
						bodyStr = body.toString();
					} else {
						bodyStr = JsonConverter.toJson(body);
					}

				}
				if (bodyStr != null) {
					PrintWriter out = new PrintWriter(request.getBody());
					out.print(bodyStr);
					out.close();
				}
			}
		}, new ResponseExtractor<String>() {
			@Override
			public String extractData(ClientHttpResponse response) throws IOException {
				return null;
			}
		}, urlVariables);
	}

	private String[] urlVars(String... vars) {
		return vars;
	}

	private String[][] headers(String[]... headers) {
		return headers;
	}

	private String[] header(String key, String value) {
		return new String[] { key, value };
	}

}
