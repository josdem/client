package com.all.client.rest;

import java.io.File;

public class DownloadCompletedEvent extends DownloadStartedEvent {

	private static final long serialVersionUID = 1821430359679820456L;
	private final File file;

	public DownloadCompletedEvent(Object source, String filename, File file) {
		super(source, filename);
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
}
