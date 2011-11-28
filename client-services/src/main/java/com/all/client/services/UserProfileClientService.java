package com.all.client.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Avatar;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.stats.usage.UserActions;

@Service
public class UserProfileClientService {

	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private PortraitUtil portraitUtil;
	@Autowired
	private ClientReporter reporter;
	@Autowired
	private ContactCacheService contactCacheService;
	@Autowired
	private ControlEngine controlEngine;

	@PostConstruct
	public void init() {

		messEngine.addMessageListener(MessEngineConstants.CONTACT_PROFILE_PUSH_TYPE,
				new MessageListener<AllMessage<ContactInfo>>() {
					@Override
					public void onMessage(AllMessage<ContactInfo> message) {
						saveContactProfile(message.getBody());
					}

				});
		messEngine.addMessageListener(MessEngineConstants.AVATAR_RESPONSE_TYPE, new MessageListener<AllMessage<Avatar>>() {
			@Override
			public void onMessage(AllMessage<Avatar> message) {
				saveAvatar(message.getProperty(MessEngineConstants.AVATAR_OWNER), message.getBody());
			}
		});
		messEngine.addMessageListener(MessEngineConstants.CONTACT_AVATAR_PUSH_TYPE,
				new MessageListener<AllMessage<Avatar>>() {
					@Override
					public void onMessage(AllMessage<Avatar> message) {
						saveAvatar(message.getProperty(MessEngineConstants.AVATAR_OWNER), message.getBody());
					}
				});

		messEngine.addMessageListener(MessEngineConstants.CONTACT_QUOTE_PUSH_TYPE,
				new MessageListener<AllMessage<String>>() {
					@Override
					public void onMessage(AllMessage<String> message) {
						saveContactQuote(new Long(message.getProperty(MessEngineConstants.SENDER_ID)), message.getBody());
					}

				});
		messEngine.addMessageListener(MessEngineConstants.UPDATE_CONTACT_PROFILE_RESPONSE,
				new MessageListener<AllMessage<ContactInfo>>() {
					@Override
					public void onMessage(AllMessage<ContactInfo> message) {
						saveContactProfile(message.getBody());
					}
				});

	}

	public void requestContactInfo(ContactInfo contact) {
		log.debug("requesting contact info for " + contact);
		AllMessage<ContactInfo> message = new AllMessage<ContactInfo>(MessEngineConstants.UPDATE_CONTACT_PROFILE_REQUEST,
				contact);
		messEngine.send(message);
	}

	public void requestAvatar(ContactInfo contact) {
		Long id = contact.getId();
		if (id != null) {
			AllMessage<String> message = new AllMessage<String>(MessEngineConstants.AVATAR_REQUEST_TYPE, id.toString());
			message.putProperty(MessEngineConstants.AVATAR_OWNER, contact.getEmail());
			messEngine.send(message);
		} else {
			log.warn("Trying to retrieve the avatar for a contact with null id.\nContact " + contact + " has null id.");
		}
	}

	public void requestAvatarIfNeeded(ContactInfo contact) {
		if (!portraitUtil.hasAvatar(contact.getEmail())) {
			requestAvatar(contact);
		}
	}

	public void requestAvatarsIfNeeded(List<ContactInfo> contacts) {
		for (ContactInfo contactInfo : contacts) {
			requestAvatarIfNeeded(contactInfo);
		}
	}

	public void updateProfile(User user) {
		AllMessage<User> message = new AllMessage<User>(MessEngineConstants.UPDATE_USER_PROFILE_TYPE, user);
		message.putProperty(MessEngineConstants.PUSH_TO, getOnlineContacts());
		messEngine.send(message);
		reporter.logUserAction(UserActions.UserData.UPDATE_PROFILE);
	}

	public void updateAvatar(Avatar avatar) {
		AllMessage<Avatar> message = new AllMessage<Avatar>(MessEngineConstants.UPDATE_USER_AVATAR_TYPE, avatar);
		message.putProperty(MessEngineConstants.AVATAR_OWNER, controlEngine.get(Model.CURRENT_USER).getEmail());
		message.putProperty(MessEngineConstants.PUSH_TO, getOnlineContacts());
		messEngine.send(message);
		reporter.logUserAction(UserActions.UserData.UPDATE_AVATAR);
	}

	public void updateQuote(String quote) {
		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.UPDATE_USER_QUOTE_TYPE, quote);
		message.putProperty(MessEngineConstants.SENDER_ID, controlEngine.get(Model.CURRENT_USER).getId().toString());
		message.putProperty(MessEngineConstants.PUSH_TO, getOnlineContacts());
		messEngine.send(message);
		reporter.logUserAction(UserActions.UserData.UPDATE_QUOTE);
	}

	private String getOnlineContacts() {
		StringBuilder sb = new StringBuilder();
		for (ContactInfo contact : contactCacheService.getContactsByChatStatus(ChatType.ALL, ChatStatus.ONLINE)) {
			sb.append(contact.getEmail());
			sb.append(",");
		}
		String result = sb.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	private void saveAvatar(String userEmail, Avatar avatar) {
		if (avatar.getAvatarData() != null && avatar.getAvatarData().length > 0) {
			contactCacheService.updateAvatar(userEmail, avatar.getAvatarData(), ChatType.ALL);
		}
	}

	private void saveContactQuote(Long contactId, String quote) {
		contactCacheService.updateQuote(contactId, quote);
	}

	private void saveContactProfile(ContactInfo contact) {
		contactCacheService.updateProfileInfo(contact);
	}

}
