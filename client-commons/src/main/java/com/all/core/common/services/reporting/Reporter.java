package com.all.core.common.services.reporting;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.core.common.logging.ErrorEventStatProcessor;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.spring.InitializeService;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.BufferedStat;
import com.all.shared.stats.IncrementalStat;
import com.all.shared.stats.StatKey;

@Controller
public class Reporter {
	@Autowired
	private ReportSender sender;
	private Map<StatKey, BufferedStat> bufferedStats = Collections.synchronizedMap(new HashMap<StatKey, BufferedStat>());
	private List<AllStat> simpleStats = new LinkedList<AllStat>();
	@Autowired
	private MessEngine messEngine;

	@InitializeService
	public void bindError() {
		ErrorEventStatProcessor.getInstance().bind(this);
	}

	@ActionMethod(ApplicationActions.REPORT_USER_STAT_ID)
	@MessageMethod(MessEngineConstants.REPORT_STAT_TYPE)
	public void log(AllStat stat) {
		if (stat instanceof BufferedStat) {
			BufferedStat bufferedStat = (BufferedStat) stat;
			StatKey key = bufferedStat.key();
			if (bufferedStat instanceof IncrementalStat && bufferedStats.containsKey(key)) {
				((IncrementalStat) bufferedStats.get(key)).increment();
			} else {
				bufferedStats.put(key, bufferedStat);
			}
		} else {
			simpleStats.add(stat);
		}
		messEngine.send(new AllMessage<AllStat>(MessEngineConstants.STAT_REPORTED_TYPE, stat));
	}

	public void send() {
		send(drainCurrentStats());
	}

	private void send(List<AllStat> currentStats) {
		if (!currentStats.isEmpty()) {
			sender.send(currentStats);
		}
	}

	private synchronized List<AllStat> drainCurrentStats() {
		if (bufferedStats.isEmpty()) {
			return Collections.emptyList();
		}
		List<AllStat> currentStats = new LinkedList<AllStat>();
		currentStats.addAll(bufferedStats.values());
		bufferedStats.clear();
		currentStats.addAll(simpleStats);
		simpleStats.clear();
		return currentStats;
	}

}
