package com.all.client.rest;


public class DownloadProgressEvent extends DownloadStartedEvent {

	private static final long serialVersionUID = 6138976067580349660L;
	private final int progress;

	public DownloadProgressEvent(Object source, String filename, int progress) {
		super(source, filename);
		this.progress = progress;
	}
	
	public int getProgress() {
		return progress;
	}
	
}
