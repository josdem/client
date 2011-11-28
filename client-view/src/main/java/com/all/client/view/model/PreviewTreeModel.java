package com.all.client.view.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.all.client.view.util.IterableUtils;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;

public final class PreviewTreeModel {
	
	private PreviewTreeModel() {
		
	}
	
	public static TreeModel convertToTreeModel(Root root) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(root);
		List<Folder> folders = IterableUtils.toList(root.getFolders());
		Collections.sort(folders);
		for (Folder folder : folders) {
			DefaultMutableTreeNode folderNode = new MusicEntityTreeNode(folder);
			List<Playlist> playlists = new ArrayList<Playlist>(folder.getPlaylists());
			Collections.sort(playlists);
			for (Playlist playlist : playlists) {
				DefaultMutableTreeNode playlistNode = new MusicEntityTreeNode(playlist);
				folderNode.add(playlistNode);
			}
			top.add(folderNode);
		}
		List<Playlist> playlists = IterableUtils.toList(root.getPlaylists());
		Collections.sort(playlists);
		for (Playlist playlist : playlists) {
			DefaultMutableTreeNode playlistNode = new MusicEntityTreeNode(playlist);
			top.add(playlistNode);
		}
		return new DefaultTreeModel(top);
	}
}
