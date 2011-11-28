package com.all.client.rest;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.json.JsonConverter;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.User;

// Temporal disable until we have good rest push
//@Component
public class RestPushAdapter {
	private final static Log log = LogFactory.getLog(RestPushAdapter.class);

	private static final String REST_PUSH_KEY = "push.url";
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private Properties clientSettings;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private String url = null;
	private Long userId;

	private RestTemplate restTemplate = new RestTemplate();

	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void start(User user) {
		userId = user.getId();
		url = clientSettings.getProperty("all.server.url") + clientSettings.getProperty(REST_PUSH_KEY);
		Runnable command = new RestPushCommand();
		executorService.execute(command);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void stop() {
		executorService.shutdownNow();
	}

	class RestPushCommand implements Runnable {
		int fails = 0;

		@Override
		public void run() {
			// log.info("getting Rest messages...");
			String json = "";
			try {
				json = restTemplate.getForObject(url, String.class, userId);
				fails = Math.max(0, fails--);
			} catch (Exception e) {
				log.error("REST PUSH NOT AVAILABLE:" + e);// , e);
				try {
					Thread.sleep(30000 * Math.max(1, fails++));
				} catch (InterruptedException e1) {
				}
			}
			// log.info("PUSH:: " + json);
			if (json != null && !"".equals(json.trim())) {
				try {
					AllMessage<?> message = JsonConverter.toBean(json, AllMessage.class);
					messEngine.send(message);
				} catch (Exception e) {
					log.warn(e, e);
					// do nothing discard message
				}
			}
			executorService.execute(this);
		}
	}
}
