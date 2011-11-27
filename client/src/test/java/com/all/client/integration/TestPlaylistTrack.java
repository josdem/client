package com.all.client.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.all.client.SimpleDBTest;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.LocalTrash;
import com.all.client.model.PlaylistTrack;
import com.all.client.model.Trash;
import com.all.shared.model.Track;
import com.all.shared.sync.ComplexSyncAble;

public class TestPlaylistTrack extends SimpleDBTest {

	PlaylistTrack playlistTrack;
	Track track;
	LocalPlaylist playlist = new LocalPlaylist();

	Trash trash;

	@SuppressWarnings("deprecation")
	@Before
	public void addTrack() {
		track = LocalTrack.createEmptyTrack();
		List<PlaylistTrack> playlists = dao.findAll(PlaylistTrack.class);
		for (PlaylistTrack playlistTrack : playlists) {
			dao.delete(playlistTrack);
		}
		dao.update(track);
		playlistTrack = new PlaylistTrack((LocalTrack) track, playlist);
		dao.update(playlistTrack);
		trash = new LocalTrash(dao);
	}

	@Test
	public void testSetTrack() {
		List<PlaylistTrack> list = new ArrayList<PlaylistTrack>();
		list.add(playlistTrack);

		BeanPropertyValueEqualsPredicate predicate = new BeanPropertyValueEqualsPredicate("track", track);
		assertTrue(CollectionUtils.exists(list, predicate));
	}
	
	@Test
	public void shouldBePostSyncAble() throws Exception {
		assertTrue(playlistTrack instanceof ComplexSyncAble);
		assertFalse(playlistTrack.requiresPostProcessing("trackPosition"));
		assertFalse(playlistTrack.requiresPostProcessing("id"));
		assertTrue(playlistTrack.requiresPostProcessing("playlist"));
		assertTrue(playlistTrack.requiresPostProcessing("track"));
	}
}
