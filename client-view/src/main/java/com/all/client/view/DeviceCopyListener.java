package com.all.client.view;

public interface DeviceCopyListener {
	 DeviceCopyListener EMPTY = new DeviceCopyListener() {

		@Override
		public void onStart() {
		}

		@Override
		public void onFinish() {
		}

		@Override
		public void onByteProgress(int progress, long size, long totalSize) {
		}

		@Override
		public void onFileProgress(int progress, int files, int totalFiles, String name) {
		}

		@Override
		public void onDeviceFull() {
		}

		@Override
		public void onCannotCopyNotExistFile() {
		}
	};

	void onByteProgress(int progress, long size, long totalSize);

	void onFileProgress(int progress, int files, int totalFiles, String name);

	void onFinish();

	void onStart();
	
	void onCannotCopyNotExistFile();
	
	void onDeviceFull();

}
