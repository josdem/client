package com.all.client.services.delegates;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.all.client.notifiers.Notifier;

public class ImportResult implements Notifier {
	private final long totalTracksBefore;
	private final long totalPlaylistsBefore;
	private final long totalFoldersBefore;
	private final long totalFiles;
	private long count = 0;
	private final Set<File> invalidFiles;
	private long afterImportedTracks;
	private long afterImportedPlaylists;
	private long afterImportedFolders;
	private Notifier notifier;

	public ImportResult(long totalTracksBefore, long totalPlaylistsBefore, long totalFoldersBefore, long totalFiles) {
		this.totalTracksBefore = totalTracksBefore;
		this.totalPlaylistsBefore = totalPlaylistsBefore;
		this.totalFoldersBefore = totalFoldersBefore;
		this.totalFiles = totalFiles;
		this.invalidFiles = new HashSet<File>();
	}

	public long getTotalTracksBefore() {
		return totalTracksBefore;
	}

	public long getTotalPlaylistsBefore() {
		return totalPlaylistsBefore;
	}

	public long getTotalFoldersBefore() {
		return totalFoldersBefore;
	}

	public long getTotalFiles() {
		return totalFiles;
	}

	public Iterable<File> getInvalidFiles() {
		return invalidFiles;
	}

	public void setAfterImportedTracks(long afterImportedTracks) {
		this.afterImportedTracks = afterImportedTracks;
	}

	public void setAfterImportedPlaylists(long afterImportedPlaylists) {
		this.afterImportedPlaylists = afterImportedPlaylists;
	}

	public void setAfterImportedFolders(long afterImportedFolders) {
		this.afterImportedFolders = afterImportedFolders;
	}

	public int getImportedTracks() {
		return (int) (afterImportedTracks - totalTracksBefore);
	}

	public int getImportedPlaylists() {
		return (int) (afterImportedPlaylists - totalPlaylistsBefore);
	}

	public int getImportedFolders() {
		return (int) (afterImportedFolders - totalFoldersBefore);
	}

	public long getCount() {
		return count;
	}

	public void add() {
		count++;
	}

	public void add(long count) {
		this.count += count;
	}

	public int getPercent() {
		return (int) (((double) count / totalFiles) * 100.0);
	}

	public Set<File> getInvalidFilesSet() {
		return invalidFiles;
	}

	@Override
	public void notifyObserver() {
		add();
		if (notifier != null) {
			notifier.notifyObserver();
		}
	}

	public void addInvalid(File file) {
		invalidFiles.add(file);
	}

	public void setNotificationResponder(Notifier notifier) {
		this.notifier = notifier;
	}
}
