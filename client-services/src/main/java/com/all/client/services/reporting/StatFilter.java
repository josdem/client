package com.all.client.services.reporting;

import com.all.shared.model.User;
import com.all.shared.stats.AllStat;

public interface StatFilter {

	void init(User user);

	void filter(AllStat stat);

	Iterable<? extends AllStat> close();

}
