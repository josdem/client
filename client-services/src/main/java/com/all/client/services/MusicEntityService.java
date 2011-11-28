package com.all.client.services;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.data.TrackFactory;
import com.all.client.model.InvalidFileException;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.PlaylistTrack;
import com.all.client.model.TrackFile;
import com.all.client.util.TrackRepository;
import com.all.core.actions.Actions;
import com.all.core.actions.UpdateTrackRatingAction;
import com.all.core.common.services.reporting.Reporter;
import com.all.core.model.Model;
import com.all.downloader.alllink.AllLink;
import com.all.messengine.MessEngine;
import com.all.shared.download.TrackProvider;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.stats.TrackRatingStat;

@Service
public class MusicEntityService implements TrackProvider {

	private final Log log = LogFactory.getLog(this.getClass());

	private final TrackFileManager fileManager = new TrackFileManager();
	@Autowired
	private LocalModelDao dao;
	@Autowired
	private TrackFactory trackFactory;
	@Autowired
	private Reporter reporter;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private RemoteSeederTracksService remoteSeederTracksService;
	@Autowired
	private MessEngine messEngine;

	@PostConstruct
	public void setUp() {
		controlEngine.set(Model.TRACK_REPOSITORY, fileManager, null);
	}

	public void moveTo(Playlist playlist, Folder destination) {
		if (playlist instanceof LocalPlaylist) {
			((LocalPlaylist) playlist).setParentFolder(destination);
			((LocalPlaylist) playlist).setModifiedDate(new Date());
			dao.update(playlist);
		}
	}

	public void removeFrom(Track track, Playlist playlist) {
		if (playlist instanceof LocalPlaylist) {
			PlaylistTrack playlistTrack = ((LocalPlaylist) playlist).remove(track);
			if (playlistTrack != null) {
				dao.delete(playlistTrack);
				dao.update(playlist);
			}
		}
	}

	public List<Track> getAllReferences() {
		List<Track> allReferences = dao.findAll(Track.class);
		Iterator<Track> iterator = allReferences.iterator();
		while (iterator.hasNext()) {
			Track track = iterator.next();
			if (isFileAvailable(track)) {
				iterator.remove();
			}
		}
		return allReferences;
	}

	public void removeFromNewContent(Track track) {
		if (track instanceof LocalTrack) {
			LocalTrack localTrack = (LocalTrack) track;
			localTrack.setNewContent(false);
			dao.update(localTrack);
		}
	}

	public void updateDuration(Track track, int newDuration) {
		if (track != null && track.getDuration() != newDuration && track instanceof LocalTrack) {
			((LocalTrack) track).setDuration(newDuration);
			dao.update(track);
		}
	}

	public boolean isFileAvailable(Track track) {
		return fileManager.getValidFileTrack(track) != null;
	}

	public File getFile(Track track) {
		return fileManager.getValidFileTrack(track);
	}

	public void updateDownloadString(String hashcode, String downloadString) {
		Track track = dao.findByHashcode(hashcode);
		if (track != null && !track.getDownloadString().equals(downloadString) && track instanceof LocalTrack) {
			((LocalTrack) track).setDownloadString(downloadString);
			dao.update(track);
		}
	}

	public Track updateDownloadedTrack(Track currentTrack, File file) throws InvalidFileException {
		TrackFile newTrackFile = new TrackFile(file);
		Track newTrack = trackFactory.createTrack(newTrackFile);
		((LocalTrack) newTrack).setDateDownloaded(new Date());
		if (!currentTrack.getHashcode().equals(newTrack.getHashcode())) {
			replaceTrack(currentTrack, newTrack);
		} else {
			dao.update(newTrack);
		}
		dao.saveOrUpdate(newTrackFile);
		fileManager.add(newTrackFile);
		return newTrack;
	}

	public List<Track> getAllTracks() {
		return dao.findAll(Track.class);
	}

	@ActionMethod(Actions.Library.UPDATE_TRACK_RATING_ID)
	public void updateRating(UpdateTrackRatingAction action) {
		Track track = action.getTrack();
		if (track instanceof LocalTrack) {
			LocalTrack localTrack = (LocalTrack) track;
			int ratingValue = action.getRating();
			localTrack.setRating(ratingValue);
			dao.update(localTrack);
			reporter.log(new TrackRatingStat(controlEngine.get(Model.CURRENT_USER).getEmail(), track.getHashcode(),
					ratingValue));
		}
	}

	@ActionMethod(Actions.Library.TOGGLE_TRACK_ENABLED_ID)
	public void toggleEnabled(Track track) {
		if (track instanceof LocalTrack) {
			LocalTrack localTrack = (LocalTrack) track;
			localTrack.setEnabled(!track.isEnabled());
			dao.update(localTrack);
		}
	}

	public void addPlaycount(Track track) {
		if (track != null && track instanceof LocalTrack) {
			((LocalTrack) track).incrementPlaycount();
			dao.update(track);
		}
	}

	public void addSkipcount(Track track) {
		if (track != null && track instanceof LocalTrack) {
			((LocalTrack) track).incrementSkips();
			dao.update(track);
		}
	}

	@Override
	public Track getTrack(String trackId) {
		return dao.findByHashcode(trackId);
	}

	@Override
	public File getFile(String trackId) {
		return fileManager.getFile(trackId);
	}

	public void deleteFile(Track track) {
		fileManager.deleteFile(track);
	}

	@ActionMethod(Actions.Library.DELETE_FILES_ID)
	public void deleteFiles(List<Track> deletableTracks) {
		fileManager.deleteFiles(deletableTracks);
		messEngine.send(new AllMessage<List<Track>>(MessEngineConstants.DELETE_TRACKS_REFERENCES, deletableTracks));
	}

	public List<File> findMusicFiles(File dir) {
		return trackFactory.listTracks(dir);
	}

	public void addTrackFile(TrackFile trackFile) {
		fileManager.add(trackFile);
	}

	private void replaceTrack(Track currentTrack, Track newTrack) {
		try {
			LocalTrack newLocalTrack = (LocalTrack) newTrack;
			List<PlaylistTrack> deprecatedPlaylistTracks = findAndDeleteDeprecatedPlaylists(currentTrack);
			AllLink newLink = new AllLink(newTrack.getHashcode(), currentTrack.getHashcode());
			newLocalTrack.setDownloadString(newLink.toString());
			dao.delete(currentTrack);
			dao.update(dao.findDownloadByTrackId(currentTrack.getHashcode()));
			dao.saveOrUpdate(newLocalTrack);
			for (PlaylistTrack playlistTrack : deprecatedPlaylistTracks) {
				dao.save(new PlaylistTrack(newLocalTrack, playlistTrack.getPlaylist()));
			}
		} catch (Exception e) {
			log.error("Could not replace downloaded track.", e);
		}
	}

	private List<PlaylistTrack> findAndDeleteDeprecatedPlaylists(Track track) {
		List<PlaylistTrack> deprecatedPlaylistTracks = dao.findPlaylistTracks(track);
		for (PlaylistTrack playlistTrack : deprecatedPlaylistTracks) {
			dao.delete(playlistTrack);
		}
		return deprecatedPlaylistTracks;
	}

	private final class TrackFileManager implements TrackRepository {

		private Map<String, TrackFile> cache = new HashMap<String, TrackFile>();

		public TrackFile getTrackFile(Track track) {
			return getTrackFile(track.getHashcode());
		}

		public TrackFile getTrackFile(String id) {
			TrackFile trackFile = cache.get(id);
			if (trackFile == null) {
				trackFile = dao.findById(TrackFile.class, id);
				if (trackFile == null) {
					trackFile = new TrackFile(id);
				}
				if (trackFile.exists()) {
					cache.put(id, trackFile);
				}
			}
			return trackFile;
		}

		public void deleteFile(Track track) {
			TrackFile fileTrack = getTrackFile(track);
			fileTrack.deleteFile();
			dao.delete(fileTrack);
		}

		public void deleteFiles(List<Track> deletableTracks) {
			for (Track track : deletableTracks) {
				deleteFile(track);
			}
		}

		private File validateFile(TrackFile fileTrack) {
			File file = fileTrack.getFile();
			if (file != null && file.exists()) {
				return file;
			}
			return null;
		}

		public File getValidFileTrack(Track track) {
			TrackFile fileTrack = this.getTrackFile(track);
			return validateFile(fileTrack);
		}

		public void add(TrackFile trackFile) {
			dao.saveOrUpdate(trackFile);
			if (trackFile.exists()) {
				cache.put(trackFile.getHashcode(), trackFile);
			}
		}

		@Override
		public boolean isLocallyAvailable(String trackId) {
			return validateFile(getTrackFile(trackId)) != null;
		}

		@Override
		public boolean isRemotelyAvailable(String trackId) {
			return remoteSeederTracksService.isRemoteTrackAvailable(trackId);
		}

		@Override
		public File getFile(String trackId) {
			TrackFile fileTrack = this.getTrackFile(trackId);
			return validateFile(fileTrack);
		}

		@Override
		public boolean isAllLocallyAvailable(ModelCollection modelCollection) {
			List<Track> tracks = modelCollection.getTracks();
			for (Track track : tracks) {
				if (!isFileAvailable(track)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean isFormatSupported(File file) {
			return trackFactory.isFileFormatSupported(file);
		}

	}
}
