package com.all.client.services.reporting;

import java.util.Collections;

import com.all.shared.model.ContactInfo;
import com.all.shared.model.Track;
import com.all.shared.model.User;
import com.all.shared.newsfeed.TrackContentFeed;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.FeedStat;

public abstract class SessionTrackCounterFilter implements StatFilter {
	private int topCount;
	private ContactInfo owner;
	private TopTrack topTracks;
	private final int feedType;

	public SessionTrackCounterFilter(int topCount, int feedId) {
		this.topCount = topCount;
		this.feedType = feedId;
	}

	@Override
	public void init(User user) {
		this.owner = new ContactInfo(user);
		this.topTracks = new TopTrack(topCount);
	}

	@Override
	public void filter(AllStat stat) {
		if (topTracks == null) {
			return;
		}
		Track track = getTrack(stat);
		if (track != null) {
			topTracks.add(track);
		}
	}

	abstract Track getTrack(AllStat stat);

	@Override
	public Iterable<FeedStat> close() {
		TopTrack topTracks = this.topTracks;
		this.topTracks = null;
		if (topTracks.getTrackCount() == 0 || topTracks.getTracks().isEmpty()) {
			return Collections.emptyList();
		}
		TrackContentFeed feed = new TrackContentFeed(owner, topTracks.getTracks(), topCount, feedType);
		feed.setTrackCount(topTracks.getTrackCount());
		FeedStat feedStat = new FeedStat(feed);
		return Collections.singletonList(feedStat);
	}
}
