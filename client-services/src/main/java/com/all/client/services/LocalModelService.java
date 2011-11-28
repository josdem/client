package com.all.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalTrack;
import com.all.core.actions.Actions;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteTrack;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Service
public class LocalModelService {
	
	@Autowired
	private LocalModelDao dao;
	
	@Autowired
	private MusicEntityService musicEntityService;
	
	@Autowired
	private RemoteSeederTracksService remoteSeederTracksService;
	
	@ActionMethod(Actions.Application.UPDATE_USER_OBJECT_EDITTED_ID)
	public void updateUserObject(Object userObject){
		dao.update(userObject);
	};
	
	@SuppressWarnings("unused")
	@RequestMethod(Actions.Application.REQUEST_IS_TRACK_DOWNLODABLE_ID)
	private boolean isTrackDownloadable( TrackContainer tracks) {
		if (tracks instanceof Playlist) {
			for (Track track : tracks.getTracks()) {
				if (!musicEntityService.isFileAvailable(track) && track instanceof LocalTrack
						&& dao.findByHashcode(track.getHashcode()) != null) {
					return true;
				} else if (!musicEntityService.isFileAvailable(track) && track instanceof RemoteTrack
						&& remoteSeederTracksService.isRemoteTrackAvailable(track)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	@RequestMethod(Actions.Application.REQUEST_FIND_TRACK_ID)
	private Track findTrackById(String id){
		return dao.findById(LocalTrack.class, id);
	}
}
