package com.all.core.common.services;

import static com.all.shared.messages.MessEngineConstants.CONTACT_LIST_STATUS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_LIST_STATUS_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_STATUS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_STATUS_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.DEFAULT_CONTACTS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.DEFAULT_CONTACTS_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.ONLINE_USERS_LIST_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.PUT_ALERT_TYPE;
import static com.all.shared.messages.MessEngineConstants.REST_UPDATE_SEEDER_TRACKS;
import static com.all.shared.messages.MessEngineConstants.REST_UPLOAD_TRACK_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.SEEDER_TRACK_LIST_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.TRACK_SEEDERS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.ULTRAPEER_SESSION_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.USER_STATUS_ADV_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.core.common.messages.ErrorMessage;
import com.all.core.common.model.ApplicationModel;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.networking.PeerNetworkingService;
import com.all.shared.alert.Alert;
import com.all.shared.download.RestUploadRequest;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.PresenceInfo;
import com.all.shared.model.PushMessage;
import com.all.shared.model.UltrapeerNode;
import com.all.shared.model.User;

@Service
public class UltrapeerProxy implements MessageListener<AllMessage<?>> {
	private static final Log log = LogFactory.getLog(UltrapeerProxy.class);

	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private PeerNetworkingService peerNetworkingService;
	@Autowired
	private UltrapeerSource ultrapeerSource;

	private User currentUser;

	private Map<String, ContactInfo> defaultContacts = Collections.unmodifiableMap(new HashMap<String, ContactInfo>());

	private static final String[] LOGIN_REQUESTS = { ULTRAPEER_SESSION_REQUEST_TYPE };

	private static final String[] PEER_REQUESTS = { USER_STATUS_ADV_TYPE, CONTACT_STATUS_REQUEST_TYPE,
			ONLINE_USERS_LIST_REQUEST_TYPE, TRACK_SEEDERS_REQUEST_TYPE, REST_UPLOAD_TRACK_REQUEST_TYPE,
			REST_UPDATE_SEEDER_TRACKS, SEEDER_TRACK_LIST_REQUEST_TYPE, CONTACT_LIST_STATUS_REQUEST_TYPE, PushMessage.TYPE };

	public static final int MAX_ATTEMPTS = 3;

	@PostConstruct
	public void initialize() {
		registerListeners();
		addInternetConnectionListsener();
	}

	public void registerListeners() {
		registerLoginListeners();
		registerClientListeners();
	}

	private void registerLoginListeners() {
		for (String messageType : LOGIN_REQUESTS) {
			messEngine.addMessageListener(messageType, this);
		}
	}

	private void registerClientListeners() {
		for (String messageType : PEER_REQUESTS) {
			messEngine.addMessageListener(messageType, this);
		}
	}

	private void addInternetConnectionListsener() {
		messEngine.addMessageListener(MessEngineConstants.INTERNET_CONNECTION_STATUS_TYPE,
				new MessageListener<AllMessage<Boolean>>() {
					@Override
					public void onMessage(AllMessage<Boolean> message) {
						if (message.getBody()) {
							peerNetworkingService.resetSessions();
						}
					}
				});
	}

	@Override
	public void onMessage(AllMessage<?> message) {
		if (controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
			if (isValid(message)) {
				sendMessage(message);
			}
		} else {
			log.warn("Trying to send a " + message.getType()
					+ " without internet connection. This message will be dismissed.");
			messEngine.send(new ErrorMessage(message));
		}
	}

	private boolean isValid(AllMessage<?> message) {
		if (CONTACT_LIST_STATUS_REQUEST_TYPE.equals(message.getType())) {
			@SuppressWarnings("unchecked")
			List<String> contactList = (List<String>) message.getBody();
			Iterator<String> contactListIterator = contactList.iterator();
			while (contactListIterator.hasNext()) {
				String contactId = contactListIterator.next();
				if (defaultContacts.containsKey(contactId)) {
					contactListIterator.remove();
					PresenceInfo presenceInfo = new PresenceInfo();
					presenceInfo.setEmail(contactId);
					presenceInfo.setOnline(true);
					messEngine.send(new AllMessage<PresenceInfo>(CONTACT_STATUS_RESPONSE_TYPE, presenceInfo));
				}
			}
			if (contactList.isEmpty()) {
				messEngine.send(new AllMessage<List<PresenceInfo>>(CONTACT_LIST_STATUS_RESPONSE_TYPE,
						new ArrayList<PresenceInfo>()));
				return false;
			}
		}

		if (CONTACT_STATUS_REQUEST_TYPE.equals(message.getType())) {
			String contactEmail = (String) message.getBody();
			if (defaultContacts.containsKey(contactEmail)) {
				PresenceInfo presenceInfo = new PresenceInfo();
				presenceInfo.setEmail(contactEmail);
				presenceInfo.setOnline(true);
				messEngine.send(new AllMessage<PresenceInfo>(CONTACT_STATUS_RESPONSE_TYPE, presenceInfo));
				log.info("Ignoring status request for Default contact");
				return false;
			}
		}

		if (PUT_ALERT_TYPE.equals(message.getType())) {
			Alert alert = (Alert) message.getBody();
			if (defaultContacts.containsValue(alert.getReceiver())) {
				log.info("Ignoring alert for Default contact");
				return false;
			}
		}
		if (REST_UPLOAD_TRACK_REQUEST_TYPE.equals(message.getType())) {
			RestUploadRequest request = (RestUploadRequest) message.getBody();
			if (currentUser == null || !currentUser.getEmail().equals(request.getRequester())) {
				return false;
			}
		}
		if (PushMessage.TYPE.equals(message.getType())) {
			List<String> recipients = ((PushMessage<?>) message).getRecipients();
			recipients.removeAll(defaultContacts.keySet());
			if (recipients.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void initSessionForUser(User currentUser) {
		this.currentUser = currentUser;
		loadDefaultContacts();
	}

	private void loadDefaultContacts() {
		MessageListener<AllMessage<List<ContactInfo>>> responseListener = new MessageListener<AllMessage<List<ContactInfo>>>() {
			@Override
			public void onMessage(AllMessage<List<ContactInfo>> message) {
				Map<String, ContactInfo> temp = new HashMap<String, ContactInfo>();
				for (ContactInfo contact : message.getBody()) {
					temp.put(contact.getEmail(), contact);
				}
				defaultContacts = Collections.unmodifiableMap(temp);
				log.info("DEFAULT CONTACTS  " + defaultContacts);
				messEngine.removeMessageListener(DEFAULT_CONTACTS_RESPONSE_TYPE, this);
			}
		};
		messEngine.addMessageListener(DEFAULT_CONTACTS_RESPONSE_TYPE, responseListener);
		if (controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
			messEngine.send(new AllMessage<String>(DEFAULT_CONTACTS_REQUEST_TYPE, currentUser.getEmail()));
		} else {
			log.info("Offline mode, unable to load default contacts");
		}
	}

	private synchronized void sendMessage(AllMessage<?> message) {
		String sender = currentUser == null ? null : currentUser.getEmail();
		boolean success = false;
		int attempt = 0;
		while (!success) {
			if (attempt < MAX_ATTEMPTS) {
				UltrapeerNode ultrapeerNode = ultrapeerSource.get();
				if (peerNetworkingService.hasDefaultSession()) {
					success = peerNetworkingService.send(sender, message);
					if (!success) {
						peerNetworkingService.setDefaultDestination(null, 0);
					}
				} else {
					success = peerNetworkingService.send(sender, message, ultrapeerNode.getAddress(), ultrapeerNode.getPort());
					if (success) {
						peerNetworkingService.setDefaultDestination(ultrapeerNode.getAddress(), ultrapeerNode.getPort());
					}
				}
				if (success) {
					ultrapeerSource.reportSuccess();
				} else {
					ultrapeerSource.reportFail();
				}
				attempt++;
			} else {
				messEngine.send(new ErrorMessage(message));
				log.fatal("Could not send " + message.getType() + " after " + MAX_ATTEMPTS
						+ " attempts. This message will be lost.");
				break;
			}
		}
	}

	@PreDestroy
	public void shutdown() {
		log.info("Shutting down offline Networking interceptor.");
		peerNetworkingService.shutdown();
	}
}
