package com.all.client.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.core.common.ClientConstants;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;

@Service
public class ContactMessageService {

	@Autowired
	private MessEngine messEngine;

	private List<ContactInfo> contactInfoList = null;
	private List<ContactInfo> onlineUsers = null;
	private CyclicBarrier requestLockForContactlistRequest = new CyclicBarrier(2);
	private CyclicBarrier onlienUsersRequestLock = new CyclicBarrier(2);
	private Log log = LogFactory.getLog(this.getClass());

	@PostConstruct
	public void init() {
		messEngine.addMessageListener(MessEngineConstants.CONTACT_LIST_RESPONSE_TYPE,
				new MessageListener<AllMessage<ArrayList<ContactInfo>>>() {
					@Override
					public void onMessage(AllMessage<ArrayList<ContactInfo>> message) {
						processContactListResponse(message);
					}
				});

		messEngine.addMessageListener(MessEngineConstants.ONLINE_USERS_LIST_RESPONSE_TYPE,
				new MessageListener<AllMessage<List<ContactInfo>>>() {
					@Override
					public void onMessage(AllMessage<List<ContactInfo>> message) {
						processOnlineUsersResponse(message);
					}
				});
	}

	private void processContactListResponse(AllMessage<ArrayList<ContactInfo>> message) {
		contactInfoList = message.getBody();
		try {
			requestLockForContactlistRequest.await();
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void processOnlineUsersResponse(AllMessage<List<ContactInfo>> message) {
		onlineUsers = message.getBody();
		try {
			onlienUsersRequestLock.await();
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	public void deleteContacts(Long userId, Set<ContactInfo> contacts) {
		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.DELETE_CONTACTS_TYPE, getIdsAsString(contacts));
		message.putProperty(MessEngineConstants.SENDER_ID, userId.toString());
		messEngine.send(message);
	}

	public void deletePendingEmails(Set<ContactInfo> pendingEmails) {
		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.DELETE_PENDING_EMAILS_TYPE, getIdsAsString(pendingEmails));
		messEngine.send(message);
	}

	public List<ContactInfo> getUserContacts(Long userId) {
		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.CONTACT_LIST_REQUEST_TYPE, userId.toString());
		sendAndWait(message, requestLockForContactlistRequest, ClientConstants.CONTACT_LIST_RETRIVAL_TIMEOUT);
		return contactInfoList;
	}

	public List<ContactInfo> getOnlineUsers() {
		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.ONLINE_USERS_LIST_REQUEST_TYPE, "");
		sendAndWait(message, onlienUsersRequestLock, ClientConstants.ONLINE_USERS_REQUEST_TIMEOUT);
		if (onlineUsers == null) {
			return Collections.emptyList();
		}
		return onlineUsers;
	}

	private void sendAndWait(AllMessage<?> message, CyclicBarrier lock, long timeout) {
		lock.reset();
		messEngine.send(message);
		try {
			lock.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	String getIdsAsString(Set<ContactInfo> contacts) {
		StringBuffer sb = new StringBuffer();
		for (ContactInfo contact : contacts) {
			sb.append(contact.getId());
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

}
