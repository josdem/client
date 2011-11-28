package com.all.client.services.reporting;

import java.util.Collections;

import org.springframework.stereotype.Component;

import com.all.shared.model.User;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.UserSessionStat;

@Component
public class SessionStatFilter implements StatFilter {
	private UserSessionStat sessionStat = null;

	@Override
	public void init(User user) {
		this.sessionStat = new UserSessionStat(user.getEmail());
	}

	@Override
	public void filter(AllStat stat) {
		sessionStat.add(stat);
	}

	@Override
	public Iterable<UserSessionStat> close() {
		UserSessionStat sessionStat = this.sessionStat;
		if (sessionStat != null) {
			sessionStat.close();
			this.sessionStat = null;
			return Collections.singletonList(sessionStat);
		} else {
			return null;
		}

	}

}
