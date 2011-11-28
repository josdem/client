package com.all.client.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.stereotype.Component;

import com.all.client.model.InvalidFileException;
import com.all.client.model.TrackFile;
import com.all.client.util.FileUtil;
import com.all.shared.model.Track;

@Component
public class TrackFactory {
	private static final String[] audioTaggerFileFormats = new String[] { "MP3", "M4A", "M4B", "OGG", "WMA", "WAV",
			"MP2", "AAC" };
	private static final String[] metadataStrategyFormat = new String[] { "AIFF", "AU", "AMR" };

	private final Map<String, MetadataStrategy> strategies;

	private final List<String> audioSuffixes;

	public TrackFactory() {
		strategies = new HashMap<String, MetadataStrategy>();
		JAudioTaggerStrategy jasStrategy = new JAudioTaggerStrategy();

		MetadataStrategy strategy = new BasicStrategy();
		for (String fileFormat : audioTaggerFileFormats) {
			strategies.put(fileFormat, jasStrategy);
		}

		for (String fileFormat : metadataStrategyFormat) {
			strategies.put(fileFormat, strategy);
		}

		audioSuffixes = new ArrayList<String>(audioTaggerFileFormats.length + metadataStrategyFormat.length);
		audioSuffixes.addAll(Arrays.asList(audioTaggerFileFormats));
		audioSuffixes.addAll(Arrays.asList(metadataStrategyFormat));
	}

	public Track createTrack(TrackFile trackFile) throws InvalidFileException {
		MetadataStrategy strategy = strategies.get(getFileExtension(trackFile.getFile()));
		return strategy == null ? null : strategy.createTrack(trackFile);
	}

	public boolean isFileFormatSupported(File file) {
		return strategies.containsKey(getFileExtension(file));
	}

	@SuppressWarnings("unchecked")
	public List<File> listTracks(File dir) {
		IOFileFilter fileFilter = new SuffixFileFilter(audioSuffixes, IOCase.INSENSITIVE);
		return (List<File>) FileUtils.listFiles(dir, fileFilter, FileFilterUtils.directoryFileFilter());
	}

	private String getFileExtension(File file) {
		return FileUtil.getExtension(file);
	}

}
