package com.all.client.devices;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.all.appControl.control.ControlEngine;
import com.all.client.util.FileUtil;
import com.all.client.util.TrackRepository;
import com.all.client.util.FileUtil.FileCopyObserver;
import com.all.core.events.ByteProgressEvent;
import com.all.core.events.Events;
import com.all.core.events.FileProgressEvent;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class DeviceCopyWorker implements FileCopyObserver {
	private final ModelCollection model;
	private final File root;
	private boolean cancel = false;
	private int totalFiles;
	private int currentFiles;
	private long totalBytes;
	private long currentBytes;
	private Set<Track> errors;
	private boolean grayCheck = false;
	private final TrackRepository trackRepository;
	private final ControlEngine controlEngine;

	public DeviceCopyWorker(ModelCollection model, File root, TrackRepository trackRepository, ControlEngine controlEngine) {
		this.model = model;
		this.trackRepository = trackRepository;
		this.controlEngine = controlEngine;
		this.errors = new HashSet<Track>();
		if (!root.isDirectory()) {
			root = root.getParentFile();
		}
		this.root = root;
	}

	public void cancel() {
		cancel = true;
	}

	public boolean doInBackground() {
		try {
			calculateTotals();
			controlEngine.fireEvent(Events.Devices.START_COPY);
			if (totalFiles > 0) {
				doCopy();
			}
		} catch (InterruptedException e) {
		}
		controlEngine.fireEvent(Events.Devices.FINISH_COPY);
		return !cancel;
	}

	private void calculateTotals() throws InterruptedException {
		sumTracks(model.getTracks());
		for (Playlist pl : model.getPlaylists()) {
			sumTracks(pl.getTracks());
		}
		for (Folder fl : model.getFolders()) {
			for (Playlist pl : fl.getPlaylists()) {
				sumTracks(pl.getTracks());
			}
		}
	}

	private void sumTracks(List<Track> tracks) throws InterruptedException {
		for (Track track : tracks) {
			checkInterrupt();
			checkInterrupt();
			if (trackRepository.isLocallyAvailable(track.getHashcode())) {
				totalFiles++;
				totalBytes += trackRepository.getFile(track.getHashcode()).length();
			} else {
				if (!grayCheck) {
					grayCheck = true;
					controlEngine.fireEvent(Events.Devices.CANNOT_COPY);
				}
				errors.add(track);
			}
		}
	}

	private void doCopy() throws InterruptedException {
		String folderName;
		String playlistName;
		copy(root, model.getTracks());
		for (Playlist pl : model.getPlaylists()) {
			playlistName = formatPlaylistFolderName(pl.getName());
			File folder = new File(root, playlistName);
			copy(folder, pl.getTracks());
		}
		for (Folder fl : model.getFolders()) {
			folderName = formatPlaylistFolderName(fl.getName());
			File fold = new File(root, folderName);
			for (Playlist pl : fl.getPlaylists()) {
				playlistName = formatPlaylistFolderName(pl.getName());
				File folder = new File(fold, playlistName);
				copy(folder, pl.getTracks());
			}
		}
	}

	/*
	 * - Illegal filename characters: \ (backslash), / (forward slash), : (colon),
	 * * (asterisk), ? (question mark), " (double quotes), < (left angle bracket),
	 * > (right angle bracket), | (pipe), % (percentage), # (pound), $ (dollar)
	 * 
	 * - Starts or ends with point or white spaces
	 */
	private String formatPlaylistFolderName(String original) {
		// TODO: Check the rules defined for BAs
		List<Character> invalidChars = Arrays.asList('*', '?', '"', '&', '<', '>', '|', '%', '#', '$', '/', '\\', '.');

		String formated = original;
		for (Character character : invalidChars) {
			formated = StringUtils.remove(formated, character);
		}
		formated = StringUtils.trim(formated);

		if (formated == null || formated.isEmpty()) {
			// TODO: Define a rule when the playlist/folder formated name is empty or
			// null
			formated = "Untitled";
		}
		return formated;
	}

	private void copy(File destinationFolder, List<Track> tracks) throws InterruptedException {
		if (tracks == null || tracks.isEmpty()) {
			return;
		}
		try {
			destinationFolder.mkdirs();
		} catch (Exception e) {
		}
		for (Track track : tracks) {
			copy(destinationFolder, track);
		}
	}

	private void copy(File destinationFolder, Track track) throws InterruptedException {
		if (trackRepository.isLocallyAvailable(track.getHashcode())) {
			copy(trackRepository.getFile(track.getHashcode()), destinationFolder, track.getName());
		}
	}

	private void copy(File source, File destinationFolder, String trackname) throws InterruptedException {
		currentFiles++;
		long currentBytes = this.currentBytes;
		int filePercentage = (int) (((double) currentFiles / totalFiles) * 100);
		controlEngine.fireEvent(Events.Devices.FILE_PROGRESS, new FileProgressEvent(filePercentage, currentFiles,
				totalFiles, trackname));

		FileUtil.copy(source, destinationFolder, this);

		this.currentBytes += source.length();
		int sizePercentage = (int) (((double) currentBytes / totalBytes) * 100);
		controlEngine.fireEvent(Events.Devices.BYTE_PROGRESS, new ByteProgressEvent(sizePercentage, currentBytes,
				totalBytes));
		checkInterrupt();
	}

	public void checkInterrupt() throws InterruptedException {
		if (cancel) {
			throw new InterruptedException("Cancel by the user");
		}
	}

	@Override
	public void complete(long totalFileBytes) {
		this.currentBytes += totalFileBytes;
		int sizePercentage = (int) (((double) currentBytes / totalBytes) * 100);
		controlEngine.fireEvent(Events.Devices.BYTE_PROGRESS, new ByteProgressEvent(sizePercentage, currentBytes,
				totalBytes));

	}

	@Override
	public void copyProgress(long currentFileBytes, long totalFileBytes) {
		long currentBytes = this.currentBytes + currentFileBytes;
		int progress = (int) (((double) currentBytes / totalBytes) * 100);
		controlEngine.fireEvent(Events.Devices.BYTE_PROGRESS, new ByteProgressEvent(progress, currentBytes, totalBytes));
	}

	@Override
	public void deviceFull() {
		controlEngine.fireEvent(Events.Devices.DEVICE_FULL);
		cancel = true;
	}
}