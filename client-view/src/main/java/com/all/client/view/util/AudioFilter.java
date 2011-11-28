package com.all.client.view.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.all.client.util.FileUtil;

public class AudioFilter extends FileFilter {
	FileUtil fileUtil = new FileUtil();

	@Override
	public boolean accept(File f) {
		String ext = FileUtil.getExtension(f);
		try {
			if (f.isDirectory()) {
				return true;
			}
			AudioFiles.valueOf(ext);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return "Audio files";
	}

	enum AudioFiles {
		MP3, M4A, OGG, AIFF, WMA, WAV, AU, M4B, MP2, M4P, AMR, AAC
	}
}
