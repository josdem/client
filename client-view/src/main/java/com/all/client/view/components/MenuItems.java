package com.all.client.view.components;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import com.all.i18n.Messages;

public enum MenuItems {
	DELETE("delete", "delete", null),
	CUT("cut", "cut", null),
	COPY("copy", "copy", (char) KeyEvent.VK_P),
	SELECT_ALL("menu.edit.selectAll", "selectAll", null),
	PASTE("paste", "paste", null),
	SHOW_WIZARD("menu.file.showWizard", "showWizard", null),
	SHOW_PORT_FWD_WIZARD("menu.file.showPortFwdWizard", "How to Share?", null),
	CREATE_SMART_PLAYLIST("menu.file.createSmartplaylist", "createSmartplaylist", null),
	MOVEOUT_FROM_FOLDER("menu.file.moveOutFromFolder", "moveOutFromFolder", null),
	IMPORT_A_TRACK("menu.file.importATrack", "importTrack", 'i'),
	RENAME("menu.file.rename", "rename", null),
	CREATE_PLAYLIST("menu.file.createPlaylist", "createPlaylist", null),
	CREATE_FOLDER("menu.file.createFolder", "createFolder", null),
	IMPORT_FOLDER("menu.file.importAFolder", "importFolder", 'f'),
	SIGN_IN_1("menu.file.signIn", "signIn1", null),
	SIGN_IN_2("menu.file.signOut", "signIn2", null),
	CLOSE_APP("menu.shortcut.closeApp", "closeApp", null),
	ADD_CONTACT("menu.contacts.add", "addContact", null),
	DELETE_CONTACT_FOLDER("menu.contacts.folder.delete", "deleteContactFolder", null),
	DELETE_CONTACT("menu.contacts.delete", "deleteContact", null),
	SEND_INVITATION("menu.contacts.sendInvitation", "sendInvitation", null),
	SYNC_MUSIC("menu.sync.syncMusic", "syncMusic", null),
	SYNC_VIDEO("menu.sync.syncVideo", "syncVideo", null),
	SYNC_FILES("menu.sync.syncFiles", "syncFiles", null),
	SYNC_PODCAST("menu.sync.syncPodcast", "syncPodcast", null),
	SYNC_PHOTOS("menu.sync.syncPhotos", "syncPhotos", null),
	SYNC_AUDIOBOOKS("menu.sync.syncAudiobooks", "syncAudiobooks", null),
	SYNC_GAMES("menu.sync.syncGames", "syncGames", null),
	IMPORT_ITUNES_LIBRARY("menu.file.importITunesLib", "importITunes", null),
	BROKEN_LINKS("menu.preferences.brokenLinks", "brokenLinks", null),
	FIND_LOCAL_TRACKS("menu.file.findLocalTracks","findLocalTracks",null),
	CHAT("menu.contacts.chat", "chat", null),
	FACEBOOK_CHAT("menu.contacts.facebook.chat", "Facebook Chat", null),
	RESUME_DOWNLOAD("menu.download.resume", "resumeDownload", null),
	REMOTE_LIBRARY("menu.contactlist.openRemoteLibrary", "remoteLibrary", null),

	CLEAR_DOWNLOAD("menu.download.clear", "clearDownload", null),
	SEND_CONTENT("menu.library.sendContent", "sendContent", null),

	CONTACT_LIST("menu.contactlist.name", "contactList", null),
	EDIT_PROFILE("menu.contactlist.editProfile", "editProfile", null),
	OPEN_PENDING_EMAILS("menu.contactlist.openPendinEmails", "openPendinEmails", null),
	OPEN_SEARCH_CONTACTS("menu.contactlist.openSearchContacts", "openSearchContacts", null),

	PAUSE_DOWNLOAD("menu.download.pause", "pauseDownload", null),
	
	PLAY_PAUSE("menu.shortcuts.player.playpause", "playpause", null),
	PREVIOUS("menu.shortcuts.player.previous", "previous", null),
	NEXT("menu.shortcuts.player.next", "next", null),
	FAST_FORWARD("menu.shortcuts.player.fastforward", "fastForward", null),
	REWIND("menu.shortcuts.player.rewind", "rewind", null),
	MUTE("menu.shortcuts.player.mute", "mute", null),
	VOLUME_UP("menu.shortcuts.player.volumeUp", "volumeUp", null),
	VOLUME_DOWN("menu.shortcuts.player.volumeDown", "volumeDown", null),
	
	PROFILE("menu.contacts.profile", "Profile", null),

	DOWNLOAD("menu.download", "Download", null), 
	I18N_EN_US("menu.languages.en_US","enUS",null),
	I18N_ES_MX("menu.languages.es_MX","esMX",null), 
	
	FIND_FRIENDS("menu.tools.findFriends", "findFriends", null);

	private final String bundle;
	private final String name;
	private final Character mnemonic;

	private MenuItems(String bundle, String name, Character mnemonic) {
		this.bundle = bundle;
		this.name = name;
		this.mnemonic = mnemonic;
	}

	public JMenuItem getItem() {
		JMenuItem menuItem = new JMenuItem();
		menuItem.setName(this.name);
		menuItem.setText(this.name);
		if (this.mnemonic != null) {
			menuItem.setMnemonic(this.mnemonic);
		}
		return menuItem;
	}

	public void internationalize(JMenuItem item, Messages messages) {
		item.setText(messages.getMessage(this.bundle));
	}

	public String getName() {
		return name;
	}

}
