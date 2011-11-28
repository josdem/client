/**
 * 
 */
package com.all.client.view.observs;

import java.util.EventObject;

import com.all.shared.model.Track;

public class TrackModifiedEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private Track track;
	private Object selectedItem;

	public TrackModifiedEvent(Track track, Object selectedItem) {
		super(track);
		this.track = track;
		this.selectedItem = selectedItem;
	}

	public Track getTrack() {
		return track;
	}

	public Object getSelectedItem() {
		return selectedItem;
	}

}