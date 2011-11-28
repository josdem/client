package com.all.client.config;

import java.io.File;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.core.common.bean.ModalUserDatabaseInitializer;
import com.all.core.common.services.ApplicationConfig;
import com.all.downloader.download.ManagedDownloaderConfig;

@Component
public class DownloaderConfigImpl implements ManagedDownloaderConfig {

	private static final String CONFIG_PATH = "configPath";

	private static final String INCOMPLETE_DOWNLOADS_PATH_KEY = "incompletePath";

	private static final String COMPLETED_DOWNLOADS_PATH_KEY = "savePath";

	private static final String SEPARATOR = System.getProperty("file.separator");

	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	private ApplicationConfig appConfig;
	@Autowired
	private Properties downloaderSettings;
	@Autowired
	private ModalUserDatabaseInitializer modalUserDatabaseInitializer;

	private String downloadCompletePath;

	private String downloadIncompletePath;

	private String userConfigPath;

	@PostConstruct
	public void setupPaths() {
		this.downloadCompletePath = appConfig.getMusicLibraryDirectory() + SEPARATOR
				+ downloaderSettings.getProperty(COMPLETED_DOWNLOADS_PATH_KEY);
		this.downloadIncompletePath = appConfig.getMusicLibraryDirectory() + SEPARATOR
				+ downloaderSettings.getProperty(INCOMPLETE_DOWNLOADS_PATH_KEY);
		this.userConfigPath = appConfig.getAllLibraryPath() + SEPARATOR + downloaderSettings.getProperty(CONFIG_PATH);
		if (createDir(downloadCompletePath) && createDir(downloadIncompletePath) && createDir(userConfigPath)) {
			log.info("Download dirs where succesfully created.");
		} else {
			log.error("Could not create download dirs.");
		}
	}

	private boolean createDir(String path) {
		try {
			File dir = new File(path);
			if (!dir.exists()) {
				return dir.mkdirs();
			} else {
				return dir.isDirectory();
			}
		} catch (Exception e) {
			log.error(e, e);
			return false;
		}
	}

	@Override
	public String getCompleteDownloadsPath() {
		if (!createDir(downloadCompletePath)) {
			throw new IllegalStateException("The following directory could not be created : " + downloadCompletePath);
		}
		return downloadCompletePath;
	}

	@Override
	public String getIncompleteDownloadsPath() {
		if (!createDir(downloadIncompletePath)) {
			throw new IllegalStateException("The following directory could not be created : " + downloadIncompletePath);
		}
		return downloadIncompletePath;
	}

	@Override
	public String getUserConfigPath() {
		if (!createDir(userConfigPath)) {
			throw new IllegalStateException("The following directory could not be created : " + userConfigPath);
		}
		return userConfigPath;
	}

	@Override
	public String getUserId() {
		return modalUserDatabaseInitializer.getMail();
	}

	@Override
	public int getDownloaderPriority(String downloaderKey) {
		return Integer.valueOf(downloaderSettings.getProperty(downloaderKey));
	}

	@Override
	public int getDownloaderSearchTimeout(String downloaderKey) {
		return Integer.valueOf(downloaderSettings.getProperty(downloaderKey)) * 1000;
	}

	@Override
	public String getProperty(String propertyKey) {
		return downloaderSettings.getProperty(propertyKey);
	}
}
