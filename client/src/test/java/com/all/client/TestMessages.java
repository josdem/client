package com.all.client;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class TestMessages extends SimpleGUITest {

	@Test
	public void shouldGetTheMessagesObjectFromSpringContext() throws Exception {
		assertEquals("ALL", messages.getMessage("client.title"));
	}

	@Ignore @Test //TODO i18nlize
	public void shouldGetAllMenuMessagesInEnglishUS() throws Exception {
		assertEquals("File", messages.getMessage("menu.file.name"));
		assertEquals("New Folder", messages.getMessage("menu.file.createFolder"));
		assertEquals("New Playlist", messages.getMessage("menu.file.createPlaylist"));
		assertEquals("Import a Track", messages.getMessage("menu.file.importATrack"));
		assertEquals("Import a Folder", messages.getMessage("menu.file.importAFolder"));
		assertEquals("Connect iPod", messages.getMessage("menu.file.connectIPod"));
		assertEquals("* Untitled Playlist ", messages.getMessage("playlist.defaultNewName"));
		assertEquals("* Untitled Folder ", messages.getMessage("folder.defaultNewName"));
	}

}
