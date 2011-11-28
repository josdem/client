package com.all.client.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalTrack;
import com.all.client.model.RepeatType;
import com.all.client.services.MusicEntityService;
import com.all.client.util.PercentageUtil;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerProgressEvent;
import com.all.core.events.MediaPlayerStateEvent;
import com.all.core.events.MediaPlayerTrackPlayedEvent;
import com.all.core.events.MusicPlayerState;
import com.all.event.EventObject;
import com.all.event.EventType;
import com.all.observ.Observer;
import com.all.shared.model.Track;

public class TestMusicPlayer {

	@InjectMocks
	private MusicPlayer player = new MusicPlayer();
	@Mock
	private ControlEngine eventEngine;
	@Mock
	private MediaPlayer audioPlayer;
	@Mock
	private MusicEntityService musicEntityService;
	@SuppressWarnings("unused")
	@Spy
	private ShuffleProvider shuffleProvider = new MockShuffleProvider();

	private LocalTrack track0 = new LocalTrack("track 0", "0123456789abcdef0123456789abcdef01234567");
	private LocalTrack track1 = new LocalTrack("track 1", "123456789abcdef0123456789abcdef012345678");
	private LocalTrack track2 = new LocalTrack("track 2", "23456789abcdef0123456789abcdef0123456789");
	private LocalTrack track3 = new LocalTrack("track 3", "3456789abcdef0123456789abcdef0123456789a");
	private LocalTrack track4 = new LocalTrack("track 4", "456789abcdef0123456789abcdef0123456789ab");

	private List<Track> trackList;
	private PlayerListener listener;
	private MockTrackPlayedListener trackPlayedListener;

	@Before
	public void initPlayerPreferences() throws Exception {
		MockitoAnnotations.initMocks(this);
		player.initialize();
		trackPlayedListener = new MockTrackPlayedListener();
		player.onTrackPlayed().add(trackPlayedListener);
		listener = player;
		trackList = new ArrayList<Track>();
		trackList.add(track0);
		trackList.add(track1);
		trackList.add(track2);
		for (Track track : trackList) {
			((LocalTrack) track).setEnabled(true);
		}
		when(musicEntityService.isFileAvailable(any(Track.class))).thenReturn(true);
	}

	@Test
	public void shouldCheckInitialStateOfThaPlayer() throws Exception {
		verify(audioPlayer).addPlayerListener((PlayerListener) player);
		assertNull(player.getCurrentTrack());
		assertEquals(0, player.getCurrentIndex());
		assertEquals(MusicPlayerState.STOP, player.getState());
	}

	@Test
	public void shouldSetCurrentTrackToFirstOnThePlaylist() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		assertEquals(track0, player.getTrackInCurrentIndex());
	}

	@Test
	public void shouldSetCurrentTrackToFirstOnThePlaylistPart2() throws Exception {
		player.setCurrentPlaylist(trackList, -1);
		assertEquals(track0, player.getTrackInCurrentIndex());
	}

	@Test
	public void shouldSetCurrentTrackToLastOnThePlaylist() throws Exception {
		player.setCurrentPlaylist(trackList, 2);
		assertEquals(track2, player.getTrackInCurrentIndex());
	}

	@Test
	public void shouldSetCurrentTrackToLastOnThePlaylistPart2() throws Exception {
		player.setCurrentPlaylist(trackList, 99);
		assertEquals(track2, player.getTrackInCurrentIndex());
	}

	@Test
	public void shouldPlayInOrder() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.play();
		assertTrackPlaying(track0, 0);
		finishPlayTrack(track0);
		// Now playing next track
		assertTrackPlaying(track1, 1);
		finishPlayTrack(track1);
		// Now playing next track
		assertTrackPlaying(track2, 2);
		finishPlayTrack(track2);
		assertPlaybackFinished(track2, 2);
	}

	@Test
	public void shouldKeepMuteOnANewTrack() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.play();
		player.changeAudioVolume(0);
		assertEquals(0.0f, player.getVolume(), 0);
		assertTrackPlaying(track0, 0);
		finishPlayTrack(track0);
		// Now playing next track
		assertTrackPlaying(track1, 1);
		assertEquals(0.0f, player.getVolume(), 0);
	}

	@Test
	public void shouldStartFromSecondTrack() throws Exception {
		player.setCurrentPlaylist(trackList, 1);
		player.play();

		assertTrackPlaying(track1, 1);
		finishPlayTrack(track1);
		// Now playing next track
		assertTrackPlaying(track2, 2);
		finishPlayTrack(track2);
		assertPlaybackFinished(track2, 2);
	}

	@Test
	public void shouldRepeatPlaylist() throws Exception {
		player.setRepeat(RepeatType.ALL);
		player.setCurrentPlaylist(trackList, 2);
		player.play();
		assertTrackPlaying(track2, 2);
		finishPlayTrack(track2);
		assertTrackPlaying(track0, 0);
		finishPlayTrack(track0);
		assertTrackPlaying(track1, 1);
	}

	@Test
	public void shouldRepeatOne() throws Exception {
		player.setRepeat(RepeatType.ONE);
		player.setCurrentPlaylist(trackList, 1);
		player.play();
		assertTrackPlaying(track1, 1);
		finishPlayTrack(track1);
		assertTrackPlaying(track1, 1);
		finishPlayTrack(track1);
		assertTrackPlaying(track1, 1);
	}

	@Test
	public void shouldPlayTheNextTrackAfterARepeatOneLauchedByAForcedPlayAKAdoubleClick() throws Exception {
		player.setRepeat(RepeatType.ONE);
		player.setCurrentPlaylist(trackList, 1);
		player.forcePlay();
		assertTrackPlaying(track1, 1);
		player.forward();
		assertTrackPlaying(track2, 2);
	}

	@Test
	public void shouldForward() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.forward();
		assertTrackPlaying(track1, 1);
	}

	@Test
	public void shouldRepeatALLWhenLastTrackUnCheked() throws Exception {
		player.setRepeat(RepeatType.ALL);
		player.setCurrentPlaylist(trackList, 0);
		track2.setEnabled(false);
		player.forward();
		player.forward();
		assertTrackPlaying(track0, 0);
	}

	@Test
	public void shouldPrevious() throws Exception {
		player.setCurrentPlaylist(trackList, 2);
		player.previous();
		assertTrackPlaying(track1, 1);
	}

	@Test
	public void shouldPreviousSkippingDisableTracks() throws Exception {
		track1.setEnabled(false);
		player.setCurrentPlaylist(trackList, 2);
		player.previous();
		assertTrackPlaying(track0, 0);
	}

	@Test
	public void shouldPreviousMissingSkippingTrack() throws Exception {
		// Lanza un error cuando intente reprod. track1
		player.setCurrentPlaylist(trackList, 2);
		player.previous();
		listener.notifyError(null, null);
		assertTrackPlaying(track0, 0);
	}

	@Test
	public void shouldPause() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.play();
		assertTrackPlaying(track0, 0);
		player.pause();
		verify(audioPlayer).pause();
		assertEquals(MusicPlayerState.PAUSE, player.getState());
		verifyEvent(Events.Player.STATE_CHANGED, new MediaPlayerStateEvent(MusicPlayerState.PAUSE, track0, 0));
	}

	@Test
	public void shouldShufflePlaylist() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.setShuffle(true);
		player.play();
		assertTrackPlaying(track2, 0);
		finishPlayTrack(track2);
		assertTrackPlaying(track0, 1);
		finishPlayTrack(track0);
		assertTrackPlaying(track1, 2);
		finishPlayTrack(track1);
		assertPlaybackFinished(track1, 2);
	}

	@Test
	public void shouldSetShuffleAtTheMiddleOfPlayback() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.play();
		assertTrackPlaying(track0, 0);
		player.setShuffle(true);
		assertTrackPlaying(track0, 0);
		finishPlayTrack(track0);

		assertTrackPlaying(track1, 2);
		finishPlayTrack(track1);
		assertPlaybackFinished(track1, 2);
	}

	@Test
	public void shouldClearShuffleAtTheMiddleOfPlayback() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.setShuffle(true);
		player.play();
		assertTrackPlaying(track2, 0);
		player.setShuffle(false);
		assertTrackPlaying(track2, 0);
		finishPlayTrack(track2);

		assertPlaybackFinished(track2, 2);
	}

	@Test
	public void shouldNotResetShuffleIfShuffleSetToTheSameShit() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.setShuffle(false);
		player.play();
		assertTrackPlaying(track0, 0);
		player.setShuffle(false);
		assertTrackPlaying(track0, 0);
		finishPlayTrack(track0);
		assertTrackPlaying(track1, 1);
		player.setShuffle(false);
		finishPlayTrack(track1);
		assertTrackPlaying(track2, 2);
		player.setShuffle(false);
		finishPlayTrack(track2);
		assertPlaybackFinished(track2, 2);
	}

	@Test
	public void shouldSkipTrackIfError() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.play();
		assertTrackPlaying(track0, 0);
		listener.notifyError(null, null);
		assertTrackPlaying(track1, 1);
	}

	@Test
	public void shouldKeepPlayingIfNewPlaylistSetContainsAndShouldPlayTheSameTrackWeArePlaying() throws Exception {
		player.setCurrentPlaylist(trackList, 1);
		player.play();
		assertTrackPlaying(track1, 1);
		List<Track> newTrackList = new ArrayList<Track>();
		newTrackList.add(track1);
		newTrackList.add(track3);
		newTrackList.add(track4);

		Mockito.reset(eventEngine);
		Mockito.reset(audioPlayer);

		player.setCurrentPlaylist(newTrackList, 0);

		assertEquals(track1, player.getCurrentTrack());

		verify(eventEngine, never()).fireEvent(eq(Events.Player.TRACK_PLAYED), (MediaPlayerTrackPlayedEvent) any());

		assertEquals(MusicPlayerState.PLAYING, player.getState());
		verify(audioPlayer, never()).play((Track) anyObject(), anyBoolean());
		verify(eventEngine, never()).fireEvent(eq(Events.Player.STATE_CHANGED), (MediaPlayerStateEvent) any());
		finishPlayTrack(track1);
		assertTrackPlaying(track3, 1);
		finishPlayTrack(track3);
		assertTrackPlaying(track4, 2);
		finishPlayTrack(track4);
		assertPlaybackFinished(track4, 2);
	}

	@Test
	public void shouldRestartPlaybackOnNewTrackWhenADifferentTrackIsSet() throws Exception {
		player.setCurrentPlaylist(trackList, 1);
		player.play();
		assertTrackPlaying(track1, 1);
		List<Track> newTrackList = new ArrayList<Track>();
		newTrackList.add(track0);
		newTrackList.add(track3);
		newTrackList.add(track4);

		Mockito.reset(eventEngine);
		Mockito.reset(audioPlayer);

		player.setCurrentPlaylist(newTrackList, 0);
		// verify(audioPlayer).stop();
		finishPlayTrack(track1);
		assertEquals(track1, trackPlayedListener.eventArgs.getTrack());
		// assertTrackPlaying(track0, 0);
	}

	@Test
	public void shouldPlayAlwaysTheSelectedTrackRegardlessShuffle() throws Exception {
		// player.setShuffleProvider(new MockShuffleProvider());
		player.setCurrentPlaylist(trackList, 0);
		player.setShuffle(true);
		player.play();
		assertTrackPlaying(track2, 0);

		Mockito.reset(eventEngine);
		Mockito.reset(audioPlayer);

		player.setCurrentPlaylist(trackList, 1);
		// verify(audioPlayer).stop();
		finishPlayTrack(track2);
		assertEquals(track2, trackPlayedListener.eventArgs.getTrack());
		// assertTrackPlaying(track1, 0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldTestTheMiscellaneousThingsNoOneCaresAboutInsideTheMusicPlayer() throws Exception {
		player.setCurrentPlaylist(trackList, 0);
		player.updateTime(12);
		verify(audioPlayer).updateTime(12);
		player.reset();
		assertEquals(track0, player.getTrackInCurrentIndex());
		player.changeVelocity(12);
		verify(audioPlayer).changeVelocity(12);
		assertNotNull(player.onTrackPlayed());
		player.stopAudioPlayerMonitor();
		player.changeAudioVolume(30);
		verify(audioPlayer).changeVolume(PercentageUtil.convertPercentage(30));
		listener.notifyProgress(100, 200);
		verifyEvent(Events.Player.PROGRESS_CHANGED, new MediaPlayerProgressEvent(100, 200));
		new DefaultShuffleProvider().shuffle(Collections.EMPTY_LIST);
		MusicPlayerState.PLAYING.compareTo(MusicPlayerState.PLAYING);
		MusicPlayerState.PLAYING.name();
		MusicPlayerState.PLAYING.ordinal();
		MusicPlayerState.valueOf("PLAYING");

		RepeatType.ALL.compareTo(RepeatType.ALL);
		RepeatType.ALL.name();
		RepeatType.ALL.ordinal();
		RepeatType.valueOf("ALL");
		assertEquals(RepeatType.OFF, RepeatType.valueOf(0));
		assertNull(RepeatType.valueOf(12));
		assertEquals(0, RepeatType.OFF.value());
		assertEquals("repeatButton", RepeatType.OFF.synth());
	}

	private void finishPlayTrack(Track track) {
		Mockito.reset(audioPlayer);
		Mockito.reset(eventEngine);
		listener.notifyTrackDonePlaying(track, 1000, 1000, true);
		assertEquals(track, trackPlayedListener.eventArgs.getTrack());
	}

	private void assertPlaybackFinished(Track track, int i) throws Exception {
		assertEquals(track, player.getCurrentTrack());
		assertEquals(MusicPlayerState.STOP, player.getState());
		verifyEvent(Events.Player.TRACK_PLAYED, new MediaPlayerTrackPlayedEvent(null, i));
		verify(audioPlayer, never()).play((Track) anyObject(), anyBoolean());
		verifyEvent(Events.Player.STATE_CHANGED, new MediaPlayerStateEvent(MusicPlayerState.STOP, track, i));
	}

	private void assertTrackPlaying(Track track, int index) throws Exception {
		assertEquals(track, player.getCurrentTrack());
		verifyEvent(Events.Player.TRACK_PLAYED, new MediaPlayerTrackPlayedEvent(track, index));
		assertEquals(MusicPlayerState.PLAYING, player.getState());
		verify(audioPlayer).play(eq(track), anyBoolean());
		verifyEvent(Events.Player.STATE_CHANGED, new MediaPlayerStateEvent(MusicPlayerState.PLAYING, track, index));
	}

	private <X extends EventObject> void verifyEvent(EventType<X> eventType, X arg) throws Exception {
		verify(eventEngine).fireEvent(eventType, arg);
	}

	private final class MockTrackPlayedListener implements Observer<TrackPlayedEvent> {
		private TrackPlayedEvent eventArgs;

		@Override
		public void observe(TrackPlayedEvent eventArgs) {
			this.eventArgs = eventArgs;
		}
	}

	public class MockShuffleProvider implements ShuffleProvider {
		@Override
		public void shuffle(List<Track> tracks) {
			tracks.clear();
			tracks.add(track2);
			tracks.add(track0);
			tracks.add(track1);
		}
	}

}
