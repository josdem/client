package com.all.client.controller;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.TrackFile;
import com.all.client.peer.share.ShareService;
import com.all.client.services.MusicEntityService;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.Actions;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.core.model.SearchState;
import com.all.event.ValueEvent;
import com.all.shared.model.Track;
import com.all.shared.stats.usage.UserActions;

@Controller
public class RestoreTracksController {

	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private ShareService shareService;
	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired
	private ClientReporter reporter;

	private Log log = LogFactory.getLog(this.getClass());

	@ActionMethod(Actions.Library.FIND_TRACKS_LOCALLY_ID)
	public void restoreTracks(File dir) {
		log.debug("Searching in dir: " + dir);
		if (dir != null) {
			controlEngine.fireEvent(Events.Application.SEARCH_TRACKS, new ValueEvent<SearchState>(SearchState.Started));
			List<Track> grayTracks = musicEntityService.getAllReferences();
			log.debug("Gray tracks: " + grayTracks);
			List<File> musicFiles = musicEntityService.findMusicFiles(dir);
			log.debug("Music Files: " + musicFiles.size());
			MultiMap fileTrackMap = getMap(grayTracks);
			filterByTracks(musicFiles, fileTrackMap);
			log.debug("Filtered Files: " + musicFiles.size());
			matchTracksAndFiles(grayTracks, musicFiles, fileTrackMap);
			log.debug("Sharing restored files");
			shareService.run();
			controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));
			controlEngine.fireEvent(Events.Application.SEARCH_TRACKS, new ValueEvent<SearchState>(SearchState.Stopped));
			reporter.logUserAction(UserActions.Player.FIND_LOCAL_MEDIA);
		}
	}

	@SuppressWarnings("unchecked")
	private void matchTracksAndFiles(List<Track> grayTracks, List<File> musicFiles, MultiMap fileTrackMap) {
		for (File file : musicFiles) {
			TrackFile trackFile = new TrackFile(file);
			Predicate hashCodePredicate = new BeanPropertyValueEqualsPredicate("hashcode", trackFile.getHashcode());
			Collection<Track> fileTracks = (Collection<Track>) fileTrackMap.get(file.getName());
			Object sameFileTrack = CollectionUtils.find(fileTracks, hashCodePredicate);
			if (sameFileTrack != null) {
				grayTracks.remove(sameFileTrack);
				musicEntityService.addTrackFile(trackFile);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void filterByTracks(List<File> musicFiles, MultiMap fileTrackMap) {
		final Set<String> trackFileNames = fileTrackMap.keySet();
		Predicate fileNamePredicate = new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				File file = (File) object;
				return trackFileNames.contains(file.getName());
			}
		};
		CollectionUtils.filter(musicFiles, fileNamePredicate);
	}

	private MultiMap getMap(List<Track> grayTracks) {
		MultiMap map = new MultiHashMap(grayTracks.size());
		for (Track track : grayTracks) {
			map.put(track.getFileName(), track);
		}
		return map;
	}

}
