package com.all.client.view.chat;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatType;
import com.all.commons.SoundPlayer;
import com.all.core.actions.Actions;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.util.ImageUtil;
import com.all.core.events.Events;
import com.all.event.EventMethod;
import com.all.i18n.Messages;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.stats.usage.UserActions;

@Component
public class ChatViewManager {
	private static final Log log = LogFactory.getLog(ChatViewManager.class);
	private final Map<ContactInfo, ChatFrame> windows = new HashMap<ContactInfo, ChatFrame>();
	private final ChatFrameFactory chatFrameFactory = new ChatFrameFactory();
	@Autowired
	private Messages messages;
	@Autowired
	private ViewEngine viewEngine;

	@PreDestroy
	@EventMethod(Events.Application.STOPED_ID)
	public void shutdown() {
		for (ChatFrame chatFrame : windows.values()) {
			chatFrame.close();
		}
	}

	@EventMethod(Events.Social.CONTACT_UPDATED_ID)
	public void onContactUpdated(ContactInfo contact) {
		notifyContactUpdated(contact);
	}

	public void showChat(ContactInfo contact) {
		getChatFrame(contact);
	}

	private ChatFrame getChatFrame(ContactInfo contact) {
		ChatFrame chatFrame = windows.get(contact);
		if (chatFrame == null) {
			chatFrame = chatFrameFactory.createChatWindow(contact);
			windows.put(contact, chatFrame);
			chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			chatFrame.setMessages(messages);
			chatFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					ChatFrame win = (ChatFrame) e.getComponent();
					win.removeMessages(messages);
					win.removeWindowListener(this);
					windows.remove(win.getContact());
				}
			});
			chatFrame.setVisible(true);
			chatFrame.setState(Frame.ICONIFIED);
		}

		ChatFrame activeChat = null;
		for (ChatFrame currentChat : windows.values()) {
			if (currentChat.isActive()) {
				activeChat = currentChat;
				break;
			}
		}
		if (activeChat == null) {
			chatFrame.setState(Frame.NORMAL);
			chatFrame.requestFocus();
		}
		return chatFrame;
	}

	private void notifyContactUpdated(ContactInfo contactInfo) {
		if (windows.containsKey(contactInfo)) {
			log.debug("Notifiying status for " + contactInfo);
			ChatFrame chatFrame = windows.get(contactInfo);
			chatFrame.notifyContactUpdated(contactInfo);
		}
	}

	@EventMethod(Events.Chat.INCOMING_MESSAGE_ID)
	public void onMessage(ChatMessage chatMessage) {
		if (chatMessage.getSender().getAvatar() == null) {
			chatMessage.getSender().setAvatar(ImageUtil.getDefaultAvatar());
		}
		ChatFrame window = getChatFrame(chatMessage.getSender());
		SoundPlayer.Sound.CHAT_MESSAGE_RECEIVED.play();
		window.addMessage(chatMessage);
	}

	public void logout(ChatType chatType) {
		viewEngine.sendValueAction(Actions.Chat.LOGOUT_CHAT_TYPE, chatType);
		closeChatFramesFor(chatType);
	}

	private void closeChatFramesFor(ChatType chatType) {
		for (ChatFrame chatFrame : windows.values()) {
			if (chatFrame.getContact().getChatType() == chatType) {
				chatFrame.close();
			}
		}
	}

	class ChatFrameFactory {

		private ChatFrame createAllChatFrame(ContactInfo contact) {
			viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.AllNetwork.MC_CHAT);
			ChatAllFrame chatFrame = new ChatAllFrame(contact, messages, viewEngine);
			chatFrame.setUp(viewEngine);
			return chatFrame;
		}

		public ChatFrame createChatWindow(ContactInfo contact) {
			ChatFrame chatFrame = null;
			switch (contact.getChatType()) {
			case FACEBOOK:
				chatFrame = createFacebookChatFrame(contact);
				break;
			default:
				chatFrame = createAllChatFrame(contact);
			}
			return chatFrame;
		}

		private ChatFrame createFacebookChatFrame(ContactInfo contact) {
			ChatFacebookFrame chatFacebookFrame = new ChatFacebookFrame(contact, messages, viewEngine);
			chatFacebookFrame.setUp();
			return chatFacebookFrame;
		}
	}
}
