package com.all.client.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.SimpleModelTest;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.LocalTrash;
import com.all.client.model.PlaylistTrack;
import com.all.client.notifiers.Notifier;
import com.all.shared.model.Folder;

public class TestTrash extends SimpleModelTest {
	LocalTrack track;
	LocalTrash trash;
	LocalFolder folder;
	private final String folderName = "testFolder";
	LocalPlaylist playlist = new LocalPlaylist();

	@Mock
	private Notifier observable;

	@Before
	public void initTrack() throws Exception {
		track = (LocalTrack) localModelFactory.createTrack(new File("src/test/resources/playlist/TestSong5.mp3"), observable, true,
				false);
		trash = new LocalTrash(dao);
		folder = new LocalFolder(folderName);
	}

	@Test
	public void shouldTrashAddAFolder() throws Exception {
		LocalTrash trash = new LocalTrash(dao);
		Folder folder = new LocalFolder(folderName);
		trash.addFolder(folder);
	}

	@Test
	public void shouldAddFolders() throws Exception {
		LocalTrash trash = new LocalTrash(dao);
		LocalFolder folder = new LocalFolder(folderName);
		folder.add(new LocalPlaylist());
		List<Folder> folders = new ArrayList<Folder>();
		folders.add(folder);
		trash.addFolders(folders);
	}

	@Test
	public void shouldRemoveFolderWithReferences() throws Exception {
		LocalPlaylist playlist = new LocalPlaylist();
		playlist.add(track);
		folder.add(playlist);
		trash.addFolderWithReferences(folder);
	}

	@Test
	public void shouldTrashPlaylistTrack() throws Exception {
		PlaylistTrack playlistTrack = new PlaylistTrack(track, playlist);
		dao.update(playlistTrack);
		trash.addPlaylistTrack(playlistTrack);
	}

}
