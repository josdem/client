package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.ADD_DOWNLOAD_TRACK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.client.components.MusicPlayer;
import com.all.client.model.RepeatType;
import com.all.client.services.MusicEntityService;
import com.all.client.services.UserPreferenceService;
import com.all.core.events.Events;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public class TestMusicPlayerController {
	private static final Long NINE_THOUSAND = 9000L;

	@InjectMocks
	private MusicPlayerController controller = new MusicPlayerController();
	@Mock
	private MusicPlayer musicPlayer;
	@Mock
	private MusicEntityService musicEntityService;
	@Mock
	private UserPreferenceService userPreferenceService;
	@Mock
	private ControlEngine controlEngine = mock(ControlEngine.class);
	@Mock
	private MessEngine messEngine;
	@Captor
	ArgumentCaptor<AllMessage<Track>> trackAllMessageCaptor;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		controller.initialize();
	}

	@Test
	public void shouldUpdateTime() throws Exception {
		// Because its OVER NINE THOUSAND!!!!
		controller.updateTime(NINE_THOUSAND);
		// WHAT, NINE THOUSAND?!!!!
		verify(musicPlayer).updateTime(NINE_THOUSAND);
	}

	@Test
	public void shouldTogleRepeatOnTheMusicPlayer() throws Exception {
		when(userPreferenceService.togglePlayerRepeatMode()).thenReturn(RepeatType.ONE);
		controller.toggleRepeat();
		verify(musicPlayer).setRepeat(RepeatType.ONE);
	}

	@Test
	public void shouldToggleShuffleOnTheMusicPlayer() throws Exception {
		when(userPreferenceService.togglePlayerShuffle()).thenReturn(true);
		controller.toggleShuffle();
		verify(musicPlayer).setShuffle(true);
	}

	@Test
	public void shouldPlayOnPlayer() throws Exception {
		Track track = mock(Track.class);
		controller.displayedPlayOrder = Arrays.asList(new Track[] { track });
		when(musicPlayer.getTrackInCurrentIndex()).thenReturn(track);
		when(musicEntityService.isFileAvailable(track)).thenReturn(true);
		controller.play();
		verify(musicPlayer).play();
	}

	@Test
	public void shouldChangeVolume() throws Exception {
		controller.setVolume(33);
		verify(musicPlayer).changeAudioVolume(33);
	}

	@Test
	public void shouldToggleMute() throws Exception {
		when(userPreferenceService.togglePlayerMute()).thenReturn(10);
		controller.toggleMute();
		verify(musicPlayer).changeAudioVolume(10);
	}

	@Test
	public void shouldDownloadGrayTrackAndNotStopPlaying() throws Exception {
		Track track = mock(Track.class);
		controller.displayedPlayOrder = Arrays.asList(new Track[] { track });
		// track file does not exist
		controller.playOrDownload();
		verify(musicPlayer, never()).stop();
		verify(messEngine).send(trackAllMessageCaptor.capture());
		AllMessage<Track> trackAllMessage = trackAllMessageCaptor.getValue();
		assertEquals(ADD_DOWNLOAD_TRACK, trackAllMessage.getType());
		assertEquals(track, trackAllMessage.getBody());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldAddDownloadInController() throws Exception {
		Track currentTrack = mock(Track.class);
		controller.displayedPlayOrder.add(currentTrack);
		when(musicEntityService.isFileAvailable(currentTrack)).thenReturn(false);

		controller.playOrDownload();

		verify(controlEngine, never()).fireEvent(eq(Events.Player.PLAYING_PLAYLIST_CHANGED), (ValueEvent<TrackContainer>) any());
		verify(messEngine).send(trackAllMessageCaptor.capture());
		AllMessage<Track> trackAllMessage = trackAllMessageCaptor.getValue();
		assertEquals(ADD_DOWNLOAD_TRACK, trackAllMessage.getType());
		assertEquals(currentTrack, trackAllMessage.getBody());
	}
}
