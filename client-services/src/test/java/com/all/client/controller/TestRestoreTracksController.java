package com.all.client.controller;

//import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.appControl.control.ControlEngine;
import com.all.client.data.Hashcoder;
import com.all.client.model.TrackFile;
import com.all.client.peer.share.ShareService;
import com.all.client.services.MusicEntityService;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.events.Events;
import com.all.core.model.SearchState;
import com.all.event.ValueEvent;
import com.all.shared.model.Track;
import com.all.shared.stats.usage.UserActions;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestRestoreTracksController {
	@UnderTest
	private RestoreTracksController controller;
	@Mock
	private MusicEntityService musicEntityService;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private ShareService shareService;
	@Mock
	private ClientReporter reporter;

	@Test
	public void shouldRestoreTracks() throws Exception {
		File musicDir = mock(File.class);
		File musicFile1 = File.createTempFile("file", "mp3");
		File musicFile2 = File.createTempFile("file2", "mp3");
		File musicFile3 = File.createTempFile("file3", "mp3");
		// unfortunately, it will be the same hashcode for all files
		Track track1 = mockTrack(musicFile1.getName());
		Track track2 = mockTrack("track2.mp3");
		List<Track> grayTracks = new ArrayList<Track>(Arrays.asList(new Track[] { track2, track1 }));
		when(musicEntityService.getAllReferences()).thenReturn(grayTracks);
		when(track1.getHashcode()).thenReturn(Hashcoder.createHashCode(musicFile1));
		when(track2.getFileName()).thenReturn(musicFile2.getName());
		List<File> musicfiles = new ArrayList<File>(Arrays.asList(musicFile1, musicFile2, musicFile3));
		when(musicEntityService.findMusicFiles(musicDir)).thenReturn(musicfiles);

		controller.restoreTracks(musicDir);

		verify(musicEntityService).addTrackFile(isA(TrackFile.class));
		verify(controlEngine).fireEvent(Events.Application.SEARCH_TRACKS, new ValueEvent<SearchState>(SearchState.Started));
		verify(shareService).run();
		verify(reporter).logUserAction(UserActions.Player.FIND_LOCAL_MEDIA);
	}

	private Track mockTrack(String name) {
		Track track1 = mock(Track.class);
		when(track1.getFileName()).thenReturn(name);
		return track1;
	}

}
