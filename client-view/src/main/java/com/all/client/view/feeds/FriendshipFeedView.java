package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.newsfeed.FriendshipFeed;

public class FriendshipFeedView extends FeedView<FriendshipFeed> {
	private static final long serialVersionUID = 2235074156356178479L;

	public FriendshipFeedView(Messages messages, FriendshipFeed feed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.FRIENDSHIP, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
		initialize(FeedSize.MEDIUM);
	}
	
	
	@Override
	public void internationalize(Messages messages) {
		ContactInfo requester = feed.getOwner();
		ContactInfo requested = feed.getRequested();
		
		StringBuilder messageInfo = new StringBuilder().append(" ");
		messageInfo.append(messages.getMessage("feed.friendship.and"));
		messageInfo.append(" ");

		clear();
		
		this.getHeaderPanel().appendContactInfo(requester);
		this.getHeaderPanel().appendText(messageInfo.toString());
		this.getHeaderPanel().appendContactInfo(requested);
		this.getHeaderPanel().appendText(messages.getMessage("feed.friendship"));
		this.getDetailsPanel().appendAvatarFriend(requested);
		super.internationalize(messages);
	}
	
}
