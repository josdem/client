package com.all.client.services.reporting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.newsfeed.TopHundredFeed;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.FeedStat;
import com.all.shared.stats.TopHundredStat;

@Component
public class TopHundredOnSessionStatFilter implements StatFilter {
	private ContactInfo owner = null;
	private Map<String, FeedStat> stats = new HashMap<String, FeedStat>();

	@Override
	public void init(User user) {
		this.owner = new ContactInfo(user);
	}

	@Override
	public void filter(AllStat stat) {
		if (owner != null) {
			if (stat instanceof TopHundredStat) {
				filterHundredStat((TopHundredStat) stat);
			}
		}
	}

	private void filterHundredStat(TopHundredStat stat) {
		String key = createKey(stat);
		if (!stats.containsKey(key)) {
			stats.put(key, createFeed(stat));
		}
	}

	private FeedStat createFeed(TopHundredStat stat) {
		return new FeedStat(new TopHundredFeed(owner, stat.category(), stat.playlist()));
	}

	private String createKey(TopHundredStat stat) {
		return stat.category().getId() + ":" + stat.playlist().getHashcode();
	}

	@Override
	public Iterable<FeedStat> close() {
		return stats.values();
	}

}
