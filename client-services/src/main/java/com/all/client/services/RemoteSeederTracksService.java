package com.all.client.services;

import static com.all.shared.messages.MessEngineConstants.SEEDER_TRACK_LIST_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.SEEDER_TRACK_LIST_RESPONSE_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.download.SeederTracks;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Track;

@Service
public class RemoteSeederTracksService {
	@Autowired
	private MessEngine messEngine;
	private ArrayList<String> allTracks = new ArrayList<String>();
	private HashMap<String, List<String>> remoteTCache = new HashMap<String, List<String>>();

	@PostConstruct
	public void registerListeners() {
		messEngine.addMessageListener(SEEDER_TRACK_LIST_RESPONSE_TYPE, new MessageListener<AllMessage<SeederTracks>>() {

			@Override
			public void onMessage(AllMessage<SeederTracks> message) {
				SeederTracks response = message.getBody();
				allTracks.addAll(response.getTracks());
				remoteTCache.put(response.getSeederId(), response.getTracks());
			}
		});
	}

	public void requestSeederAvailableTracks(String seeder, String currentUser) {
		messEngine
				.send(new AllMessage<SeederTracks>(SEEDER_TRACK_LIST_REQUEST_TYPE, new SeederTracks(seeder, currentUser)));
	}

	public boolean isRemoteTrackAvailable(Track track) {
		return isRemoteTrackAvailable(track.getHashcode());
	}

	public boolean isRemoteTrackAvailable(String trackId) {
		return allTracks.contains(trackId);
	}

	public void updateCache(ContactInfo contactInfo) {
		if (!contactInfo.isOnline()) {
			String email = contactInfo.getEmail();
			List<String> remoteTracks = remoteTCache.get(email);
			if (remoteTracks != null) {
				allTracks.removeAll(remoteTracks);
			}
			remoteTCache.remove(email);
		}
	}

}
