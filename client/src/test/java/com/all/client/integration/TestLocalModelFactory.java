package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.all.appControl.control.ControlEngine;
import com.all.client.SimpleModelTest;
import com.all.client.model.Container;
import com.all.client.model.FileContainer;
import com.all.client.model.LocalTrack;
import com.all.client.notifiers.Notifier;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;
import com.all.shared.model.RemotePlaylist;
import com.all.shared.model.RemoteTrack;
import com.all.shared.model.Root;
import com.all.shared.model.Track;

public class TestLocalModelFactory extends SimpleModelTest {
	@Mock
	ControlEngine controlEngine;
	@Mock
	Root root;
	@Mock
	private Notifier observable;

	private Set<File> invalidFiles = new HashSet<File>();

	@Before
	public void setUp() {
		localModelFactory.controlEngine = controlEngine;
	}

	@Test
	public void shouldCreateLocalTrackFromRemoteTrack() throws Exception {
		RemoteTrack remote = new RemoteTrack();
		String album = "Black Album";
		remote.setAlbum(album);
		String artist = "Metallica";
		remote.setArtist(artist);
		remote.setHashcode("0123456789abcdef0123456789abcdef01234567");

		LocalTrack local = localModelFactory.relocateTrack(remote);
		assertEquals(album, local.getAlbum());
		assertEquals(artist, local.getArtist());
		assertNotNull(dao.findByHashcode(local.getHashcode()));
	}

	@Ignore
	@Test
	// TODO i18nlize
	public void shouldCreateUntitledFolderInRoot() throws Exception {
		List<Folder> folders = new ArrayList<Folder>();
		Folder untitled = localModelFactory.createUntitledFolder(folders);
		assertNotNull(untitled);
		assertEquals("* Untitled Folder 01", untitled.toString());

		folders.add(untitled);
		untitled = localModelFactory.createUntitledFolder(folders);
		assertNotNull(untitled);
		assertEquals("* Untitled Folder 02", untitled.toString());
		folders.add(untitled);
		untitled = localModelFactory.createUntitledFolder(folders);
		assertNotNull(untitled);
		assertEquals("* Untitled Folder 03", untitled.toString());
		folders.add(untitled);
	}

	@Ignore
	@Test
	// TODO i18nlize
	public void shouldCreateUntitledPlaylistInRoot() throws Exception {
		List<Playlist> playlists = new ArrayList<Playlist>();
		Playlist playlist = localModelFactory.createUntitledPlaylist(playlists);
		playlists.add(playlist);
		assertNotNull(playlist);
		assertEquals("* Untitled Playlist 01", playlist.toString());
		playlist = localModelFactory.createUntitledPlaylist(playlists);
		playlists.add(playlist);
		assertNotNull(playlist);
		assertEquals("* Untitled Playlist 02", playlist.toString());
		playlist = localModelFactory.createUntitledPlaylist(playlists);
		playlists.add(playlist);
		assertNotNull(playlist);
		assertEquals("* Untitled Playlist 03", playlist.toString());
	}

	@Test
	public void shouldCreateFolderFromAFile() throws Exception {
		File file = new File("src/test/resources/folder");
		assertTrue(file.exists());
		Folder folder = localModelFactory.createFolder(createFolder(file), invalidFiles, observable, false);
		assertNotNull(folder);
		assertEquals("folder", folder.getName());
		assertEquals(1, folder.getPlaylists().size());
	}

	@Test
	public void shouldCreateANewPlaylistInRoot() throws Exception {
		File file = new File("src/test/resources/playlist");
		Playlist playlist = localModelFactory.createPlaylist(createPlaylist(file), invalidFiles, null, observable, false);
		assertNotNull(playlist);
		assertEquals(6, playlist.getTracks().size());
	}

	@Test
	public void shouldAddSameTrackTwiceInTwoPlaylistsSameFolder() throws Exception {
		Container<List<FileContainer>> folderContainer = createFolder(new File("src/test/resources/folderWith2PlaylistsWithIdenticalTracks"));
		Folder folder = localModelFactory.createFolder(folderContainer, invalidFiles, observable, false);
		Track track = folder.getPlaylist().getTracks().get(0);
		assertNotNull(track);
		List<Playlist> playlists = folder.getPlaylists();
		assertEquals(2, playlists.size());
		assertTrue(playlists.get(0).contains(track));
		assertTrue(playlists.get(1).contains(track));

	}

	@Test
	public void shouldRelocateModelCollection() throws Exception {
		String hashcode = "82ad6f88ccba340b8d4fe9e209fe001d686036a6";
		RemoteFolder remoteFolder = new RemoteFolder();
		remoteFolder.setName("remoteFolder");
		remoteFolder.setHashcode("1234567890");

		Date date = new Date();
		List<Playlist> remotePlaylists = new ArrayList<Playlist>();
		remoteFolder.setPlaylists(remotePlaylists);

		RemotePlaylist remotePlaylist = new RemotePlaylist();
		remotePlaylist.setName("remotePlaylistInFolder");
		remotePlaylist.setHashcode("1234567890");
		remotePlaylist.setLastPlayed(date);
		remotePlaylist.setModifiedDate(date);
		remotePlaylist.setOwner("owner");
		remotePlaylist.setLastPlayed(date);
		remotePlaylist.setModifiedDate(date);
		remotePlaylist.setOwner("owner");
		remotePlaylist.setSmartPlaylist(false);

		List<Track> remoteTracks = new ArrayList<Track>();
		remotePlaylist.setTracks(remoteTracks);
		remotePlaylists.add(remotePlaylist);
		RemoteTrack remoteTrack = new RemoteTrack();
		remoteTrack.setName("trackInFolder");
		remoteTrack.setHashcode(hashcode);
		remoteTracks.add(remoteTrack);

		ModelCollection remoteModel = new ModelCollection(remoteFolder, remotePlaylist, remoteTrack);
		remoteModel.setRemote(true);

		assertTrue(dao.findAll(Playlist.class).isEmpty());
		assertTrue(dao.findAll(Folder.class).isEmpty());
		assertNull(dao.findById(LocalTrack.class, "12345678"));

		ModelCollection relocatedModel = localModelFactory.doRelocate(remoteModel);

		assertNotNull(relocatedModel);
		List<Folder> folders = dao.findAll(Folder.class);
		assertEquals(1, folders.size());
		assertTrue(folders.get(0).isNewContent());
		List<Playlist> playlists = dao.findAll(Playlist.class);
		assertEquals(2, playlists.size());
		assertTrue(playlists.get(0).isNewContent());
		assertTrue(playlists.get(1).isNewContent());
		assertNotNull(dao.findById(LocalTrack.class, hashcode));
	}

	public static Container<List<FileContainer>> createFolder(File file) {
		List<FileContainer> playlists = new ArrayList<FileContainer>();
		for (File f : file.listFiles()) {
			if (!f.isHidden() && f.isDirectory()) {
				playlists.add(createPlaylist(f));
			}
		}
		return new Container<List<FileContainer>>(file.getName(), playlists);
	}

	public static FileContainer createPlaylist(File file) {
		List<File> files = new ArrayList<File>();
		for (File file2 : file.listFiles()) {
			if (!file2.isHidden() && !file2.isDirectory()) {
				files.add(file2);
			}
		}
		return new FileContainer(file.getName(), files);
	}

	@Test
	public void shouldCreateTrackFromHashcode() throws Exception {
		Track track = localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong1.mp3"), observable, true, false);
		Track track2 = localModelFactory.findByHashCode(track.getHashcode());
		assertEquals(track, track2);
	}

	@Test
	public void shouldNotThrowExceptionIfTrackNotFound() throws Exception {
		Track track = localModelFactory.findByHashCode("not possibly track could be found with this hashcode");
		assertNull(track);
	}
}
