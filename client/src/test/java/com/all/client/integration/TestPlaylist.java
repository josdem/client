package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.SimpleModelTest;
import com.all.client.model.InvalidFileException;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalRoot;
import com.all.client.model.LocalTrack;
import com.all.client.model.LocalTrash;
import com.all.client.notifiers.Notifier;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.sync.ComplexSyncAble;

public class TestPlaylist extends SimpleModelTest {
	private final static String playlistBasename = "* Untitled Playlist ";

	private Track track1;
	private Track track2;
	private LocalPlaylist playlist;
	private Track track3;
	private LocalTrash trash;


	@Mock
	private Notifier observable;

	@SuppressWarnings("deprecation")
	@Before
	public void init() throws Exception {
		trash = new LocalTrash(dao);
		dao.deleteAll(Playlist.class);
		playlist = new LocalPlaylist();
		dao.save(playlist);
		track1 = LocalTrack.createEmptyTrack("one");
		track2 = LocalTrack.createEmptyTrack("two");
		track3 = LocalTrack.createEmptyTrack("three");
	}

	@Test
	public void shouldAddATrack() throws Exception {
		Playlist playlist = new LocalPlaylist();
		Track track = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong1.mp3"), observable, true, false);
		dao.save(playlist);
		List<Track> trackList = playlist.getTracks();
		assertNotNull(track);
		assertNotNull(trackList);
	}

	@Test
	public void shouldGetAllTracks() throws Exception {
		List<Playlist> playlistList = dao.findAll(Playlist.class);
		assertNotNull(playlistList);
	}

	@Test
	public void shouldContainsTheSongsAdded() throws Exception {
		assertFalse(playlist.contains(track1));
		addTrack1();
		assertTrue(playlist.contains(track1));
	}

	@Test
	public void shouldKnowWhenEmpty() throws Exception {
		assertTrue(playlist.isEmpty());
		addTrack1();
		assertFalse(playlist.isEmpty());
	}

	@Test
	public void shouldRenamePlaylist() throws Exception {
		LocalPlaylist playlist = new LocalPlaylist();
		playlist.setName("The new name");
		assertEquals("The new name", playlist.toString());
	}

	@Test
	public void shouldSortPlaylist() throws Exception {
		playlist.setName("a");
		LocalPlaylist playlist2 = new LocalPlaylist("b");
		List<Playlist> playlistList = new ArrayList<Playlist>();
		playlistList.add(playlist2);
		playlistList.add(playlist);
		Collections.sort(playlistList);
		assertEquals(playlist, playlistList.get(0));
	}

	@Test
	public void shouldSortPlaylistWithNulls() throws Exception {
		Playlist playlist2 = new LocalPlaylist("b");
		List<Playlist> playlistList = new ArrayList<Playlist>();
		playlistList.add(playlist2);
		playlistList.add(playlist);
		Collections.sort(playlistList);
		assertNotNull(playlistList.get(0));
		assertNotNull(playlist);
		assertEquals(playlist2, playlistList.get(0));
	}

	@Test
	public void shouldTwoPlaylistWithNullNameBeEqual() throws Exception {
		assertEquals(0, playlist.compareTo(new LocalPlaylist()));
	}

	@Test
	public void shouldSortCorrectlyIgnoringUpper() throws Exception {
		playlist.setName("a");
		assertEquals(0, playlist.compareTo(new LocalPlaylist("A")));
	}

	private void addTrack1() {
		playlist.add(track1);
		dao.update(playlist);
	}

	private void addTrack2() {
		playlist.add(track2);
		dao.update(playlist);
	}

	@Test
	public void shouldSizeOfPlayListFolderReturnOne() throws Exception {
		playlist.add(track1);
		playlist.add(track1);

		dao.update(track1);
		assertEquals(1, playlist.getTracks().size());
	}

	@Test
	public void shouldAddOnlyOnceTheSameTrackToPlayList() throws Exception {
		File file = new File("src/test/resources/playlist/TestSong2.mp3");
		track2 = localModelFactory.createTrack(file, observable, true, false);

		track3 = localModelFactory.createTrack(file, observable, true, false);
		assertEquals(track2, track3);

		playlist.add(track2);
		playlist.add(track3);
		dao.update(playlist);

		assertEquals(1, playlist.trackCount());
	}

	@Test
	public void shouldPersistWithTrack() throws Exception {
		addTrack1();
		dao.update(playlist);
		assertNotNull(playlist.getHashcode());
	}

	@Test
	public void shouldAddTrackByFileToPlaylistBothPersisted() throws Exception {
		File file = new File("src/test/resources/playlist/TestSong1.mp3");
		Track track = localModelFactory.createTrack(file, observable, true, false);
		playlist.add(track);
		assertEquals(playlist.getTrack(0), track);
	}

	@Test
	public void shouldNotAddANewTrackToPlaylistWithNotExistentFile() throws Exception {
		File file = new File("src/test/resources/playlist/notexist.mp3");
		Track track = localModelFactory.createTrack(file, observable, true, false);
		playlist.add(track);
		assertEquals(0, playlist.getTracks().size());
	}

	@Test(expected = InvalidFileException.class)
	public void shouldNotCreateANewTrackWithImages() throws Exception {
		File file = new File("src/test/resources/images/02715.jpg");
		Track track = localModelFactory.createTrack(file, observable, true, false);
		playlist.add(track);
		assertEquals(0, playlist.getTracks().size());
	}

	@Test
	public void shouldCreateUntitledPlaylist() throws Exception {
		List<Playlist> playlists = new ArrayList<Playlist>();
		Playlist untitled = LocalPlaylist.createUntitledPlaylist(playlists, playlistBasename);
		assertNotNull(untitled);
		assertEquals("* Untitled Playlist 01", untitled.toString());
		playlists.add(untitled);
		untitled = LocalPlaylist.createUntitledPlaylist(playlists, playlistBasename);
		assertNotNull(untitled);
		playlists.add(untitled);
		assertEquals("* Untitled Playlist 02", untitled.toString());
		untitled = LocalPlaylist.createUntitledPlaylist(playlists, playlistBasename);
		assertNotNull(untitled);
		playlists.add(untitled);
		assertEquals("* Untitled Playlist 03", untitled.toString());
		playlists.remove(untitled);
		untitled = LocalPlaylist.createUntitledPlaylist(playlists, playlistBasename);
		assertNotNull(untitled);
		playlists.add(untitled);
		assertEquals("* Untitled Playlist 03", untitled.toString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldNotReturnTrashedTracks() {
		Track deadTrack = LocalTrack.createEmptyTrack();
		dao.save(deadTrack);

		LocalPlaylist playlist = new LocalPlaylist();
		playlist.add(deadTrack);
		dao.save(playlist);
		playlist.remove(deadTrack);
		trash.addTrack(deadTrack);
		dao.delete(deadTrack);

		Track liveTrack = LocalTrack.createEmptyTrack();
		dao.save(liveTrack);
		playlist.add(liveTrack);
		dao.update(playlist);

		List<Track> tracks = playlist.getTracks();

		assertTrue(tracks.contains(liveTrack));
		assertFalse(tracks.contains(deadTrack));
	}

	@Test
	public void shouldRemoveARootPlaylistFromRootWhenTrashed() throws Exception {
		Root root = new LocalRoot(dao);
		Playlist playlist = localModelFactory.createUntitledPlaylist(root);
		root.add(playlist);
		assertEquals(2, root.size(Playlist.class));
		trash.addPlayLists(Arrays.asList(playlist));
		dao.delete(playlist);
		assertEquals(1, root.size(Playlist.class));
	}

	@Test
	public void shouldUpdateChangeDateWhenTrackTrashed() throws Exception {
		Date date = playlist.getModifiedDate();
		playlist.add(track1);
		dao.save(track1);
		trash.addTrack(track1);
		assertNotSame(date, playlist.getModifiedDate());
	}

	@Test
	public void shouldPlaylistBeEqualWithItself() throws Exception {
		Playlist playlist1 = new LocalPlaylist();
		assertEquals(playlist1, playlist1);
	}

	@Test
	public void shouldOrderTracks() throws Exception {
		addTrack1();
		assertEquals(0, playlist.trackPosition(track1));
		assertEquals(-1, playlist.trackPosition(track2));
		addTrack2();
		assertEquals(1, playlist.trackPosition(track2));
	}

	@Test
	public void shouldRemoveTracks() throws Exception {
		addTrack1();
		playlist.remove(track1);
		assertEquals(0, playlist.trackCount());
	}

	@Test
	public void shouldGetName() throws Exception {
		String name = "name";
		playlist.setName(name);
		assertEquals(name, playlist.getName());
	}

	@Test
	public void shouldEqualPlaylistsHaveSameHashcode() throws Exception {
		Playlist p1 = new LocalPlaylist();
		LocalPlaylist p2 = new LocalPlaylist();
		p2.setHashcode(p1.getHashcode());
		assertTrue(p1.equals(p2));
		assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void shouldEqualSmartPlaylistsHaveSameHashcode() throws Exception {
		LocalPlaylist p1 = new LocalPlaylist("A");
		p1.setSmartPlaylist(true);
		LocalPlaylist p2 = new LocalPlaylist("A");
		p2.setSmartPlaylist(true);
		assertTrue(p1.equals(p2));
		assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void shouldBePostSyncAble() throws Exception {
		LocalPlaylist playlist = new LocalPlaylist();
		assertTrue(playlist instanceof ComplexSyncAble);
		assertTrue(playlist.requiresPostProcessing("parentFolder"));
		int totalPostSyncAbleFields = 0;
		for (Field field : playlist.getClass().getDeclaredFields()) {
			if (playlist.requiresPostProcessing(field.getName())) {
				totalPostSyncAbleFields++;
			}
		}
		assertEquals(1, totalPostSyncAbleFields);
	}
}
