/**
 * 
 */
package com.all.core.common.services.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.shared.json.JsonConverter;
import com.all.shared.stats.AllStat;

class FileStatExtractIterator implements Iterator<List<AllStat>> {
	private static final Log LOG = LogFactory.getLog(FileStatExtractIterator.class);
	Iterator<File> innerIterator;
	private final Charset charset;

	public FileStatExtractIterator(List<File> files, Charset charset) {
		this.charset = charset;
		innerIterator = files.iterator();
	}

	@Override
	public boolean hasNext() {
		return innerIterator.hasNext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AllStat> next() {
		File file = innerIterator.next();
		try {
			String json = readAndDelete(file);
			if (json == null) {
				return Collections.EMPTY_LIST;
			}
			json = json.trim();
			return JsonConverter.toTypedCollection(json, ArrayList.class, AllStat.class);
		} catch (Exception e) {
			LOG.error("FILE: " + file + " FAILED TO PARSE STATS. " + e, e);
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public void remove() {
	}

	private String readAndDelete(File file) {
		if (file.isDirectory()) {
			return null;
		}
		Reader reader = null;
		StringBuilder json = new StringBuilder();
		try {
			reader = new InputStreamReader(new FileInputStream(file), charset);
			int c = -1;
			while ((c = reader.read()) != -1) {
				json.append((char) c);
			}
		} catch (IOException e) {
			LOG.error(e, e);
		}
		IOUtils.closeQuietly(reader);
		file.delete();
		return json.toString();

	}
}