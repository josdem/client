package com.all.client.services.reporting;

import java.util.LinkedList;
import java.util.List;

import com.all.shared.model.Track;

public class TopTrack {
	private final int count;
	private List<Track> tracks = new LinkedList<Track>();
	private int trackCount;

	public TopTrack(int count) {
		this.count = count;
	}

	public void add(Track track) {
		if (tracks.contains(track)) {
			return;
		}
		trackCount++;
		if (tracks.size() < count) {
			tracks.add(track);
			return;
		}
		int index = getNoMetadataIndex();
		if (!trackHasMetadata(track) && index == -1) {
			return;
		}
		if (!(trackHasMetadata(track) && index >= 0)) {
			index = (int) Math.floor((Math.random() * (count * 2)));
		}
		if (index >= 0 && index < count) {
			tracks.remove(index);
			tracks.add(track);
		}
	}

	private boolean trackHasMetadata(Track track) {
		return validString(track.getName()) && validString(track.getArtist());
	}

	private boolean validString(String string) {

		return string != null && !"".equals(string.trim());
	}

	private int getNoMetadataIndex() {
		for (int i = 0; i < tracks.size(); i++) {
			if (!trackHasMetadata(tracks.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public int getTrackCount() {
		return trackCount;
	}

}
