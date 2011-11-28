package com.all.client.components;

import com.all.shared.model.Track;

public class MockPlayer implements MediaPlayer {
	public boolean pause;
	long currentTime;
	float volume;

	@Override
	public void addPlayerListener(PlayerListener player) {
	}

	@Override
	public void changeVolume(float volume) {
		this.volume = volume;
	}

	@Override
	public long getCurrentTime() {
		return currentTime;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public void pause() {
		pause = true;
	}

	@Override
	public void play(Track track, boolean forcePlay) {
	}

	@Override
	public void stop() {
	}

	@Override
	public void updateTime(long currentTime) {
		this.currentTime = currentTime;
	}

	@Override
	public void changeVelocity(int velocity) {
	}

	@Override
	public void removePlayerListener(PlayerListener player) {
	}

}
