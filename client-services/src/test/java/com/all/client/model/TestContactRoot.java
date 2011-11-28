package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.Track;

public class TestContactRoot extends UnitTestCase {

	private ContactRoot contactRoot;
	
	@Mock
	private ModelCollection modelCollection;
	
	@Mock
	private SmartPlaylist smartPlaylist;

	@Before
	public void createContactRoot() {
		contactRoot = new ContactRoot(null);
	}

	@Test
	public void shouldBeSerializable() throws Exception {
		assertTrue(contactRoot instanceof Serializable);
	}

	@Test
	public void shouldFoldersNotBeNull() throws Exception {
		assertNotNull(contactRoot.getFolders());
	}

	@Test
	public void shouldPlaylistsNotBeNull() throws Exception {
		assertNotNull(contactRoot.getPlaylists());
	}

	@Test
	public void shouldTracksNotBeNull() throws Exception {
		assertNotNull(contactRoot.getTracks());
	}
	
	@Test
	public void shouldFilterContactRoot() throws Exception {
		Iterable<Track> smartPlaylistIterable = new ArrayList<Track>();
		when(smartPlaylist.getTracks()).thenReturn(smartPlaylistIterable);
		contactRoot.setContent(modelCollection, smartPlaylist);
		
		verify(modelCollection).filterTracksWithoutMagnetlink();
	}
	
	@Test
	public void shouldCopyContent() throws Exception {
		Iterable<Track> trackIterable = new ArrayList<Track>();
		
		Track track = mock(Track.class);
		Playlist playlist = mock(Playlist.class);
		Folder folder = mock(Folder.class);
		
		when(folder.getTracks()).thenReturn(trackIterable);
		when(smartPlaylist.getTracks()).thenReturn(trackIterable);
		when(track.getDownloadString()).thenReturn("fdsf");
		SmartPlaylist mockSmartPlaylist = mock(SmartPlaylist.class);
		when(mockSmartPlaylist.getTracks()).thenReturn(trackIterable);
		ModelCollection collection = new ModelCollection(track, playlist, folder, mockSmartPlaylist);
		contactRoot.setContent(collection, smartPlaylist);
		
		@SuppressWarnings("deprecation")
		ContactRoot newContactRoot = new ContactRoot(new ContactInfo());
		newContactRoot.copyContent(contactRoot);
		
		assertEquals(newContactRoot.getTracks(), contactRoot.getTracks());
		assertEquals(newContactRoot.getPlaylists(), contactRoot.getPlaylists());
		assertEquals(newContactRoot.getFolders() , contactRoot.getFolders());
		assertEquals(newContactRoot.getAllMusicSmartPlaylist() , contactRoot.getAllMusicSmartPlaylist());
		assertEquals(newContactRoot.getSmartPlaylists(), contactRoot.getSmartPlaylists());
	}

}
