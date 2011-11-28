package com.all.client.update;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.all.appControl.control.ControlEngine;
import com.all.client.rest.AbstractFileHttpMessageConverter;
import com.all.client.rest.DownloadCompletedEvent;
import com.all.client.rest.DownloadErrorEvent;
import com.all.client.rest.DownloadProgressEvent;
import com.all.client.rest.DownloadStartedEvent;
import com.all.client.rest.FileHttpMessageConverterListener;
import com.all.client.services.reporting.ClientReporter;
import com.all.commons.Environment;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.services.ApplicationConfig;
import com.all.core.events.Events;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;

@Service
public class UpdateServiceImpl {

	private static final Log LOG = LogFactory.getLog(UpdateServiceImpl.class);
	public static final String WINDOWS = "WIN";
	public static final String MAC = "MAC";
	public static final String LINUX = "LINUX";
	public static final String UNKOWN = "UNKOWN";
	public static final String UNDERSCORE = "_";
	public static final String TRACKER_URL_KEY = "tracker.url";
	public static final String UPDATES_URL_KEY = "updates.get";
	public static final String ARTIFACT_ID_KEY = "artifact.id";
	public static final String VERSION_KEY = "artifact.version";

	@Autowired
	private Properties clientSettings;
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private ControlEngine controlEngine;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor(new IncrementalNamedThreadFactory(
			"UpdateServiceThread"));
	private RestTemplate restTemplate = new RestTemplate();
	
	private CheckUpdateTask checkUpdateTask = new CheckUpdateTask();
	
	private Future<?> future = new MockFuture();

	private int currentProgress = 0;
	
	@Autowired
	private ClientReporter reporter;

	@PostConstruct
	public void init() {
		registerUpdateFileHttpMessageConverter();
	}

	@PreDestroy
	public void shutdown() {
		executor.shutdownNow();
	}

	private void registerUpdateFileHttpMessageConverter() {
		UpdateFileHttpMessageConverter updateFileHttpMessageConverter = new UpdateFileHttpMessageConverter();
		updateFileHttpMessageConverter.setFileHttpMessageConverterListener(new UpdateFileHttpMessageConverterListener());
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		messageConverters.add(updateFileHttpMessageConverter);
		restTemplate.setMessageConverters(messageConverters);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void checkForUpdates() {
		if (future.isDone()) {
			future = executor.submit(checkUpdateTask);
		} else {
			LOG.warn("Already running a thread to check for updates");
		}
	}

	private void updateAvailable() {
		restTemplate.getForEntity(getUpdatesServerUrl(), File.class, getArtifactId(), getRunningVersion(), getOS());
	}

	private Object getOS() {
		if (Environment.isWindows()) {
			return WINDOWS;
		} else if (Environment.isMac()) {
			return MAC;
		} else if (Environment.isLinux()) {
			return LINUX;
		} else {
			return UNKOWN;
		}
	}

	private String getUpdatesServerUrl() {
		return getTrackerServerUrl() + clientSettings.getProperty(UPDATES_URL_KEY);
	}

	private String getTrackerServerUrl() {
		return clientSettings.getProperty(TRACKER_URL_KEY);
	}

	private String getArtifactId() {
		return clientSettings.getProperty(ARTIFACT_ID_KEY);
	}

	private String getRunningVersion() {
		return clientSettings.getProperty(VERSION_KEY);
	}

	private void notifyUpdateFound(String filename) {
		controlEngine.fireValueEvent(Events.AutoUpdate.UPDATE_FOUND, filename);
	}

	private void notifyUpdateNotFound() {
		controlEngine.fireEvent(Events.AutoUpdate.UPDATE_NOT_FOUND);
	}

	private void notifyDownloadUpdateProgress(String filename, int progress) {
		if (currentProgress != progress) {
			currentProgress = progress;
			controlEngine.fireValueEvent(Events.AutoUpdate.UPDATE_DOWNLOAD_PROGRESS, currentProgress);
		}
	}

	private void notifyUpdateDownloadCompleted(String filename, File updateFile) {
		controlEngine.fireValueEvent(Events.AutoUpdate.UPDATE_DOWNLOAD_COMPLETED, updateFile);
		reporter.logUpdaterEvent(getRunningVersion());
	}

	private void notifyUpdateDownloadError(String error) {
		controlEngine.fireValueEvent(Events.AutoUpdate.UPDATE_DOWNLOAD_ERROR, error);
	}

	class UpdateFileHttpMessageConverterListener implements FileHttpMessageConverterListener {
		@Override
		public void onDownloadStarted(DownloadStartedEvent downloadStartedEvent) {
			notifyUpdateFound(downloadStartedEvent.getFileName());
		}

		@Override
		public void onDownloadProgress(DownloadProgressEvent downloadProgressEvent) {
			notifyDownloadUpdateProgress(downloadProgressEvent.getFileName(), downloadProgressEvent.getProgress());
		}

		@Override
		public void onDownloadError(DownloadErrorEvent downloadErrorEvent) {
			LOG.error(String.format("Error while downloading update file [%s]: %s", downloadErrorEvent.getFileName(),
					downloadErrorEvent.getError()));
			future.cancel(true);
			notifyUpdateDownloadError(downloadErrorEvent.getError());
		}

		@Override
		public void onDownloadCompleted(DownloadCompletedEvent downloadCompletedEvent) {
			notifyUpdateDownloadCompleted(downloadCompletedEvent.getFileName(), downloadCompletedEvent.getFile());
		}
	}

	class UpdateFileHttpMessageConverter extends AbstractFileHttpMessageConverter {
		@Override
		protected File createDownloadFile(HttpInputMessage inputMessage) throws IOException {
			return new File(applicationConfig.getUpdatePath(), getDownloadFileName(inputMessage));
		}
	}

	class CheckUpdateTask implements Runnable {
		@Override
		public void run() {
			try {
				updateAvailable();
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
					notifyUpdateNotFound();
				} else {
					LOG.error("Error while checking for updates", e);
				}
			} catch (Exception e) {
				LOG.error("Error while checking for updates", e);
				notifyUpdateDownloadError(e.getMessage());
			}
		}
	}

	class MockFuture implements Future<Void> {
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return true;
		}

		@Override
		public boolean isCancelled() {
			return true;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public Void get() throws InterruptedException, ExecutionException {
			return null;
		}

		@Override
		public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return null;
		}
	}
	
}
