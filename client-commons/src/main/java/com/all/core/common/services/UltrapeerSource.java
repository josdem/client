package com.all.core.common.services;

import static com.all.shared.messages.MessEngineConstants.NEW_ULTRAPEER_SESSION_TYPE;
import static com.all.shared.messages.MessEngineConstants.ULTRAPEER_SESSION_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.ULTRAPEER_SESSION_RESPONSE_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.model.AllMessage;
import com.all.shared.model.UltrapeerNode;
import com.all.shared.model.UltrapeerSessionResponse;

@Repository
public class UltrapeerSource implements MessageListener<AllMessage<UltrapeerSessionResponse>> {
	private static final Log log = LogFactory.getLog(UltrapeerSource.class);
	private static final String ULTRAPEERS_LIST_KEY = "ultrapeers.list";

	@Autowired
	private ApplicationDao loginDao;
	@Autowired
	private Properties clientSettings;
	@Autowired
	private MessEngine messEngine;

	private UltrapeerNode currentUltrapeer;

	private Set<UltrapeerNode> ultrapeers;
	private final Set<UltrapeerNode> unavailableUltrapeers = new HashSet<UltrapeerNode>();

	private ScheduledExecutorService executorService;

	public UltrapeerSource() {
		executorService = Executors.newSingleThreadScheduledExecutor();
	}

	@PostConstruct
	public void initialize() {
		messEngine.addMessageListener(ULTRAPEER_SESSION_RESPONSE_TYPE, this);
	}

	@PreDestroy
	public void destroy() {
		executorService.shutdownNow();
		messEngine.removeMessageListener(ULTRAPEER_SESSION_RESPONSE_TYPE, this);
	}

	public UltrapeerNode get() {
		if (currentUltrapeer == null) {
			synchronized (this) {
				if (currentUltrapeer == null) {
					List<UltrapeerNode> ultrapeers = new ArrayList<UltrapeerNode>(this.getUltrapeers());
					if (ultrapeers.isEmpty()) {
						throw new RuntimeException("No ultrapeers anywhere were configured!!");
					}
					ultrapeers.removeAll(unavailableUltrapeers);
					if (ultrapeers.isEmpty()) {
						throw new RuntimeException("All ultrapeers are marked as unavailable!");
					}
					Collections.sort(ultrapeers);

					UltrapeerNode currentUltrapeer = (ultrapeers.get(ultrapeers.size() - 1));
					if (currentUltrapeer.getSc() == 0) {
						int index = (int) Math.floor((Math.random() * ultrapeers.size()));
						currentUltrapeer = ultrapeers.get(index);
					}
					this.currentUltrapeer = currentUltrapeer;
				}
			}
		}
		return currentUltrapeer;
	}

	private void delete(UltrapeerNode deprecatedUltrapeer) {
		getUltrapeers().remove(deprecatedUltrapeer);
		loginDao.delete(deprecatedUltrapeer);
	}

	private void add(UltrapeerNode newUltrapeer) {
		getUltrapeers().add(newUltrapeer);
		loginDao.save(newUltrapeer);
	}

	private List<UltrapeerNode> getDefaultUltrapeerAddresses() {
		List<UltrapeerNode> ultrapeers = new ArrayList<UltrapeerNode>();
		String separator = ",";
		String addresses = clientSettings.getProperty(ULTRAPEERS_LIST_KEY);

		if (addresses.contains(separator)) {
			for (String address : addresses.split(separator)) {

				ultrapeers.add(new UltrapeerNode(address.trim()));
			}
		} else {
			ultrapeers.add(new UltrapeerNode(addresses.trim()));
		}
		return ultrapeers;
	}

	public void reportFail() {
		synchronized (this) {
			get().incrementUnsuccesfulConnections();
			currentUltrapeer = null;
		}
	}

	public void reportSuccess() {
		synchronized (this) {
			get().incrementSuccesfulConnections();
		}
	}

	@Override
	public void onMessage(AllMessage<UltrapeerSessionResponse> message) {
		UltrapeerSessionResponse response = message.getBody();
		if (response.isAccepted()) {
			UltrapeerNode ultrapeerNode = get();
			ultrapeerNode.incrementSuccesfulConnections();
			messEngine.send(new AllMessage<String>(NEW_ULTRAPEER_SESSION_TYPE, ultrapeerNode.getAddress()));
			log.info("Client succesfully connected to ultrapeer " + ultrapeerNode.getAddress());
		} else {
			reportFail();
		}
		try {
			for (UltrapeerNode newUltrapeer : response.getNewUltrapeers()) {
				log.info("Adding ultrapeer " + newUltrapeer.getAddress());
				add(newUltrapeer);
			}
			for (UltrapeerNode deprecatedUltrapeer : response.getDeprecatedUltrapeers()) {
				log.info("Removing ultrapeer " + deprecatedUltrapeer.getAddress());
				delete(deprecatedUltrapeer);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void verifyUltrapeer() {
		ArrayList<UltrapeerNode> ultrapeerList = new ArrayList<UltrapeerNode>(getUltrapeers());
		AllMessage<ArrayList<UltrapeerNode>> sessionRequest;
		sessionRequest = new AllMessage<ArrayList<UltrapeerNode>>(ULTRAPEER_SESSION_REQUEST_TYPE, ultrapeerList);
		messEngine.send(sessionRequest);

	}

	private Set<UltrapeerNode> getUltrapeers() {
		if (ultrapeers == null) {
			HashSet<UltrapeerNode> ultrapeers = new HashSet<UltrapeerNode>();
			ultrapeers.addAll(loginDao.getKnownUltrapeers());
			for (UltrapeerNode ultrapeer : getDefaultUltrapeerAddresses()) {
				ultrapeers.add(ultrapeer);
				loginDao.save(ultrapeer);
			}
			this.ultrapeers = ultrapeers;
		}
		return ultrapeers;
	}

	class UltrapeerRequester implements Runnable {
		@Override
		public void run() {
			verifyUltrapeer();
		}
	}

}
