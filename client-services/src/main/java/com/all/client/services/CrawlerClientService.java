package com.all.client.services;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.messages.CrawlerRequest;
import com.all.shared.messages.CrawlerResponse;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;

@Service
public class CrawlerClientService implements MessageListener<AllMessage<CrawlerResponse>> {

	private static final long REQUEST_TIMEOUT = 30;
	@Autowired
	private MessEngine messEngine;
	private CrawlerResponse contacts;
	private final CyclicBarrier requestLock = new CyclicBarrier(2);
	private Log log = LogFactory.getLog(this.getClass());

	@PostConstruct
	public void init() {
		messEngine.addMessageListener(MessEngineConstants.IMPORT_CONTACTS_RESPONSE_TYPE, this);
	}

	@Override
	public void onMessage(AllMessage<CrawlerResponse> message) {
		contacts = message.getBody();
		try {
			requestLock.await();
		} catch (InterruptedException e) {
			log.error(e, e);
		} catch (BrokenBarrierException e) {
			log.error(e, e);
		}
	}

	public CrawlerResponse getContacts() {
		return contacts;
	}

	public synchronized CrawlerResponse requestImportContacts(CrawlerRequest crawlerRequest) {
		AllMessage<CrawlerRequest> message = new AllMessage<CrawlerRequest>(
				MessEngineConstants.IMPORT_CONTACTS_REQUEST_TYPE, crawlerRequest);
		sendAndWait(message);
		return getContacts();
	}

	private void sendAndWait(AllMessage<CrawlerRequest> message) {
		requestLock.reset();
		messEngine.send(message);
		try {
			requestLock.await(REQUEST_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e, e);
		} catch (BrokenBarrierException e) {
			log.error(e, e);
		} catch (TimeoutException e) {
			log.error(e, e);
		}
	}

}
