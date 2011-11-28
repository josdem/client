package com.all.client.services.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.services.MusicEntityService;
import com.all.shared.model.Track;
import com.all.shared.newsfeed.FeedType;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.PlayCountStat;

@Component
public class PlayedOnSessionFeedFilter extends SessionTrackCounterFilter {
	@Autowired
	private MusicEntityService musicEntityService;
	private static final int TOP_COUNT = 5;

	public PlayedOnSessionFeedFilter() {
		super(TOP_COUNT, FeedType.PLAYED_TRACKS);
	}

	@Override
	Track getTrack(AllStat stat) {
		if (stat instanceof PlayCountStat) {
			return musicEntityService.getTrack(((PlayCountStat) stat).getHashcode());
		}
		return null;
	}
}
