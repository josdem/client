package com.all.client.rest;


public interface FileHttpMessageConverterListener {

	void onDownloadStarted(DownloadStartedEvent downloadStartedEvent);
	
	void onDownloadProgress(DownloadProgressEvent downloadProgressEventj);
	
	void onDownloadCompleted(DownloadCompletedEvent downloadCompletedEvent);

	void onDownloadError(DownloadErrorEvent downloadErrorEvent);
}
