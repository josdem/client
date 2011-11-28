package com.all.client.rest;

import java.util.EventObject;

public class DownloadStartedEvent extends EventObject {

	private static final long serialVersionUID = 6603831384048966066L;
	private final String fileName;

	public DownloadStartedEvent(Object source, String fileName) {
		super(source);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
}
