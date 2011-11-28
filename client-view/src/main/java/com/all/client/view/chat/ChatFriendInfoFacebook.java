package com.all.client.view.chat;

import java.awt.Rectangle;

import com.all.chat.ChatStatus;
import com.all.core.common.util.ImageUtil;
import com.all.shared.model.ContactInfo;

public class ChatFriendInfoFacebook extends ChatFriendInfoPanel {
	private static final long serialVersionUID = 5577463656950962081L;
	private static final String USER_ICON_FACEBOOK_LABEL_NAME = "userIconFacebookLabel";
	private static final String USER_ICON_AWAY_FACEBOOK_LABEL_NAME = "userIconFacebookAwayLabel";
	private static final String USER_ICON_OFFLINE_FACEBOOK_LABEL_NAME = "userIconFacebookOfflineLabel";
	private static final Rectangle USER_ICON_FACEBOOK_BOUNDS = new Rectangle(82, 12, 16, 16);
	private static final double ARC_IMAGE = .17;

	public ChatFriendInfoFacebook(ContactInfo contact) {
		super();
		initialize(contact);
	}

	private void initialize(ContactInfo contact) {
		getContactNameLabel().setText(contact.getChatName());
		getShowRemoteLibraryButton().setVisible(false);
		getUserIconLabel().setName(
				contact.isOnline() ? USER_ICON_FACEBOOK_LABEL_NAME : USER_ICON_AWAY_FACEBOOK_LABEL_NAME);
		getUserIconLabel().setBounds(USER_ICON_FACEBOOK_BOUNDS);
		getImagePanel().setImage(ImageUtil.getImage(contact.getAvatar()),  ARC_IMAGE, ARC_IMAGE);
		getContactQuoteLabel().setText(contact.getChatStatus().equals(ChatStatus.ONLINE) ? "Avaliable" : "Away");
	}

	@Override
	public void notifyPresence(ChatStatus status) {
		if (status == ChatStatus.AWAY) {
			getUserIconLabel().setName(USER_ICON_AWAY_FACEBOOK_LABEL_NAME);
		}
		if (status == ChatStatus.ONLINE) {
			getUserIconLabel().setName(USER_ICON_FACEBOOK_LABEL_NAME);
		}
		if (status == ChatStatus.OFFLINE) {
			getUserIconLabel().setName(USER_ICON_OFFLINE_FACEBOOK_LABEL_NAME);
		}

	}
}
