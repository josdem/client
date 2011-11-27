package com.all.client.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.all.client.SimpleDBTest;
import com.all.client.model.LocalPlaylist;
import com.all.shared.model.Playlist;


public class TestPlaylistDB extends SimpleDBTest {
	Playlist playlist = new LocalPlaylist("Not untitled");
	
	@Before
	public void playlistsToTest() {
		dao.deleteAll(Playlist.class);
		Playlist playlistU1 = new LocalPlaylist("Untitled Playlist 1");
		Playlist playlistU2 = new LocalPlaylist("Untitled Playlist 2");
		dao.save(playlist);
		dao.save(playlistU1);
		dao.save(playlistU2);
	}

	@Test
	public void shouldFindAllWithRootParent() throws Exception {
		assertEquals(3, dao.findAllWithRootParent().size());
	}
	
}
