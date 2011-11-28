package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.newsfeed.RecommendedTrackFacebookFeed;

public class RecommendedTrackFacebookFeedView extends FeedView<RecommendedTrackFacebookFeed> {

	private static final long serialVersionUID = 5375141153167200823L;

	public RecommendedTrackFacebookFeedView(RecommendedTrackFacebookFeed recommendedTrackFacebookFeed, Messages messages,
			ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(recommendedTrackFacebookFeed, FeedIconType.FACEBOOK, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);

		intialize();
	}

	private void intialize() {
		this.getDetailsPanel().appendFeedTrack(feed.getOwner(), feed.getFeedTrack());
	}

	@Override
	public void internationalize(Messages messages) {
		this.getHeaderPanel().clear();
		this.getHeaderPanel().appendContactInfo(feed.getOwner());
		this.getHeaderPanel().appendText(messages.getMessage("feed.facebook.recommendedTrack"));
		super.internationalize(messages);
	}

}
