package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.ADD_DOWNLOAD_TRACK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.components.MusicPlayer;
import com.all.client.model.PlaylistOrder;
import com.all.client.model.RepeatType;
import com.all.client.services.MusicEntityService;
import com.all.client.services.UserPreferenceService;
import com.all.core.actions.Actions;
import com.all.core.events.Events;
import com.all.core.events.MusicPlayerState;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Controller
public class MusicPlayerController {
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired
	private MusicPlayer musicPlayer;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private UserPreferenceService userPreferenceService;

	private int currentIndex;
	private TrackContainer playingContainer;
	private TrackContainer displayedContainer;
	List<Track> displayedPlayOrder = new ArrayList<Track>();

	// private Log log = LogFactory.getLog(this.getClass());

	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void initialize() {
		musicPlayer.changeAudioVolume(userPreferenceService.getPlayerVolume());
		musicPlayer.setShuffle(userPreferenceService.isPlayerShuffleOption());
		musicPlayer.setRepeat(userPreferenceService.getPlayerRepeatMode());
	}

	@Autowired
	public void setTrackActions(TrackPlayedListener trackPlayedListener) {
		musicPlayer.onTrackPlayed().add(trackPlayedListener);
	}

	@ActionMethod(Actions.Player.UPDATE_TIME_ID)
	public void updateTime(Long time) {
		musicPlayer.updateTime(time);
	}

	@RequestMethod(Actions.Player.REQUEST_TOGGLE_REPEAT_ID)
	public RepeatType toggleRepeat() {
		RepeatType repeatMode = userPreferenceService.togglePlayerRepeatMode();
		musicPlayer.setRepeat(repeatMode);
		return repeatMode;
	}

	@RequestMethod(Actions.Player.REQUEST_TOGGLE_SHUFFLE_ID)
	public Boolean toggleShuffle() {
		boolean shuffle = userPreferenceService.togglePlayerShuffle();
		musicPlayer.setShuffle(shuffle);
		return shuffle;
	}

	@ActionMethod(Actions.Player.STOP_ID)
	public void stop() {
		musicPlayer.stop();
		musicPlayer.reset();
	}

	@MessageMethod(MessEngineConstants.VALIDATE_STOP_PLAYER)
	public void validateStopPlayer(AllMessage<List<String>> allMessage) {
		List<String> hashcodes = allMessage.getBody();
		if (isPlaying() && hashcodes.contains(getCurrentTrack().getHashcode())) {
			stop();
		}
	}

	private boolean isPlaying() {
		return musicPlayer.getState() != MusicPlayerState.STOP;
	}

	@ActionMethod(Actions.Player.PAUSE_ID)
	public void pause() {
		musicPlayer.pause();
	}

	@ActionMethod(Actions.Player.UPDATE_VOLUME_ID)
	public void setVolume(Integer volume) {
		userPreferenceService.updatePlayerVolume(volume);
		musicPlayer.changeAudioVolume(volume);
	}

	@ActionMethod(Actions.Player.TOGGLE_MUTE_ID)
	public void toggleMute() {
		int volume = userPreferenceService.togglePlayerMute();
		musicPlayer.changeAudioVolume(volume);
	}

	public int getCurrentIndex() {
		return musicPlayer.getCurrentIndex();
	}

	@RequestMethod(Actions.Player.REQUEST_CURRENT_TRACK_ID)
	public Track getCurrentTrack() {
		return musicPlayer.getCurrentTrack();
	}

	@ActionMethod(Actions.Player.UPDATE_PLAYLIST_ORDER_ID)
	public void setPlaylist(PlaylistOrder playlistOrder) {
		List<Track> playOrder = playlistOrder.getPlayOrder();
		this.displayedPlayOrder = playOrder;
		this.displayedContainer = playlistOrder.getTrackContainer();
		if (displayedContainer == playingContainer) {
			// We need to give the player the new order the tracks will be
			// played
			// without disrupting current playback.
			Track currentTrack = musicPlayer.getCurrentTrack();
			int playIndex = playOrder.indexOf(currentTrack);
			if (playIndex < 0) {
				// We set a 1 item list with only this track since it will be
				// the last
				// one to be played on this session.
				playOrder = new ArrayList<Track>(1);
				playOrder.add(currentTrack);
				playIndex = 0;
			}
			// Set the current track to where the track lies inside the new
			// playorder
			musicPlayer.setCurrentPlaylist(playOrder, playIndex);
		}
		setCurrentIndex(null);
	}

	@ActionMethod(Actions.Player.UPDATE_CURRENT_INDEX_ID)
	public void setCurrentIndex(int[] currentIndexArray) {
		List<Track> tracks = Collections.emptyList();
		if (currentIndexArray != null && currentIndexArray.length > 0) {
			int[] currentIndexArrayClone = currentIndexArray == null ? null : currentIndexArray.clone();
			this.currentIndex = currentIndexArrayClone[0];
			tracks = new ArrayList<Track>(currentIndexArrayClone.length);
			for (int i = 0; i < currentIndexArrayClone.length; i++) {
				int index = currentIndexArray[i];
				if (index < displayedPlayOrder.size()) {
					tracks.add(displayedPlayOrder.get(index));
				}
			}
			controlEngine.fireValueEvent(Events.Player.SELECTED_TRACKS_CHANGED, tracks);
		}
		if (tracks.isEmpty()) {
			this.currentIndex = 0;
			controlEngine.fireValueEvent(Events.Player.SELECTED_TRACKS_CHANGED, this.displayedPlayOrder);
		}

	}

	// it occurs when the button play is pressed
	@ActionMethod(Actions.Player.PLAY_ID)
	public void play() {
		if (musicPlayer.getState() != MusicPlayerState.PAUSE) {
			setPlayingPlaylistAndNotify();
		}
		musicPlayer.play();
	}

	@ActionMethod(Actions.Player.PLAY_DOWNLOAD_ID)
	public void playOrDownload() {
		Track currentTrack = displayedPlayOrder.get(currentIndex);
		if (musicEntityService.isFileAvailable(currentTrack)) {
			setPlayingPlaylistAndNotify();
			musicPlayer.forcePlay();
		} else {
			messEngine.send(new AllMessage<Track>(ADD_DOWNLOAD_TRACK, currentTrack));
		}
	}

	private void setPlayingPlaylistAndNotify() {
		boolean hasOneFileAvailable = false;
		for (Track track : displayedPlayOrder) {
			if (musicEntityService.isFileAvailable(track)) {
				hasOneFileAvailable = true;
				break;
			}
		}
		if (hasOneFileAvailable) {
			playingContainer = displayedContainer;
			musicPlayer.setCurrentPlaylist(displayedPlayOrder, currentIndex);
			controlEngine.set(Model.PLAYING_TRACKCONTAINER, displayedContainer, null);
			controlEngine.fireEvent(Events.Player.PLAYING_PLAYLIST_CHANGED,
					new ValueEvent<TrackContainer>(displayedContainer));
		} else {
			throw new IllegalArgumentException("cant play this playlist is evil since it does not have any references.");
		}
	}

	@ActionMethod(Actions.Player.FORWARD_ID)
	public void forward() {
		musicPlayer.forward();
	}

	@ActionMethod(Actions.Player.PREVIOUS_ID)
	public void previous() {
		musicPlayer.previous();
	}

	@ActionMethod(Actions.Player.CHANGE_VELOCITY_ID)
	public void changeVelocity(Integer velocity) {
		musicPlayer.changeVelocity(velocity);
	}

	// public ObserverCollection<ObservePropertyChanged<MusicPlayerController, List<Track>>> onSelectedTracksChanged() {
	// return selectedTracks.on();
	// }
}
