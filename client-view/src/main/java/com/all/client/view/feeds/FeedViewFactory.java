package com.all.client.view.feeds;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.newsfeed.AllFeed;
import com.all.shared.newsfeed.AvatarFeed;
import com.all.shared.newsfeed.ContactsFeed;
import com.all.shared.newsfeed.DeviceExportFeed;
import com.all.shared.newsfeed.FeedType;
import com.all.shared.newsfeed.FriendshipFeed;
import com.all.shared.newsfeed.MediaImportFeed;
import com.all.shared.newsfeed.PostedListeningTrackTwitterFeed;
import com.all.shared.newsfeed.QuoteFeed;
import com.all.shared.newsfeed.RecommendedMediaFacebookFeed;
import com.all.shared.newsfeed.RecommendedTrackFacebookFeed;
import com.all.shared.newsfeed.RecommendedTrackTwtterFeed;
import com.all.shared.newsfeed.RemoteLibraryBrowsingFeed;
import com.all.shared.newsfeed.SendMediaFeed;
import com.all.shared.newsfeed.TopHundredFeed;
import com.all.shared.newsfeed.TotalTracksFeed;
import com.all.shared.newsfeed.TrackContentFeed;
import com.all.shared.newsfeed.UpdateProfileFeed;

@Controller
public class FeedViewFactory {

	@Autowired
	private Messages messages;
	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private DialogFactory dialogFactory;

	private Map<AllFeed, FeedView<?>> cache = new HashMap<AllFeed, FeedView<?>>();

	public FeedView<?> create(AllFeed allFeed) {
		FeedView<?> feedView = cache.get(allFeed);
		if (feedView == null) {
			feedView = doCreate(allFeed);
			cache.put(allFeed, feedView);
		}
		return feedView;
	}

	/**
	 * Shall never return <code>null</code>
	 * 
	 * @param allFeed
	 * @return FeedView<?>
	 * @throws IllegalArgumentException
	 */
	private FeedView<?> doCreate(AllFeed allFeed) {
		int feedType = allFeed.getType();
		switch (feedType) {
		case FeedType.EXPORT_TO_DEVICE:
			return new DeviceExportFeedView(messages, (DeviceExportFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.QUOTE:
			return new QuoteFeedView(messages, (QuoteFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.TOTAL_TRACKS:
			return new TotalTracksFeedView(messages, (TotalTracksFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.AVATAR_UPDATE:
			return new AvatarFeedView(messages, (AvatarFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.RECOMMENDED_TRACK_TWITTER:
			return new RecommendedTrackTwitterFeedView(messages, (RecommendedTrackTwtterFeed) allFeed, viewEngine,
					dialogFactory);
		case FeedType.RECOMMENDED_TRACK_FACEBOOK:
			return new RecommendedTrackFacebookFeedView((RecommendedTrackFacebookFeed) allFeed, messages, viewEngine,
					dialogFactory);
		case FeedType.RECOMMENDED_MEDIA_FACEBOOK:
			return new RecommendedMediaFacebookFeedView((RecommendedMediaFacebookFeed) allFeed, messages, viewEngine,
					dialogFactory);
		case FeedType.POSTED_TRACK_TWITTER:
			return new PostedListeningTrackTwitterFeedView(messages, (PostedListeningTrackTwitterFeed) allFeed, viewEngine,
					dialogFactory);
		case FeedType.UPDATE_PROFILE:
			return new ProfileFeedView(messages, (UpdateProfileFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.FRIENDSHIP:
			return new FriendshipFeedView(messages, (FriendshipFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.MEDIA_IMPORT:
			return new MediaImportFeedView((MediaImportFeed) allFeed, messages, viewEngine, dialogFactory);
		case FeedType.CONTACTS_FEED:
			return new ContactsFeedView(messages, (ContactsFeed) allFeed, viewEngine, dialogFactory);
		case FeedType.DOWNLOADED_TRACKS:
			return new DownloadedTracksFeedView((TrackContentFeed) allFeed, messages, viewEngine, dialogFactory);
		case FeedType.PLAYED_TRACKS:
			return new PlayedTracksFeedView((TrackContentFeed) allFeed, messages, viewEngine, dialogFactory);
		case FeedType.TOP_HUNDRED:
			return new TopHundredFeedView((TopHundredFeed) allFeed, messages, viewEngine, dialogFactory);
		case FeedType.SEND_MEDIA:
			return new SendMediaFeedView((SendMediaFeed) allFeed, messages, viewEngine, dialogFactory);
		case FeedType.BROWSE_REMOTE_LIBRARY:
			return new RLBrowsingFeedView((RemoteLibraryBrowsingFeed) allFeed, messages, viewEngine, dialogFactory);
		default:
			throw new IllegalArgumentException(
					"Unable to match any view to the current feed. It will not be displayed nor processed. Type="
							+ allFeed.getType());
		}
	}
}
