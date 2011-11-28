package com.all.client.components;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.client.model.TrackFile;
import com.all.client.services.MusicEntityService;
import com.all.shared.model.Track;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;
import com.sun.media.jmc.Media;

@RunWith(MockInyectRunner.class)
public class TestAudioPlayerMC {

	@UnderTest
	AudioPlayerMC playerMC;

	@Mock
	MediaPlayer player;

	@Mock
	private List<PlayerListener> playListeners;

	@Mock
	PlayerListener playerListener;

	@Mock
	MusicEntityService musicEntityService;

	@Mock
	TrackFile trackFile;

	@Mock
	Track currentTrack;

	@Mock
	Thread progressThread;

	@Mock
	Media media;

	boolean paused;

	@Before
	public void setup() throws URISyntaxException {

	}
	

	@Test
	public void shouldAddPlayerListener() throws Exception {
		playerMC.addPlayerListener(playerListener);

		verify(playListeners).add(playerListener);
	}

	@Test
	public void shouldRemovePlayerListener() throws Exception {
		playerMC.removePlayerListener(playerListener);

		verify(playListeners).remove(playerListener);
	}

	@Test
	public void shouldPlay() throws Exception {

		when(trackFile.getFile()).thenReturn(new File("file.txt"));

	}

}
