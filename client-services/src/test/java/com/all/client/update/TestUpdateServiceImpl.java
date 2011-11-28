package com.all.client.update;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.all.appControl.control.ControlEngine;
import com.all.client.UnitTestCase;
import com.all.client.rest.DownloadCompletedEvent;
import com.all.client.rest.DownloadErrorEvent;
import com.all.client.rest.DownloadProgressEvent;
import com.all.client.rest.DownloadStartedEvent;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.update.UpdateServiceImpl.CheckUpdateTask;
import com.all.client.update.UpdateServiceImpl.UpdateFileHttpMessageConverter;
import com.all.client.update.UpdateServiceImpl.UpdateFileHttpMessageConverterListener;
import com.all.commons.Environment;
import com.all.core.events.Events;

public class TestUpdateServiceImpl extends UnitTestCase {

	@InjectMocks
	private UpdateServiceImpl updateService = new UpdateServiceImpl();
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private ExecutorService executor;
	@Mock
	private CheckUpdateTask checkUpdateTask;
	@SuppressWarnings("unused")
	@Mock
	private ClientReporter reporter;
	@Mock
	private Properties clientSettings;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private File file;

	private String filename = UpdateServiceImpl.ARTIFACT_ID_KEY + "_" + UpdateServiceImpl.VERSION_KEY + ".zip";

	private UpdateFileHttpMessageConverterListener updateFileHttpMessageConverterListener;

	@Before
	public void setup() {
		stubClientSettings(UpdateServiceImpl.TRACKER_URL_KEY);
		stubClientSettings(UpdateServiceImpl.UPDATES_URL_KEY);
		stubClientSettings(UpdateServiceImpl.ARTIFACT_ID_KEY);
		stubClientSettings(UpdateServiceImpl.VERSION_KEY);

		updateFileHttpMessageConverterListener = updateService.new UpdateFileHttpMessageConverterListener();
	}

	private void stubClientSettings(String keyValue) {
		when(clientSettings.getProperty(keyValue)).thenReturn(keyValue);
	}

	@Test
	@SuppressWarnings( { "unchecked" })
	public void shouldInit() throws Exception {
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		when(restTemplate.getMessageConverters()).thenReturn(converters);

		updateService.init();

		verify(restTemplate).setMessageConverters(captor.capture());
		List value = captor.getValue();
		assertEquals(1, value.size());
		assertTrue(value.get(0) instanceof UpdateFileHttpMessageConverter);
	}

	@Test
	public void shouldShutdown() throws Exception {
		updateService.shutdown();

		verify(executor).shutdownNow();
	}

	@Test
	public void shouldExecuteUpdateTaskOnCheckUpdate() throws Exception {
		updateService.checkForUpdates();

		verify(executor).submit(checkUpdateTask);

		checkUpdateTask = updateService.new CheckUpdateTask();

		checkUpdateTask.run();

		verify(restTemplate).getForEntity(UpdateServiceImpl.TRACKER_URL_KEY + UpdateServiceImpl.UPDATES_URL_KEY,
				File.class, UpdateServiceImpl.ARTIFACT_ID_KEY, UpdateServiceImpl.VERSION_KEY, getOs());
		checkUpdateServiceListenerNotCalled();
	}

	private String getOs() {
		if (Environment.isWindows()) {
			return UpdateServiceImpl.WINDOWS;
		} else if (Environment.isMac()) {
			return UpdateServiceImpl.MAC;
		} else if (Environment.isLinux()) {
			return UpdateServiceImpl.LINUX;
		} else {
			return UpdateServiceImpl.UNKOWN;
		}
	}

	private void checkUpdateServiceListenerNotCalled() {
		verify(controlEngine, never()).fireEvent(Events.AutoUpdate.UPDATE_NOT_FOUND);
		verify(controlEngine, never()).fireValueEvent(eq(Events.AutoUpdate.UPDATE_FOUND), anyString());
		verify(controlEngine, never()).fireValueEvent(eq(Events.AutoUpdate.UPDATE_DOWNLOAD_PROGRESS), anyInt());
		verify(controlEngine, never()).fireValueEvent(eq(Events.AutoUpdate.UPDATE_DOWNLOAD_COMPLETED), any(File.class));
	}

	@Test
	public void shouldNotifyUpdateNotFound() throws Exception {
		checkUpdateTask = updateService.new CheckUpdateTask();

		when(
				restTemplate.getForEntity(UpdateServiceImpl.TRACKER_URL_KEY + UpdateServiceImpl.UPDATES_URL_KEY,
						File.class, UpdateServiceImpl.ARTIFACT_ID_KEY, UpdateServiceImpl.VERSION_KEY, getOs()))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		checkUpdateTask.run();

		verify(controlEngine).fireEvent(Events.AutoUpdate.UPDATE_NOT_FOUND);
	}

	@Test
	public void shouldDoNothingOnException() throws Exception {
		checkUpdateTask = updateService.new CheckUpdateTask();

		when(
				restTemplate.getForEntity(UpdateServiceImpl.TRACKER_URL_KEY + UpdateServiceImpl.UPDATES_URL_KEY,
						File.class, UpdateServiceImpl.ARTIFACT_ID_KEY, UpdateServiceImpl.ARTIFACT_ID_KEY)).thenThrow(
				new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE), new RestClientException(""));

		checkUpdateTask.run();
		checkUpdateTask.run();

		checkUpdateServiceListenerNotCalled();
	}

	@Test
	public void shouldNotifyUpdateFound() throws Exception {
		updateFileHttpMessageConverterListener.onDownloadStarted(new DownloadStartedEvent(this, filename));

		verify(controlEngine).fireValueEvent(Events.AutoUpdate.UPDATE_FOUND, filename);
	}

	@Test
	public void shouldNotifyProgressJustOnceForTheSameProgress() throws Exception {
		int progress = 52;

		updateFileHttpMessageConverterListener.onDownloadProgress(new DownloadProgressEvent(this, filename, progress));
		updateFileHttpMessageConverterListener.onDownloadProgress(new DownloadProgressEvent(this, filename, progress));
		updateFileHttpMessageConverterListener.onDownloadProgress(new DownloadProgressEvent(this, filename, progress));

		verify(controlEngine).fireValueEvent(Events.AutoUpdate.UPDATE_DOWNLOAD_PROGRESS, progress);
	}

	@Test
	public void shouldNotifyDownloadCompleted() throws Exception {
		
		updateFileHttpMessageConverterListener.onDownloadCompleted(new DownloadCompletedEvent(this, filename, file));

		verify(controlEngine).fireValueEvent(Events.AutoUpdate.UPDATE_DOWNLOAD_COMPLETED, file);
	}

	@Test
	public void shouldNotifyError() throws Exception {
		String error = "error";
		updateFileHttpMessageConverterListener.onDownloadError(new DownloadErrorEvent(this, filename, error));
		verify(controlEngine).fireValueEvent(Events.AutoUpdate.UPDATE_DOWNLOAD_ERROR, error);
	}
}
