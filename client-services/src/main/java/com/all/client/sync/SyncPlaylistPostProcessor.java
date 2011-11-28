package com.all.client.sync;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;
import com.all.shared.sync.ComplexSyncAble;
import com.all.shared.sync.SyncAble;
import com.all.shared.sync.ComplexSyncAblePostProcessor;

public final class SyncPlaylistPostProcessor implements ComplexSyncAblePostProcessor {

	private final Log log = LogFactory.getLog(this.getClass());

	private final LocalModelDao dao;

	public SyncPlaylistPostProcessor(LocalModelDao localModelDao) {
		this.dao = localModelDao;
	}

	@Override
	public void process(ComplexSyncAble postSyncAble, Map<String, Object> attributes,
			Map<Class<? extends SyncAble>, Map<String, ? extends SyncAble>> cachedEntities) {
		String folderId = (String) attributes.get("parentFolder");
		if (StringUtils.isEmpty(folderId)) {
			return;
		}
		Map<String, ? extends SyncAble> folders = cachedEntities.get(LocalFolder.class);
		LocalFolder folder = null;
		if (folders != null) {
			folder = (LocalFolder) folders.get(folderId);
			if (folder != null) {
				dao.refresh(folder);
			}
		} else {
			folder = dao.findById(LocalFolder.class, folderId);
		}

		if (folder == null) {
			log.warn("Could not find folder with id " + folderId + " for playlist " + postSyncAble
					+ ". Parent folder will be set to null.");
		} else {
			((LocalPlaylist) postSyncAble).setParentFolder(((LocalFolder) folder));
		}
	}
}
