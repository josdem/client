package com.all.client.services.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.core.common.services.reporting.ReportSender;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;

@Service
public class ReporterSessionFilterManager {
	private final static Log log = LogFactory.getLog(ReporterSessionFilterManager.class);

	private List<StatFilter> filters = new ArrayList<StatFilter>();

	@Autowired
	private ReportSender sender;

	private User user;

	@MessageMethod(MessEngineConstants.STAT_REPORTED_TYPE)
	public void onStatReportedMessage(AllStat stat) {
		if (user == null) {
			return;
		}
		for (StatFilter statFilter : filters) {
			try {
				statFilter.filter(stat);
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}

	@Autowired
	public void setStatFilters(Collection<StatFilter> statFilters) {
		for (StatFilter statFilter : statFilters) {
			this.filters.add(statFilter);
		}
	}

	public synchronized void login(User user) {
		this.user = user;
		for (StatFilter filter : filters) {
			try {
				filter.init(user);
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}

	public synchronized void logout() {
		this.user = null;
		List<AllStat> stats = new ArrayList<AllStat>();
		for (StatFilter filter : filters) {
			try {
				for (AllStat stat : filter.close()) {
					if (stat != null) {
						stats.add(stat);
					}
				}
			} catch (Exception e) {
				log.warn(e);
			}
		}
		if (!stats.isEmpty()) {
			sender.send(stats);
		}
	}

}
