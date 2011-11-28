package com.all.client.rest;

public class DownloadErrorEvent extends DownloadStartedEvent {

	private static final long serialVersionUID = -7909208606150409300L;
	private final String error;

	public DownloadErrorEvent(Object source, String filename, String error) {
		super(source, filename);
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
}
