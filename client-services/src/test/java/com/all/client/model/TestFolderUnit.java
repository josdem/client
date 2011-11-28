package com.all.client.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;

public class TestFolderUnit {

	@Mock
	private LocalPlaylist playlist;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldBeMarkedAsHavingNewContentIfAnyContainedPlaylistHasNewContent() throws Exception {
		LocalFolder folder = new LocalFolder("LF");
		folder.add(playlist);
		Playlist playlistB = mock(LocalPlaylist.class);
		folder.add(playlistB);

		assertFalse(folder.isNewContent());

		Mockito.when(playlistB.isNewContent()).thenReturn(true);

		assertTrue(folder.isNewContent());
	}

	@Test
	public void shouldNeverMarkedAsHavingNewContentWhenRemoteFolder() throws Exception {
		LocalFolder localFolder = new LocalFolder("LF");
		localFolder.add(playlist);
		Mockito.when(playlist.isNewContent()).thenReturn(true);
		Folder remoteFolder = new RemoteFolder(localFolder);
	
		assertTrue(localFolder.isNewContent());
		assertFalse(remoteFolder.isNewContent());
	}
}
