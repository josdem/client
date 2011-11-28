package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.newsfeed.UpdateProfileFeed;

public class ProfileFeedView extends FeedView<UpdateProfileFeed> {
	private static final long serialVersionUID = 2235074156356178479L;


	public ProfileFeedView(Messages messages, UpdateProfileFeed feed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.PROFILE, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
		initialize(FeedSize.SMALL);
	}
	
	
	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner = feed.getOwner();
		String gender = Gender.FEMALE == owner.getGender() ? "feed.import.female" : "feed.import.male";
		
		clear();
		
		StringBuilder sb = new StringBuilder();
		sb.append(messages.getMessage("feed.updated"));
		sb.append(messages.getMessage(gender));
		sb.append(messages.getMessage("feed.profile.update"));
		
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(sb.toString());
		super.internationalize(messages);
	}
	
}
