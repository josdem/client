package com.all.client.services;

import org.springframework.stereotype.Service;

import com.all.appControl.RequestMethod;
import com.all.core.actions.Actions;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Service
public class ModelActionService {
	@RequestMethod(Actions.Application.getTracksFromContainerId)
	public Iterable<Track> getTracksFromContainer(TrackContainer trackContainer) {
		return trackContainer.getTracks();
	}

}
