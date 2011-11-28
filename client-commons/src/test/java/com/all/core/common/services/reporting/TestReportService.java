package com.all.core.common.services.reporting;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestReportService {
	@Mock
	private Reporter reporter;
	@Mock
	private ReporterErrorMessageHandler reporterError;
	@Mock
	private ScheduledExecutorService executor;
	@InjectMocks
	private ReportService service = new ReportService();
	@Captor
	private ArgumentCaptor<Runnable> runnableCaptor;

	private Runnable process;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service.initialize();
		verify(executor).scheduleWithFixedDelay(runnableCaptor.capture(), anyLong(), anyLong(), any(TimeUnit.class));
		process = runnableCaptor.getValue();
	}

	@After
	public void teardown() {
		service.destroy();
		verify(executor).shutdownNow();
	}

	@Test
	public void shouldCheckReportServiceShuttingDownJustBeforeInitialization() throws Exception {
		executor.awaitTermination(10, TimeUnit.SECONDS);
		process.run();
		verify(reporter).send();
		verify(reporterError).send();
	}

	@Test
	public void shouldStartAndStopServices() throws Exception {
		process.run();

		verify(reporter).send();
		verify(reporterError).send();
	}
}
