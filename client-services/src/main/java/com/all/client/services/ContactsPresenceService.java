package com.all.client.services;

import static com.all.shared.messages.MessEngineConstants.CONTACT_LIST_STATUS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_LIST_STATUS_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_STATUS_PUSH_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_STATUS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.CONTACT_STATUS_RESPONSE_TYPE;
import static com.all.shared.messages.MessEngineConstants.USER_STATUS_ADV_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.ClientConstants;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;
import com.all.shared.model.PresenceInfo;
import com.all.shared.model.User;

@Service
public class ContactsPresenceService {

	private final Log log = LogFactory.getLog(this.getClass());
	private final ScheduledExecutorService presenceExecutor = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("PresenceService"));
	private final AtomicBoolean forceAnnounce = new AtomicBoolean(false);
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ContactCacheService contactCacheService;
	@Autowired
	private ControlEngine controlEngine;

	@PostConstruct
	public void initialize() {
		messEngine.addMessageListener(CONTACT_STATUS_RESPONSE_TYPE, new MessageListener<AllMessage<PresenceInfo>>() {
			@Override
			public void onMessage(AllMessage<PresenceInfo> message) {
				updateContactStatus(message.getBody());
			}
		});

		messEngine.addMessageListener(CONTACT_STATUS_PUSH_TYPE, new MessageListener<AllMessage<PresenceInfo>>() {
			@Override
			public void onMessage(AllMessage<PresenceInfo> message) {
				updateContactStatus(message.getBody());
			}
		});

		messEngine.addMessageListener(CONTACT_LIST_STATUS_RESPONSE_TYPE,
				new MessageListener<AllMessage<List<PresenceInfo>>>() {
					@Override
					public void onMessage(AllMessage<List<PresenceInfo>> message) {
						updateContactListStatus(message.getBody());
						if (forceAnnounce.get()) {
							announce(ContactStatus.online);
							forceAnnounce.set(false);
						}
					}
				});
	}

	public void start() {
		requestContactListStatus();
		presenceExecutor.scheduleWithFixedDelay(new PresenceAnnouncer(), ClientConstants.PRESENCE_ANNOUNCEMENT_DELAY,
				ClientConstants.PRESENCE_ANNOUNCEMENT_DELAY, TimeUnit.MILLISECONDS);
		presenceExecutor.scheduleWithFixedDelay(new PresenceVerifier(), ClientConstants.PRESENCE_TIMEOUT,
				ClientConstants.PRESENCE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@MessageMethod(MessEngineConstants.INTERNET_CONNECTION_STATUS_TYPE)
	public void onInternetConnectionStatusChanged(AllMessage<Boolean> message) {
		if (message.getBody()) {
			restart();
		}
	}

	public void restart() {
		requestContactListStatus();
	}

	public void stop() {
		presenceExecutor.shutdown();
		announce(ContactStatus.offline);
	}

	@PreDestroy
	public void shutdown() {
		if (!presenceExecutor.isShutdown()) {
			presenceExecutor.shutdownNow();
		}
	}

	private void announce(ContactStatus status) {
		PresenceInfo presenceInfo = new PresenceInfo();
		User currentUser = controlEngine.get(Model.CURRENT_USER);
		if (currentUser != null) {
			presenceInfo.setEmail(currentUser.getEmail());
			presenceInfo.setOnline(status == ContactStatus.online);
			AllMessage<PresenceInfo> message = new AllMessage<PresenceInfo>(USER_STATUS_ADV_TYPE, presenceInfo);
			message.putProperty(MessEngineConstants.PUSH_TO, toStringList(getOnlineContacts()));
			messEngine.send(message);
		}
	}

	private List<ContactInfo> getOnlineContacts() {
		return contactCacheService.getContactsByChatStatus(ChatType.ALL, ChatStatus.ONLINE);
	}

	private String toStringList(List<ContactInfo> contacts) {
		StringBuilder sb = new StringBuilder();
		for (ContactInfo contact : contacts) {
			sb.append(contact.getEmail());
			sb.append(",");
		}
		String result = sb.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	private void verifyContactListStatus() {
		requestContactListStatus(getOnlineContacts());
	}

	private void requestContactListStatus() {
		List<String> contactList = new ArrayList<String>();
		for (ContactInfo contact : contactCacheService.getContactsByType(ChatType.ALL)) {
			contactList.add(contact.getEmail());
		}
		forceAnnounce.set(true);
		messEngine.send(new AllMessage<List<String>>(CONTACT_LIST_STATUS_REQUEST_TYPE, contactList));
	}

	private void requestContactListStatus(List<ContactInfo> contactList) {
		List<String> contacts = new ArrayList<String>(contactList.size());
		for (ContactInfo contactInfo : contactList) {
			contacts.add(contactInfo.getEmail());
		}
		messEngine.send(new AllMessage<List<String>>(CONTACT_LIST_STATUS_REQUEST_TYPE, contacts));
	}

	public void requestContactStatus(ContactInfo contact) {
		if (contact != null && !contact.isPending()) {
			messEngine.send(new AllMessage<String>(CONTACT_STATUS_REQUEST_TYPE, contact.getEmail()));
		}
	}

	private void updateContactStatus(PresenceInfo presenceInfo) {
		contactCacheService.updateStatus(presenceInfo);
	}

	private void updateContactListStatus(List<PresenceInfo> contactList) {
		for (PresenceInfo presenceInfo : contactList) {
			contactCacheService.updateStatus(presenceInfo);
		}
	}

	private final class PresenceAnnouncer implements Runnable {
		@Override
		public void run() {
			try {
				announce(ContactStatus.online);
			} catch (Exception e) {
				log.error("Unexpected exception announcing presence.", e);
			}
		}
	}

	private final class PresenceVerifier implements Runnable {
		@Override
		public void run() {
			try {
				verifyContactListStatus();
			} catch (Exception e) {
				log.error("Unexpected exception verifying contact list status.", e);
			}
		}
	}

}
