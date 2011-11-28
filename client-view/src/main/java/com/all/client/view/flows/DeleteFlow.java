package com.all.client.view.flows;

import java.util.ArrayList;
import java.util.List;

import com.all.action.ResponseCallback;
import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.view.dialog.DeleteDialog;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dialog.DeleteDownloadsDialog.DeleteDownloadsAction;
import com.all.core.actions.Actions;
import com.all.core.actions.ModelDeleteAction;
import com.all.core.actions.ModelDeleteAction.DeleteMode;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public class DeleteFlow {
	private final ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	public DeleteFlow(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	public void deleteSelected() {
		List<?> list = viewEngine.get(Model.CLIPBOARD_SELECTION);
		ModelCollection model = new ModelCollection();
		List<Download> downloads = new ArrayList<Download>();
		for (Object object : list) {
			if (object instanceof Download) {
				downloads.add((Download) object);
			} else {
				model.add(object);
			}
		}
		if (!downloads.isEmpty()) {
			deleteDownloads(downloads);
		} else {
			TrackContainer trackContainer = viewEngine.get(Model.SELECTED_CONTAINER);
			delete(trackContainer, model);
		}
	}

	public void delete(ModelCollection model) {
		delete(viewEngine.get(Model.SELECTED_CONTAINER), model);
	}

	public void delete(TrackContainer container, ModelCollection model) {
		if (model.isEmpty() || model.isRemote()) {
			return;
		}
		boolean hasTracks = model.has(ModelTypes.tracks);
		DeleteMode mode = null;
		if (hasTracks) {
			if (isRootOrSmartPlaylist(container)) {
				if (dialogFactory.showConfirmationDeleteDialog(model)) {
					mode = DeleteMode.REF_AND_FILES;
				}
			} else {
				mode = validateDeleteMode(model, mode);
			}
		} else {
			mode = validateDeleteMode(model, mode);
		}
		if (mode != null) {
			doDelete(container, model, mode);
		}

	}

	private DeleteMode validateDeleteMode(ModelCollection model, DeleteMode mode) {
		int deleteAction = dialogFactory.showDeleteDialog(model);
		switch (deleteAction) {
		case DeleteDialog.DELETE_AND_FILES:
			if (dialogFactory.showConfirmationDeleteDialog(model)) {
				mode = DeleteMode.REF_AND_FILES;
			}
			break;
		case DeleteDialog.DELETE:
			mode = DeleteMode.ONLY_REFERENCES;
		}
		return mode;
	}

	private boolean isRootOrSmartPlaylist(TrackContainer container) {
		if (container instanceof Root) {
			return true;
		}
		if (container instanceof SmartPlaylist) {
			return true;
		}
		if (container instanceof Playlist) {
			return ((Playlist) container).isSmartPlaylist();
		}
		return false;
	}

	public void deleteDownloads(final List<Download> downloads) {
		final DeleteDownloadsAction action = dialogFactory.showDeleteDownloadsDialog();
		if (action == DeleteDownloadsAction.CANCEL) {
			return;
		}

		List<String> hashcodes = new ArrayList<String>();
		for (Download download : downloads) {
			hashcodes.add(download.getDownloadId());
		}

		viewEngine.request(Actions.Library.MODEL_FIND_TRACKS, hashcodes, new ResponseCallback<List<Track>>() {
			@Override
			public void onResponse(List<Track> tracks) {
				ModelCollection model = new ModelCollection(tracks);
				if (action == DeleteDownloadsAction.DELETE_ALL) {
					if (dialogFactory.showConfirmationDeleteDialog(model)) {
						viewEngine.send(Actions.Library.DELETE_FILES, new ValueAction<List<Track>>(tracks));
					} else {
						return; // user canceled operation
					}
				}
				viewEngine.sendValueAction(Actions.Downloads.DELETE_DOWNLOAD_COLLECTION, downloads);
			}
		});

	}

	public void deleteTracks(TrackContainer container, List<Track> selectedValues) {
		ModelCollection model = new ModelCollection();
		model.addList(selectedValues);
		delete(container, model);
	}

	private void doDelete(TrackContainer container, ModelCollection model, DeleteMode mode) {
		ModelDeleteAction action = new ModelDeleteAction(container, model, mode);
		viewEngine.send(Actions.Library.MODEL_DELETE, action);
	}
}
