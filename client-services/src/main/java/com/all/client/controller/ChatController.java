package com.all.client.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatCredentials;
import com.all.chat.ChatService;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.Message;
import com.all.chat.exceptions.ChatException;
import com.all.client.model.ChatConstants;
import com.all.client.services.ContactCacheService;
import com.all.client.services.UserPreferenceService;
import com.all.core.actions.Actions;
import com.all.core.actions.ContactMessage;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

@Controller
public class ChatController {

	private final Log log = LogFactory.getLog(ChatController.class);
	private final Map<ChatType, ContactInfo> loggedUsers = new HashMap<ChatType, ContactInfo>();
	private final ChatServiceListener chatServiceListener = new ChatServiceListenerImpl();
	@Autowired
	private ContactCacheService contactCacheService;
	@Autowired
	@Qualifier("chatServiceManager")
	private ChatService chatService;
	@Autowired
	private UserPreferenceService userPreferenceService;
	@Autowired
	private ControlEngine controlEngine;

	@PostConstruct
	public void initialize() {
		chatService.addListener(chatServiceListener);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void start(AllMessage<User> message) {
		User currentUser = message.getBody();
		login(new ChatCredentials(currentUser.getEmail(), currentUser.getPassword(), ChatType.ALL));
	}

	@PreDestroy
	public void shutdown() {
		chatService.removeListener(chatServiceListener);
	}
	
	@MessageMethod(MessEngineConstants.FACEBOOK_CHAT_LOGIN)
	public void loginIntoFacebookChat(AllMessage<ChatCredentials> message){
		login(message.getBody());
	}

	@RequestMethod(Actions.Chat.LOGIN_INTO_CHAT_ID)
	public Boolean login(ChatCredentials chatCredentials) {
		if (chatCredentials != null && loggedUsers.get(chatCredentials.getType()) == null) {
			try {
				ChatUser chatUser = chatService.login(chatCredentials);
				if (chatUser != null) {
					loggedUsers.put(chatUser.getChatType(), new ContactInfo(chatUser));
				}
			} catch (Exception e) {
				log.error("Could not login to chat " + chatCredentials.getType(), e);
				if(chatCredentials.getType() == ChatType.FACEBOOK) {
					userPreferenceService.setFacebookChatStatus(false);
				}
				return false;
			}
		}

		boolean isLoggedIn = loggedUsers.get(chatCredentials.getType()) != null;

		userPreferenceService.setFacebookChatStatus(isLoggedIn && chatCredentials.getType() == ChatType.FACEBOOK);
		
		contactCacheService.setSelectedChatType(chatCredentials.getType());

		return isLoggedIn;
	}

	@RequestMethod(Actions.Chat.REQUEST_SELECT_CHAT_TYPE_ID)
	public Boolean isSelectionChatType(ChatType chatType) {
		if (chatType.equals(ChatType.FACEBOOK)) {
			if (!controlEngine.get(Model.UserPreference.FACEBOOK_CHAT_STATUS)) {
				controlEngine.fireEvent(Events.Chat.DISPLAY_FACEBOOK_AUTHORIZATION_DIALOG);
				return Boolean.FALSE;
			}
		}
		contactCacheService.setSelectedChatType(chatType);
		return Boolean.TRUE;
	}

	@ActionMethod(Actions.Chat.LOGOUT_CHAT_TYPE_ID)
	public void logout(ChatType chatType) {
		if(chatType == ChatType.FACEBOOK) {
			userPreferenceService.setFacebookChatStatus(false);
		}
		ContactInfo contactInfo = loggedUsers.remove(chatType);
		if (contactInfo != null) {
			chatService.logout(contactInfo);
			contactCacheService.removeContactsByType(chatType);
			contactCacheService.setSelectedChatType(ChatType.ALL);
		}
	}

	@RequestMethod(Actions.Chat.SEND_MESSAGE_TO_CONTACT_ID)
	public ChatMessage sendTo(ContactMessage contactMessage) {
		String message = contactMessage.getMessage(); 
		ContactInfo contact = contactMessage.getContact();
		
		if (contact.getChatType() == ChatType.ALL && !message.equals(ChatConstants.RECEIVE_CONTENT_TAG)) {
			message = message.replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;");
		}
		try {
			return new ChatMessage(chatService.sendMessage(message, contact));
		} catch (ChatException e) {
			log.error("Could not send chat message.", e);
		}
		return null;
	}

	@RequestMethod(Actions.Chat.SENT_CONTENT_ID)
	public ChatMessage sentContent(ContactInfo contact) {
		sendTo(new ContactMessage(contact, ChatConstants.RECEIVE_CONTENT_TAG));
		return new ChatMessage(loggedUsers.get(ChatType.ALL), contact, ChatConstants.SENT_CONTENT_TAG);
	}

	private final class ChatServiceListenerImpl implements ChatServiceListener {
		@Override
		public void onChatContactPresenceChanged(ChatUser chatUser) {
			contactCacheService.updateStatus(new ContactInfo(chatUser));
		}

		@Override
		public void onChatContactsRetrieved(Collection<ChatUser> chatUsers) {
			Set<ContactInfo> contacts = new HashSet<ContactInfo>();
			for (ChatUser chatUser : chatUsers) {
				contacts.add(new ContactInfo(chatUser));
			}
			contactCacheService.addContacts(contacts);
		}

		@Override
		public void onMessage(Message message) {
			ChatMessage chatMessage = new ChatMessage(message);
			contactCacheService.decorate(chatMessage.getSender());
			controlEngine.fireValueEvent(Events.Chat.INCOMING_MESSAGE, chatMessage);
		}

		@Override
		public void onChatContactAvatarChanged(ChatUser chatUser) {
			contactCacheService.updateAvatar(chatUser.getChatId(), chatUser.getAvatar(), chatUser.getChatType());
		}
	}

}
