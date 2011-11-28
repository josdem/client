package com.all.core.common.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.SystemConstants;
import com.all.core.common.services.reporting.Reporter;
import com.all.shared.stats.ErrorEventStat;

public class ErrorEventStatProcessor {

	private static final String ORG_SLF4J_IMPL_JCL_LOGGER_ADAPTER = "org.slf4j.impl.JCLLoggerAdapter";

	private static Log LOG = LogFactory.getLog(ErrorEventStatProcessor.class);

	private static ErrorEventStatProcessor errorEventStatProcessor;

	private static final String PROP_FILE = "config/clientSettings.properties";

	private BlockingQueue<LoggingEvent> queue = new LinkedBlockingQueue<LoggingEvent>();

	private ExecutorService executor = Executors.newCachedThreadPool(new IncrementalNamedThreadFactory(this.getClass()
			.getSimpleName()));

	private Future<?> future;

	private String email;

	private String artifactID;

	private ErrorEventStatProcessor() {
	}

	public static ErrorEventStatProcessor getInstance() {
		if (errorEventStatProcessor == null) {
			errorEventStatProcessor = new ErrorEventStatProcessor();
		}
		return errorEventStatProcessor;
	}

	public void process(LoggingEvent loggingEvent) {
		//we ignore events for SLF4J
		if (!ORG_SLF4J_IMPL_JCL_LOGGER_ADAPTER.equals(loggingEvent.getLocationInformation().getClassName())) {
			queue.offer(loggingEvent);
		}
	}

	public void bind(Reporter reporter) {
		if (reporter != null) {
			if (future != null) {
				future.cancel(true);
			}
			future = executor.submit(new ProcessorThread(reporter));
		}
	}

	public void stop() {
		executor.shutdownNow();
	}

	private ErrorEventStat convert(LoggingEvent loggingEvent) {
		ErrorEventStat errorEventStat = new ErrorEventStat();
		LocationInfo locationInformation = loggingEvent.getLocationInformation();

		errorEventStat.setEmail(email);
		errorEventStat.setClientVersion(getArtifactId());
		errorEventStat.setOs(System.getProperty(SystemConstants.OS_NAME_PROP_KEY));
		errorEventStat.setOsVersion(System.getProperty(SystemConstants.OS_VERSION_PROP_KEY));
		errorEventStat.setJvm(System.getProperty(SystemConstants.JAVA_VERSION_PROP_KEY));
		errorEventStat.setThreadName(loggingEvent.getThreadName());
		errorEventStat.setMessage(loggingEvent.getRenderedMessage());
		errorEventStat.setFileName(locationInformation.getFileName());
		errorEventStat.setLineNumber(locationInformation.getLineNumber());
		errorEventStat.setMethodName(locationInformation.getMethodName());
		errorEventStat.setClassName(locationInformation.getClassName());
		
		ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();
		if(throwableInformation != null) {
			errorEventStat.setStackTrace(throwableInformation.getThrowable().getMessage());
		}

		return errorEventStat;
	}

	private String getArtifactId() {
		if (artifactID == null) {
			InputStream is = null;
			try {
				is = ErrorEventStatProcessor.class.getClassLoader().getResourceAsStream(PROP_FILE);
				Properties clientSettings = new Properties();
				clientSettings.load(is);
				artifactID = clientSettings.getProperty(SystemConstants.ARTIFACT_VERSION_KEY);
			} catch (IOException e) {
				LOG.error("Couldn't get artifact id");
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		return artifactID;
	}

	public void setUser(String email) {
		this.email = email;
	}

	class ProcessorThread implements Runnable {

		private final Reporter reporter;

		public ProcessorThread(Reporter reporter) {
			this.reporter = reporter;
		}

		@Override
		public void run() {
			while (true) {
				try {
					LoggingEvent loggingEvent = queue.take();
					ErrorEventStat errorEventStat = convert(loggingEvent);
					reporter.log(errorEventStat);
				} catch (Exception e) {
					LOG.error("Unable to log error event stat", e);
				}
			}
		}
	}

}
