package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import com.all.client.SimpleDBTest;
import com.all.client.model.LocalDefaultSmartPlaylist;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.LocalTrash;
import com.all.client.model.PlaylistTrack;
import com.all.client.model.Trash;
import com.all.shared.model.Track;

public class TestTrackDB extends SimpleDBTest {
	private static final int SIZE = 120;
	private static final long serialVersionUID = 8879218253896275138L;
	private List<Track> tracks;
	private LocalPlaylist playlist;

	Trash trash;

	private static final Log log = LogFactory.getLog(TestTrackDB.class);

	@SuppressWarnings("deprecation")
	@Before
	public void createTracksAndUntitlePlaylist() {
		dao.deleteAll(PlaylistTrack.class);
		dao.deleteAll(Track.class);
		tracks = new ArrayList<Track>();
		for (int i = 0; i < SIZE; i++) {
			LocalTrack track = LocalTrack.createEmptyTrack(i + "");
			track.setPlaycount(120 + i);
			track.setDateAdded(new Date(serialVersionUID + i));
			track.setName("rola " + i);
			if (i % 10 == 0) {
				track.setBitRate("100");
				track.setName("idkghskfgkjsfg jhgftRacK" + i);
			}
			if (i % 20 == 0) {
				track.setLastPlayed(new Date(serialVersionUID + i));
			}
			dao.save(track);
			tracks.add(track);
		}
		trash = new LocalTrash(dao);

		playlist = new LocalPlaylist();
		playlist.add(tracks);
		dao.save(playlist);

	}

	// TODO Necesitamos manejar la relacion con hibernate OneToOne y cambiar los queries de LocalTrack Estos metodos no
	// funcionaran por que necesitan TrackUserStats No se pueden arreglar hasta que hagamos se cambio

	@Test
	public void shouldFindTheMostPlayedTracks() throws Exception {
		List<Track> theMostPlayed = dao.findMostPlayed();
		assertEquals(100, theMostPlayed.size());

		Track localTrack = theMostPlayed.get(0);
		Track localTrack2 = theMostPlayed.get(1);
		assertTrue(localTrack.getPlaycount() > localTrack2.getPlaycount());
	}

	@Test
	public void shouldFindRecentlyAddedTracks() throws Exception {
		List<Track> recentlyAdded = dao.findRecentlyAdded();
		assertEquals(100, recentlyAdded.size());
		Date theMostRecent = recentlyAdded.get(0).getDateAdded();
		assertTrue(theMostRecent.after(recentlyAdded.get(1).getDateAdded()));
	}

	@Test
	public void shouldFindCrappyTracks() throws Exception {
		List<Track> crappyTracks = LocalDefaultSmartPlaylist.ALL_MUSIC.findCrappyTracks(dao);
		assertTrue(crappyTracks.size() > 10);
	}

	@Test
	public void shouldFindTrackInTitle() throws Exception {
		List<Track> trackInTitle = LocalDefaultSmartPlaylist.ALL_MUSIC.findTrackInName(dao);
		assertEquals(12, trackInTitle.size());
	}

	@Test
	public void shouldFindRecentlyPlayed() throws Exception {
		List<Track> recentlyPlayed = dao.findRecentlyPlayed();
		assertEquals(100, recentlyPlayed.size());
		assertTrue(recentlyPlayed.get(0).getLastPlayed().after(recentlyPlayed.get(1).getLastPlayed()));
	}

	private void createUntitledPlaylist() {
		LocalPlaylist untitledPlaylist = new LocalPlaylist();
		untitledPlaylist.setName("untitled playlist 1");
		for (int i = 0; i < 3; i++) {
			addUntitledTrack(untitledPlaylist);
		}
		dao.save(untitledPlaylist);
	}

	@SuppressWarnings("deprecation")
	private void addUntitledTrack(LocalPlaylist untitledPlaylist) {
		Track track = LocalTrack.createEmptyTrack();
		dao.save(track);
		untitledPlaylist.add(track);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldFindLooseTracksInRoot() throws Exception {
		Track looseTrack = LocalTrack.createEmptyTrack("LooseTrack");
		dao.save(looseTrack);
		List<Track> looseTracks = LocalDefaultSmartPlaylist.ALL_MUSIC.findLooseTracksInRoot(dao);
		assertEquals(1, looseTracks.size());
		assertEquals(120, playlist.getTracks().size());
		playlist.add(looseTrack);
		looseTracks = LocalDefaultSmartPlaylist.ALL_MUSIC.findLooseTracksInRoot(dao);
		assertEquals(0, looseTracks.size());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldCountLooseTracks() throws Exception {
		Track looseTrack = LocalTrack.createEmptyTrack("LooseTrack");
		dao.save(looseTrack);

		List<Track> findAll = dao.findAll(Track.class);
		log.debug("TRACKS IN DB " + findAll.size());

		List<PlaylistTrack> allPlaylistTrack = dao.findAll(PlaylistTrack.class);
		log.debug("PLAYLIST TRACKS BEFORE REMOVING = " + allPlaylistTrack.size());

		List<Track> looseTracks = LocalDefaultSmartPlaylist.ALL_MUSIC.findLooseTracksInRoot(dao);
		log.debug("Loose Tracks Size " + looseTracks.size());

		int looseTracksCount = LocalDefaultSmartPlaylist.LOOSE_TRACKS.countLooseTracks(dao);
		assertEquals(1, looseTracksCount);

		// TODO: This part is intended to test when a track is removed from all of it's playlists and become again a
		// LooseTrack
		// PlaylistTrack playlistTrack = allPlaylistTrack.get(0);
		// playlistTrack.moveToTrash();
		// dao.update(playlistTrack);
		//
		// allPlaylistTrack = dao.findAllPlaylistTrack();
		// log.debug("PLAYLIST TRACKS AFTER REMOVING = " + allPlaylistTrack.size());
		//
		// looseTracks = LocalDefaultSmartPlaylist.ALL_MUSIC.findLooseTracksInRoot(dao);
		// log.debug("Loose Tracks Size " + looseTracks.size());
		//
		// looseTracksCount = LocalDefaultSmartPlaylist.LOOSE_TRACKS.countLooseTracks(dao);
		// assertEquals(2, looseTracksCount);
	}

	@SuppressWarnings("deprecation")
	@Test
	// This test is to assure that Tracks in untitled playlist don't appear in Loose Tracks list (Defect #4172)
	public void shouldFindLooseTracksAndNotConsiderTracksOnUntitledPlaylist() throws Exception {
		createUntitledPlaylist();

		Track looseTrack = LocalTrack.createEmptyTrack("LooseTrack");
		dao.save(looseTrack);

		List<Track> looseTracks = LocalDefaultSmartPlaylist.ALL_MUSIC.findLooseTracks(dao);
		assertEquals(1, looseTracks.size());
	}

}
