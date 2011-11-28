package com.all.itunes;

import com.all.itunes.exception.ITunesException;

public interface LibraryiTunes {
	public void sync(String directoryPath) throws ITunesException;
}
