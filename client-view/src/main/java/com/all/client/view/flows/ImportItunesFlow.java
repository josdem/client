package com.all.client.view.flows;

import java.io.File;

import com.all.appControl.control.ViewEngine;
import com.all.client.util.ITunesLibraryFinder;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;

public class ImportItunesFlow {

	private final ViewEngine viewEngine;
	private DialogFactory dialogFactory;

	public ImportItunesFlow(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	/*
	 * Should only be called by DialogFactory due to bizz flow -rare- rules
	 */
	public ImportItunesFlow(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

	public void importItunesFromFile() {
		File iTunesLibraryFile = getiTunesLibraryFile();
		if (iTunesLibraryFile == null && dialogFactory != null) {
			iTunesLibraryFile = dialogFactory.showITunesFileChooserDialog();
		}
		if (iTunesLibraryFile != null) {
			viewEngine.sendValueAction(Actions.Library.IMPORT_FROM_ITUNES, iTunesLibraryFile);
		}
	}

	public File getiTunesLibraryFile() {
		File iTunesLibraryFile = new ITunesLibraryFinder().getITunesLibraryFile();
		return iTunesLibraryFile;
	}

}
