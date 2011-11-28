package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.newsfeed.RemoteLibraryBrowsingFeed;

public class RLBrowsingFeedView extends FeedView<RemoteLibraryBrowsingFeed> {

	private static final long serialVersionUID = 1L;
	
	protected RLBrowsingFeedView(RemoteLibraryBrowsingFeed feed, Messages messages, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.RLBROWSING, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner   = feed.getOwner();
		ContactInfo visited = feed.getVisited();
		String messageHeader;
		
		clear();

		if (owner.getGender() == Gender.MALE) {
			messageHeader = messages.getMessage("feed.RLBrowsing.visitedLibrary.male");
		} else {
			messageHeader = messages.getMessage("feed.RLBrowsing.visitedLibrary.female");
		}
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(messageHeader);
		this.getHeaderPanel().appendContactInfo(visited, ".");
		this.getDetailsPanel().appendAvatarFriend(visited);
		super.internationalize(messages);
	}

}
