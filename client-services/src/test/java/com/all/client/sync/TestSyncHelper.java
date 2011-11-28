package com.all.client.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.model.ContactFolder;
import com.all.client.model.ContactUserFolder;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.PlaylistTrack;
import com.all.client.services.UserPreferenceService;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.model.SyncValueObject;

public class TestSyncHelper {

	private static final int MAX_DELTAS_PER_SNAPSHOT = 999;
	private static final double MAX_DELTA_SIZE_PER_SNAPSHOT = 512.0;
	private SyncHelper syncHelper = new SyncHelper();
	@Mock
	private UserPreferenceService userPreferenceService;
	@Mock
	private LocalModelDao dao;
	private String email = "user@all.com";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		syncHelper.setUserPreferenceService(userPreferenceService);
		syncHelper.setLocalModelDao(dao);
	}

	@Test
	public void shouldLoadEvents() throws Exception {
		syncHelper.loadEvents();
		verify(dao).loadAll(SyncEventEntity.class);
	}

	@Test
	public void shouldKnowIfSnapshotRequiredDependingOnAccumulatedSize() throws Exception {
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>();
		when(userPreferenceService.getDeltasTotalSize()).thenReturn(0.0);
		assertFalse(syncHelper.isSnapshotRequired(events));
		when(userPreferenceService.getDeltasTotalSize()).thenReturn(MAX_DELTA_SIZE_PER_SNAPSHOT + 1);
		assertTrue(syncHelper.isSnapshotRequired(events));
	}

	@Test
	public void shouldKnowIfSnapshotRequiredDependingOnTotalNumberOfDeltas() throws Exception {
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>();
		when(userPreferenceService.getDeltasTotalSize()).thenReturn(0.0);
		when(userPreferenceService.getCurrentDelta()).thenReturn(0);
		assertFalse(syncHelper.isSnapshotRequired(events));
		when(userPreferenceService.getCurrentDelta()).thenReturn(MAX_DELTAS_PER_SNAPSHOT);
		assertTrue(syncHelper.isSnapshotRequired(events));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCreateNewSnapshot() throws Exception {
		when(dao.loadAll(any(Class.class))).thenReturn(Collections.emptyList());
		int currentSnapshot = 1;
		when(userPreferenceService.getCurrentSnapshot()).thenReturn(currentSnapshot);
		int nextSnapshot = currentSnapshot + 1;
		when(userPreferenceService.getNextSnapshot()).thenReturn(nextSnapshot);
		SyncValueObject snapshot = syncHelper.createNewSnapshot(email);

		verify(dao).loadAll(LocalTrack.class);
		verify(dao).loadAll(LocalFolder.class);
		verify(dao).loadAll(LocalPlaylist.class);
		verify(dao).loadAll(PlaylistTrack.class);
		verify(dao).loadAll(ContactFolder.class);
		verify(dao).loadAll(ContactInfo.class);
		verify(dao).loadAll(ContactUserFolder.class);

		assertEquals(email, snapshot.getEmail());
		assertEquals(1, snapshot.getEvents().size());
		assertEquals(nextSnapshot, snapshot.getSnapshot());
		assertEquals(0, snapshot.getDelta());
	}

	@Test
	public void shouldCreateMergeRequest() throws Exception {
		int snapshot = 1;
		when(userPreferenceService.getCurrentSnapshot()).thenReturn(snapshot);
		int delta = 20;
		when(userPreferenceService.getCurrentDelta()).thenReturn(delta);

		SyncValueObject mergeRequest = syncHelper.createMergeRequest(email);
		assertEquals(email, mergeRequest.getEmail());
		assertEquals(snapshot, mergeRequest.getSnapshot());
		assertEquals(delta, mergeRequest.getDelta());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldCreateNewDelta() throws Exception {
		int snapshot = 1;
		when(userPreferenceService.getCurrentSnapshot()).thenReturn(snapshot);
		int deltaId = 20;
		when(userPreferenceService.getCurrentDelta()).thenReturn(deltaId);
		SyncEventEntity eventA = new SyncEventEntity();
		SyncEventEntity eventB = new SyncEventEntity();
		List<SyncEventEntity> events = Arrays.asList(eventA, eventB);
		assertNull(eventA.getTimestamp());
		assertNull(eventB.getTimestamp());

		SyncValueObject delta = syncHelper.createNewDelta(email, events);

		assertNotNull(eventA.getTimestamp());
		assertNotNull(eventB.getTimestamp());
		assertEquals(email, delta.getEmail());
		assertEquals(snapshot, delta.getSnapshot());
		assertEquals(deltaId, delta.getDelta());
		assertEquals(1, delta.getEvents().size());
	}

	@Test
	public void shouldUpdateSyncStatus() throws Exception {
		int currentSnapshot = 1;
		when(userPreferenceService.getCurrentSnapshot()).thenReturn(currentSnapshot);
		long timestamp = System.currentTimeMillis();
		String encodedSnapshot = syncHelper.createNewSnapshot(email).getEvents().get(0);
		SyncValueObject syncRequest = new SyncValueObject(email, currentSnapshot, 2, timestamp);
		syncRequest.getEvents().add(encodedSnapshot);
		SyncValueObject syncResponse = new SyncValueObject(email, currentSnapshot + 1, 2, timestamp);

		syncHelper.updateSyncStatus(syncRequest, syncResponse);

		verify(userPreferenceService).updateSyncStatus(syncRequest, syncResponse);
		verify(dao).deleteSyncEventEntities(timestamp);
	}
	

}
