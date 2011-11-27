package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.all.client.SimpleDBTest;
import com.all.client.model.LocalDefaultSmartPlaylist;

public class TestSmartPlaylist extends SimpleDBTest {

	@Test
	public void shouldGetLabel() throws Exception {
		assertEquals("Low Quality Tracks", LocalDefaultSmartPlaylist.CRAPPY_KBPS.create(dao).getLabel());
		assertEquals("Untitled Tracks", LocalDefaultSmartPlaylist.TRACK_IN_TITLE.create(dao).getLabel());
	}

	@Test
	public void shouldGetPlaylist() throws Exception {
		assertEquals(LocalDefaultSmartPlaylist.ALL_MUSIC.findLooseTracksInRoot(dao),
				LocalDefaultSmartPlaylist.LOOSE_TRACKS.create(dao).getTracks());
	}

	@Test
	public void shouldKnowIfPlaylistIsSmartPlaylist() throws Exception {
		assertTrue(LocalDefaultSmartPlaylist.CRAPPY_KBPS.create(dao).getPlaylist().isSmartPlaylist());
	}
}
