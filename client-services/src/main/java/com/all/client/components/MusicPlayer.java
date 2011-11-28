package com.all.client.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.RepeatType;
import com.all.client.services.MusicEntityService;
import com.all.client.util.PercentageUtil;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerProgressEvent;
import com.all.core.events.MediaPlayerStateEvent;
import com.all.core.events.MediaPlayerTrackPlayedEvent;
import com.all.core.events.MusicPlayerErrorEvent;
import com.all.core.events.MusicPlayerErrorType;
import com.all.core.events.MusicPlayerState;
import com.all.event.ValueEvent;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;
import com.all.shared.model.Track;

@Component
public class MusicPlayer implements PlayerListener {
	private final Log log = LogFactory.getLog(this.getClass());

	// COLLABORATORS
	@Autowired
	private MediaPlayer audioPlayer;
	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired
	private ControlEngine eventEngine;
	private ShuffleProvider shuffleProvider = new DefaultShuffleProvider();
	// EVENTS
	private Observable<TrackPlayedEvent> trackPlayed = new Observable<TrackPlayedEvent>();
	// FIELDS
	private List<Track> originalTrackList = new ArrayList<Track>();
	private List<Track> trackList = new ArrayList<Track>();
	private RepeatType repeatType = RepeatType.OFF;
	private MusicPlayerState currentState = MusicPlayerState.STOP;
	private boolean moveForward = true;
	private boolean goingForward = true;
	private int currentIndex;
	private boolean shuffle;
	private Track currentTrack;

	@PostConstruct
	public void initialize() {
		this.audioPlayer.addPlayerListener(this);
	}

	public void changeAudioVolume(int volume) {
		audioPlayer.changeVolume(PercentageUtil.convertPercentage(volume));
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(volume));
	}

	@Override
	public void notifyError(PlayerErrorType errorType, Track track) {
		log.error("Player error: " + errorType + " in track: " + track);
		MusicPlayerErrorType type = MusicPlayerErrorType.FILE_NOT_FOUND;
		if (errorType != null) {
			switch (errorType) {
			case NO_CODECS:
				type = MusicPlayerErrorType.NO_CODECS;
				break;
			}
		}
		eventEngine.fireEvent(Events.Player.PLAY_ERROR, new MusicPlayerErrorEvent(type, track));
		if (goingForward) {
			forward();
		} else {
			previous();
		}
	}

	@Override
	public void notifyProgress(long currentTime, long totalDuration) {
		eventEngine.fireEvent(Events.Player.PROGRESS_CHANGED, new MediaPlayerProgressEvent(currentTime, totalDuration));
	}

	@Override
	public void notifyTrackDonePlaying(Track track, long duration, long netPlayedTime, boolean wasNaturalEnd) {
		log.info("Track: " + track + " played in: " + netPlayedTime + " duration: " + duration + " wasNatural:"
				+ wasNaturalEnd);
		trackPlayed.fire(new TrackPlayedEvent(track, duration, netPlayedTime));
		if (!wasNaturalEnd) {
			return;
		}
		if (RepeatType.ONE == repeatType) {
			if (getTrackInCurrentIndex().isEnabled()) {
				play();
			} else {
				stop();
			}
		} else {
			forward();
		}
	}

	public Track getCurrentTrack() {
		return currentTrack;
	}

	public Track getTrackInCurrentIndex() {
		if (trackList.isEmpty() || currentIndex < 0 || currentIndex >= trackList.size()) {
			return null;
		} else {
			return trackList.get(currentIndex);
		}
	}

	public void forward() {
		log.info("Player forwarding to next track");
		goingForward = true;
		if (moveForward) {
			if (!isLastTrack()) {
				goToNextEnabledTrack();
				log.debug(currentTrack);
				if (getTrackInCurrentIndex() == null) {
					repeatOrStop();
				} else {
					stop();
					play();
				}
			} else {
				repeatOrStop();
			}
		} else {
			moveForward = true;
			play();
		}
	}

	public void setCurrentPlaylist(List<Track> playlist, int index) {
		log.info("Playlist changed in player now with " + playlist.size() + " tracks;");
		goingForward = true;
		int receivedIndex = index;
		if (playlist.isEmpty()) {
			this.originalTrackList = new ArrayList<Track>(playlist);
			this.currentIndex = receivedIndex;
			stop();
			return;
		}
		if (receivedIndex < 0) {
			receivedIndex = 0;
		}
		if (receivedIndex >= playlist.size()) {
			receivedIndex = playlist.size() - 1;
		}
		Track track = playlist.get(receivedIndex);
		log.info("Will play: " + track);
		// boolean requireRestart = currentState == MusicPlayerState.PLAYING
		// && !(currentTrack == track || (currentTrack != null &&
		// currentTrack.equals(track)));
		this.originalTrackList = new ArrayList<Track>(playlist);
		this.currentIndex = receivedIndex;
		resolveShuffle(track);
		if (shuffle) {
			this.currentIndex = 0;
		}
		// if (requireRestart) {
		// moveForward = false;
		// stop();
		// play(track, false);
		// }
	}

	private void resolveShuffle(Track track) {
		if (shuffle) {
			// No shuffle yet
			this.trackList = new ArrayList<Track>(originalTrackList);
			shuffleProvider.shuffle(trackList);
			if (track != null) {
				this.trackList.remove(track);
				List<Track> newTrackList = new ArrayList<Track>();
				newTrackList.add(track);
				newTrackList.addAll(this.trackList);
				this.trackList = newTrackList;
			}
		} else {
			// No shuffle yet
			this.trackList = originalTrackList;
		}
	}

	private void repeatOrStop() {
		if (repeatType != RepeatType.OFF) {
			currentIndex = 0;
			play();
		} else {
			stop();
		}
	}

	private void goToNextEnabledTrack() {
		do {
			currentIndex++;
		} while (getTrackInCurrentIndex() != null
				&& !(getTrackInCurrentIndex().isEnabled() && musicEntityService.isFileAvailable(getTrackInCurrentIndex())));
	}

	private boolean isLastTrack() {
		if (isTheNextTrackLastTrackAndUncheked()) {
			return true;
		}
		return currentIndex >= trackList.size() - 1;
	}

	private boolean isTheNextTrackLastTrackAndUncheked() {
		if (currentIndex + 1 == trackList.size() - 1) {
			Track nextTrack = trackList.get(currentIndex + 1);
			if (nextTrack != null && !nextTrack.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public void previous() {
		log.info("will play previous track");
		goingForward = false;
		if (audioPlayer.getCurrentTime() > 2010L) {
			audioPlayer.updateTime(0L);
		} else {
			previousTrack();
		}
	}

	private void previousTrack() {
		goToPreviousEnabledTrack();
		if (getTrackInCurrentIndex() == null) {
			if (RepeatType.OFF != repeatType) {
				currentIndex = trackList.size();
				previousTrack();
			} else {
				currentIndex = 0;
				stop();
			}
		} else {
			stop();
			play();
		}
	}

	private void goToPreviousEnabledTrack() {
		do {
			currentIndex--;
		} while (getTrackInCurrentIndex() != null
				&& !(getTrackInCurrentIndex().isEnabled() && musicEntityService.isFileAvailable(getTrackInCurrentIndex())));
	}

	// TODO: Move change state to audioPlayer
	public void stop() {
		log.info("player stoped");
		audioPlayer.stop();
		currentState = MusicPlayerState.STOP;
		notifyPlayerState();
		// Notify null track to update view.
		eventEngine.fireEvent(Events.Player.TRACK_PLAYED, new MediaPlayerTrackPlayedEvent(null, currentIndex));
	}

	public void play() {
		log.info("play");
		play(getTrackInCurrentIndex(), false);
	}

	public void forcePlay() {
		log.info("forcePlay");
		play(getTrackInCurrentIndex(), true);
	}

	private void play(Track track, boolean forcePlay) {
		currentTrack = track;
		// Since there's no resume state the player always changes to play as a new
		// track so, the progress bar is set to 0
		// so that we need to validate when play is executed after a pause
		if (currentState != MusicPlayerState.PAUSE || forcePlay) {
			eventEngine.fireEvent(Events.Player.TRACK_PLAYED, new MediaPlayerTrackPlayedEvent(getTrackInCurrentIndex(),
					currentIndex));
		}

		if (currentState == MusicPlayerState.PLAYING) {
			// Avoid state changed notification.
			log.info("stoping player before a play");
		}
		if (currentTrack != null) {
			if (currentTrack.isEnabled()) {
				if (currentTrack.isNewContent()) {
					musicEntityService.removeFromNewContent(currentTrack);
				}
				log.info("playing " + currentTrack + " in player!");
				audioPlayer.play(currentTrack, forcePlay);
				currentState = MusicPlayerState.PLAYING;
				notifyPlayerState();
			} else {
				forward();
			}
		}
	}

	private void notifyPlayerState() {
		MusicPlayerState state = getState();
		Track track = getTrackInCurrentIndex();
		eventEngine.fireEvent(Events.Player.STATE_CHANGED, new MediaPlayerStateEvent(state, track, currentIndex));
	}

	public void pause() {
		log.info("pause!");
		audioPlayer.pause();
		currentState = MusicPlayerState.PAUSE;
		notifyPlayerState();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public MusicPlayerState getState() {
		return currentState;
	}

	public void setShuffle(boolean shuffle) {
		if (this.shuffle != shuffle) {
			this.shuffle = shuffle;
			resolveShuffle(null);
			if (currentState == MusicPlayerState.PLAYING || currentState == MusicPlayerState.PAUSE) {
				currentIndex = trackList.indexOf(currentTrack);
				// moveForward = false;
			}
		}
	}

	public void setRepeat(RepeatType repeatType) {
		this.repeatType = repeatType;
	}

	public void updateTime(long time) {
		audioPlayer.updateTime(time);
	}

	public void reset() {
		currentIndex = 0;
	}

	public void changeVelocity(int velocity) {
		audioPlayer.changeVelocity(velocity);
	}

	@PreDestroy
	public void stopAudioPlayerMonitor() {
		audioPlayer.stop();
	}

	public ObserverCollection<TrackPlayedEvent> onTrackPlayed() {
		return trackPlayed;
	}

	public float getVolume() {
		return audioPlayer.getVolume();
	}

}
