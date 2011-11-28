package com.all.client.view.observs;

import java.util.EventObject;
import java.util.HashMap;

import com.all.shared.model.Folder;
import com.all.shared.model.MusicEntity;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class ModelBatchEvent extends EventObject {

	private static final long serialVersionUID = -5976914952003830208L;

	public enum DomainEntities {
		TRACK,
		PLAYLIST,
		FOLDER
	}

	private HashMap<Object, Class<? extends MusicEntity>> domainEntityMap;

	public ModelBatchEvent(Object source, Class<? extends MusicEntity>... domainEntities) {
		super(source);
		domainEntityMap = new HashMap<Object, Class<? extends MusicEntity>>();
		for (Class<? extends MusicEntity> domainEntityClass : domainEntities) {
			if (domainEntityClass == Track.class) {
				domainEntityMap.put(DomainEntities.TRACK, domainEntityClass);
			}
			if (domainEntityClass == Playlist.class) {
				domainEntityMap.put(DomainEntities.PLAYLIST, domainEntityClass);
			}
			if (domainEntityClass == Folder.class) {
				domainEntityMap.put(DomainEntities.FOLDER, domainEntityClass);
			}

		}
	}

	public boolean isTrack() {
		if (domainEntityMap.get(DomainEntities.TRACK) != null) {
			return true;
		}
		return false;
	}

	public boolean isPlaylist() {
		if (domainEntityMap.get(DomainEntities.PLAYLIST) != null) {
			return true;
		}
		return false;
	}

	public boolean isFolder() {
		if (domainEntityMap.get(DomainEntities.FOLDER) != null) {
			return true;
		}
		return false;
	}

}
