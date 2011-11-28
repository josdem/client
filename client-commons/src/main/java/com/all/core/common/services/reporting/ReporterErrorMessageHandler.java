package com.all.core.common.services.reporting;

import static com.all.shared.messages.MessEngineConstants.USAGE_STATS_TYPE;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.core.common.messages.ErrorMessage;
import com.all.core.common.messages.ResponseMessage;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.AllStat;

@Service
public class ReporterErrorMessageHandler implements MessageListener<ErrorMessage> {
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ReportSender sender;
	@Autowired
	private ReporterStatFileManager statFileManager;

	@PostConstruct
	public void setup() {
		messEngine.addMessageListener(ErrorMessage.getType(USAGE_STATS_TYPE), this);
	}

	@Override
	public void onMessage(ErrorMessage message) {
		try {
			AllMessage<?> reportMessage = message.getBody();
			@SuppressWarnings("unchecked")
			List<AllStat> stats = (List<AllStat>) reportMessage.getBody();
			statFileManager.saveStats(stats);
			messEngine.send(new ResponseMessage("stats-saved", reportMessage));
		} catch (Exception e) {
			messEngine.send(new ResponseMessage("error-saving", message.getBody()));
		}
	}

	public void send() {
		for (List<AllStat> actions : statFileManager.load()) {
			if (actions.isEmpty()) {
				continue;
			}
			sender.send(actions);
		}
	}
}
