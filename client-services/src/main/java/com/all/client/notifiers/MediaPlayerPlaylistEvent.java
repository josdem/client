package com.all.client.notifiers;

import java.util.EventObject;
import java.util.List;

import com.all.shared.model.Track;

public class MediaPlayerPlaylistEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final List<Track> trackList;
	private final Object playObject;

	public MediaPlayerPlaylistEvent(Object source, List<Track> trackList, Object playObject) {
		super(source);
		this.trackList = trackList;
		this.playObject = playObject;
	}

	public List<Track> getTrackList() {
		return trackList;
	}

	public Object getPlayObject() {
		return playObject;
	}

}
