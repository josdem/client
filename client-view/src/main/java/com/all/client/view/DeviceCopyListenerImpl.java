/**
 * 
 */
package com.all.client.view;

import com.all.client.view.dialog.DeviceCopyDialog;
import com.all.client.view.dialog.DialogFactory;

final class DeviceCopyListenerImpl implements DeviceCopyListener {
	/**
	 * 
	 */
	private final DeviceCopyDialog copyProgressDialog;
	private final DialogFactory dialogFactory;
	private final Refreshable refreshable;

	DeviceCopyListenerImpl(Refreshable refreshable, DialogFactory dialogFactory) {
		this.refreshable = refreshable;
		this.dialogFactory = dialogFactory;
		this.copyProgressDialog = dialogFactory.getCopyProgressDialog();
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onFinish() {
		copyProgressDialog.onFinish();
		refreshable.refresh();
	}

	@Override
	public void onByteProgress(int progress, long size, long totalSize) {
		copyProgressDialog.onByteProgress(progress, size, totalSize);
	}

	@Override
	public void onFileProgress(int progress, int files, int totalFiles, String name) {
		copyProgressDialog.onFileProgress(progress, files, totalFiles, name);
	}

	public void onDeviceFull() {
		dialogFactory.showNotEnoughSpaceDialog(copyProgressDialog);
	}

	@Override
	public void onCannotCopyNotExistFile() {
		dialogFactory.showUnableToCopyGrayReferencesDialog(copyProgressDialog);
	}
}