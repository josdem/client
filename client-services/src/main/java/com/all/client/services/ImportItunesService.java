package com.all.client.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.importx.itunes.ImportItunesLibController;
import com.all.client.itunes.ByteObject;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.peer.share.ShareService;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.sync.SyncMessages;
import com.all.core.actions.Actions;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;
import com.all.shared.stats.MediaImportStat.ImportType;

@Service
public class ImportItunesService {

	private static Log log = LogFactory.getLog(ImportItunesService.class);

	@Autowired
	private ControlEngine controlEngine;

	@Autowired
	private ShareService shareService;

	@Autowired
	private ImportItunesLibController importItunesLibController;

	@Autowired
	private MessEngine messEngine;

	@Autowired
	private ClientReporter reporter;

	@Autowired
	private LocalModelDao dao;

	@PostConstruct
	public void initialize() {
		refreshFileExist();
	}

	@ActionMethod(Actions.Library.IMPORT_FROM_ITUNES_ID)
	protected void importFromItunesFile(File iTunesFile) {
		messEngine.send(new AllMessage<String>(MessEngineConstants.PAUSE_SYNC, SyncMessages.IMPORT_LIBRARY));
		long totalTracksBefore = 0;
		long totalPlaylistsBefore = 0;
		long totalFoldersBefore = 0;
		controlEngine.fireEvent(Events.Library.IMPORTING_ITUNES_LIBRARY);
		Root root = controlEngine.get(Model.USER_ROOT);

		ModelCollection collection = null;
		totalTracksBefore = dao.count(LocalTrack.class);
		totalPlaylistsBefore = dao.count(LocalPlaylist.class);
		totalFoldersBefore = dao.count(LocalFolder.class);

		log.info(new StringBuilder().append("There are ").append(totalTracksBefore).append(" tracks, ").append(totalPlaylistsBefore)
				.append(" playlists and ").append(totalFoldersBefore).append(" folders before import from iTunes.").toString());
		collection = importItunesLibController.importITunesLibrary(iTunesFile);
		controlEngine.fireEvent(Events.Library.IMPORTING_ITUNES_LIBRARY_DONE);
		
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));

		if (collection != null && !collection.isEmpty()) {
			controlEngine.fireEvent(Events.Errors.IMPORT_FROM_ITUNES_MISSING_FILES, new ValueEvent<ModelCollection>(collection));
		}

		AllMessage<String> message = new AllMessage<String>(MessEngineConstants.RESUME_SYNC, SyncMessages.IMPORT_LIBRARY);
		messEngine.send(message);
		shareService.run();

		int importedTracks = (int) (dao.count(LocalTrack.class) - totalTracksBefore);
		int importedPlaylists = (int) (dao.count(LocalPlaylist.class) - totalPlaylistsBefore);
		int importedFolders = (int) (dao.count(LocalFolder.class) - totalFoldersBefore);
		log.info(new StringBuilder().append("There were ").append(importedTracks).append(" tracks, ").append(importedPlaylists).append(" playlists and ")
				.append(importedFolders).append(" folders imported from iTunes.").toString());
		reporter.logImportEvent(ImportType.ITUNES, importedTracks, importedPlaylists, importedFolders);
	}

	@ActionMethod(Actions.Library.SAVE_ITUNES_UNIMPORTED_FILES_ID)
	public void saveModel(ValueAction<ModelCollection> action) {
		ModelCollection model = action.getValue();
		// Serialize and save the model collection
		FileOutputStream f;
		try {
			// TODO: Define the store object
			f = new FileOutputStream("failedImport.tmp");
			ObjectOutputStream s = new ObjectOutputStream(f);

			ByteObject serializedModel = new ByteObject(model);

			s.writeObject(serializedModel);
			s.flush();
			f.close();
		} catch (FileNotFoundException e) {
			log.error(e, e);
		} catch (IOException e) {
			log.error(e, e);
		}
		refreshFileExist();
	}

	@ActionMethod(Actions.View.SHOW_ITUNES_UNIMPORTED_FILES_ID)
	public void readStoredModel() {
		ModelCollection model = new ModelCollection();
		FileInputStream f;
		try {
			f = new FileInputStream("failedImport.tmp");
			ObjectInputStream in = new ObjectInputStream(f);
			ByteObject readObject = (ByteObject) in.readObject();
			model = (ModelCollection) readObject.readObject();
			in.close();
			f.close();
		} catch (ClassNotFoundException e) {
			log.error(e, e);
		} catch (FileNotFoundException e) {
			log.error(e, e);
		} catch (IOException e) {
			log.error(e, e);
		}
		if (!model.isEmpty()) {
			controlEngine.fireEvent(Events.Errors.IMPORT_FROM_ITUNES_MISSING_FILES, new ValueEvent<ModelCollection>(model));
		}
	}

	@ActionMethod(Actions.Library.DELETE_ITUNES_UNIMPORTED_FILES_ID)
	public void deleteStoredModel() {
		File f;
		f = new File("failedImport.tmp");
		if (f != null && f.exists()) {
			f.delete();
		}
		refreshFileExist();
	}

	private void refreshFileExist() {
		controlEngine.set(Model.ITUNES_UNIMPORTED_FILE_EXISTS, new File("failedImport.tmp").exists(),
				Events.Library.ITUNES_UNIMPORTED_FILE_EXISTS_CHANGED);
	}

}
