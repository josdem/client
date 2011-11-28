package com.all.core.common.services;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationConfig {

	private static final Log log = LogFactory.getLog(ApplicationConfig.class);

	private static final String APP_BASE = "All";
	private static final String DATABASE = "DB";
	private static final String UPDATE = "update";
	private static final String DATABASE_NAME = "all";
	private static final String MUSIC_DIR = "Music";
	private static final String DOWNLOADS_DIR = "Downloads";
	private static final String UNDERSCORE = "_";

	private final String allLibraryPath;
	private final String databasePrefix;
	private final String databaseFile;
	private final String musicLibraryDirrectory;
	private final String downloadLibraryDirectory;
	private final String separator;
	private final String workPath;
	private final String updatePath;

	public ApplicationConfig() {
		String basePath = System.getProperty("user.home");

		this.separator = System.getProperty("file.separator");
		this.workPath = System.getProperty("user.dir");
		this.musicLibraryDirrectory = basePath + separator + MUSIC_DIR + separator + APP_BASE;
		this.downloadLibraryDirectory = musicLibraryDirrectory + separator + DOWNLOADS_DIR;
		this.allLibraryPath = basePath + separator + APP_BASE;

		String databasePath = allLibraryPath + separator + DATABASE;
		log.debug("databasePath=" + databasePath);

		this.databasePrefix = databasePath + separator + DATABASE_NAME + UNDERSCORE;
		log.debug("databasePrefix=" + databasePrefix);

		this.databaseFile = databasePrefix + DATABASE_NAME;
		log.debug("databaseFile=" + databaseFile);

		this.updatePath = allLibraryPath + separator + UPDATE;
		log.debug("updatePath=" + updatePath);

		makeDir(allLibraryPath);
		makeDir(databasePath);
		makeDir(updatePath);
	}

	private File makeDir(String strdir) {
		File dir = new File(strdir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public String getAllLibraryPath() {
		return allLibraryPath;
	}

	public String getDatabasePrefix() {
		return databasePrefix;
	}

	public String getDatabaseFile() {
		return databaseFile;
	}

	public static void main(String[] args) {
		new ApplicationConfig();
	}

	public String getMusicLibraryDirectory() {
		return musicLibraryDirrectory;
	}

	public File getAppFolder(String dir) {
		return makeDir(workPath + separator + dir);
	}

	public File getUserFolder(String dir) {
		return makeDir(allLibraryPath + separator + dir);

	}

	public String getUpdatePath() {
		return updatePath;
	}

	public File getAppFolder() {
		return makeDir(workPath);
	}

	public File getUserFolder() {
		return makeDir(allLibraryPath);
	}

	public String getDownloadsDirectory() {
		return downloadLibraryDirectory;
	}
}
