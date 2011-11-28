package com.all.client.view.chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public class ChatFacebookFrame extends ChatFrame {
	private static final long serialVersionUID = 8064260785259650373L;
	private ChatFriendInfoFacebook chatFriendInfoFacebook;
	private final Messages messages;
	private ContactInfo contact;
	
	public ChatFacebookFrame(ContactInfo contact, Messages messages, ViewEngine viewEngine) {
		super(contact, messages, viewEngine);
		this.contact = contact;
		this.messages = messages;
		
		final JTextArea sendArea = getChatMainPanel().getTextArea();
		JButton emotIconButton = getChatMainPanel().getEmotIconButton();
		emotIconButton.removeActionListener(getChatMainPanel().emoticonAction);
		emotIconButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EmoticonsFacebookDialog(getChatMainPanel().parent, sendArea);
			}
		});
		
		getChatMainPanel().add(getFriendInfoPanel(), getFriendsInfoPanelConstraints());
	}

	ChatFriendInfoPanel getFriendInfoPanel() {
		if (chatFriendInfoFacebook == null) {
			chatFriendInfoFacebook = new ChatFriendInfoFacebook(contact);
			chatFriendInfoFacebook.internationalize(messages);
		}
		return chatFriendInfoFacebook;
	}

	public void setUp() {
		getChatMainPanel().setup(contact);
	}
	
		@Override
	public void notifyContactUpdated(ContactInfo contact) {
		getFriendInfoPanel().notifyPresence(contact.getChatStatus());

	}

}
