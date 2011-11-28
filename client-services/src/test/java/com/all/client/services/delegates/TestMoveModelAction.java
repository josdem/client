package com.all.client.services.delegates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.services.MusicEntityService;
import com.all.client.util.ModelValidation;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class TestMoveModelAction {
	private ModelCollection modelCollection;
	private Playlist playlist = new LocalPlaylist();

	@Mock
	private LocalModelDao modelDao;
	@Mock
	private LocalModelFactory modelFactory;
	@SuppressWarnings("unused")
	@Mock
	private ControlEngine controlEngine;
	@SuppressWarnings("unused")
	@Mock
	private MusicEntityService musicEntityService;
	@SuppressWarnings("unused")
	@Mock
	private Messages messages;

	@InjectMocks
	private MoveDelegate moveDelegate = new MoveDelegate();

	@Before
	public void initModelCollection() {
		@SuppressWarnings("deprecation")
		Track track = LocalTrack.createEmptyTrack();
		modelCollection = new ModelCollection(track);
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldBeAbbleToPaste() throws Exception {
		assertTrue(ModelValidation.isAbleToPaste(modelCollection, playlist));
	}

	@Test
	public void shouldPersistPlaylistAndAddTracksWhenSomethingMovedToIt() throws Exception {
		LocalPlaylist mockedPlaylist = new LocalPlaylist();
		moveDelegate.doMove(modelCollection, mockedPlaylist);
		assertEquals(1, mockedPlaylist.trackCount());
		verify(modelDao).update(mockedPlaylist);
	}

	@Test
	public void shouldPersistFolderAndCreatedPlaylist() throws Exception {
		@SuppressWarnings("deprecation")
		Folder folder = new LocalFolder();
		moveDelegate.doMove(modelCollection, folder);
		verify(modelDao).update(folder);
		verify(modelDao).update(Matchers.isA(Playlist.class));
	}

	@Test
	public void shouldRelocateRemoteModel() throws Exception {
		when(modelFactory.doRelocate(modelCollection)).thenReturn(modelCollection);
		Playlist mockedPlaylist = new LocalPlaylist();
		modelCollection.setRemote(true);
		moveDelegate.doMove(modelCollection, mockedPlaylist);

		verify(modelFactory).doRelocate(modelCollection);
	}
}
