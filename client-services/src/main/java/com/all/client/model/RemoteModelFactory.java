package com.all.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.all.client.services.MusicEntityService;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;
import com.all.shared.model.RemotePlaylist;
import com.all.shared.model.RemoteTrack;
import com.all.shared.model.Root;
import com.all.shared.model.SimpleSmartPlaylist;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.Track;

@Service
public class RemoteModelFactory {

	@Autowired
	private MusicEntityService musicEntityService;

	public ContactRoot createRemoteLibrary(Root root, ContactInfo contact) {
		ContactRoot remoteLibrary = new ContactRoot(contact);
		Map<String, RemoteTrack> daTracks = new HashMap<String, RemoteTrack>();
		List<SmartPlaylist> convertSmartPlaylists = convertSmartPlaylists(root.getSmartPlaylists(), root
				.size(SmartPlaylist.class), daTracks, true);
		SimpleSmartPlaylist allMusic = new SimpleSmartPlaylist(root.getAllMusicSmartPlaylist().getLabel(), convertPlaylist(
				root.getAllMusicSmartPlaylist().getPlaylist(), daTracks, true), root.getAllMusicSmartPlaylist().isReadOnly());
		List<Folder> convertFolders = convertFolders(root.getFolders(), root.size(Folder.class), daTracks, true);
		List<Playlist> convertPlaylists = convertPlaylists(root.getPlaylists(), root.size(Playlist.class), daTracks, true);
		List<Track> convertTracks = convertTracks(root.getTracks(), root.size(Track.class), daTracks, true);

		ModelCollection model = new ModelCollection(convertFolders, convertPlaylists, convertTracks, convertSmartPlaylists);
		remoteLibrary.setContent(model, allMusic);
		return remoteLibrary;
	}

	public ModelCollection createRemoteModel(ModelCollection localModel) {
		Map<String, RemoteTrack> daTracks = new HashMap<String, RemoteTrack>();

		ModelCollection result = new ModelCollection();
		result.setRemote(true);
		result.getTracks().addAll(convertTracks(localModel.getTracks(), localModel.getTracks().size(), daTracks, true));
		result.getSmartPlaylists().addAll(
				convertSmartPlaylists(localModel.getSmartPlaylists(), localModel.getSmartPlaylists().size(), daTracks, true));
		result.getPlaylists().addAll(
				convertPlaylists(localModel.getPlaylists(), localModel.getPlaylists().size(), daTracks, true));
		result.getFolders().addAll(convertFolders(localModel.getFolders(), localModel.getFolders().size(), daTracks, true));
		return result;
	}

	public ModelCollection createRemoteModelWithoutReferences(ModelCollection localModel) {
		Map<String, RemoteTrack> daTracks = new HashMap<String, RemoteTrack>();

		ModelCollection result = new ModelCollection();
		result.setRemote(true);
		result.getTracks().addAll(convertTracks(localModel.getTracks(), localModel.getTracks().size(), daTracks, false));
		result.getSmartPlaylists().addAll(
				convertSmartPlaylists(localModel.getSmartPlaylists(), localModel.getSmartPlaylists().size(), daTracks, false));
		result.getPlaylists().addAll(
				convertPlaylists(localModel.getPlaylists(), localModel.getPlaylists().size(), daTracks, false));
		result.getFolders()
				.addAll(convertFolders(localModel.getFolders(), localModel.getFolders().size(), daTracks, false));
		return result;
	}

	private List<Folder> convertFolders(Iterable<Folder> folders, int size, Map<String, RemoteTrack> daTracks,
			boolean includeReferences) {
		List<Folder> remoteFolders = new ArrayList<Folder>(size);
		for (Folder folder : folders) {
			RemoteFolder remoteFolder = convertFolder(folder, daTracks, includeReferences);
			if(includeReferences || !remoteFolder.getPlaylist().isEmpty()){
				remoteFolders.add(remoteFolder);
			}
		}
		return remoteFolders;
	}

	private List<Playlist> convertPlaylists(Iterable<Playlist> playlists, int size, Map<String, RemoteTrack> daTracks,
			boolean includeReferences) {
		List<Playlist> remotePlaylists = new ArrayList<Playlist>(size);
		for (Playlist playlist : playlists) {
			RemotePlaylist remotePlaylist = convertPlaylist(playlist, daTracks, includeReferences);
			if (includeReferences || !remotePlaylist.isEmpty()) {
				remotePlaylists.add(remotePlaylist);
			}
		}
		return remotePlaylists;
	}

	private List<SmartPlaylist> convertSmartPlaylists(Iterable<SmartPlaylist> smartPlaylists, int size,
			Map<String, RemoteTrack> daTracks, boolean includeReferences) {
		List<SmartPlaylist> remoteSmartPlaylists = new ArrayList<SmartPlaylist>(size);
		for (SmartPlaylist localSmartPlaylist : smartPlaylists) {
			RemotePlaylist remotePlaylist = convertPlaylist(localSmartPlaylist.getPlaylist(), daTracks, includeReferences);
			if (includeReferences || !remotePlaylist.isEmpty()) {
				remoteSmartPlaylists.add(new SimpleSmartPlaylist(localSmartPlaylist.getLabel(), remotePlaylist,
						localSmartPlaylist.isReadOnly()));
			}
		}
		return remoteSmartPlaylists;
	}

	private List<Track> convertTracks(Iterable<Track> tracks, int size, Map<String, RemoteTrack> daTracks,
			boolean includeReferences) {
		List<Track> remoteTracks = new ArrayList<Track>(size);
		for (Track track : tracks) {
			RemoteTrack remoteTrack = convertTrack(track, daTracks, includeReferences);
			if (remoteTrack != null) {
				remoteTracks.add(remoteTrack);
			}
		}
		return remoteTracks;
	}

	private RemoteFolder convertFolder(Folder folder, Map<String, RemoteTrack> daTracks, boolean includeReferences) {
		RemoteFolder remoteFolder = new RemoteFolder(folder);

		List<Playlist> remotePlaylists = new ArrayList<Playlist>();
		for (Playlist playlist : folder.getPlaylists()) {
			remotePlaylists.add(convertPlaylist(playlist, daTracks, includeReferences));
		}
		remoteFolder.setPlaylists(remotePlaylists);
		return remoteFolder;
	}

	private RemotePlaylist convertPlaylist(Playlist playlist, Map<String, RemoteTrack> daTracks, boolean includeReferences) {
		Assert.notNull(playlist, "Valio madres");
		RemotePlaylist remotePlaylist = new RemotePlaylist(playlist);
		List<Track> localTracks = playlist.getTracks();
		List<Track> tracks = new ArrayList<Track>(localTracks.size());
		for (Track track : localTracks) {
			RemoteTrack remoteTrack = convertTrack(track, daTracks, includeReferences);
			if (remoteTrack != null) {
				tracks.add(remoteTrack);
			}
		}
		remotePlaylist.setTracks(tracks);
		return remotePlaylist;
	}

	private RemoteTrack convertTrack(Track track, Map<String, RemoteTrack> daTracks, boolean includeReferences) {
		if(track == null){
			return null;
		}
		if (!daTracks.containsKey(track.getHashcode())) {
			if (includeReferences || musicEntityService.isFileAvailable(track)) {
				daTracks.put(track.getHashcode(), new RemoteTrack(track));
			}
		}
		return daTracks.get(track.getHashcode());
	}
}
