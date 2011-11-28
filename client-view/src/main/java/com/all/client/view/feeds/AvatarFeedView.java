package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.newsfeed.AvatarFeed;

public class AvatarFeedView extends FeedView<AvatarFeed> {
	private static final long serialVersionUID = -8006202108445540754L;

	public AvatarFeedView(Messages messages, AvatarFeed feed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.AVATAR, FeedSize.SMALL, messages, viewEngine, dialogFactory);
		
		initialize(FeedSize.SMALL);
	}

	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner = feed.getOwner();
		String gender = Gender.FEMALE == owner.getGender() ? "feed.import.female" : "feed.import.male";
		
		clear();
		
		StringBuilder sb = new StringBuilder();
		sb.append(messages.getMessage("feed.changed"));
		sb.append(messages.getMessage(gender));
		sb.append(messages.getMessage("feed.avatar"));
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(sb.toString());
		super.internationalize(messages);
	}

}