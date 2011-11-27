package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.all.appControl.control.ControlEngine;
import com.all.client.SimpleModelTest;
import com.all.client.model.FileContainer;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalRoot;
import com.all.client.notifiers.Notifier;
import com.all.client.services.MusicEntityService;
import com.all.client.services.UserPreferenceService;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;

public class TestFolder extends SimpleModelTest {
	private final static String playlistBasename = "* Untitled Playlist ";
	private LocalFolder folder;
	private LocalPlaylist playlist;
	private LocalPlaylist playlist2;
	private LocalPlaylist playlist3;
	private Track track;
	private Track track2;
	private Track track3;
	private String newName = "name";
	private File file = new File("src/test/resources/playlist/TestSong1.mp3");

	private Set<File> invalidFiles = new HashSet<File>();

	@Mock
	UserPreferenceService userPreferenceService;
	@Mock
	MusicEntityService musicEntityService;
	@Mock
	ControlEngine controlEngine;

	@Mock
	Playlist mockPlaylist;
	@Mock
	private Notifier observable;
	Root root = new LocalRoot(dao);

	@Before
	public void setUp() throws Exception {
		when(controlEngine.get(Model.USER_ROOT)).thenReturn(root);
		createPlayLists();
		createTracks();
		createFolder();
	}

	private void createFolder() {
		folder = new LocalFolder("folder");
		dao.save(folder);
	}

	@Test
	public void shouldSeeNameChangeWhenRenamingAFolder() throws Exception {
		folder.setName(newName);
		assertEquals(newName, folder.getName());
		String newName2 = "name2";
		folder.setName(newName2);
		assertEquals(newName2, folder.getName());
	}

	@Test
	public void shouldAddPlaylistAndSeeItInTheCollection() throws Exception {
		folder.add(new LocalPlaylist());
		assertEquals(1, folder.getPlaylists().size());
	}

	@Test
	public void shouldRecoverTheTrackFromOnePlayListInTheFolder() throws Exception {
		playlist.add(track);
		folder.add(playlist);
		assertEquals(track, folder.getPlaylist().getTrack(0));
	}

	@Test
	public void shouldRecoverTwoTracksFromOnePlayListInTheFolder() throws Exception {
		playlist.add(track);
		playlist.add(track2);
		folder.add(playlist);

		assertTrue(folder.getPlaylist().getTracks().contains(track));
		assertTrue(folder.getPlaylist().getTracks().contains(track2));
	}

	@Test
	public void shouldRecoverTrackFromSecondPlayListInTheFolder() throws Exception {
		playlist2.add(track);
		addPlaylists();
		assertEquals(track, folder.getPlaylist().getTracks().get(0));
	}

	@Test
	public void shouldRecoverTwoTrackFromSecondPlayListInTheFolder() throws Exception {
		playlist2.add(track);
		playlist2.add(track2);
		folder.add(playlist);
		folder.add(playlist2);
		assertTrue(folder.getPlaylist().getTracks().contains(track));
		assertTrue(folder.getPlaylist().getTracks().contains(track2));
	}

	@Test
	public void shouldRecoverTwoTracksOneFromFirstPlayListOtherFromSecondPlayListInTheFolder() throws Exception {
		playlist.add(track);
		playlist2.add(track2);
		folder.add(playlist);
		folder.add(playlist2);
		assertTrue(folder.getPlaylist().getTracks().contains(track));
		assertTrue(folder.getPlaylist().getTracks().contains(track2));
	}

	@Test
	public void shouldRecoverOneTrackFromThirdPlayListInTheFolder() throws Exception {
		playlist3.add(track);
		addPlaylists();
		folder.add(playlist3);
		assertEquals(track, folder.getPlaylist().getTrack(0));
	}

	@Test
	public void shouldRecoverTwoTracksFromThirdPlayListInTheFolder() throws Exception {
		playlist3.add(track);
		playlist3.add(track2);
		folder.add(playlist);
		folder.add(playlist2);
		folder.add(playlist3);
		assertTrue(folder.getPlaylist().getTracks().contains(track));
		assertTrue(folder.getPlaylist().getTracks().contains(track2));
	}

	private void createPlayLists() {
		playlist = new LocalPlaylist("playlist");
		dao.save(playlist);
		playlist2 = new LocalPlaylist("playlist2");
		dao.save(playlist2);
		playlist3 = new LocalPlaylist("playlist3");
		dao.save(playlist3);
	}

	private void createTracks() throws Exception {
		track = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong1.mp3"), observable, true, false);
		dao.save(track);
		track2 = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong2.mp3"), observable, true, false);
		dao.save(track2);
		track3 = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong3.mp3"), observable, true, false);
		dao.save(track3);
	}

	@Test
	public void shouldRecoverThreeTracksOneForEachPlayListInTheFolder() throws Exception {
		playlist.add(track);
		playlist2.add(track2);
		playlist3.add(track3);
		addPlaylists();
		folder.add(playlist3);
		assertTrue(folder.getPlaylist().getTracks().contains(track));
		assertTrue(folder.getPlaylist().getTracks().contains(track2));
		assertTrue(folder.getPlaylist().getTracks().contains(track3));
	}

	@Test
	public void shouldRecoverThreeTracksOneForEachPlayListInTheFolder1() throws Exception {
		LocalFolder folder2 = new LocalFolder("Folder2");
		LocalPlaylist playlist4 = new LocalPlaylist("playlist4");
		LocalPlaylist playlist5 = new LocalPlaylist("playlist5");
		LocalPlaylist playlist6 = new LocalPlaylist("playlist6");

		Track track4 = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong4.mp3"), observable, true, false);
		Track track5 = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong5.mp3"), observable, true, false);
		Track track6 = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong6.mp3"), observable, true, false);

		playlist4.add(track4);
		playlist5.add(track5);
		playlist6.add(track6);

		folder2.add(playlist4);
		folder2.add(playlist5);
		folder2.add(playlist6);

		dao.save(folder2);

		assertTrue(folder2.getPlaylist().getTracks().contains(track4));
		assertTrue(folder2.getPlaylist().getTracks().contains(track5));
		assertTrue(folder2.getPlaylist().getTracks().contains(track6));
	}

	@Test
	public void shouldReturnNameToString() throws Exception {
		Folder folder2 = new LocalFolder(this.newName);
		assertEquals(this.newName, folder2.toString());
	}

	@Test
	public void shouldCompare() throws Exception {
		LocalFolder that = new LocalFolder(folder.getName());
		assertEquals(0, folder.compareTo(that));
		String newName2 = "a";
		that.setName(newName2);
		assertTrue(folder.compareTo(that) > 0);
		folder.setName(newName);
		that.setName(null);
		assertEquals(-1, folder.compareTo(that));
		that.setName(newName2);
		assertTrue(folder.compareTo(that) > 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptLongNames() throws Exception {
		LocalFolder folder = new LocalFolder("new");
		folder.setName("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptBlankNames() throws Exception {
		LocalFolder folder = new LocalFolder("new");
		folder.setName("");
	}

	@Test
	public void shouldAddUntitledPlaylist() throws Exception {
		folder = new LocalFolder("new");
		Playlist untitled = LocalPlaylist.createUntitledPlaylist(folder.getPlaylists(), playlistBasename);
		assertNotNull(untitled);
		folder.add(untitled);
		dao.update(untitled);
		Playlist untitled1 = LocalPlaylist.createUntitledPlaylist(folder.getPlaylists(), playlistBasename);
		assertNotNull(untitled1);
		folder.add(untitled1);
		dao.update(untitled1);
		Playlist untitled2 = LocalPlaylist.createUntitledPlaylist(folder.getPlaylists(), playlistBasename);
		assertNotNull(untitled2);
		folder.add(untitled2);
		dao.update(untitled2);

		assertTrue(folder.getPlaylists().contains(untitled));
		assertTrue(folder.getPlaylists().contains(untitled1));
		assertTrue(folder.getPlaylists().contains(untitled2));
		assertEquals("* Untitled Playlist 01", untitled.toString());
		assertEquals("* Untitled Playlist 02", untitled1.toString());
		assertEquals("* Untitled Playlist 03", untitled2.toString());
	}

	@Test
	public void shouldAddPlaylistByFileToFolder() throws Exception {
		File file = new File("src/test/resources/playlist");
		FileContainer createPlaylist = TestLocalModelFactory.createPlaylist(file);
		Playlist playlist = folder.add(localModelFactory.createPlaylist(createPlaylist, invalidFiles, null, observable, false));
		assertNotNull(playlist);
		assertEquals("playlist", playlist.getName());
		assertEquals(6, playlist.getTracks().size());
		assertEquals(1, folder.getPlaylists().size());
		assertTrue(folder.getPlaylists().contains(playlist));
	}

	@Test
	public void shouldNotAddNullToPlaylists() throws Exception {
		Playlist noPlay = null;
		folder.add(noPlay);
		assertEquals(0, folder.getPlaylists().size());
	}

	@Test
	@Ignore
	public void shouldFIXBUGNotAddPlaylistToRootIfThePlaylistIsAddedThroughFolder() throws Exception {
		LocalFolder folder = new LocalFolder("some folder");
		Playlist playlist = new LocalPlaylist();
		folder.add(playlist);
		assertFalse(root.contains(playlist));
		FileContainer createPlaylist = TestLocalModelFactory.createPlaylist(new File("src/test/resources/playlist"));
		playlist = folder.add(localModelFactory.createPlaylist(createPlaylist, invalidFiles, null, observable, false));
		folder.add(playlist);
		assertFalse("Playlist Should not be in root!!!", root.contains(playlist));
	}

	@Test
	public void shouldNotAddTracksTwice() throws Exception {
		Track trackCreated = localModelFactory.createTrack(file, observable, true, false);
		LocalPlaylist playlist = new LocalPlaylist("playlist");
		playlist.add(trackCreated);
		LocalPlaylist playlist2 = new LocalPlaylist("playlist2");
		playlist2.add(trackCreated);
		LocalFolder folder = new LocalFolder("folder");
		folder.add(playlist);
		folder.add(playlist2);
		dao.save(folder);
		assertEquals(playlist.getTrack(0), playlist2.getTrack(0));
		assertEquals(1, folder.getPlaylist().trackCount());
	}

	private void addPlaylists() {
		folder.add(playlist);
		folder.add(playlist2);
	}

	@Test
	public void should2FoldersWithSameIdBeEqualYAY() throws Exception {
		LocalFolder folder1 = new LocalFolder("folder1");
		String hashcode = "CER54fwrRR55";
		folder1.setHashcode(hashcode);
		LocalFolder folder2 = new LocalFolder("folder2");
		folder2.setHashcode(hashcode);
		assertEquals(folder1, folder2);
	}

	@Test
	public void shouldChangeFolderName() throws Exception {
		folder.setName("folder1");
		assertEquals("folder1", folder.getName());
	}

	@Test
	public void shouldTwoEqualFoldersHaveSameHashcode() throws Exception {
		Folder f1 = new LocalFolder("LF");
		LocalFolder f2 = new LocalFolder("LF");
		f2.setHashcode(f1.getHashcode());
		assertTrue(f1.equals(f2));
		assertEquals(f1.hashCode(), f2.hashCode());
	}

}
