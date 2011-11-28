package com.all.core.common.services.reporting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
	private static final Log LOG = LogFactory.getLog(ReportService.class);

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private ReporterServiceRunner serviceRunner;

	@Autowired
	private Reporter reporter;

	@Autowired
	private ReporterErrorMessageHandler reporterError;

	public ReportService() {
	}

	@PostConstruct
	public void initialize() {
		LOG.info("Initializing report sender service...");
		serviceRunner = new ReporterServiceRunner(reporter, reporterError);
		executor.scheduleWithFixedDelay(serviceRunner, 30, 30, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void destroy() {
		try {
			serviceRunner.run();
		} catch (Exception e) {
			LOG.error(e, e);
		}
		LOG.info("Destroying report sender service...");
		try {
			executor.shutdownNow();
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	private final class ReporterServiceRunner implements Runnable {
		private final Reporter reporter;
		private final ReporterErrorMessageHandler reporterError;

		public ReporterServiceRunner(Reporter reporter, ReporterErrorMessageHandler reporterError) {
			this.reporter = reporter;
			this.reporterError = reporterError;
		}

		public void run() {
			reporter.send();
			reporterError.send();
		}

	}
}
