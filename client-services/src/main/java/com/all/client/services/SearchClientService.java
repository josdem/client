package com.all.client.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.client.services.reporting.ClientReporter;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.stats.usage.UserActions;

@Service
public class SearchClientService implements MessageListener<AllMessage<ArrayList<ContactInfo>>> {

	private static final long SEARCH_TIMEOUT = 10000;

	private Log log = LogFactory.getLog(this.getClass());

	@Autowired
	MessEngine messEngine;
	@Autowired
	ClientReporter reporter;
	private List<ContactInfo> results;
	private CyclicBarrier requestLock = new CyclicBarrier(2);

	@PostConstruct
	public void init() {
		messEngine.addMessageListener(MessEngineConstants.SEARCH_CONTACTS_RESPONSE_TYPE, this);
	}

	public List<ContactInfo> search(String keyword) {
		String messageBody = new String(Base64.encode(keyword.getBytes()));
		sendAndWait(new AllMessage<String>(MessEngineConstants.SEARCH_CONTACTS_REQUEST_TYPE, messageBody));
		reporter.logUserAction(UserActions.Friendships.SEARCH_USER);
		return getResults();
	}

	private List<ContactInfo> getResults() {
		return results;
	}

	private void sendAndWait(AllMessage<String> message) {
		requestLock.reset();
		messEngine.send(message);
		try {
			requestLock.await(SEARCH_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.debug(e, e);
		} catch (BrokenBarrierException e) {
			log.debug(e, e);
		} catch (TimeoutException e) {
			log.debug(e, e);
		}
	}

	@Override
	public void onMessage(AllMessage<ArrayList<ContactInfo>> message) {
		results = message.getBody();
		try {
			requestLock.await();
		} catch (InterruptedException e) {
			log.error(e, e);
		} catch (BrokenBarrierException e) {
			log.error(e, e);
		}
	}

}
