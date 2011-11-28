package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.newsfeed.RecommendedTrackTwtterFeed;

public class RecommendedTrackTwitterFeedView extends FeedView<RecommendedTrackTwtterFeed> {
	private static final long serialVersionUID = 1L;

	public RecommendedTrackTwitterFeedView(Messages messages, RecommendedTrackTwtterFeed feed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.TWITTER, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
		initialize();
	}
	
	private void initialize(){
		this.getDetailsPanel().appendFeedTrack(feed.getOwner(), feed.getFeedTrack());
	}

	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner = feed.getOwner();
		
		this.getHeaderPanel().clear();
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(messages.getMessage("feed.twitter.recommendedTrack"));
		super.internationalize(messages);
	}
}
