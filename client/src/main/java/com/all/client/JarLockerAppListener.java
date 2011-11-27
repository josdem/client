package com.all.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.app.AppListener;

public class JarLockerAppListener implements AppListener {
	private static final Log LOG = LogFactory.getLog(JarLockerAppListener.class);
	List<FileLock> locks = new ArrayList<FileLock>();

	@Override
	public void initialize() {
		File systemDir = new File("System/");
		lock(systemDir, locks);
	}

	private void lock(File file, List<FileLock> locks) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				lock(file2, locks);
			}
		} else {
			try {
				FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
				FileLock lock = channel.tryLock();
				locks.add(lock);
				LOG.info("File: " + file.getAbsolutePath() + " locked");
			} catch (Exception e) {
				LOG.error(e, e);
			}

		}
	}

	@Override
	public void destroy() {
		for (FileLock lock : locks) {
			try {
				lock.release();
			} catch (Exception e) {
			}
		}
		LOG.info("File locks released");
	}
}
