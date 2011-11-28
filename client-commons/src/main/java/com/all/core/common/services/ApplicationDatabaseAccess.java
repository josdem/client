package com.all.core.common.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.core.common.model.ApplicationDatabase;
import com.all.shared.json.JsonConverter;

@Service
public class ApplicationDatabaseAccess implements Runnable {

	private static final Log LOG = LogFactory.getLog(ApplicationDatabaseAccess.class);

	private File file;

	private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	private long loadTime = -1;

	private ApplicationDatabase db;

	@Autowired
	public ApplicationDatabaseAccess(ApplicationConfig appConfig) {
		JsonConverter.addJsonReader(ApplicationDatabase.class, new ApplicationDatabaseReader());
		file = new File(appConfig.getUserFolder("config"), "cdata.dbg");
	}

	@PostConstruct
	public void setup() {
		executorService.scheduleAtFixedRate(this, 15, 60, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void shutdown() {
		executorService.shutdownNow();
		run();
	}

	public ApplicationDatabase getDB() {
		long lastModified = file.lastModified();
		if (db == null) {
			synchronized (this) {
				if (db == null) {
					db = loadDatabase(file);
					loadTime = lastModified;
				}
			}
		}
		if (loadTime != lastModified) {
			synchronized (this) {
				if (loadTime != lastModified) {
					loadTime = lastModified;
					ApplicationDatabase fileDb = loadDatabase(file);
					db.addAll(fileDb);
					save(file, db);
				}
			}
		}
		return db;
	}

	@Override
	public void run() {
		try {
			save(file, getDB());
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	private static ApplicationDatabase loadDatabase(File file) {
		ApplicationDatabase db = null;
		if (file.exists()) {
			Scanner scanner = null;
			try {
				StringBuilder text = new StringBuilder();
				String NL = System.getProperty("line.separator");
				scanner = new Scanner(new FileInputStream(file));
				while (scanner.hasNextLine()) {
					text.append(scanner.nextLine() + NL);
				}
				db = JsonConverter.toBean(text.toString(), ApplicationDatabase.class);
			} catch (Exception e) {
				LOG.error(e, e);
			} finally {
				try {
					scanner.close();
				} catch (Exception e) {
					LOG.error(e, e);
				}
			}
		}
		if (db == null) {
			db = new ApplicationDatabase();
		}
		return db;
	}

	private static void save(File file, ApplicationDatabase db) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file));
			out.write(JsonConverter.toJson(db));
		} catch (Exception e) {
			LOG.error(e, e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				LOG.error(e, e);
			}
		}
	}

}
