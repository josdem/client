package com.all.client.services.reporting;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalTrack;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.newsfeed.TotalTracksFeed;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.FeedStat;
import com.all.shared.stats.MediaImportStat;

@Component
public class TotalTracksSessionStatFilter implements StatFilter {
	private boolean hasImported = false;
	private ContactInfo contact;
	@Autowired
	private LocalModelDao dao;

	@Override
	public void init(User user) {
		contact = new ContactInfo(user);
	}

	@Override
	public void filter(AllStat stat) {
		if (stat instanceof MediaImportStat) {
			hasImported = true;
		}
	}

	@Override
	public Iterable<FeedStat> close() {
		ContactInfo contact = this.contact;
		this.contact = null;
		boolean hasImported = this.hasImported;
		this.hasImported = false;
		if (hasImported && contact != null) {
			long trackCount = dao.count(LocalTrack.class);
			TotalTracksFeed feed = new TotalTracksFeed(contact, trackCount);
			FeedStat stat = new FeedStat(feed);
			return Collections.singleton(stat);
		} else {
			return null;
		}
	}
}
