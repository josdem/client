package com.all.client.importx.itunes.xml.legacy;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.importx.itunes.ImportItunesLibraryException;
import com.all.client.importx.itunes.xml.Visitor;
import com.all.client.importx.itunes.xml.dto.XmlLibrary;
import com.all.client.importx.itunes.xml.dto.XmlPlaylist;
import com.all.client.importx.itunes.xml.dto.XmlTrack;
import com.all.client.model.BrokenLinkValidator;
import com.all.client.model.FailedImportFolder;
import com.all.client.model.FailedImportPlaylist;
import com.all.client.model.FileFormatSupportedValidatorFactory;
import com.all.client.model.InvalidFileException;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.format.FileFormatSupportedValidator;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class VisitorImpl implements Visitor {
	private static Log log = LogFactory.getLog(VisitorImpl.class);
	private BrokenLinkValidator brokenLinkValidator = new BrokenLinkValidator();
	private FileFormatSupportedValidatorFactory fileFormatSupportedValidatorFactory = new FileFormatSupportedValidatorFactory();

	private Map<String, List<String>> podcastTracksMap = new HashMap<String, List<String>>();
	private List<Track> failedImportLooseTracksList = null;
	private List<FailedImportPlaylist> failedImportPlaylists = new ArrayList<FailedImportPlaylist>();
	private List<FailedImportFolder> failedImportFolders = new ArrayList<FailedImportFolder>();
	private Map<String, FailedImportPlaylist> failedImportPlaylistMap = new HashMap<String, FailedImportPlaylist>();
	private Map<String, Track> drmProtectedFailedImportMap = new HashMap<String, Track>();
	private Map<String, Track> brokenLinkFailedImportMap = new HashMap<String, Track>();
	private Map<String, Track> unsupportedFormatFailedImportMap = new HashMap<String, Track>();
	private Map<String, Track> unknownFailedImportMap = new HashMap<String, Track>();
	private List<Playlist> playlists = new ArrayList<Playlist>();
	private List<Folder> folders = new ArrayList<Folder>();
	private Map<String, String> childParentMap = new HashMap<String, String>();
	private Map<String, LocalFolder> folderMap = new HashMap<String, LocalFolder>();
	private Map<String, LocalPlaylist> playlistMap = new HashMap<String, LocalPlaylist>();
	private Map<String, Track> tracks = new HashMap<String, Track>();

	private LocalModelFactory modelFactory;
	private LocalModelDao dao;
	private boolean noOneTrackProcessedYet = true;
	private boolean noOnePlaylistProcessedYet = true;

	public VisitorImpl(LocalModelFactory a1, LocalModelDao a2) {
		modelFactory = a1;
		dao = a2;
	}

	/**
	 * according to the expected schema of the preprocessed form of the itunes lib
	 * xml, tracks should be processed first by the visitor
	 */
	@Override
	public void visit(XmlTrack xmlTrack) {
		if (xmlTrack == null) {
			throw new IllegalArgumentException("ex-3838:-xmlTrack- cannot be 'null'");
		}
		if (noOneTrackProcessedYet) {
			noOneTrackProcessedYet = false;
		}
		String trackId = xmlTrack.getTrackId();
		String location = null;
		if (trackId != null) {
			Track track = null;
			try {// TODO CHECAR EL ORDEN EN EL QUE SE VA A AVISAR DE
				// LOS ERRORES POR IMPORTACION, QUIZA CONVIENE MEJOR
				// REPORTAR AL FINAL SI ES UNSUPPORTED FILE FORMAT
				location = Binder.getLocation(xmlTrack.getLocation());
				File decodedFile = new File(location);
				String album = Binder.getTrackAlbum(xmlTrack.getAlbum());
				Boolean isPodcast = xmlTrack.getPodcast();
				boolean isBrokenLink = this.brokenLinkValidator.isBrokenLink(decodedFile);
				boolean isDrmProtected = false;
				FileFormatSupportedValidator validator = null;
				if (isBrokenLink) {
					FailedImportHandler.handleBrokenLink(trackId, location, decodedFile, brokenLinkFailedImportMap);
				} else {
					validator = this.fileFormatSupportedValidatorFactory.createValidator(decodedFile);
					if (validator == null) {
						FailedImportHandler.handleNonSupportedFileFormat(trackId, decodedFile, unsupportedFormatFailedImportMap);
					} else {
						isDrmProtected = validator.isDrmProtected();
						if (isDrmProtected) {
							FailedImportHandler.handleDrmProtectedFile(trackId, decodedFile, drmProtectedFailedImportMap);
						} else {
							if (!validator.isAllowedToBeImportedByBusinessRule()) {
								FailedImportHandler
										.handleNonSupportedFileFormat(trackId, decodedFile, unsupportedFormatFailedImportMap);
							} else {
								if (log.isTraceEnabled()) {
									String message = MessageFormat.format(
											"log-440 File ''{0}'' will be passed to modelFactory.createTrack(). TrackId:''{1}''", xmlTrack
													.getLocation(), trackId);
									log.trace(message);
								}
								try {
									track = modelFactory.createTrack(new File(location), false, false);
									if (trackId != null && track != null) {
										tracks.put(trackId, track);
										if (isPodcast) {
											PodcastHandler.processPodcastTrack(trackId, album, podcastTracksMap);
										}
										isPodcast = false;
										trackId = null;
									} else {
										if (log.isWarnEnabled()) {
											String message = MessageFormat.format("log-110 Unhandled File ''{0}'' has DRM. TrackId:''{1}''",
													xmlTrack.getLocation(), trackId);
											log.warn(message);
										}
										FailedImportHandler.handleUnknownFailedImport(trackId, location, unknownFailedImportMap);
									}
								} catch (InvalidFileException e4) {
									log.error("Could not import " + xmlTrack.getLocation() + "\n" + e4.getMessage(), e4);
									if (location == null) {
										location = xmlTrack.getLocation();
									}
									FailedImportHandler.handleNonSupportedFileFormat(trackId, decodedFile,
											unsupportedFormatFailedImportMap);
								}
							}
						}
					}
				}
			} catch (IllegalArgumentException e2) {
				log.error("Could not import " + xmlTrack.getLocation() + "\n" + e2.getMessage(), e2);
				if (location == null) {
					location = xmlTrack.getLocation();
				}
				FailedImportHandler.handleUnknownFailedImport(trackId, location, unknownFailedImportMap);
			} catch (Exception e3) {
				log.error("Could not import " + xmlTrack.getLocation() + "\n" + e3.getMessage(), e3);
				if (location == null) {
					location = xmlTrack.getLocation();
				}
				FailedImportHandler.handleUnknownFailedImport(trackId, location, unknownFailedImportMap);
			}
		}
	}

	/**
	 * according to the expected schema of the preprocessed form of the itunes lib
	 * xml, tracks should be processed first by the visitor and then the playlist
	 * 
	 * @throws ImportItunesLibraryException
	 */
	@Override
	public void visit(XmlPlaylist xmlPlaylist) {
		if (noOnePlaylistProcessedYet) {
			failedImportLooseTracksList = createAllUnimportedTracksList();
			noOnePlaylistProcessedYet = false;
		}
		if (xmlPlaylist == null) {
			throw new IllegalArgumentException("ex-838728: -xmlPlaylist- cannot be 'null'");
		}
		// Don't add empty folder/playlists
		if (xmlPlaylist.getTrackIdList().isEmpty()) {
			return;
		}
		String name = Binder.getPlaylistName(xmlPlaylist.getName());
		boolean isDeep = false;
		if (xmlPlaylist.getParentPersistentId() != null) {
			childParentMap.put(xmlPlaylist.getPlaylistPersistentId(), xmlPlaylist.getParentPersistentId());
			isDeep = true;
		}
		boolean isPlaylist = isPlaylist(xmlPlaylist);
		if (name != null && isPlaylist) {
			if (xmlPlaylist.isFolder()) {
				if (!isDeep) {
					LocalFolder folder = new LocalFolder(name);
					dao.save(folder);
					folders.add(folder);
					folderMap.put(xmlPlaylist.getPlaylistPersistentId(), folder);
					log.info("New Folder imported: " + folder);
				}
			} else {
				List<String> unimportedTrackIds = new ArrayList<String>();
				LocalPlaylist playlist = null;
				try {
					playlist = createPlaylist(tracks, name, xmlPlaylist.getTrackIdList(), unimportedTrackIds);
				} catch (IOException e) {
					String pattern = "log-83091202:cannot import playlist ''{0}''";
					String message = MessageFormat.format(pattern, xmlPlaylist.getName());
					ImportItunesLibraryException e2 = new ImportItunesLibraryException(message, e);
					log.error(message, e2);
					throw e2;
				}
				// Only add playlists that has at least 1 track
				if (playlist != null && playlist.trackCount() > 0) {
					playlists.add(playlist);
					playlistMap.put(xmlPlaylist.getPlaylistPersistentId(), playlist);
				}
				if (!unimportedTrackIds.isEmpty()) {
					FailedImportPlaylist brokenLinkPlaylist = createPlaylistWithUnimportedTracks(playlist, unimportedTrackIds);
					if (brokenLinkPlaylist != null) {
						failedImportPlaylists.add(brokenLinkPlaylist);
						failedImportPlaylistMap.put(xmlPlaylist.getPlaylistPersistentId(), brokenLinkPlaylist);
					}
				}
			}
		}
	}

	/**
	 * according to the expected schema of the preprocessed form of the itunes lib
	 * xml, tracks should be processed first by the visitor and then the playlist,
	 * and at the end the library element, so this call should be performed at the
	 * end
	 */
	@Override
	public void visit(XmlLibrary xmlLibrary) {
		for (Playlist playlist : playlists) {
			try {
				dao.update(playlist);
				log.trace("A new playlist has been imported from iTunes: " + playlist);
			} catch (Exception e) {
				// TODO FIXME This exception occurs with big playlists.
				// Hibernate throws an StaleObjectException.
				log.error(e, e);
			}
		}

		for (String id : playlistMap.keySet()) {
			String parent = childParentMap.get(id);
			if (parent != null) {
				String newParent = parent;
				while ((newParent = childParentMap.get(parent)) != null) {
					parent = newParent;
				}
				LocalFolder folder = folderMap.get(parent);
				folder.add(playlistMap.get(id));
				dao.update(folder);
			}
		}

		for (String id : failedImportPlaylistMap.keySet()) {
			String parent = childParentMap.get(id);
			if (parent != null) {
				String newParent = parent;
				while ((newParent = childParentMap.get(parent)) != null) {
					parent = newParent;
				}
				FailedImportFolder folder = new FailedImportFolder(folderMap.get(parent).getName());
				if (folder != null) {
					folder.add(failedImportPlaylistMap.get(id));
					failedImportFolders.add(folder);
					failedImportPlaylists.remove(failedImportPlaylistMap.get(id));
				}
			}
		}

		for (Folder folder : folderMap.values()) {
			if (folder.getPlaylists().isEmpty()) {
				log.info("Removing folder " + folder
						+ "  since it might not be a Music folder or the content is not fully supported by the All app.");
				dao.delete(folder);
			}
		}
		PodcastHandler.savePodcastPlaylists(tracks, podcastTracksMap, dao);
	}

	private List<Track> createAllUnimportedTracksList() {
		List<Track> list = new ArrayList<Track>();

		list.addAll(brokenLinkFailedImportMap.values());
		list.addAll(drmProtectedFailedImportMap.values());
		list.addAll(unsupportedFormatFailedImportMap.values());
		list.addAll(unknownFailedImportMap.values());

		return list;
	}

	private LocalPlaylist createPlaylist(Map<String, Track> tracks, String playlistName, List<String> trackIdList,
			List<String> unimportedTrackIds) throws IOException {
		LocalPlaylist playlist = new LocalPlaylist();
		playlist.setName(playlistName);
		for (String trackId : trackIdList) {
			Track track = tracks.get(trackId);
			if (track != null) {
				playlist.add(track);
			} else {
				unimportedTrackIds.add(trackId);
			}
		}
		dao.save(playlist);
		return playlist;
	}

	private FailedImportPlaylist createPlaylistWithUnimportedTracks(Playlist playlist, List<String> unimportedTrackIds) {
		FailedImportPlaylist brokenLinkPlaylist = new FailedImportPlaylist(playlist.getName());
		for (String string : unimportedTrackIds) {
			Track track = null;
			track = brokenLinkFailedImportMap.get(string);
			if (track != null) {
				brokenLinkPlaylist.add(track);
				failedImportLooseTracksList.remove(track);
				continue;
			}
			track = drmProtectedFailedImportMap.get(string);
			if (track != null) {
				brokenLinkPlaylist.add(track);
				failedImportLooseTracksList.remove(track);
				continue;
			}
			track = unsupportedFormatFailedImportMap.get(string);
			if (track != null) {
				brokenLinkPlaylist.add(track);
				failedImportLooseTracksList.remove(track);
				continue;
			}
			track = unknownFailedImportMap.get(string);
			if (track != null) {
				brokenLinkPlaylist.add(track);
				failedImportLooseTracksList.remove(track);
				continue;
			}
		}
		return brokenLinkPlaylist;
	}

	public static boolean isPlaylist(XmlPlaylist xmlPlaylist) {
		return !(xmlPlaylist.isMaster() || (xmlPlaylist.getDistinguishedKind() != null) || xmlPlaylist.isSmart());
	}

	public ModelCollection createModelCollection() {
		List<Object> list = new ArrayList<Object>();

		list.addAll(failedImportFolders);
		list.addAll(failedImportPlaylists);
		list.addAll(failedImportLooseTracksList);

		return new ModelCollection(list);
	}
}
