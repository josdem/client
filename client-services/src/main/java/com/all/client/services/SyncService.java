package com.all.client.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.ContactRoot;
import com.all.client.notifiers.MergeLibraryNotifier;
import com.all.client.notifiers.MergeLibraryNotifiersFactory;
import com.all.client.notifiers.MergeLibraryNotifier.State;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.sync.ContactLibraryContext;
import com.all.client.sync.SyncDaoInterceptor;
import com.all.client.sync.SyncHelper;
import com.all.client.sync.SyncMessages;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEventType;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Root;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.model.SyncValueObject;
import com.all.shared.model.User;
import com.all.shared.newsfeed.RemoteLibraryBrowsingFeed;
import com.all.shared.stats.FeedStat;
import com.all.shared.stats.usage.UserActions;

@Controller
public class SyncService {

	private static final int SYNC_DELAY = 1; // MIN
	private static final int MERGE_DELAY = 2; // Seconds
	private final Log log = LogFactory.getLog(this.getClass());

	private final ScheduledExecutorService syncExecutor = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("SyncController-SyncThread"));
	private final ScheduledExecutorService mergeExecutor = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("SyncController-MergeThread"));

	private final ContextLibraryContextFactory contactLibraryContextFactory = new ContextLibraryContextFactory();

	private final Set<String> mergeCancellations = new HashSet<String>();
	private final Map<String, MergeLibraryNotifier> mergeNotifiers = new HashMap<String, MergeLibraryNotifier>();

	@Autowired
	private SyncHelper syncHelper;
	@Autowired
	private SyncClientService syncClientService;
	@Autowired
	private ApplicationModelService applicationModelService;
	@Autowired
	private RemoteSeederTracksService remoteSeederTracksService;
	@Autowired
	private MergeLibraryNotifiersFactory notifierFactory;
	@Autowired
	private ClientReporter reporter;
	@Autowired
	private SyncDaoInterceptor daoNotifier;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private ContactCacheService contactCacheService;

	private AtomicBoolean syncEnabled = new AtomicBoolean(true);
	private List<String> pauseReasons = new ArrayList<String>();
	private Runnable syncTask = new SyncTask();
	private Runnable mergeTask = new MergeTask();
	private User currentUser;
	private boolean forceSnapshot = false;
	private Set<String> synchinglibs = new HashSet<String>();

	@MessageMethod(MessEngineConstants.PAUSE_SYNC)
	public void onPauseMessage(AllMessage<String> message) {
		pauseSync(message.getBody());
	}

	@MessageMethod(MessEngineConstants.RESUME_SYNC)
	public void onResumeMessage(AllMessage<String> message) {
		tryResumeSync(message.getBody());
	}

	@MessageMethod(MessEngineConstants.FORCE_SYNC_SNAPSHOT)
	public void onForceSnapshotMessage(AllMessage<String> message) {
		forceSnapshot = true;
		syncExecutor.execute(syncTask);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void startService() {
		currentUser = controlEngine.get(Model.CURRENT_USER);
		syncExecutor.scheduleWithFixedDelay(syncTask, SYNC_DELAY, SYNC_DELAY, TimeUnit.MINUTES);
		mergeExecutor.schedule(mergeTask, MERGE_DELAY, TimeUnit.SECONDS);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void stopService() {
		try {
			mergeExecutor.shutdownNow();
			syncExecutor.execute(syncTask);
			syncExecutor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("SyncExecutor was abruptly interrupted.", e);
		}
	}

	@RequestMethod(Actions.Library.IS_SYNCH_DOWNLOAD_ID)
	public boolean isDownloadingSynch(String email) {
		return synchinglibs.contains(email);
	}

	@PreDestroy
	public void shutdown() {
		mergeExecutor.shutdownNow();
		syncExecutor.shutdownNow();
	}

	private void sync() {
		if (hasValidSession() && isSyncEnabled()) {
			pauseSync(SyncMessages.SENDING_DELTAS);
			try {
				controlEngine.fireEvent(Events.Library.SYNC_UPLOAD_EVENT, new ValueEvent<LibrarySyncEventType>(
						LibrarySyncEventType.SYNC_STARTED));
				processSyncEvents();
			} catch (Exception e) {
				log.error("Unexpected error during sync.", e);
				controlEngine.fireEvent(Events.Errors.SYNC_UPLOAD_FAILED);
			} finally {
				controlEngine.fireEvent(Events.Library.SYNC_UPLOAD_EVENT, new ValueEvent<LibrarySyncEventType>(
						LibrarySyncEventType.SYNC_FINISHED));
				reporter.log(syncHelper.getLibraryOverview(currentUser.getEmail()));
			}

			tryResumeSync(SyncMessages.SENDING_DELTAS);
		}
	}

	@ActionMethod(Actions.Library.CANCEL_SYNC_DOWNLOAD_ID)
	public void cancelLibraryLoad(String libraryOwner) {
		mergeCancellations.add(libraryOwner);
		if (mergeNotifiers.containsKey(libraryOwner)) {
			mergeNotifiers.get(libraryOwner).cancel();
		}
		syncClientService.cancelMergeRequest(libraryOwner);
	}

	private boolean isSyncEnabled() {
		synchronized (syncEnabled) {
			if (syncEnabled.get()) {
				return true;
			}
			log.info("Sync is not enabled due to the follwing reasons: " + pauseReasons);
			return false;
		}
	}

	private void pauseSync(String pauseEvent) {
		synchronized (syncEnabled) {
			syncEnabled.set(false);
			pauseReasons.add(pauseEvent);
			log.info("Sync Process has been paused by " + pauseEvent);
		}
	}

	private synchronized void tryResumeSync(String resumeEvent) {
		synchronized (syncEnabled) {
			if (pauseReasons.remove(resumeEvent) && pauseReasons.isEmpty()) {
				syncEnabled.set(true);
				log.info("Sync process has been resumed.");
				if (resumeEvent.compareTo(SyncMessages.IMPORT_LIBRARY) == 0) {
					syncExecutor.execute(syncTask);
				}
			}
		}
	}

	private void processSyncEvents() {
		List<SyncEventEntity> events = syncHelper.loadEvents();
		if (!events.isEmpty()) {
			log.info("Found " + events.size() + " events to sync.");
			if (forceSnapshot || syncHelper.isSnapshotRequired(events)) {
				log.info("A new snapshot will be created...");
				forceSnapshot = false;
				syncNewSnapshot();
			} else {
				syncNewDelta(events);
			}
		} else {
			log.info("There is nothing to sync.");
		}
	}

	private boolean hasValidSession() {
		if (controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
			if (currentUser == null) {
				log.warn("Trying to sync without a user session. Sync will be skipped.");
			}
			return currentUser != null;
		}
		log.warn("Trying to sync without an internet connection. Sync will be skipped.");
		return false;
	}

	private void syncNewSnapshot() {
		sync(syncHelper.createNewSnapshot(currentUser.getEmail()));
	}

	private void syncNewDelta(List<SyncEventEntity> events) {
		sync(syncHelper.createNewDelta(currentUser.getEmail(), events));
	}

	private void sync(SyncValueObject syncRequest) {
		SyncValueObject syncResponse = syncClientService.sync(syncRequest);
		if (syncResponse == null) {
			throw new IllegalArgumentException("Null response received from syncService.");
		}
		syncHelper.updateSyncStatus(syncRequest, syncResponse);
	}

	private void merge() {
		MergeLibraryNotifier mergeNotifier = notifierFactory.newLocalLibraryNotifier(new ContactInfo(currentUser));
		try {
			if (!hasValidSession() || !isSyncEnabled()) {
				return;
			}
			pauseSync(SyncMessages.DOING_MERGE);
			daoNotifier.setEnabled(false);
			mergeNotifier.notifyMergeLibraryStarted();
			SyncValueObject mergeRequest = syncHelper.createMergeRequest(currentUser.getEmail());
			mergeRequest.addProgressListener(mergeNotifier);
			SyncValueObject mergeResponse = syncClientService.merge(mergeRequest);
			if (!isEmpty(mergeResponse)) {
				mergeNotifier
						.changeState(syncHelper.isNewSnapshot(mergeResponse) ? State.SAVING_SNAPSHOT : State.SAVING_DELTAS);
				long time_to_sync = System.currentTimeMillis();
				log.info("Updating from " + mergeRequest + " to  " + mergeResponse);
				syncHelper.mergeLibrary(mergeRequest, mergeResponse, currentUser.getId(), mergeNotifier);
				log.info("TOTAL SECONDS IN SYNC PROCESS : " + (System.currentTimeMillis() - time_to_sync) / 1000);
			}
		} catch (Exception e) {
			log.error("Unexpected error during merge.", e);
			mergeNotifier.notifyError();
		} finally {
			daoNotifier.setEnabled(true);
			Root root = controlEngine.get(Model.USER_ROOT);
			mergeNotifier.notifyMergeLibraryDone(root);
			reporter.log(syncHelper.getLibraryOverview(currentUser.getEmail()));
		}
		tryResumeSync(SyncMessages.DOING_MERGE);
	}

	private boolean isEmpty(SyncValueObject response) {
		if (response == null || response.getEvents().isEmpty()) {
			log.info("Response is null or empty. Library is up to date or library no longer needed.");
			return true;
		}
		return false;
	}

	@ActionMethod(Actions.Library.LOAD_CONTACT_LIBRARY_ID)
	@RequestMethod(Actions.Library.LOAD_CONTACT_LIBRARY_REQUEST_ID)
	public Void loadContactLibrary(LoadContactLibraryAction action) {
		ContactInfo contact = contactCacheService.findContactByEmail(action.getMail());
		if (contact != null) {
			if (!contact.isPending() && !controlEngine.get(Model.CURRENT_USER).getEmail().equals(contact.getEmail())) {
				loadContactLibrary(contact, action.isForceReload());
			}
		}
		return null;
	}

	private synchronized void loadContactLibrary(final ContactInfo contact, boolean reload) {
		String email = contact.getEmail();
		if (!reload) {
			boolean success = applicationModelService.addRootFromCache(email);
			if (success) {
				remoteSeederTracksService.requestSeederAvailableTracks(email, currentUser.getEmail());
				return;
			}
		}
		synchinglibs.add(email);
		log.info("Will load library for: " + email);
		MergeLibraryNotifier remoteLibraryNotifier = notifierFactory.newRemoteLibraryNotifier(contact);
		mergeNotifiers.put(email, remoteLibraryNotifier);
		ContactLibraryContext contactLibraryContext = contactLibraryContextFactory.createContext(contact);
		contactLibraryContext.load();
		try {
			applicationModelService.addLibrary(contactLibraryContext.loadRoot());
			remoteLibraryNotifier.notifyMergeLibraryStarted();
			SyncValueObject mergeRequest = contactLibraryContext.getMergeRequest();
			mergeRequest.addProgressListener(remoteLibraryNotifier);
			remoteLibraryNotifier.changeState(State.DOWNLOADING);
			SyncValueObject mergeResponse = syncClientService.merge(mergeRequest);
			boolean cancelled = mergeCancellations.remove(email);
			if (cancelled) {
				log.info("Loading of " + email + "'s library was canceled.");
				return;
			}
			if (!isEmpty(mergeResponse)) {
				SyncHelper syncHelper = contactLibraryContext.getSyncHelper();
				remoteLibraryNotifier.changeState(syncHelper.isNewSnapshot(mergeResponse) ? State.SAVING_SNAPSHOT
						: State.SAVING_DELTAS);
				syncHelper.mergeLibrary(mergeRequest, mergeResponse, contact.getId(), remoteLibraryNotifier);
				reporter.logUserAction(UserActions.AllNetwork.BROWSE_REMOTE_LIBRARY);
				reporter.log(new FeedStat(new RemoteLibraryBrowsingFeed(new ContactInfo(currentUser), contact)));
			}
			remoteSeederTracksService.requestSeederAvailableTracks(email, currentUser.getEmail());
		} catch (Exception e) {
			log.error("Unexpected exception loading contact library.", e);
			remoteLibraryNotifier.notifyError();
		} finally {
			synchinglibs.remove(email);
			ContactRoot loadedRoot = contactLibraryContext.loadRoot();
			remoteLibraryNotifier.notifyMergeLibraryDone(loadedRoot);
			applicationModelService.replaceRoot(loadedRoot);
			mergeCancellations.remove(email);
			mergeNotifiers.remove(email);
		}
		contactLibraryContext.close();
	}

	private final class SyncTask implements Runnable {
		@Override
		public void run() {
			try {
				sync();
			} catch (Exception e) {
				log.error("Unexpected error on sync thread.", e);
			}
		}
	}

	class ContextLibraryContextFactory {

		public ContactLibraryContext createContext(ContactInfo contact) {
			return new ContactLibraryContext(contact);
		}
	}

	private final class MergeTask implements Runnable {
		@Override
		public void run() {
			try {
				merge();
			} catch (Exception e) {
				log.error("Unexpected error on merge thread.", e);
			}
		}
	}

}
