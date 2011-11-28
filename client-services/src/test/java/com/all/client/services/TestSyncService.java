package com.all.client.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.all.appControl.control.TestEngine;
import com.all.client.model.ContactRoot;
import com.all.client.notifiers.MergeLibraryNotifier;
import com.all.client.notifiers.MergeLibraryNotifiersFactory;
import com.all.client.notifiers.MergeRemoteLibraryNotifier;
import com.all.client.services.SyncService.ContextLibraryContextFactory;
import com.all.client.sync.ContactLibraryContext;
import com.all.client.sync.SyncDaoInterceptor;
import com.all.client.sync.SyncHelper;
import com.all.client.sync.SyncMessages;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEventType;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Root;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.model.SyncValueObject;
import com.all.shared.model.User;

public class TestSyncService {

	@InjectMocks
	private SyncService syncController = new SyncService();
	@Spy
	private MessEngine messEngine = new StubMessEngine();
	@Mock
	private SyncHelper syncHelper;
	@Mock
	private SyncClientService syncService;
	@Mock
	private ApplicationModelService appState;
	@Mock
	private ScheduledExecutorService syncExecutor;
	@Mock
	private SyncDaoInterceptor daoNotifier;
	@Mock
	private ScheduledExecutorService mergeExecutor;
	@Captor
	private ArgumentCaptor<Runnable> syncTaskCaptor;
	@Captor
	private ArgumentCaptor<Runnable> mergeTaskCaptor;
	@Mock
	private ContextLibraryContextFactory contextFactory;
	@Mock
	private MergeLibraryNotifiersFactory notifiersFactory;
	@Spy
	private TestEngine engine = new TestEngine();
	@Mock
	private MergeRemoteLibraryNotifier remoteNotifier;
	@Mock
	private MergeLibraryNotifier localNotifier;
	@Mock
	private ContactCacheService contactCacheService;

	private String email = "user@all.com";

	private String contactEmail = "contact@all.com";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		User user = new User();
		user.setEmail(email);
		when(engine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(true);
		when(engine.get(Model.CURRENT_USER)).thenReturn(user);

		engine.setup(syncController);
		((StubMessEngine) messEngine).setup(syncController);
		syncController.startService();

		verify(syncExecutor).scheduleWithFixedDelay(syncTaskCaptor.capture(), anyLong(), anyLong(), isA(TimeUnit.class));
		verify(mergeExecutor).schedule(mergeTaskCaptor.capture(), isA(Long.class), isA(TimeUnit.class));

		when(notifiersFactory.newLocalLibraryNotifier(any(ContactInfo.class))).thenReturn(localNotifier);
		when(notifiersFactory.newRemoteLibraryNotifier(any(ContactInfo.class))).thenReturn(remoteNotifier);
	}

	@Test
	public void shouldMergeUserLibraryAsynchronously() throws Exception {
		SyncValueObject mergeRequest = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
		when(syncHelper.createMergeRequest(email)).thenReturn(mergeRequest);
		SyncValueObject mergeResponse = new SyncValueObject(email, 0, 1, System.currentTimeMillis());
		mergeResponse.getEvents().add("Some sync event");
		when(syncService.merge(mergeRequest)).thenReturn(mergeResponse);
		Root root = mock(Root.class);
		when(engine.get(Model.USER_ROOT)).thenReturn(root);
		Runnable mergeTask = mergeTaskCaptor.getValue();
		when(notifiersFactory.newLocalLibraryNotifier(isA(ContactInfo.class))).thenReturn(localNotifier);

		mergeTask.run();

		verify(daoNotifier).setEnabled(false);
		verify(localNotifier).notifyMergeLibraryStarted();
		verify(syncHelper).mergeLibrary(mergeRequest, mergeResponse, null, localNotifier);
		verify(daoNotifier).setEnabled(true);
		verify(localNotifier).notifyMergeLibraryDone(root);
	}

	@Test
	public void shouldShowErrorDialogIfUnexpectedExceptionFromService() throws Exception {
		SyncValueObject mergeRequest = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
		when(syncHelper.createMergeRequest(email)).thenReturn(mergeRequest);
		SyncValueObject mergeResponse = new SyncValueObject(email, 0, 1, System.currentTimeMillis());
		mergeResponse.getEvents().add("Some sync event");
		when(syncService.merge(mergeRequest)).thenThrow(new IllegalStateException("For example, server down."));
		Root root = mock(Root.class);
		when(engine.get(Model.USER_ROOT)).thenReturn(root);
		Runnable mergeTask = mergeTaskCaptor.getValue();

		mergeTask.run();

		verify(daoNotifier).setEnabled(false);
		verify(localNotifier).notifyMergeLibraryStarted();
		verify(localNotifier).notifyError();
		verify(daoNotifier).setEnabled(true);
		verify(localNotifier).notifyMergeLibraryDone(root);
	}

	@Test
	public void shouldNotMergeIfLibraryIsUpToDate() throws Exception {
		MergeLibraryNotifier mergeNotifier = Mockito.mock(MergeLibraryNotifier.class);
		SyncValueObject mergeRequest = new SyncValueObject(email, 1, 1, System.currentTimeMillis());
		when(syncHelper.createMergeRequest(email)).thenReturn(mergeRequest);
		SyncValueObject mergeResponse = new SyncValueObject(email, 1, 1, System.currentTimeMillis());
		when(syncService.merge(mergeRequest)).thenReturn(mergeResponse);
		Root root = mock(Root.class);
		when(engine.get(Model.USER_ROOT)).thenReturn(root);
		Runnable mergeTask = mergeTaskCaptor.getValue();

		mergeTask.run();

		verify(daoNotifier).setEnabled(false);
		verify(localNotifier).notifyMergeLibraryStarted();
		verify(syncHelper, never()).mergeLibrary(mergeRequest, mergeResponse, null, mergeNotifier);
		verify(daoNotifier).setEnabled(true);
		verify(localNotifier).notifyMergeLibraryDone(root);
	}

	@Test
	public void shouldCloseUserSession() throws Exception {
		syncController.stopService();

		verify(mergeExecutor).shutdownNow();
		verify(syncExecutor).execute(syncTaskCaptor.getValue());
		verify(syncExecutor).awaitTermination(anyLong(), any(TimeUnit.class));

	}

	@Test
	public void shouldShutDown() throws Exception {
		syncController.shutdown();

		verify(syncExecutor).shutdownNow();
		verify(mergeExecutor).shutdownNow();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldLoadContactLibrary() throws Exception {
		ContactInfo contact = new ContactInfo();
		contact.setEmail(contactEmail);
		String nickname = "some nickname";
		contact.setNickName(nickname);
		ContactLibraryContext context = mock(ContactLibraryContext.class);
		when(contextFactory.createContext(contact)).thenReturn(context);
		ContactRoot contactRoot = mock(ContactRoot.class);
		when(context.loadRoot()).thenReturn(contactRoot);
		SyncValueObject mergeRequest = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
		when(context.getMergeRequest()).thenReturn(mergeRequest);
		SyncValueObject mergeResponse = new SyncValueObject(email, 0, 1, System.currentTimeMillis());
		mergeResponse.getEvents().add("Some sync event");
		when(syncService.merge(mergeRequest)).thenReturn(mergeResponse);
		when(context.getSyncHelper()).thenReturn(syncHelper);
		when(notifiersFactory.newLocalLibraryNotifier(isA(ContactInfo.class))).thenReturn(remoteNotifier);
		when(contactCacheService.findContactByEmail(email)).thenReturn(contact);
		syncController.loadContactLibrary(LoadContactLibraryAction.load(email));

		verify(context).load();
		verify(remoteNotifier).notifyMergeLibraryStarted();
		verify(appState).addLibrary(contactRoot);
		verify(syncHelper).mergeLibrary(mergeRequest, mergeResponse, null, remoteNotifier);
		verify(remoteNotifier).notifyMergeLibraryDone(contactRoot);
		verify(context).close();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void shouldNotLoadUserLibrary() throws Exception {
		ContactInfo contact = new ContactInfo();
		contact.setEmail(email);
		String nickname = "some nickname";
		contact.setNickName(nickname);
		ContactLibraryContext context = mock(ContactLibraryContext.class);
		when(contextFactory.createContext(contact)).thenReturn(context);
		ContactRoot contactRoot = mock(ContactRoot.class);
		when(context.loadRoot()).thenReturn(contactRoot);
		SyncValueObject mergeRequest = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
		when(context.getMergeRequest()).thenReturn(mergeRequest);
		SyncValueObject mergeResponse = new SyncValueObject(email, 0, 1, System.currentTimeMillis());
		mergeResponse.getEvents().add("Some sync event");
		when(syncService.merge(mergeRequest)).thenReturn(mergeResponse);
		when(context.getSyncHelper()).thenReturn(syncHelper);
		when(notifiersFactory.newLocalLibraryNotifier(isA(ContactInfo.class))).thenReturn(remoteNotifier);
		when(contactCacheService.findContactByEmail(email)).thenReturn(contact);
		syncController.loadContactLibrary(LoadContactLibraryAction.load(email));

		verify(context, never()).load();
		verify(remoteNotifier, never()).notifyMergeLibraryStarted();
		verify(appState, never()).addLibrary(contactRoot);
		verify(syncHelper, never()).mergeLibrary(mergeRequest, mergeResponse, null, remoteNotifier);
		verify(remoteNotifier, never()).notifyMergeLibraryDone(contactRoot);
		verify(context, never()).close();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldShowErrorDialogIfCannotLoadContactLibrary() throws Exception {
		ContactInfo contact = new ContactInfo();
		contact.setEmail(contactEmail);
		String nickname = "some nickname";
		contact.setNickName(nickname);
		ContactLibraryContext context = mock(ContactLibraryContext.class);
		when(contextFactory.createContext(contact)).thenReturn(context);
		ContactRoot contactRoot = mock(ContactRoot.class);
		when(context.loadRoot()).thenReturn(contactRoot);
		SyncValueObject mergeRequest = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
		when(context.getMergeRequest()).thenReturn(mergeRequest);
		SyncValueObject mergeResponse = new SyncValueObject(email, 0, 1, System.currentTimeMillis());
		mergeResponse.getEvents().add("Some sync event");
		when(syncService.merge(mergeRequest)).thenThrow(new IllegalStateException("For example, server down"));
		when(context.getSyncHelper()).thenReturn(syncHelper);

		when(contactCacheService.findContactByEmail(email)).thenReturn(contact);

		syncController.loadContactLibrary(LoadContactLibraryAction.load(email));

		verify(context).load();
		verify(remoteNotifier).notifyMergeLibraryStarted();
		verify(appState).addLibrary(contactRoot);
		verify(remoteNotifier).notifyError();
		verify(remoteNotifier).notifyMergeLibraryDone(contactRoot);
		verify(context).close();
	}

	@Test
	public void shouldNotSyncIfNotOnline() throws Exception {
		when(engine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(false);
		Runnable syncTask = syncTaskCaptor.getValue();

		syncTask.run();

		// verify(engine, never()).fireEvent(eq(Events.Library.SYNC_UPLOAD_EVENT),
		// any(ValueEvent.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSyncNewSnapshotAsynchronously() throws Exception {
		Runnable syncTask = syncTaskCaptor.getValue();
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>();
		events.add(new SyncEventEntity());
		when(syncHelper.isSnapshotRequired(events)).thenReturn(true);
		SyncValueObject snapshotObj = new SyncValueObject(email, 2, 0, System.currentTimeMillis());
		when(syncHelper.createNewSnapshot(email)).thenReturn(snapshotObj);
		when(syncHelper.loadEvents()).thenReturn(events);
		SyncValueObject syncResponse = new SyncValueObject(email, 2, 0, System.currentTimeMillis() + 100);
		when(syncService.sync(snapshotObj)).thenReturn(syncResponse);

		syncTask.run();

		verify(engine).fireEvent(Events.Library.SYNC_UPLOAD_EVENT,
				new ValueEvent<LibrarySyncEventType>(LibrarySyncEventType.SYNC_STARTED));
		verify(syncHelper).updateSyncStatus(snapshotObj, syncResponse);
		verify(engine).fireEvent(Events.Library.SYNC_UPLOAD_EVENT,
				new ValueEvent<LibrarySyncEventType>(LibrarySyncEventType.SYNC_FINISHED));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSyncNewDeltaAsynchronously() throws Exception {
		Runnable syncTask = syncTaskCaptor.getValue();
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>();
		events.add(new SyncEventEntity());
		when(syncHelper.isSnapshotRequired(events)).thenReturn(false);
		SyncValueObject deltaObj = new SyncValueObject(email, 2, 1, System.currentTimeMillis());
		when(syncHelper.createNewDelta(email, events)).thenReturn(deltaObj);
		when(syncHelper.loadEvents()).thenReturn(events);
		SyncValueObject syncResponse = new SyncValueObject(email, 2, 2, System.currentTimeMillis());
		when(syncService.sync(deltaObj)).thenReturn(syncResponse);

		syncTask.run();

		verify(engine).fireEvent(Events.Library.SYNC_UPLOAD_EVENT,
				new ValueEvent<LibrarySyncEventType>(LibrarySyncEventType.SYNC_STARTED));
		verify(syncHelper).updateSyncStatus(deltaObj, syncResponse);
		verify(engine).fireEvent(Events.Library.SYNC_UPLOAD_EVENT,
				new ValueEvent<LibrarySyncEventType>(LibrarySyncEventType.SYNC_FINISHED));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldShowErrorDialogIfCannotSyncChange() throws Exception {
		Runnable syncTask = syncTaskCaptor.getValue();
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>();
		events.add(new SyncEventEntity());
		when(syncHelper.isSnapshotRequired(events)).thenReturn(false);
		SyncValueObject deltaObj = new SyncValueObject(email, 2, 1, System.currentTimeMillis());
		when(syncHelper.createNewDelta(email, events)).thenReturn(deltaObj);
		when(syncHelper.loadEvents()).thenReturn(events);
		when(syncService.sync(deltaObj)).thenReturn(null);

		syncTask.run();

		verify(engine).fireEvent(Events.Library.SYNC_UPLOAD_EVENT,
				new ValueEvent<LibrarySyncEventType>(LibrarySyncEventType.SYNC_STARTED));
		verify(syncHelper, never()).updateSyncStatus(any(SyncValueObject.class), any(SyncValueObject.class));
		verify(engine).fireEvent(Events.Errors.SYNC_UPLOAD_FAILED);
		verify(engine).fireEvent(Events.Library.SYNC_UPLOAD_EVENT,
				new ValueEvent<LibrarySyncEventType>(LibrarySyncEventType.SYNC_FINISHED));
	}

	@Test
	public void shouldNotRunSyncProcessWhileMergingLibrary() throws Exception {
		when(notifiersFactory.newLocalLibraryNotifier(isA(ContactInfo.class))).thenReturn(localNotifier);
		Runnable syncTask = syncTaskCaptor.getValue();
		SyncValueObject mergeRequest = new SyncValueObject(email, 0, 0, System.currentTimeMillis());
		when(syncHelper.createMergeRequest(email)).thenReturn(mergeRequest);
		final SyncValueObject mergeResponse = new SyncValueObject(email, 0, 1, System.currentTimeMillis());
		mergeResponse.getEvents().add("Some sync event");
		when(syncService.merge(mergeRequest)).thenAnswer(new Answer<SyncValueObject>() {
			@Override
			public SyncValueObject answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(50);
				return mergeResponse;
			}
		});
		Root root = mock(Root.class);
		when(engine.get(Model.USER_ROOT)).thenReturn(root);
		Runnable mergeTask = mergeTaskCaptor.getValue();

		new Thread(mergeTask).start();
		Thread.sleep(10);
		new Thread(syncTask).start();

		verify(daoNotifier, timeout(500)).setEnabled(false);
		verify(localNotifier, timeout(500)).notifyMergeLibraryStarted();
		verify(syncHelper, timeout(100)).mergeLibrary(mergeRequest, mergeResponse, null, localNotifier);
		verify(daoNotifier, timeout(100)).setEnabled(true);
		verify(localNotifier, timeout(100)).notifyMergeLibraryDone(root);
		verify(syncHelper, timeout(100).never()).loadEvents();

	}

	@Test
	public void shouldNotSyncWhileImportingALibrary() throws Exception {
		Runnable syncTask = syncTaskCaptor.getValue();
		messEngine.send(new AllMessage<String>(MessEngineConstants.PAUSE_SYNC, SyncMessages.IMPORT_LIBRARY));

		syncTask.run();

		verify(syncHelper, never()).loadEvents();

		messEngine.send(new AllMessage<String>(MessEngineConstants.RESUME_SYNC, SyncMessages.IMPORT_LIBRARY));

		syncTask.run();
		verify(syncHelper).loadEvents();
	}

}
