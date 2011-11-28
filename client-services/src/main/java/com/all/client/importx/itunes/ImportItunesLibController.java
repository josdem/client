package com.all.client.importx.itunes;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.events.Events;
import com.all.shared.model.ModelCollection;

@Service
public class ImportItunesLibController {
	private static Log log = LogFactory.getLog(ImportItunesLibController.class);

	@Autowired
	private ItunesImporterService itunesImporterService;
	@Autowired
	private ControlEngine controlEngine;

	public ModelCollection importITunesLibrary(File iTunesFile) {
		ModelCollection modelCollection = null;
		try {

			if (iTunesFile == null) {
				return null;
			}
			modelCollection = this.itunesImporterService.importItunesLibrary(iTunesFile);
		} catch (Exception e) {
			log.error("=======================PROBLEM IMPORTING FROM ITUNES====================", e);
			controlEngine.fireValueEvent(Events.Errors.EXCEPTION, e);
		}
		Sound.ITUNES_IMPORT.play();
		return modelCollection;
	}
}
