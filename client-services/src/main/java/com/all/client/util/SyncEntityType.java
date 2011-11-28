package com.all.client.util;

import com.all.client.model.BugPatch;
import com.all.client.model.ContactFolder;
import com.all.client.model.ContactUserFolder;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.PlaylistTrack;
import com.all.shared.model.ContactInfo;
import com.all.shared.sync.SyncAble;

public enum SyncEntityType {
	LocalFolder(LocalFolder.class), LocalTrack(LocalTrack.class), LocalPlaylist(LocalPlaylist.class), ContactFolder(
			ContactFolder.class), ContactInfo(ContactInfo.class), ContactUserFolder(ContactUserFolder.class), PlaylistTrack(
			PlaylistTrack.class), BugPatch(BugPatch.class);

	private Class<? extends SyncAble> clazz;

	private SyncEntityType(Class<? extends SyncAble> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends SyncAble> getClazz() {
		return clazz;
	}

	public static SyncEntityType getType(SyncAble object) {
		if (object instanceof LocalTrack) {
			return LocalTrack;
		} else if (object instanceof LocalPlaylist) {
			return LocalPlaylist;
		} else if (object instanceof LocalFolder) {
			return LocalFolder;
		} else if (object instanceof ContactFolder) {
			return ContactFolder;
		} else if (object instanceof ContactInfo) {
			return ContactInfo;
		} else if (object instanceof ContactUserFolder) {
			return ContactUserFolder;
		} else if (object instanceof PlaylistTrack) {
			return PlaylistTrack;
		} else if (object instanceof BugPatch) {
			return BugPatch;
		}
		return null;
	}
}
