package com.all.core.common.services.reporting;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.core.common.services.ApplicationConfig;
import com.all.core.common.services.reporting.FileStatExtractIterable;
import com.all.core.common.services.reporting.ReporterStatFileManager;
import com.all.shared.json.JsonConverter;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.UserSessionStat;
import com.all.shared.stats.usage.UserActionStat;

public class TestReporterStatFileManager {
	@InjectMocks
	private ReporterStatFileManager fileManager;
	@Mock
	private ApplicationConfig config;

	@Before
	public void setup() {
		fileManager = new ReporterStatFileManager();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSaveStats() throws Exception {
		File tempFile = File.createTempFile("abc", null);
		when(config.getAppFolder("reporting")).thenReturn(tempFile.getParentFile());
		List<AllStat> stats = new ArrayList<AllStat>();

		File savedStats = fileManager.saveStats(stats);
		savedStats.deleteOnExit();

		String fileContents = readFile(savedStats);
		String expectedJson = JsonConverter.toJson(stats);
		assertEquals(expectedJson, fileContents);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSaveStatsWithMoreComplexThings() throws Exception {
		File tempFile = File.createTempFile("abc", null);
		when(config.getAppFolder("reporting")).thenReturn(tempFile.getParentFile());
		List<AllStat> stats = new ArrayList<AllStat>();
		stats.add(new UserActionStat());
		stats.add(new UserSessionStat());

		File savedStats = fileManager.saveStats(stats);
		savedStats.deleteOnExit();

		String fileContents = readFile(savedStats);
		String expectedJson = JsonConverter.toJson(stats);
		assertEquals(expectedJson, fileContents);
	}

	@SuppressWarnings("deprecation")
	@Test
	@Ignore("For safety, though it runs it may generate errors since its accessing the filesystem directories")
	public void UBERTEST() throws Exception {
		File tempFile = File.createTempFile("abc", null);
		File tempDir = new File(tempFile.getParent(), "tempTestUberTest");
		tempDir.mkdir();
		tempDir.deleteOnExit();
		when(config.getAppFolder("reporting")).thenReturn(tempDir);
		List<AllStat> stats = new ArrayList<AllStat>();
		stats.add(new UserActionStat());
		stats.add(new UserSessionStat());

		File savedStats = fileManager.saveStats(stats);
		savedStats.deleteOnExit();

		Iterable<List<AllStat>> load = fileManager.load();
		Iterator<List<AllStat>> iterator = load.iterator();
		assertTrue(iterator.hasNext());
		List<AllStat> next = iterator.next();
		assertNotNull(next);
		assertFalse(next.isEmpty());
		assertEquals(2, next.size());
		assertFalse(iterator.hasNext());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void TestTheIteratorHandling() throws Exception {
		File tempFile = File.createTempFile("abc", null);
		when(config.getAppFolder("reporting")).thenReturn(tempFile.getParentFile());
		List<AllStat> stats = new ArrayList<AllStat>();
		stats.add(new UserActionStat());
		stats.add(new UserSessionStat());

		File savedStats = fileManager.saveStats(stats);
		savedStats.deleteOnExit();

		FileStatExtractIterable fileStatExtractIterable = new FileStatExtractIterable(new File[] { savedStats },
				Charset.forName("UTF-8"));
		Iterator<List<AllStat>> iterator = fileStatExtractIterable.iterator();
		assertTrue(iterator.hasNext());
		List<AllStat> next = iterator.next();
		assertNotNull(next);
		assertFalse(next.isEmpty());
		assertEquals(2, next.size());
		assertFalse(iterator.hasNext());

	}

	private String readFile(File savedStats) throws Exception {
		Reader reader = new InputStreamReader(new FileInputStream(savedStats), "UTF-8");
		StringBuilder text = new StringBuilder();
		int c = -1;
		while ((c = reader.read()) != -1) {
			text.append((char) c);
		}
		reader.close();
		return text.toString();
	}
}
