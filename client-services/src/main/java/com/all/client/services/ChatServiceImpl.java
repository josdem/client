package com.all.client.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatCredentials;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.ManagedChatService;
import com.all.chat.Message;
import com.all.chat.exceptions.ChatException;
import com.all.chat.impl.CommonChatService;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.PushMessage;

@Service
public class ChatServiceImpl extends CommonChatService implements ManagedChatService {

	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ControlEngine controlEngine;

	private ContactInfo sender;

	@MessageMethod(MessEngineConstants.CHAT_MESSAGE_RESPONSE)
	public void onChatMessageReceived(ChatMessage chatMessage) {
		notifyMessage(chatMessage);
	}

	@Override
	public ChatUser login(ChatCredentials chatCredentials) throws ChatException {
		if (sender == null) {
			sender = new ContactInfo(controlEngine.get(Model.CURRENT_USER));
		}
		return sender;
	}

	@Override
	public void logout(ChatUser loggedContact) {
		sender = null;
	}

	@Override
	public Message sendMessage(String message, ChatUser to) throws ChatException {
		ChatMessage body = new ChatMessage(sender, (ContactInfo) to, message);
		AllMessage<ChatMessage> chatMessage = new AllMessage<ChatMessage>(MessEngineConstants.CHAT_MESSAGE_RESPONSE, body);
		messEngine.send(new PushMessage<ChatMessage>(chatMessage, Arrays.asList(to.getChatId())));
		return body;
	}

	@Override
	public ChatType getChatType() {
		return ChatType.ALL;
	}

}
