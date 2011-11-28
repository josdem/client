package com.all.client.view.dnd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.awt.Point;

import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.MyMusicHoverAnimation;
import com.all.client.view.components.PreviewTree;
import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;

public class TestTreeDropListener {
	private static final Point LOCATION = new Point(0, 0);
	@Mock
	private ViewEngine viewEngine;
	@Mock
	private PreviewTree previewTree;
	@Mock
	private JTreeCoordinateHelper treeHelper;
	@Mock
	private Root root;
	@Mock
	private DefaultMutableTreeNode treeNode;
	@Mock
	private Folder folder;
	@Mock
	private Playlist playlist;
	@Mock
	private Track track;
	@Mock
	private MyMusicHoverAnimation animation;

	private TreeDropListener listener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(viewEngine.get(Model.USER_ROOT)).thenReturn(root);
		when(previewTree.getTreeHelper()).thenReturn(treeHelper);
		when(treeHelper.getDefaultMutableTreeNodeFromCoordinates(isA(Point.class))).thenReturn(treeNode);
		when(treeNode.getUserObject()).thenReturn(root, folder, playlist);
		listener = new TreeDropListener(animation, viewEngine, previewTree);

	}

	@Test
	public void shouldAllowDnDOfRemoteFoldersOverRoot() throws Exception {
		ModelCollection model = new ModelCollection(folder);
		model.setRemote(true);

		assertTrue(listener.validateDrop(model, LOCATION));
		assertFalse(listener.validateDrop(model, LOCATION));
		assertFalse(listener.validateDrop(model, LOCATION));
	}

	@Test
	public void shouldAllowDropOfRemotePlaylistOverFolderAndRoot() throws Exception {
		ModelCollection model = new ModelCollection(playlist);
		model.setRemote(true);

		assertTrue(listener.validateDrop(model, LOCATION));
		assertTrue(listener.validateDrop(model, LOCATION));
		assertFalse(listener.validateDrop(model, LOCATION));
	}

	@Test
	public void shouldAllowDropOfRemoteTracksOverFolderPlaylistOrRoot() throws Exception {
		ModelCollection model = new ModelCollection(track);
		model.setRemote(true);

		assertTrue(listener.validateDrop(model, LOCATION));
		assertTrue(listener.validateDrop(model, LOCATION));
		assertTrue(listener.validateDrop(model, LOCATION));
	}

	@Test
	public void shouldAllowDropOfPlaylistAndFoldersOverRoot() throws Exception {
		ModelCollection model = new ModelCollection(folder, playlist);
		model.setRemote(true);

		assertTrue(listener.validateDrop(model, LOCATION));
		assertFalse(listener.validateDrop(model, LOCATION));
		assertFalse(listener.validateDrop(model, LOCATION));
	}

	// @Test
	// public void shouldRelocateRemoteModelCollectionOnDrop() throws Exception {
	// ModelCollection model = new ModelCollection(folder, playlist, track);
	// model.setRemote(true);
	// when(appState.getSelectedItem()).thenReturn(root);
	//
	// listener.doDrop(model);
	//
	// verify(folder).relocate(null);
	// verify(playlist).relocate(null);
	// verify(track).relocate(null);
	// verify(modelController).move(model, root);
	// }

}
