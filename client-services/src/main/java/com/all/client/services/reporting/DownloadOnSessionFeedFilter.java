package com.all.client.services.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.services.MusicEntityService;
import com.all.shared.model.Track;
import com.all.shared.newsfeed.FeedType;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.DownloadStat;

@Component
public class DownloadOnSessionFeedFilter extends SessionTrackCounterFilter {
	@Autowired
	private MusicEntityService musicEntityService;
	private static final int TOP_COUNT = 5;

	public DownloadOnSessionFeedFilter() {
		super(TOP_COUNT, FeedType.DOWNLOADED_TRACKS);
	}

	@Override
	Track getTrack(AllStat stat) {
		if (stat instanceof DownloadStat) {
			return musicEntityService.getTrack(((DownloadStat) stat).getTrackId());
		}
		return null;
	}

}
