/**
 * 
 */
package com.all.core.common.services.reporting;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.all.shared.stats.AllStat;

class FileStatExtractIterable implements Iterable<List<AllStat>> {
	private final List<File> files;
	private final Charset charset;

	public FileStatExtractIterable(File[] files, Charset charset) {
		this.charset = charset;
		this.files = new ArrayList<File>();
		for (File file : files) {
			this.files.add(file);
		}
	}

	@Override
	public Iterator<List<AllStat>> iterator() {
		return new FileStatExtractIterator(files, charset);
	}
}