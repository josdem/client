package com.all.client.services;

import static com.all.shared.messages.MessEngineConstants.SYNC_LIBRARY_MERGE_REQUEST;
import static com.all.shared.messages.MessEngineConstants.SYNC_LIBRARY_MERGE_RESPONSE;
import static com.all.shared.messages.MessEngineConstants.SYNC_SEND_DELTA_RESPONSE;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.SyncValueObject;

@Service
public class SyncClientService {

	public static final int REQUEST_TIMEOUT = 300;

	private final Log log = LogFactory.getLog(this.getClass());

	private final CyclicBarrier syncBarrier = new CyclicBarrier(2);

	private final CyclicBarrier mergeBarrier = new CyclicBarrier(2);

	private SyncValueObject mergeResponse;

	private SyncValueObject syncResponse;

	private String libraryOwner;

	@Autowired
	private MessEngine messEngine;

	@PostConstruct
	public void initialize() {
		messEngine.addMessageListener(SYNC_LIBRARY_MERGE_RESPONSE, new MessageListener<AllMessage<SyncValueObject>>() {
			@Override
			public void onMessage(AllMessage<SyncValueObject> message) {
				setMergeResponse(message.getBody());
			}

		});
		messEngine.addMessageListener(SYNC_SEND_DELTA_RESPONSE, new MessageListener<AllMessage<SyncValueObject>>() {
			@Override
			public void onMessage(AllMessage<SyncValueObject> message) {
				syncResponse = message.getBody();
				releaseSyncBarrier();
			}
		});
	}

	public SyncValueObject sync(SyncValueObject syncObject) {
		resetSyncBarrier();
		sendRequest(new AllMessage<SyncValueObject>(MessEngineConstants.SYNC_SEND_DELTA_REQUEST, syncObject));
		awaitSyncBarrier();
		return getSyncResponse();
	}

	public SyncValueObject merge(SyncValueObject syncObject) {
		resetMergeBarrier();
		sendRequest(new AllMessage<SyncValueObject>(SYNC_LIBRARY_MERGE_REQUEST, syncObject));
		awaitMergeBarrier(syncObject.getEmail());
		return getMergeResponse();
	}

	public void cancelMergeRequest(String email) {
		if (email.equals(libraryOwner)) {
			log.info("Cancelling merge request for " + email);
			libraryOwner = null;
			mergeResponse = null;
			releaseMergeBarrier();
		}
	}

	private void setMergeResponse(SyncValueObject response) {
		if (libraryOwner != null && response != null && libraryOwner.equals(response.getEmail())) {
			mergeResponse = response;
			libraryOwner = null;
			releaseMergeBarrier();
		} else {
			log.info("Will discard merge response since it is no longer needed.");
		}
	}

	private SyncValueObject getSyncResponse() {
		return syncResponse;
	}

	private SyncValueObject getMergeResponse() {
		return mergeResponse;
	}

	private void resetSyncBarrier() {
		syncBarrier.reset();
		syncResponse = null;
	}

	private void sendRequest(AllMessage<SyncValueObject> message) {
		messEngine.send(message);
	}

	private void resetMergeBarrier() {
		mergeBarrier.reset();
		mergeResponse = null;
	}

	private void awaitSyncBarrier() {
		try {
			syncBarrier.await(REQUEST_TIMEOUT, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void releaseSyncBarrier() {
		try {
			syncBarrier.await();
		} catch (InterruptedException ignore) {
			log.warn("response no longer needed");
		} catch (BrokenBarrierException ignore) {
			log.warn("response no longer needed");
		}
	}

	private void awaitMergeBarrier(String email) {
		try {
			libraryOwner = email;
			mergeBarrier.await(REQUEST_TIMEOUT, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected exception awaiting merge barrier.", e);
		}
	}

	private void releaseMergeBarrier() {
		try {
			mergeBarrier.await();
		} catch (Exception ignore) {
			log.warn("Unexpected exception releasing merge barrier.");
		}
	}

}
