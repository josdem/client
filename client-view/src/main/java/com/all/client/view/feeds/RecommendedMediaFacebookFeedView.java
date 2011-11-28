package com.all.client.view.feeds;

import java.text.DecimalFormat;
import java.util.List;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.FeedTrack;
import com.all.shared.newsfeed.RecommendedMediaFacebookFeed;

public class RecommendedMediaFacebookFeedView extends FeedView<RecommendedMediaFacebookFeed> {

	private static final String SPACER = "    ";
	private static final long serialVersionUID = 1763588680852041624L;

	public RecommendedMediaFacebookFeedView(RecommendedMediaFacebookFeed recommendedMediaFacebookFeed,
			Messages messages, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(recommendedMediaFacebookFeed, FeedIconType.FACEBOOK, FeedSize.LARGE, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner = feed.getOwner();
		int i = 0;

		clear();
		
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(messages.getMessage("feed.facebook.recommendedMedia.header"));

		List<String> folders = feed.getFolders();
		for (int j = 0; i < RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES && j < folders.size(); j++, i++) {
			this.getDetailsPanel().appendFolder(feed.getOwner(), folders.get(j));
			this.getDetailsPanel().newLine();
		}

		List<String> playlists = feed.getPlaylists();
		for (int j = 0; i < RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES && j < playlists.size(); j++, i++) {
			this.getDetailsPanel().appendPlaylist(feed.getOwner(), playlists.get(j));
			this.getDetailsPanel().newLine();
		}

		List<FeedTrack> tracks = feed.getTracks();
		for (int j = 0; i < RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES && j < tracks.size(); j++, i++) {
			this.getDetailsPanel().appendFeedTrack(feed.getOwner(), tracks.get(j));
			this.getDetailsPanel().newLine();
		}

		this.getDetailsPanel().appendText(SPACER); // spacer to align the text outside icon margins
		this.getDetailsPanel().appendText(messages.getMessage("feed.facebook.recommendedMedia.and"));
		int numberOfTracks = feed.getNumberOfTracks();
		this.getDetailsPanel().appendLinkLibraryLabel(
				feed.getOwner(),
				messages.getMessage("feed.facebook.recommendedMedia.numberOfTracks", new DecimalFormat("#,###,###,###")
						.format(numberOfTracks > RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES ? numberOfTracks
								- RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES : numberOfTracks)));
		this.getDetailsPanel().appendText(messages.getMessage("feed.facebook.recommendedMedia.more"));
		super.internationalize(messages);
	}

}
