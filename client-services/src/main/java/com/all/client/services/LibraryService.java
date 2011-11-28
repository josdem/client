package com.all.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalModelDao;
import com.all.core.actions.Actions;
import com.all.core.actions.ReorderPlaylistAction;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;

@Service
public class LibraryService {
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private LocalModelDao dao;

	@ActionMethod(Actions.Library.REORDER_PLAYLIST_ID)
	public void doReorderPlaylist(ReorderPlaylistAction action) {
		action.getPlaylist()._moveTracks(action.getTracks(), action.getRow());
		dao.update(action.getPlaylist());
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(action.getPlaylist()));
	}
}
