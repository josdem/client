package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.ContactRoot;
import com.all.client.model.ModelTransfereable;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemotePlaylist;
import com.all.shared.model.SmartPlaylist;

public class ContactSmartPlaylistTreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;
	private final JTree tree;
	private final boolean remoteSource;
	private final ContactRoot contact;
	private final ViewEngine viewEngine;

	public ContactSmartPlaylistTreeTransferHandler(ContactRoot contact, JTree tree, boolean remoteSource,
			ViewEngine viewEngine) {
		this.contact = contact;
		this.tree = tree;
		this.remoteSource = remoteSource;
		this.viewEngine = viewEngine;
	}

	@Override
	public Transferable createTransferable(JComponent c) {
		java.util.List<Object> selection = new ArrayList<Object>();
		for (TreePath path : tree.getSelectionPaths()) {
			Object model = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			if (isSmartPlaylistDraggable(model)) {
				Playlist smartPlaylistPlaylist = ((SmartPlaylist) model).getPlaylist();
				RemotePlaylist playlist = new RemotePlaylist(smartPlaylistPlaylist);
				playlist.setName(contact.getOwner().getNickName() + " " + playlist.getName());
				playlist.setTracks(smartPlaylistPlaylist.getTracks());
				model = playlist;
			}
			if (!(model instanceof SmartPlaylist)) {
				selection.add(model);
			}
		}
		ModelCollection modelCollection = new ModelCollection(selection);
		modelCollection.cleanUp();
		if (remoteSource) {
			modelCollection.setRemote(true);
		}
		if (modelCollection.isEmpty()) {
			return null;
		} else {
			return new ModelTransfereable(ModelSource.remote(), modelCollection, viewEngine.get(Model.TRACK_REPOSITORY));
		}
	}

	private boolean isSmartPlaylistDraggable(Object model) {
		if (!(model instanceof SmartPlaylist)) {
			return false;
		}
		if (contact.getAllMusicSmartPlaylist() == model || model.equals(contact.getAllMusicSmartPlaylist())) {
			return false;
		}
		int i = 0;

		for (SmartPlaylist sm : contact.getSmartPlaylists()) {
			if (sm == model || model.equals(sm)) {
				return false;
			}
			i++;
			if (i >= 1) {
				break;
			}
		}
		return true;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return false;
	}

	@Override
	public void exportDone(JComponent source, Transferable data, int action) {
	}

	@Override
	public boolean importData(TransferSupport support) {
		return true;
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

}
