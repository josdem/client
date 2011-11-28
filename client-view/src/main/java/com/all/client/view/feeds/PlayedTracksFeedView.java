package com.all.client.view.feeds;

import java.util.List;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.FeedTrack;
import com.all.shared.newsfeed.TrackContentFeed;

public class PlayedTracksFeedView extends FeedView<TrackContentFeed> {
	private static final long serialVersionUID = 1L;
	public static final int MAXIMUM_ENTITIES = 3;
	private static final String SPACER = "    ";

	public PlayedTracksFeedView(TrackContentFeed feed, Messages messages, ViewEngine viewEngine,
			DialogFactory dialogFactory) {
		super(feed, FeedIconType.PLAYED_TRACKS, FeedSize.LARGE, messages, viewEngine, dialogFactory);
		initialize(FeedSize.LARGE);
	}

	@Override
	public void internationalize(Messages messages) {
		clear();

		getHeaderPanel().appendContactInfo(feed.getOwner());
		getHeaderPanel().appendText(messages.getMessage("feed.playcount.title"));

		List<FeedTrack> tracks = feed.getTracks();
		for (int i = 0; i < tracks.size() && i < MAXIMUM_ENTITIES; i++) {
			this.getDetailsPanel().appendFeedTrack(feed.getOwner(), tracks.get(i));
			this.getDetailsPanel().newLine();
		}

		if (feed.getTrackCount() > MAXIMUM_ENTITIES) {
			this.getDetailsPanel().appendText(SPACER);
			this.getDetailsPanel().appendText(messages.getMessage("feed.sendMedia.and"));
			String message = messages.getMessage("feed.sendMedia.numberOfTracks", "" + feed.getTrackCount());
			this.getDetailsPanel().appendLinkLibraryLabel(feed.getOwner(), message);
			this.getDetailsPanel().appendText(messages.getMessage("feed.sendMedia.more"));
		}
		super.internationalize(messages);
	}

}
