package com.all.client.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;

public class EmptyPlaylist implements Playlist {
	private static final long serialVersionUID = 1L;
	private final String owner;
	private final Date time;
	private final String name;

	public EmptyPlaylist(Root root) {
		owner = root.getOwnerMail();
		time = new Date();
		name = root.getName();
	}

	@Override
	public int compareTo(Playlist o) {
		return -1;
	}

	@Override
	public boolean contains(Track song) {
		return false;
	}

	@Override
	public List<Track> getTracks() {
		return Collections.emptyList();
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public int trackCount() {
		return 0;
	}

	@Override
	public Track getTrack(int position) {
		return null;
	}

	@Override
	public Folder getParentFolder() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return time;
	}

	@Override
	public Date getCreationDate() {
		return time;
	}

	@Override
	public Date getLastPlayed() {
		return time;
	}

	@Override
	public int trackPosition(Track track) {
		return -1;
	}

	@Override
	public boolean isSmartPlaylist() {
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getHashcode() {
		return "0";
	}

	@Override
	public boolean isNewContent() {
		return false;
	}

}
