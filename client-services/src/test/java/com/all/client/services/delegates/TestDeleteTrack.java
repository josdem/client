package com.all.client.services.delegates;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.UnitTestCase;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.LocalTrash;
import com.all.client.model.Trash;
import com.all.client.services.MusicEntityService;
import com.all.core.actions.ModelDeleteAction.DeleteMode;
import com.all.shared.model.Track;

public class TestDeleteTrack extends UnitTestCase {
	Trash mockTrash = mock(LocalTrash.class);

	@SuppressWarnings("deprecation")
	Track track = LocalTrack.createEmptyTrack("track1");

	@Mock
	LocalPlaylist playlist;
	@Mock
	LocalTrash trash;
	@Mock
	MusicEntityService musicEntityService;

	@InjectMocks
	private DeleteDelegate deleteAction = new DeleteDelegate();

	@Before
	public void addTracksToList() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldDeleteTracks() throws Exception {
		List<Track> tracks = new ArrayList<Track>();
		tracks.add(track);
		deleteAction.deleteTracks(DeleteMode.ONLY_REFERENCES, tracks, playlist, trash, false);

		verify(musicEntityService).removeFrom(track, playlist);
	}

	@Test
	public void shouldDeleteAllTracks() throws Exception {
		List<Track> tracks = new ArrayList<Track>();
		tracks.add(track);

		deleteAction.deleteTracks(DeleteMode.REF_AND_FILES, tracks, playlist, trash, true);

		verify(musicEntityService).deleteFile(track);
		verify(trash).addTrack(track);
	}

}
