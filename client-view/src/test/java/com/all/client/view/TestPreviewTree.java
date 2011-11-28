package com.all.client.view;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.view.components.PreviewTree;
import com.all.client.view.model.MusicEntityTreeNode;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;

public class TestPreviewTree {

	@Mock
	private Root root;

	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode node;
	private DefaultMutableTreeNode node2;
	private LocalFolder folder;
	private Playlist playlist;
	private Folder folder2;
	private PreviewTree emptyPreviewTree;
	private PreviewTree previewTree;

	@Before
	public void createPreviewTree() throws Exception {
		MockitoAnnotations.initMocks(this);
		playlist = new LocalPlaylist("playlist");
		folder2 = new LocalFolder("TwoTwoTwoTwo");
		folder = new LocalFolder("One");

		rootNode = new DefaultMutableTreeNode(root);
		folder.add(playlist);
		node = new DefaultMutableTreeNode(folder);
		node.add(new DefaultMutableTreeNode(playlist));
		node2 = new DefaultMutableTreeNode(folder2);
		rootNode.add(node);
		rootNode.add(node2);
		rootNode.add(new DefaultMutableTreeNode(new LocalFolder("someFolder1")));
		rootNode.add(new DefaultMutableTreeNode(new LocalFolder("someFolder2")));

		previewTree = new PreviewTree();
		previewTree.setModel(new DefaultTreeModel(rootNode));
		previewTree.setName("previewTree");

		emptyPreviewTree = new PreviewTree();

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(root);
		TreeModel treeModel = new DefaultTreeModel(top);
		emptyPreviewTree.setModel(treeModel);
		emptyPreviewTree.setSelectionRow(0);
	}

	@Test
	public void shouldEditTree() {
		DefaultMutableTreeNode playlistNode = new MusicEntityTreeNode(playlist);
		DefaultMutableTreeNode folderNode = new MusicEntityTreeNode(folder);

		folderNode.add(playlistNode);

		TreeModel treeModel = new DefaultTreeModel(folderNode);

		previewTree.setModel(treeModel);

		TreePath path = new TreePath(playlistNode.getPath());

		previewTree.setSelectionPath(path);

		previewTree.startEditingAtPath(previewTree.getSelectionPath());
		assertNotNull(previewTree.getSelectionPath());
		assertTrue(previewTree.isEditable());
		assertTrue(previewTree.isEditing());
	}

}
