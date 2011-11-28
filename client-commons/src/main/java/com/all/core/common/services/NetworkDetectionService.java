package com.all.core.common.services;

import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.model.ApplicationEvents;
import com.all.core.common.model.ApplicationModel;
import com.all.messengine.MessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;

@Service
public class NetworkDetectionService {

	private static final int NETWORK_DETECTION_DELAY = 10;

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private MessEngine messEngine;
	@Autowired
	@Qualifier("clientSettings")
	private Properties clientSettings;
	private static final String URL_BASE = "urlBaseConnection";
	private static final String PORT = "portConnection";
	private ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("NetworkdDetectionService"));
	private final AtomicBoolean connected = new AtomicBoolean(false);

	@PostConstruct
	public void initialize() {
		connected.set(isConnected());
		controlEngine.set(ApplicationModel.HAS_INTERNET_CONNECTION, connected.get(), ApplicationEvents.INTERNET_CONNECTION);
		executorService.scheduleAtFixedRate(new DetectNetworkStatusTask(), NETWORK_DETECTION_DELAY,
				NETWORK_DETECTION_DELAY, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void shutdown() {
		executorService.shutdownNow();
	}

	private void updateNetworkStatus() {
		if (connected.get() != isConnected()) {
			connected.set(!connected.get());
			controlEngine.set(ApplicationModel.HAS_INTERNET_CONNECTION, connected.get(), ApplicationEvents.INTERNET_CONNECTION);
			messEngine.send(new AllMessage<Boolean>(MessEngineConstants.INTERNET_CONNECTION_STATUS_TYPE, connected.get()));
		}
	}

	private boolean isConnected() {
		boolean value = false;
		try {
			Socket socket = new Socket(clientSettings.getProperty(URL_BASE), Integer.parseInt(clientSettings
					.getProperty(PORT)));
			socket.setSoTimeout(1);
			value = socket.isConnected();
			socket.close();
		} catch (Exception e) {
			log.error("Connection refused to: " + clientSettings.getProperty(URL_BASE) + ":"
					+ clientSettings.getProperty(PORT) + " " + e.getClass().getName() + " - " + e.getMessage());
			value = false;
		}
		return value;
	}

	private final class DetectNetworkStatusTask implements Runnable {
		@Override
		public void run() {
			updateNetworkStatus();
		}
	}

}
