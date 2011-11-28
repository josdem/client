package com.all.client.view.dialog;

import java.io.File;

public class FileDialogResult {
	private final int dialogResult;
	
	private final File file;

	public FileDialogResult(int dialogResult, File file) {
		super();
		this.dialogResult = dialogResult;
		this.file = file;
	}
	
	public int getDialogResult() {
		return dialogResult;
	}
	
	public File getFile() {
		return file;
	}

}
