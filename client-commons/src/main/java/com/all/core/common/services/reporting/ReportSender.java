package com.all.core.common.services.reporting;

import static com.all.shared.messages.MessEngineConstants.USAGE_STATS_TYPE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.core.common.messages.ResponseMessage;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.AllStat;

@Service
public class ReportSender implements MessageListener<ResponseMessage> {
	private static final String REQUEST_NUMBER = "REQUEST_NUMBER";
	private static final Log log = LogFactory.getLog(ReportSender.class);

	private Set<String> pendingSents = new HashSet<String>();
	private int count = 1;

	@Autowired
	private MessEngine messEngine;

	public ReportSender() {
		log.info("Starting ReportSender Service");
	}

	@PostConstruct
	public void setup() {
		messEngine.addMessageListener(ResponseMessage.getType(USAGE_STATS_TYPE), this);
	}

	public synchronized void send(List<AllStat> stats) {
		count++;
		String reqId = "" + count;
		AllMessage<List<AllStat>> originalMessage = new AllMessage<List<AllStat>>(USAGE_STATS_TYPE, stats);
		originalMessage.putProperty(REQUEST_NUMBER, reqId);
		pendingSents.add(reqId);
		messEngine.send(originalMessage);
		log.info("SENDING STATS!!! >>> ID: " + reqId);
	}

	@Override
	public void onMessage(ResponseMessage response) {
		log.info("SENDING STATS RESPONSE!!! >>> ID: " + response.getProperty(REQUEST_NUMBER) + " STATUS: "
				+ response.getProperty(ResponseMessage.RESPONSE_CODE));
		pendingSents.remove(response.getProperty(REQUEST_NUMBER));
	}

	public void waitForTermination() {
		int maxCount = 0;
		while (!pendingSents.isEmpty()) {
			try {
				log.info("waiting for responses: " + pendingSents);
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			maxCount++;
			if (maxCount > 20) {
				log.error("COULD NOT FINISH SENDING STATS!!!");
				break;
			}
		}
	}

}
