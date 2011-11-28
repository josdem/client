package com.all.client.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.model.BugPatch;
import com.all.client.model.ContactFolder;
import com.all.client.model.ContactUserFolder;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.PlaylistTrack;
import com.all.client.notifiers.MergeLibraryNotifier;
import com.all.client.notifiers.MergeLibraryNotifier.State;
import com.all.client.services.UserPreferenceService;
import com.all.client.util.SyncEntityType;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.model.SyncValueObject;
import com.all.shared.model.SyncEventEntity.SyncOperation;
import com.all.shared.stats.UserLibraryOverview;
import com.all.shared.sync.ComplexSyncAble;
import com.all.shared.sync.ComplexSyncAblePostProcessor;
import com.all.shared.sync.SyncAble;
import com.all.shared.sync.SyncGenericConverter;
import com.all.shared.util.SyncUtils;

@Component
public class SyncHelper {

	private static final int MAX_DELTAS_SIZE_PER_SNAPSHOT = 512;

	private static final int MAX_DELTAS_PER_SNAPSHOT = 999;

	private final Log log = LogFactory.getLog(this.getClass());

	private final Map<Class<? extends ComplexSyncAble>, ComplexSyncAblePostProcessor> postProcessors = new HashMap<Class<? extends ComplexSyncAble>, ComplexSyncAblePostProcessor>();

	private UserPreferenceService userPreferenceService;

	private LocalModelDao dao;

	public List<SyncEventEntity> loadEvents() {
		return (List<SyncEventEntity>) dao.loadAll(SyncEventEntity.class);
	}

	public UserLibraryOverview getLibraryOverview(String email) {
		int tracks = (int) dao.count(LocalTrack.class);
		int pls = (int) dao.count(LocalPlaylist.class);
		int folders = (int) dao.count(LocalFolder.class);
		int contacts = (int) dao.count(ContactInfo.class);
		return new UserLibraryOverview(email, contacts, folders, pls, tracks);
	}

	@Autowired
	public void setUserPreferenceService(UserPreferenceService userPreferenceService) {
		this.userPreferenceService = userPreferenceService;
	}

	@Autowired
	public void setLocalModelDao(LocalModelDao localModelDao) {
		this.dao = localModelDao;
		addPostProcessors(localModelDao);
	}

	private void addPostProcessors(LocalModelDao dao) {
		postProcessors.put(PlaylistTrack.class, new SyncPlaylistTrackPostProcessor(dao));
		postProcessors.put(LocalPlaylist.class, new SyncPlaylistPostProcessor(dao));
	}

	public boolean isSnapshotRequired(List<SyncEventEntity> events) {
		double deltasSize = userPreferenceService.getDeltasTotalSize() + getCompressedSize(events);
		if (deltasSize > MAX_DELTAS_SIZE_PER_SNAPSHOT) {
			return true;
		}
		if (userPreferenceService.getCurrentDelta() >= MAX_DELTAS_PER_SNAPSHOT) {
			return true;
		}
		return false;
	}

	public SyncValueObject createNewSnapshot(String libraryOwner) {
		log.info("Creating new snapshot...");
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>();
		synchronized (dao) {
			dao.deleteAll(SyncEventEntity.class);
			events.addAll(toSyncEvents(dao.loadAll(LocalTrack.class)));
			events.addAll(toSyncEvents(dao.loadAll(LocalFolder.class)));
			events.addAll(toSyncEvents(dao.loadAll(LocalPlaylist.class)));
			events.addAll(toSyncEvents(dao.loadAll(PlaylistTrack.class)));
			events.addAll(toSyncEvents(dao.loadAll(ContactFolder.class)));
			events.addAll(toSyncEvents(dao.loadAll(ContactInfo.class)));
			events.addAll(toSyncEvents(dao.loadAll(ContactUserFolder.class)));
			events.addAll(toSyncEvents(dao.loadAll(BugPatch.class)));
		}

		SyncValueObject snapshot = new SyncValueObject(libraryOwner, userPreferenceService.getNextSnapshot(), 0, System.currentTimeMillis());
		String encodedEvents = SyncUtils.encodeAndZip(events);
		snapshot.getEvents().add(encodedEvents);
		return snapshot;
	}

	public SyncValueObject createMergeRequest(String libraryOwner) {
		return new SyncValueObject(libraryOwner, userPreferenceService.getCurrentSnapshot(), userPreferenceService.getCurrentDelta(),
				System.currentTimeMillis());
	}

	public SyncValueObject createNewDelta(String libraryOwner, List<SyncEventEntity> events) {
		log.info("Creating new delta...");
		SyncValueObject delta = new SyncValueObject(libraryOwner, userPreferenceService.getCurrentSnapshot(), userPreferenceService.getCurrentDelta(),
				System.currentTimeMillis());
		for (SyncEventEntity event : events) {
			event.setTimestamp(delta.getTimestamp());
			dao.update(event);
		}
		String encodedEvents = SyncUtils.encodeAndZip(events);
		delta.getEvents().add(encodedEvents);
		return delta;
	}

	public void updateSyncStatus(SyncValueObject syncRequest, SyncValueObject syncResponse) {
		userPreferenceService.updateSyncStatus(syncRequest, syncResponse);
		Object deleteSyncEventEntities = dao.deleteSyncEventEntities(syncRequest.getTimestamp());
		log.info("Total Event entities synced : " + deleteSyncEventEntities);
	}

	public synchronized void mergeLibrary(SyncValueObject mergeRequest, SyncValueObject mergeResponse, Long owner, MergeLibraryNotifier mergeNotifier) {
		Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities = new HashMap<Class<? extends SyncAble>, Map<String, ? extends SyncAble>>();
		if (isNewSnapshot(mergeResponse)) {
			log.info("Saving new snapshot...");
			String encodedSnapshot = mergeResponse.getEvents().remove(0);
			List<SyncEventEntity> snapshotEvents = SyncUtils.decodeAndUnzip(encodedSnapshot);
			clearLibraryBeforeApplyNewSnapshotEvents();
			processNewSnapshot(mergeRequest, snapshotEvents, cachedEntities);
		}
		mergeNotifier.changeState(State.SAVING_DELTAS);
		int numDeltas = mergeResponse.getEvents().size();
		log.info("Will process " + numDeltas + " deltas...");
		int deltaCount = 0;
		for (String encodedEvents : mergeResponse.getEvents()) {
			deltaCount++;
			List<SyncEventEntity> decodeAndUnzip = SyncUtils.decodeAndUnzip(encodedEvents);
			applySyncEventList(mergeResponse.getEmail(), decodeAndUnzip, cachedEntities);
			mergeRequest.notifyProgress((deltaCount * 100) / numDeltas);
		}

		log.info("Events were processed succesfully.");
		for (Class<? extends SyncAble> syncables : cachedEntities.keySet()) {
			cachedEntities.get(syncables).clear();
		}
		cachedEntities.clear();
		updateSyncStatus(mergeRequest, mergeResponse);
		processPreviousDeltaIfExist(mergeResponse.getEmail(), cachedEntities);
	}

	private void processNewSnapshot(SyncValueObject mergeRequest, List<SyncEventEntity> events,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities) {
		List<SyncAble> unsavedEntities = new ArrayList<SyncAble>();
		int savedEntities = 0;
		int totalEntities = events.size();
		for (SyncEventEntity syncEvent : events) {
			Map<String, Object> attributes = syncEvent.getEntity();
			SyncEntityType type = SyncEntityType.valueOf((String) attributes.get(SyncGenericConverter.ENTITY));
			SyncAble syncEntity = restoreSyncEntity(type.getClazz(), attributes, cachedEntities);
			if (syncEntity != null) {
				syncEntity.setSyncAble(false);
				unsavedEntities.add(syncEntity);
			}
			if (unsavedEntities.size() >= 1000) {
				dao.saveAll(unsavedEntities);
				savedEntities += unsavedEntities.size();
				unsavedEntities.clear();
				mergeRequest.notifyProgress((savedEntities * 100) / totalEntities);
			}
		}
		if (unsavedEntities.size() > 0) {
			log.info("Saving remaining " + unsavedEntities.size() + " entities.");
			dao.saveAll(unsavedEntities);
			savedEntities += unsavedEntities.size();
			mergeRequest.notifyProgress((savedEntities * 100) / totalEntities);
		}
		unsavedEntities.clear();
	}

	@SuppressWarnings("unchecked")
	private <T extends SyncAble> void cacheEntity(Class<? extends SyncAble> clazz, SyncAble syncableEntity,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities) {
		Map<String, SyncAble> map = (Map<String, SyncAble>) cachedEntities.get(clazz);
		if (map == null) {
			map = new HashMap<String, SyncAble>();
		}
		map.put(syncableEntity.getSyncAbleId(), syncableEntity);
		cachedEntities.put((Class<? extends SyncAble>) clazz, map);
	}

	private void processPreviousDeltaIfExist(String libraryOwner, Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> syncAblesEntities) {
		List<SyncEventEntity> events = (List<SyncEventEntity>) dao.loadAll(SyncEventEntity.class);
		applySyncEventList(libraryOwner, events, syncAblesEntities);
	}

	private void applySyncEventList(String libraryOwner, List<SyncEventEntity> events,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities) {
		for (SyncEventEntity syncEventEntity : events) {
			Map<String, Object> attributes = syncEventEntity.getEntity();
			SyncEntityType type = SyncEntityType.valueOf((String) attributes.get(SyncGenericConverter.ENTITY));
			SyncOperation action = syncEventEntity.getOperation();
			applySyncOperation(cachedEntities, attributes, type, action);
		}
	}

	private void applySyncOperation(Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities, Map<String, Object> attributes,
			SyncEntityType type, SyncOperation action) {
		try {
			switch (action) {
			case DELETE:
				applyDeleteEvent(attributes, type);
				break;
			case SAVE:
				applySaveEvent(restoreSyncEntity(type.getClazz(), attributes, cachedEntities));
				break;
			case UPDATE:
				applyUpdateEvent(restoreSyncEntity(type.getClazz(), attributes, cachedEntities));
				break;
			}
		} catch (Exception e) {
			log.error("Could not apply sync event of type " + action, e);
		}
	}

	private void applyUpdateEvent(SyncAble syncEntity) {
		SyncAble currentEntity = (SyncAble) dao.findById(syncEntity.getClass(), syncEntity.getSyncAbleId());
		if (currentEntity != null) {
			currentEntity.clone(syncEntity);
			currentEntity.setSyncAble(false);
			dao.merge(currentEntity);
			dao.evict(currentEntity);
		}
	}

	private void applySaveEvent(SyncAble syncEntity) {
		syncEntity.setSyncAble(false);
		// We used save or update here because we have some inconsistent
		// registers in data base and we need to prevent lose data
		dao.saveOrUpdate(syncEntity);
	}

	private void applyDeleteEvent(Map<String, Object> map, SyncEntityType type) {
		String id = (String) map.get(SyncGenericConverter.SYNC_HASHCODE);
		SyncAble sa = (SyncAble) dao.findById(type.getClazz(), id);
		if (sa != null) {
			sa.setSyncAble(false);
			dao.delete(sa);
		}
	}

	private <T extends SyncAble> SyncAble restoreSyncEntity(Class<T> clazz, Map<String, Object> attributes,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities) {
		T syncEntity = SyncGenericConverter.toBean(attributes, clazz);
		if (postProcessors.containsKey(clazz)) {
			postProcessors.get(clazz).process((ComplexSyncAble) syncEntity, attributes, cachedEntities);
		}
		syncEntity.setSyncAble(false);
		cacheEntity(clazz, syncEntity, cachedEntities);
		return syncEntity;
	}

	private void clearLibraryBeforeApplyNewSnapshotEvents() {
		// TODO CHANGE THIS IMPLEMENTATION TO IMPROVE PERFORMANCE.
		for (PlaylistTrack playlistTrack : dao.loadAll(PlaylistTrack.class)) {
			playlistTrack.setSyncAble(false);
			dao.delete(playlistTrack);
		}

		for (LocalTrack track : dao.loadAll(LocalTrack.class)) {
			track.setSyncAble(false);
			dao.delete(track);
		}

		for (LocalPlaylist playlist : dao.loadAll(LocalPlaylist.class)) {
			playlist.setSyncAble(false);
			dao.delete(playlist);
		}

		for (LocalFolder folder : dao.loadAll(LocalFolder.class)) {
			folder.setSyncAble(false);
			dao.delete(folder);
		}

		for (ContactUserFolder cuf : dao.loadAll(ContactUserFolder.class)) {
			cuf.setSyncAble(false);
			dao.delete(cuf);
		}

		for (ContactFolder cf : dao.loadAll(ContactFolder.class)) {
			cf.setSyncAble(false);
			dao.delete(cf);
		}

		for (ContactInfo ci : dao.loadAll(ContactInfo.class)) {
			ci.setSyncAble(false);
			dao.delete(ci);
		}
	}

	private SyncEventEntity toSyncEvent(SyncAble entity) {
		return new SyncEventEntity(SyncOperation.SAVE, SyncGenericConverter.toMap(entity, SyncOperation.SAVE));
	}

	private List<SyncEventEntity> toSyncEvents(List<? extends SyncAble> syncables) {
		List<SyncEventEntity> events = new ArrayList<SyncEventEntity>(syncables.size());
		for (SyncAble syncAble : syncables) {
			// TODO: Ask JC why this is required.
			if (syncAble instanceof PlaylistTrack) {
				PlaylistTrack playlistTrack = (PlaylistTrack) syncAble;
				if (playlistTrack.getTrack() == null || playlistTrack.getPlaylist() == null) {
					continue;
				}
			}
			events.add(toSyncEvent(syncAble));
		}
		return events;
	}

	private double getCompressedSize(List<SyncEventEntity> events) {
		return SyncUtils.encodeAndZip(events).getBytes().length / 1024;
	}


	public boolean isNewSnapshot(SyncValueObject mergeResponse) {
		return userPreferenceService.isNewSnapshot(mergeResponse);
	}

}
