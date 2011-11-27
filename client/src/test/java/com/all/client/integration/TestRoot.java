package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import com.all.client.SimpleDBTest;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalRoot;
import com.all.client.model.LocalTrack;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;

public class TestRoot extends SimpleDBTest {
	Root root;

	@Before
	public void setUp() {
		root = new LocalRoot(dao);
	}

	@Test
	public void shouldBeSerializable() throws Exception {
		assertTrue(root instanceof Serializable);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldAddTrackToRoot() throws Exception {
		Track track = LocalTrack.createEmptyTrack("something");
		root.add(track);
		assertEquals(1, root.size(Track.class));
		assertEquals(track, root.getTracks().iterator().next());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldNotAddTheTrackTwiceToRoot() throws Exception {
		Track track = LocalTrack.createEmptyTrack("something");
		root.add(track);
		root.add(track);
		assertEquals(1, root.size(Track.class));
		assertEquals(track, root.getTracks().iterator().next());
	}

	@Test
	public void shouldAddPlaylistToRoot() throws Exception {
		Playlist playlist = new LocalPlaylist("something");
		root.add(playlist);
		assertEquals(1, root.size(Playlist.class));
		assertEquals(playlist, root.getPlaylists().iterator().next());
	}

	@Test
	public void shouldNotAddThePlaylistTwiceToRoot() throws Exception {
		Playlist playlist = new LocalPlaylist("something");
		root.add(playlist);
		root.add(playlist);
		assertEquals(1, root.size(Playlist.class));
		assertEquals(playlist, root.getPlaylists().iterator().next());
	}

	@Test
	public void shouldAddFolderToRoot() throws Exception {
		Folder folder = new LocalFolder("something");
		root.add(folder);
		assertEquals(1, root.size(Folder.class));
		assertEquals(folder, root.getFolders().iterator().next());
	}

	@Test
	public void shouldNotAddTheFolderTwiceToRoot() throws Exception {
		Folder folder = new LocalFolder("something");
		root.add(folder);
		root.add(folder);
		assertEquals(1, root.size(Folder.class));
		assertEquals(folder, root.getFolders().iterator().next());
	}

	@Test
	public void shouldRootReturnItsName() throws Exception {
		assertEquals("Music", root.toString());
	}
}
