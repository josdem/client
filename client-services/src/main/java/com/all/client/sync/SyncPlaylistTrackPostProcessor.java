package com.all.client.sync;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.PlaylistTrack;
import com.all.shared.sync.ComplexSyncAble;
import com.all.shared.sync.ComplexSyncAblePostProcessor;
import com.all.shared.sync.SyncAble;

public final class SyncPlaylistTrackPostProcessor implements ComplexSyncAblePostProcessor {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final LocalModelDao dao;

	public SyncPlaylistTrackPostProcessor(LocalModelDao localModelDao) {
		this.dao = localModelDao;
	}

	@Override
	public void process(ComplexSyncAble postSyncAble, Map<String, Object> attributes,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities) {
		String playlistId = (String) attributes.get("playlist");
		if (StringUtils.isEmpty(playlistId)) {
			log.warn("Could not restore PlaylistTrack because playlist attribute is null or empty.");
			return;
		}
		Map<String, ? extends SyncAble> playlists = cachedEntities.get(LocalPlaylist.class);
		if (playlists == null) {
			log.warn("Could not restore PlaylistTrack because playlist instance was not found.");
			return;
		}
		SyncAble playlist = playlists.get(playlistId);
		if (playlist == null) {
			log.warn("Could not restore PlaylistTrack because playlist instance was not found.");
			return;
		}
		String trackId = (String) attributes.get("track");
		if (StringUtils.isEmpty(trackId)) {
			log.warn("Could not restore PlaylistTrack because track attribute was null or empty.");
			return;
		}

		LocalTrack track = getTrackFromListEntities(trackId, cachedEntities);
		if (track == null) {
			track = getTrackFromDB(trackId);
			if (track == null) {
				log.warn("Could not restore PlaylistTrack because track instance could not be found in cached entities nor the DB.");
				return;
			}
		}
		((PlaylistTrack) postSyncAble).setPlaylist(((LocalPlaylist) playlist));
		((PlaylistTrack) postSyncAble).setTrack(((LocalTrack) track));
	}

	private LocalTrack getTrackFromDB(String trackId) {
		return dao.findById(LocalTrack.class, trackId);
	}

	private LocalTrack getTrackFromListEntities(String trackId,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> entities) {
		Map<String, ? extends SyncAble> tracks = entities.get(LocalTrack.class);
		if (tracks == null) {
			return null;
		}

		return (LocalTrack) tracks.get(trackId);
	}

}
