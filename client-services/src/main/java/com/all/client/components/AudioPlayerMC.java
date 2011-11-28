package com.all.client.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.services.MusicEntityService;
import com.all.shared.model.Track;

@Component
public class AudioPlayerMC extends BasePlayer implements MediaPlayer, AllMediaListener, Runnable {

	private static final int MAX_VELOCITY = 8;

	private static final int INCREMENT_VELOCITY_FACTOR = 2;

	private static final float GAIN_FACTOR = 0.8f;

	private final Log log = LogFactory.getLog(AudioPlayerMC.class);

	@Autowired
	private MusicEntityService musicEntityService;

	@Autowired
	private AllMediaProvider player;

	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	private double elapsedPlaybackTime = 0;

	private double lastTime = 0;

	private float volume;

	public AudioPlayerMC() {
	}

	@PostConstruct
	public void setup() {
		player.setAllMediaListener(this);
		service.scheduleAtFixedRate(this, 300, 300, TimeUnit.MILLISECONDS);
	}

	@PreDestroy
	public void teardown() {
		service.shutdownNow();
	}

	@Override
	public void changeVelocity(int velocity) {
		int receivedVelocity = velocity;
		// in case we are fast forwarding
		receivedVelocity = receivedVelocity > MAX_VELOCITY ? MAX_VELOCITY : receivedVelocity;
		// in case we are rewinding
		receivedVelocity = receivedVelocity < -MAX_VELOCITY ? -MAX_VELOCITY : receivedVelocity;
		int delta = receivedVelocity * INCREMENT_VELOCITY_FACTOR;
		player.setMediaTime(player.getMediaTime() + delta);
	}

	@Override
	public void changeVolume(float volume) {
		this.volume = volume;
		final float playerVolume = GAIN_FACTOR * volume;
		player.setVolume(playerVolume);
	}

	@Override
	public long getCurrentTime() {
		long currentTimeDouble = 0l;
		currentTimeDouble = (long) player.getMediaTime() * 1000;
		return currentTimeDouble;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public void pause() {
		player.pause();
	}

	@Override
	public void play(Track track, boolean forceRestart) {
		File soundFile = null;
		try {
			soundFile = musicEntityService.getFile(track);
			if (soundFile == null || soundFile.toURI() == null || !soundFile.exists()) {
				throw new FileNotFoundException(soundFile.getAbsolutePath());
			}
			play(track, soundFile, forceRestart);
		} catch (Exception e) {
			log.error(
					"Tried to play track : " + track + " but couldnt play it. > " + e.getClass().getName() + " : "
							+ e.getMessage(), e);
			if (soundFile != null && soundFile.exists()) {
				log.warn("Need codecs for this format");
				notifyError(PlayerErrorType.NO_CODECS, track);
			} else {
				notifyError(PlayerErrorType.FILE_NOT_FOUND, track);
			}
		}
	}

	private Semaphore playLock = new Semaphore(1);
	private AtomicInteger currentId = new AtomicInteger(0);

	private void play(Track track, File soundFile, boolean restart) {
		if (track == null) {
			throw new NullPointerException("Track is null");
		}
		int id = currentId.incrementAndGet();
		log.info("play[" + id + "] " + track + " REQUESTING LOCK");
		try {
			playLock.acquire();
		} catch (InterruptedException e1) {
			return;
		}
		try {
			log.info("play[" + id + "] " + track + " STARTED");
			if (id != currentId.get()) {
				log.info("play[" + id + "] " + track + " SKIP");
				// only the last one may survive in this.
				return;
			}
			if (!player.isPlaying(track) || restart) {
				// this call might deadlock for bad karma!
				player.play(track, soundFile);
			} else {
				// player is playing the current track and we just need to resume playback
				player.play();
			}
		} catch (Exception ex2) {
			log.error("play[" + id + "] " + track + " Player thread died before playing the track", ex2);
		} finally {
			playLock.release();
			log.info("play[" + id + "] " + track + " LOCK RELEASED");
		}
	}

	@Override
	public void stop() {
		Track track = player.getTrack();
		double duration = player.getDuration();
		player.stop();
		if (track != null) {
			notifyTrackDonePlaying(false, track, duration);
		}
	}

	@Override
	public void updateTime(long currentTime) {
		long totalDuration = (long) player.getDuration() * 1000;
		if (currentTime >= 0 && currentTime <= totalDuration) {
			player.setMediaTime(currentTime / 1000);
			// THIS IS REDUNDANT BUT IS NECESSARY BECAUSE player.setMediaTime IS NOT WORKING
			notifyProgress(currentTime);
		} else {
			notifyProgress();
		}
	}

	private void notifyProgress(long currentTime) {
		notifyProgress(currentTime, (long) player.getDuration() * 1000);
	}

	private void notifyProgress() {
		notifyProgress(getCurrentTime(), (long) player.getDuration() * 1000);
	}

	private void notifyTrackDonePlaying(boolean wasNaturalEnd, Track track, double playerDuration) {
		double millis = playerDuration * 1000;
		double elapsedPlaybackTime2 = elapsedPlaybackTime;

		elapsedPlaybackTime = 0;
		lastTime = 0;
		if (track == null) {
			return;
		}
		notifyTrackDonePlaying(track, (long) millis, (long) elapsedPlaybackTime2, wasNaturalEnd);
	}

	public void run() {
		if (player.isPlaying()) {
			long t = System.currentTimeMillis();
			if (lastTime != 0) {
				elapsedPlaybackTime += t - lastTime;
			}
			lastTime = t;
			notifyProgress();
		}
	}

	@Override
	public void endOfMediaReached(Track track, double playerDuration) {
		notifyTrackDonePlaying(true, track, playerDuration);
	}

	@Override
	public void onPlaying(Track track) {
		changeVolume(getVolume());
	}

	@Override
	public void mediaDurationChanged(Track track, double duration) {
		if (track != null && track.getDuration() == 0) {
			musicEntityService.updateDuration(track, (int) (duration));
		}
	}

}
