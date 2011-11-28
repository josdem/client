package com.all.core.common.services.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.all.core.common.services.ApplicationConfig;
import com.all.shared.json.JsonConverter;
import com.all.shared.stats.AllStat;

@Repository
public class ReporterStatFileManager {
	private static final Log LOG = LogFactory.getLog(ReporterStatFileManager.class);
	private static final String REPORTING_FOLDER_NAME = "reporting";
	@Autowired
	private ApplicationConfig config;

	private final Charset charset;
	private long lastTimestamp = 0;

	public ReporterStatFileManager() {
		charset = Charset.forName("UTF-8");
	}

	public File saveStats(List<AllStat> stats) {
		File reportFolder = config.getUserFolder(REPORTING_FOLDER_NAME);
		File outputFile = getOutputFile(reportFolder);
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(outputFile), charset);
			String json = JsonConverter.toJson(stats);
			out.write(json);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		IOUtils.closeQuietly(out);
		return outputFile;
	}

	private synchronized File getOutputFile(File reportFolder) {
		long currentTime = System.currentTimeMillis();
		while (currentTime == lastTimestamp) {
			currentTime = System.currentTimeMillis();
		}
		lastTimestamp = currentTime;
		String filename = Long.toString(currentTime);
		File outputFile = new File(reportFolder, filename);
		return outputFile;
	}

	public Iterable<List<AllStat>> load() {
		File reportFolder = config.getAppFolder(REPORTING_FOLDER_NAME);
		File[] files = reportFolder.listFiles();
		return new FileStatExtractIterable(files, charset);
	}
}
