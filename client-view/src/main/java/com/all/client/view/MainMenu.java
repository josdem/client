package com.all.client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatType;
import com.all.client.model.Download;
import com.all.client.util.ModelValidation;
import com.all.client.view.components.MenuItems;
import com.all.client.view.contacts.ContactListPanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dialog.FileDialogResult;
import com.all.client.view.flows.AddContactFlow;
import com.all.client.view.flows.CloseFlow;
import com.all.client.view.flows.DeleteFlow;
import com.all.client.view.flows.EditProfileFlow;
import com.all.client.view.flows.ImportItunesFlow;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.client.view.util.ClipboardConverter;
import com.all.client.view.wizard.WizardDialog;
import com.all.commons.Environment;
import com.all.core.actions.Actions;
import com.all.core.actions.ModelImportAction;
import com.all.core.actions.ShareContentAction;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.model.ApplicationLanguage;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservePropertyChanged;
import com.all.observ.Observer;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.MediaImportStat.ImportType;
import com.all.shared.stats.usage.UserActions;

@Component
public class MainMenu extends JMenuBar implements Internationalizable {

	private static final long serialVersionUID = 78020294661279120L;

	private static final Dimension MENU_DEFAULT_SIZE = new Dimension(1024, 19);

	private static final String MENU_FILE_SIGN_IN = "menu.file.signIn";

	private static final String PARENT_MENU_NAME = "parentMenu";

	private static final String SON_MENU_NAME = "sonMenu";

	private JMenu fileMenu;
	private JMenuItem signIn1MenuItem;
	private JMenuItem signIn2MenuItem;
	private JMenuItem createFolderMenuItem;
	private JMenuItem renameMenuItem;
	private JMenuItem importTrackItem;
	private JMenuItem importFolderItem;
	private JMenuItem createPlaylistMenuItem;
	private JMenu editMenu;
	private JMenuItem deleteMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem selectAllMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenu toolsMenu;
	private JMenu shortcutMenu;
	private JMenuItem closeApp1MenuItem;
	private JMenuItem closeApp2MenuItem;
	private JMenu playerMenu;
	private JMenuItem playPause;
	private JMenuItem next;
	private JMenuItem fastForward;
	private JMenuItem previous;
	private JMenuItem rewind;
	private JMenuItem mute;
	private JMenuItem volumeUp;
	private JMenuItem volumeDown;
	private JMenu downloadMenu;
	private JMenu helpMenu;
	private JMenuItem showWizardMenuItem;

	private JMenuItem importITunesLibItem;
	private JMenuItem brokenLinksItem;
	private JMenuItem localTracks;
	private JMenuItem pauseDownloadMenuItem;
	private JMenuItem resumeDownloadMenuItem;
	private JMenuItem clearDownloadsMenuItem;
	private JMenu libraryMenu;
	private JMenuItem createPlaylistLibraryMenuItem;
	private JMenuItem createFolderLibraryMenuItem;
	private JMenuItem sendContentMenuItem;
	private JMenu contactListMenu;
	private JMenuItem editProfileMenuItem;
	private JMenuItem openPendinEmailsMenuItem;
	private JMenuItem openSearchContactsMenuItem;

	private JMenu languagesMenu;
	private JMenuItem i18nEnUs;
	private JMenuItem i18nEsMx;

	private JMenuItem findFriendsItem;

	private Messages messages;

	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private VolumePanel volumePanel;
	@Autowired
	private HipecotechTopPanel hipecotechTopPanel;
	@Autowired
	private ContactListPanel contactListPanel;
	@Autowired
	private PanelFactory panelFactory;
	@Autowired
	private ViewEngine viewEngine;

	public void setUGLYdependencies(final ContactListPanel contactListPanel, final MainFrame mainFrame,
			final LocalDescriptionPanel localDescriptionPanel) {
		getSelectAllMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (localDescriptionPanel != null) {
					if (localDescriptionPanel.isShowing()) {
						localDescriptionPanel.selectAllTracksInDescriptionTable();
					}

				} else {
					throw new IllegalStateException("localDescriptionPanel cant not be null to call sellect all");
				}
				if (panelFactory.getDownloadPanel() != null) {
					if (panelFactory.getDownloadPanel().isShowing()) {
						panelFactory.getDownloadPanel().selectAllTracksInDownloadTable();
					}
				} else {
					throw new IllegalStateException("downloadPanel cant not be null to call sellect all");
				}
			}
		});

		getCloseApp1MenuItem().addActionListener(new CloseAppListener(viewEngine, dialogFactory));
		getCloseApp2MenuItem().addActionListener(new CloseAppListener(viewEngine, dialogFactory));

		getSignIn1MenuItem().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new CloseFlow(viewEngine, dialogFactory).logout();
			}
		});
		getSignIn2MenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CloseFlow(viewEngine, dialogFactory).logout();
			}
		});

		contactListPanel.onPendingEmailSelected().add(new Observer<ObservePropertyChanged<ContactListPanel, Boolean>>() {
			@Override
			public void observe(ObservePropertyChanged<ContactListPanel, Boolean> t) {
				getOpenPendinEmailsMenuItem().setEnabled(t.getValue());
			}
		});

	}

	public MainMenu() {
		super();
		setSize(MENU_DEFAULT_SIZE);
		setPreferredSize(MENU_DEFAULT_SIZE);
		add(getFileMenu());
		add(getEditMenu());
		add(getPreferencesMenu());
		add(getShortcutsMenu());
		add(getHelpMenu());
		enableControls(false);
	}

	public void enableControls(boolean enabled) {
		fileMenu.setEnabled(enabled);
		editMenu.setEnabled(enabled);
		toolsMenu.setEnabled(enabled);
		helpMenu.setEnabled(enabled);
		shortcutMenu.setEnabled(enabled);
	}

	public void internationalize(Messages messages) {
		MenuItems.SIGN_IN_2.internationalize(getSignIn1MenuItem(), messages);
		MenuItems.SIGN_IN_2.internationalize(getSignIn2MenuItem(), messages);
		MenuItems.CREATE_PLAYLIST.internationalize(getNewPlaylistMenuItem(), messages);
		MenuItems.CREATE_PLAYLIST.internationalize(getCreatePlaylistMenuItem(), messages);
		MenuItems.CREATE_FOLDER.internationalize(getNewFolderMenuItem(), messages);
		MenuItems.CREATE_FOLDER.internationalize(getCreateFolderMenuItem(), messages);
		MenuItems.IMPORT_A_TRACK.internationalize(getImportTrackItem(), messages);
		MenuItems.IMPORT_FOLDER.internationalize(getImportFolderItem(), messages);
		MenuItems.FIND_LOCAL_TRACKS.internationalize(getLocalTracks(), messages);

		MenuItems.IMPORT_ITUNES_LIBRARY.internationalize(getImportITunesLibraryItem(), messages);
		MenuItems.RENAME.internationalize(getRenameMenuItem(), messages);
		MenuItems.DELETE.internationalize(getDeleteMenuItem(), messages);
		MenuItems.COPY.internationalize(getCopyMenuItem(), messages);
		MenuItems.SELECT_ALL.internationalize(getSelectAllMenuItem(), messages);
		MenuItems.PASTE.internationalize(getPasteMenuItem(), messages);
		MenuItems.BROKEN_LINKS.internationalize(getBrokenLinksItem(), messages);
		MenuItems.CLOSE_APP.internationalize(getCloseApp1MenuItem(), messages);
		MenuItems.CLOSE_APP.internationalize(getCloseApp2MenuItem(), messages);

		MenuItems.CONTACT_LIST.internationalize(getContactListMenu(), messages);
		MenuItems.EDIT_PROFILE.internationalize(getEditProfileMenuItem(), messages);
		MenuItems.OPEN_PENDING_EMAILS.internationalize(getOpenPendinEmailsMenuItem(), messages);
		MenuItems.OPEN_SEARCH_CONTACTS.internationalize(getOpenSearchContactsMenuItem(), messages);

		MenuItems.PAUSE_DOWNLOAD.internationalize(getPauseDownloadMenuItem(), messages);
		MenuItems.RESUME_DOWNLOAD.internationalize(getResumeDownloadMenuItem(), messages);
		MenuItems.CLEAR_DOWNLOAD.internationalize(getClearDownloadsMenuItem(), messages);
		MenuItems.SEND_CONTENT.internationalize(getSendContentMenuItem(), messages);

		MenuItems.PLAY_PAUSE.internationalize(getPlayPauseMenuItem(), messages);
		MenuItems.PREVIOUS.internationalize(getPrevious(), messages);
		MenuItems.NEXT.internationalize(getNext(), messages);
		MenuItems.FAST_FORWARD.internationalize(getFastForward(), messages);
		MenuItems.REWIND.internationalize(getRewind(), messages);
		MenuItems.MUTE.internationalize(getMute(), messages);
		MenuItems.VOLUME_UP.internationalize(getVolumeUp(), messages);
		MenuItems.VOLUME_DOWN.internationalize(getVolumeDown(), messages);

		MenuItems.SHOW_WIZARD.internationalize(getShowWizardMenuItem(), messages);

		MenuItems.I18N_EN_US.internationalize(getI18nEnUS(), messages);
		MenuItems.I18N_ES_MX.internationalize(getI18nEsMX(), messages);

		MenuItems.FIND_FRIENDS.internationalize(getFindFriendsItem(), messages);

		getFileMenu().setText(messages.getMessage("menu.file.name"));
		getEditMenu().setText(messages.getMessage("menu.edit.name"));
		getPreferencesMenu().setText(messages.getMessage("menu.preferences.name"));
		getShortcutsMenu().setText(messages.getMessage("menu.shortcut.name"));
		getDownloadMenu().setText(messages.getMessage("menu.download.name"));
		getHelpMenu().setText(messages.getMessage("menu.help"));
		getLibraryMenu().setText(messages.getMessage("menu.library.name"));
		getPlayerMenu().setText(messages.getMessage("menu.shortcuts.player.name"));
		getLanguagesMenu().setText(messages.getMessage("menu.languages.name"));

	}

	private JMenu getPlayerMenu() {
		if (playerMenu == null) {
			playerMenu = new JMenu();
			playerMenu.setName(SON_MENU_NAME);
			playerMenu.add(getPlayPauseMenuItem());
			playerMenu.add(getNext());
			playerMenu.add(getPrevious());
			playerMenu.add(getMute());
			playerMenu.add(getVolumeUp());
			playerMenu.add(getVolumeDown());
		}
		return playerMenu;
	}

	private JMenu getPreferencesMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu();
			toolsMenu.setName(PARENT_MENU_NAME);
			toolsMenu.setMnemonic('p');
			toolsMenu.add(getLanguagesMenu());
			toolsMenu.add(getBrokenLinksItem());
			toolsMenu.add(getFindFriendsItem());
		}
		return toolsMenu;
	}

	private JMenuItem getFindFriendsItem() {
		if (findFriendsItem == null) {
			findFriendsItem = MenuItems.FIND_FRIENDS.getItem();
			findFriendsItem.setEnabled(false);
		}
		return findFriendsItem;
	}

	private JMenuItem getLanguagesMenu() {
		if (languagesMenu == null) {
			languagesMenu = new JMenu();
			languagesMenu.setName(SON_MENU_NAME);
			languagesMenu.add(getI18nEnUS());
			languagesMenu.add(getI18nEsMX());
		}
		return languagesMenu;
	}

	private JMenuItem getI18nEsMX() {
		if (i18nEsMx == null) {
			i18nEsMx = MenuItems.I18N_ES_MX.getItem();
		}
		return i18nEsMx;
	}

	private JMenuItem getI18nEnUS() {
		if (i18nEnUs == null) {
			i18nEnUs = MenuItems.I18N_EN_US.getItem();
		}
		return i18nEnUs;
	}

	private JMenuItem getBrokenLinksItem() {
		if (brokenLinksItem == null) {
			brokenLinksItem = MenuItems.BROKEN_LINKS.getItem();
		}
		return brokenLinksItem;
	}

	private JMenuItem getLocalTracks() {
		if (localTracks == null) {
			localTracks = MenuItems.FIND_LOCAL_TRACKS.getItem();
		}
		return localTracks;
	}

	private JMenu getShortcutsMenu() {
		if (shortcutMenu == null) {
			shortcutMenu = new JMenu();
			shortcutMenu.setName(PARENT_MENU_NAME);
			shortcutMenu.setMnemonic('s');

			shortcutMenu.add(getPlayerMenu());
			shortcutMenu.addSeparator();
			shortcutMenu.add(getLibraryMenu());
			shortcutMenu.addSeparator();
			shortcutMenu.add(getContactListMenu());
			shortcutMenu.addSeparator();
			shortcutMenu.add(getDownloadMenu());
			shortcutMenu.addSeparator();
			shortcutMenu.add(getSignIn2MenuItem());
			shortcutMenu.add(getCloseApp2MenuItem());
		}
		return shortcutMenu;
	}

	private JMenu getContactListMenu() {
		if (contactListMenu == null) {
			contactListMenu = new JMenu();
			contactListMenu.setName(SON_MENU_NAME);
			contactListMenu.add(getEditProfileMenuItem());
			contactListMenu.add(getOpenPendinEmailsMenuItem());
			contactListMenu.add(getOpenSearchContactsMenuItem());
		}
		return contactListMenu;
	}

	private JMenuItem getEditProfileMenuItem() {
		if (editProfileMenuItem == null) {
			editProfileMenuItem = MenuItems.EDIT_PROFILE.getItem();
			if (Environment.isMac()) {
				editProfileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.META_MASK));
			} else {
				editProfileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
			}
		}
		return editProfileMenuItem;
	}

	private JMenuItem getOpenPendinEmailsMenuItem() {
		if (openPendinEmailsMenuItem == null) {
			openPendinEmailsMenuItem = MenuItems.OPEN_PENDING_EMAILS.getItem();
			if (Environment.isMac()) {
				openPendinEmailsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.META_MASK));
			} else {
				openPendinEmailsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
			}
			openPendinEmailsMenuItem.setEnabled(false);
		}
		return openPendinEmailsMenuItem;
	}

	private JMenuItem getOpenSearchContactsMenuItem() {
		if (openSearchContactsMenuItem == null) {
			openSearchContactsMenuItem = MenuItems.OPEN_SEARCH_CONTACTS.getItem();
			if (Environment.isMac()) {
				openSearchContactsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_MASK));
			} else {
				openSearchContactsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
			}
		}
		return openSearchContactsMenuItem;
	}

	private JMenu getLibraryMenu() {
		if (libraryMenu == null) {
			libraryMenu = new JMenu();
			libraryMenu.setName(SON_MENU_NAME);
			libraryMenu.add(getCreatePlaylistMenuItem());
			libraryMenu.add(getCreateFolderMenuItem());
			libraryMenu.add(getSendContentMenuItem());
		}
		return libraryMenu;
	}

	private JMenuItem getCreatePlaylistMenuItem() {
		if (createPlaylistLibraryMenuItem == null) {
			createPlaylistLibraryMenuItem = MenuItems.CREATE_PLAYLIST.getItem();
			if (Environment.isMac()) {
				createPlaylistLibraryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_MASK));
			} else {
				createPlaylistLibraryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
			}
		}
		return createPlaylistLibraryMenuItem;
	}

	private JMenuItem getCreateFolderMenuItem() {
		if (createFolderLibraryMenuItem == null) {
			createFolderLibraryMenuItem = MenuItems.CREATE_FOLDER.getItem();
			int modifiers = Environment.isMac() ? InputEvent.META_MASK | InputEvent.SHIFT_MASK : InputEvent.CTRL_DOWN_MASK
					| InputEvent.SHIFT_DOWN_MASK;
			createFolderLibraryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, modifiers));
		}
		return createFolderLibraryMenuItem;
	}

	private JMenuItem getSendContentMenuItem() {
		if (sendContentMenuItem == null) {
			sendContentMenuItem = MenuItems.SEND_CONTENT.getItem();
			int modifiers = Environment.isMac() ? InputEvent.META_MASK | InputEvent.SHIFT_MASK : InputEvent.CTRL_DOWN_MASK
					| InputEvent.SHIFT_DOWN_MASK;
			sendContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, modifiers));
		}
		return sendContentMenuItem;
	}

	private JMenu getDownloadMenu() {
		if (downloadMenu == null) {
			downloadMenu = new JMenu();
			downloadMenu.setName(SON_MENU_NAME);
			downloadMenu.add(getPauseDownloadMenuItem());
			downloadMenu.add(getResumeDownloadMenuItem());
			downloadMenu.add(getClearDownloadsMenuItem());
		}
		return downloadMenu;
	}

	private JMenuItem getPauseDownloadMenuItem() {
		if (pauseDownloadMenuItem == null) {
			pauseDownloadMenuItem = MenuItems.PAUSE_DOWNLOAD.getItem();
			pauseDownloadMenuItem.setPreferredSize(new java.awt.Dimension(220, (int) pauseDownloadMenuItem.getPreferredSize()
					.getHeight()));
			pauseDownloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK));
		}
		return pauseDownloadMenuItem;
	}

	private JMenuItem getResumeDownloadMenuItem() {
		if (resumeDownloadMenuItem == null) {
			resumeDownloadMenuItem = MenuItems.RESUME_DOWNLOAD.getItem();
			int modifiers = Environment.isMac() ? InputEvent.META_MASK : InputEvent.CTRL_DOWN_MASK;
			resumeDownloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, modifiers));
		}
		return resumeDownloadMenuItem;
	}

	private JMenuItem getClearDownloadsMenuItem() {
		if (clearDownloadsMenuItem == null) {
			clearDownloadsMenuItem = MenuItems.CLEAR_DOWNLOAD.getItem();
			int modifiers = Environment.isMac() ? InputEvent.META_MASK : InputEvent.CTRL_DOWN_MASK
					| InputEvent.SHIFT_DOWN_MASK;
			clearDownloadsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, modifiers));
		}
		return clearDownloadsMenuItem;
	}

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setName(PARENT_MENU_NAME);
			fileMenu.setMnemonic('f');
			fileMenu.add(getNewFolderMenuItem());
			fileMenu.add(getNewPlaylistMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getImportTrackItem());
			fileMenu.add(getImportFolderItem());
			fileMenu.add(getImportITunesLibraryItem());
			fileMenu.addSeparator();
			fileMenu.add(getLocalTracks());
			fileMenu.addSeparator();
			fileMenu.add(getSignIn1MenuItem());
			fileMenu.add(getCloseApp1MenuItem());
		}
		return fileMenu;
	}

	private JMenuItem getSignIn1MenuItem() {
		if (signIn1MenuItem == null) {
			signIn1MenuItem = MenuItems.SIGN_IN_1.getItem();
			int modifiers = 0;
			if (Environment.isMac()) {
				modifiers = InputEvent.META_MASK | InputEvent.SHIFT_MASK;
				signIn1MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, modifiers));
			} else {
				modifiers = InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK;
				signIn1MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, modifiers));
			}
		}
		return signIn1MenuItem;
	}

	private JMenuItem getSignIn2MenuItem() {
		if (signIn2MenuItem == null) {
			signIn2MenuItem = MenuItems.SIGN_IN_2.getItem();
			int modifiers = 0;
			if (Environment.isMac()) {
				modifiers = InputEvent.META_MASK | InputEvent.SHIFT_MASK;
				signIn2MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, modifiers));
			} else {
				modifiers = InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK;
				signIn2MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, modifiers));
			}
		}
		return signIn2MenuItem;
	}

	private JMenuItem getCloseApp1MenuItem() {
		if (closeApp1MenuItem == null) {
			closeApp1MenuItem = MenuItems.CLOSE_APP.getItem();
			if (Environment.isMac()) {
				int modifiers = InputEvent.META_MASK;
				closeApp1MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, modifiers));

			} else {
				closeApp1MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
			}
			closeApp1MenuItem.setPreferredSize(new java.awt.Dimension(220, (int) closeApp1MenuItem.getPreferredSize()
					.getHeight()));
		}
		return closeApp1MenuItem;
	}

	private JMenuItem getCloseApp2MenuItem() {
		if (closeApp2MenuItem == null) {
			closeApp2MenuItem = MenuItems.CLOSE_APP.getItem();
			if (Environment.isMac()) {
				int modifiers = InputEvent.META_MASK;
				closeApp2MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, modifiers));

			} else {
				closeApp2MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
			}
			closeApp2MenuItem.setPreferredSize(new java.awt.Dimension(220, (int) closeApp2MenuItem.getPreferredSize()
					.getHeight()));
		}
		return closeApp2MenuItem;
	}

	private JMenuItem getNewPlaylistMenuItem() {
		if (createPlaylistMenuItem == null) {
			createPlaylistMenuItem = MenuItems.CREATE_PLAYLIST.getItem();
			if (Environment.isMac()) {
				createPlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_MASK));
			} else {
				createPlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
			}
			createPlaylistMenuItem.setPreferredSize(new java.awt.Dimension(220, (int) createPlaylistMenuItem
					.getPreferredSize().getHeight()));
		}
		return createPlaylistMenuItem;
	}

	private JMenuItem getNewFolderMenuItem() {
		if (createFolderMenuItem == null) {
			createFolderMenuItem = MenuItems.CREATE_FOLDER.getItem();
			if (Environment.isMac()) {
				createFolderMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_MASK
						| InputEvent.SHIFT_MASK));
			} else {
				createFolderMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK
						| InputEvent.SHIFT_DOWN_MASK));
			}
		}
		return createFolderMenuItem;
	}

	private JMenuItem getImportTrackItem() {
		if (importTrackItem == null) {
			importTrackItem = MenuItems.IMPORT_A_TRACK.getItem();
		}
		return importTrackItem;
	}

	private JMenuItem getImportITunesLibraryItem() {
		if (importITunesLibItem == null) {
			importITunesLibItem = MenuItems.IMPORT_ITUNES_LIBRARY.getItem();
		}
		return importITunesLibItem;
	}

	private JMenuItem getImportFolderItem() {
		if (importFolderItem == null) {
			importFolderItem = MenuItems.IMPORT_FOLDER.getItem();
		}
		return importFolderItem;
	}

	private JMenuItem getRenameMenuItem() {
		if (renameMenuItem == null) {
			renameMenuItem = MenuItems.RENAME.getItem();
			if (Environment.isMac()) {
				renameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
			} else {
				renameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
			}
			renameMenuItem.setEnabled(false);
		}
		return renameMenuItem;
	}

	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setName(PARENT_MENU_NAME);
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
			editMenu.add(getSelectAllMenuItem());
			editMenu.addSeparator();
			editMenu.add(getRenameMenuItem());
			editMenu.addSeparator();
			editMenu.add(getDeleteMenuItem());
		}
		return editMenu;
	}

	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = MenuItems.DELETE.getItem();
		}
		return deleteMenuItem;
	}

	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = MenuItems.COPY.getItem();
			copyMenuItem.setPreferredSize(new java.awt.Dimension(220, (int) copyMenuItem.getPreferredSize().getHeight()));
			if (Environment.isMac()) {
				copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_MASK));
			} else {
				copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
			}
			copyMenuItem.setEnabled(false);
		}
		return copyMenuItem;
	}

	private JMenuItem getSelectAllMenuItem() {
		if (selectAllMenuItem == null) {
			selectAllMenuItem = MenuItems.SELECT_ALL.getItem();
			if (Environment.isMac()) {
				selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.META_MASK));
			} else {
				selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
			}
			selectAllMenuItem.setEnabled(false);
		}
		return selectAllMenuItem;
	}

	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = MenuItems.PASTE.getItem();
			if (Environment.isMac()) {
				pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_MASK));
			} else {
				pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
			}
			pasteMenuItem.setEnabled(false);
		}
		return pasteMenuItem;
	}

	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setName(PARENT_MENU_NAME);
			helpMenu.add(getShowWizardMenuItem());
		}
		return helpMenu;
	}

	private JMenuItem getShowWizardMenuItem() {
		if (showWizardMenuItem == null) {
			showWizardMenuItem = MenuItems.SHOW_WIZARD.getItem();
		}
		return showWizardMenuItem;
	}

	private JMenuItem getPrevious() {
		if (previous == null) {
			previous = MenuItems.PREVIOUS.getItem();

			if (Environment.isMac()) {
				previous.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK));
			} else {
				previous.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK));
			}
		}
		return previous;
	}

	private JMenuItem getPlayPauseMenuItem() {
		if (playPause == null) {
			playPause = MenuItems.PLAY_PAUSE.getItem();
			if (Environment.isMac()) {
				playPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
			} else {
				playPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
			}
			playPause.setPreferredSize(new java.awt.Dimension(220, (int) playPause.getPreferredSize().getHeight()));
		}
		return playPause;
	}

	private JMenuItem getNext() {
		if (next == null) {
			next = MenuItems.NEXT.getItem();

			if (Environment.isMac()) {
				next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK));
			} else {
				next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK));
			}
		}
		return next;
	}

	private JMenuItem getFastForward() {
		if (fastForward == null) {
			fastForward = MenuItems.FAST_FORWARD.getItem();
			if (Environment.isMac()) {
				fastForward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK
						| InputEvent.SHIFT_MASK));
			} else {
				fastForward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK
						| InputEvent.SHIFT_MASK));
			}
		}
		return fastForward;
	}

	private JMenuItem getRewind() {
		if (rewind == null) {
			rewind = MenuItems.REWIND.getItem();

			if (Environment.isMac()) {
				rewind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
			} else {
				rewind.setAccelerator(KeyStroke
						.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_MASK));
			}
		}
		return rewind;
	}

	private JMenuItem getVolumeUp() {
		if (volumeUp == null) {
			volumeUp = MenuItems.VOLUME_UP.getItem();
			if (Environment.isMac()) {
				volumeUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_MASK));
			} else {
				volumeUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));
			}
		}
		return volumeUp;
	}

	private JMenuItem getVolumeDown() {
		if (volumeDown == null) {
			volumeDown = MenuItems.VOLUME_DOWN.getItem();
			if (Environment.isMac()) {
				volumeDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK));
			} else {
				volumeDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));
			}
		}
		return volumeDown;
	}

	private JMenuItem getMute() {
		if (mute == null) {
			mute = MenuItems.MUTE.getItem();
			if (Environment.isMac()) {
				mute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_MASK));
			} else {
				mute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK));
			}
		}
		return mute;
	}

	@PostConstruct
	public void wire() {
		getDeleteMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new DeleteFlow(viewEngine, dialogFactory).deleteSelected();
			}
		});

		getCopyMenuItem().addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.CLIPBOARD_COPY);
			}
		});

		getPasteMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.CLIPBOARD_PASTE);
			}
		});
		getImportFolderItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
				FileDialogResult dialogResult = dialogFactory.showImportFolderDialog(target);
				if (dialogResult.getDialogResult() == JFileChooser.APPROVE_OPTION) {
					File file = dialogResult.getFile();
					if (file != null) {
						viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction(target, ImportType.FILE_CHOOSER, file));
					}
				}
			}
		});

		getImportITunesLibraryItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getImportITunesLibraryItem().setEnabled(false);
				new ImportItunesFlow(viewEngine, dialogFactory).importItunesFromFile();
			}
		});

		getLocalTracks().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.sendValueAction(Actions.Library.FIND_TRACKS_LOCALLY, dialogFactory.showDirChooserDialog());
			}
		});

		ActionListener createPlaylistActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_CREATE_PLAYLIST);
			}
		};
		getNewPlaylistMenuItem().addActionListener(createPlaylistActionListener);
		getCreatePlaylistMenuItem().addActionListener(createPlaylistActionListener);
		ActionListener createFolderActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_CREATE_FOLDER);
			}
		};
		getNewFolderMenuItem().addActionListener(createFolderActionListener);
		getCreateFolderMenuItem().addActionListener(createFolderActionListener);
		getImportTrackItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files = dialogFactory.showFileChooserDialog();
				if (files != null && files.length > 0) {
					TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
					viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction(target, ImportType.FILE_CHOOSER, files));
				}
			}
		});
		getShowWizardMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Wizard.REOPEN_WIZARD);
				WizardDialog wizardDialog = dialogFactory.getWizardDialog();
				wizardDialog.setVisible(true);
			}
		});
		getRenameMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_REQUEST_RENAME);
			}
		});
		getBrokenLinksItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.SHOW_ITUNES_UNIMPORTED_FILES);
			}
		});

		getPlayPauseMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hipecotechTopPanel.playOnShorCut();
			}
		});
		getPrevious().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hipecotechTopPanel.prevTrack();
			}
		});

		getNext().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hipecotechTopPanel.nextTrack();
			}
		});

		getFastForward().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		getRewind().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		getVolumeUp().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				volumePanel.volumeUp();
			}
		});

		getVolumeDown().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				volumePanel.volumeDown();
			}
		});

		getPauseDownloadMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelFactory.getDownloadPanel().pauseDownloads();
			}
		});

		getResumeDownloadMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelFactory.getDownloadPanel().resumeDownloads();
			}
		});
		getClearDownloadsMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelFactory.getDownloadPanel().cleanUpDownloads();
			}
		});

		getMute().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Player.TOGGLE_MUTE);
			}
		});

		getEditProfileMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (contactListPanel.isShowing()) {
					new EditProfileFlow(viewEngine, dialogFactory).execute(false);
				}
			}
		});
		getOpenPendinEmailsMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (contactListPanel.isShowing()) {
					contactListPanel.sendInvitation();
				}
			}
		});
		getOpenSearchContactsMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (contactListPanel.isShowing()) {
					new AddContactFlow(viewEngine, dialogFactory).executeAdd();
				}
			}
		});
		getI18nEnUS().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.sendValueAction(ApplicationActions.CHANGE_LANGUAGE, ApplicationLanguage.ENGLISH);
			}
		});
		getI18nEsMX().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.sendValueAction(ApplicationActions.CHANGE_LANGUAGE, ApplicationLanguage.SPANISH);
			}
		});
		getFindFriendsItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.request(Actions.Social.REQUEST_CONTACTS, ChatType.ALL, new ResponseCallback<List<ContactInfo>>() {
					public void onResponse(java.util.List<ContactInfo> t) {
						dialogFactory.showImportContactsDialog(t);
					};
				});
			}
		});

		getShortcutsMenu().addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				setDownloadButtonState();
			}

			@Override
			public void menuDeselected(MenuEvent e) {
			}

			@Override
			public void menuCanceled(MenuEvent e) {
			}

			private void setDownloadButtonState() {
				if (viewEngine.get(Model.DOWNLOADS_SORTED_BY_PRIORITY).isEmpty()) {
					getClearDownloadsMenuItem().setEnabled(false);
					getPauseDownloadMenuItem().setEnabled(false);
					getResumeDownloadMenuItem().setEnabled(false);
				} else {
					getClearDownloadsMenuItem().setEnabled(isAnyDownloadCompleted());
					getPauseDownloadMenuItem().setEnabled(isAnyDownloadInProgress());
					getResumeDownloadMenuItem().setEnabled(anyDownloadNeedsRestart());
				}
			}

			private boolean isAnyDownloadInProgress() {
				for (Download download : viewEngine.get(Model.DOWNLOADS_SORTED_BY_PRIORITY)) {
					if (download.getStatus().equals(DownloadState.Downloading)) {
						return true;
					}
				}
				return false;
			}

			private boolean isAnyDownloadCompleted() {
				for (Download download : viewEngine.get(Model.DOWNLOADS_SORTED_BY_PRIORITY)) {
					if (download.getStatus().equals(DownloadState.Complete)) {
						return true;
					}
				}
				return false;

			}

			private boolean anyDownloadNeedsRestart() {
				for (Download download : viewEngine.get(Model.DOWNLOADS_SORTED_BY_PRIORITY)) {
					if (download.getStatus().equals(DownloadState.Paused)
							|| download.getStatus().equals(DownloadState.MoreSourcesNeeded)
							|| download.getStatus().equals(DownloadState.Error)) {
						return true;
					}
				}
				return false;
			}
		});
		getSendContentMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Social.SHOW_SEND_CONTENT_DIALOG, new ShareContentAction());
			}
		});

	}

	@EventMethod(Events.View.SELECTED_TRACKCONTAINER_CHANGED_ID)
	public void onSelectedItemChanged(SelectTrackContainerEvent event) {
		TrackContainer lastContainer = event.getContainer();
		boolean isAbleToPaste = ModelValidation.isAbleToPaste(ClipboardConverter.getModelCollection(), lastContainer);
		getPasteMenuItem().setEnabled(isAbleToPaste);
		// log.debug("isAbleToPaste:" + isAbleToPaste);
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onAppStarted() {
		enableControls(true);
		getSignIn1MenuItem().setText(messages.getMessage("menu.file.signOut"));// preserve
		// this line because this label is dynamic
		getSignIn2MenuItem().setText(messages.getMessage("menu.file.signOut"));// preserve
		// this line because this label is dynamic
		getFindFriendsItem().setEnabled(true);
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onUserLogout() {
		enableControls(false);
		getSignIn1MenuItem().setText(messages.getMessage(MENU_FILE_SIGN_IN));// preserve
		// this line because this label is dynamic
		getSignIn2MenuItem().setText(messages.getMessage(MENU_FILE_SIGN_IN));// preserve
		// this line because this label is dynamic
		getFindFriendsItem().setEnabled(false);
	}

	@EventMethod(ApplicationModel.HAS_INTERNET_CONNECTION_ID)
	public void onInternetConnectionStatusChanged(ValueEvent<Boolean> event) {
		getFindFriendsItem().setEnabled(event.getValue());
	}

	@EventMethod(Events.Library.IMPORTING_ITUNES_LIBRARY_ID)
	public void onImportItunesLibrary() {
		getImportITunesLibraryItem().setEnabled(false);
	}

	@EventMethod(Events.Library.IMPORTING_ITUNES_LIBRARY_DONE_ID)
	public void onImportItunesLibraryDone() {
		getImportITunesLibraryItem().setEnabled(true);
	}

	@EventMethod(Model.ITUNES_UNIMPORTED_FILE_EXISTS_ID)
	public void onUnimportedItunesFileExistChanged(ValueEvent<Boolean> event) {
		getBrokenLinksItem().setEnabled(event.getValue());
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onStartup() {
		Boolean itunesFileExists = viewEngine.get(Model.ITUNES_UNIMPORTED_FILE_EXISTS);
		brokenLinksItem.setEnabled(itunesFileExists == null ? false : itunesFileExists);
	}

	@EventMethod(Events.View.CLIPBOARD_SELECTION_CHANGED_ID)
	public void onClipboardChanged(List<?> selectedItems) {
		ModelCollection selection = new ModelCollection(selectedItems);
		getCopyMenuItem().setEnabled(selection.only(ModelTypes.tracks) && selection.has(ModelTypes.tracks));
		// TODO THE FOLLOWING IS MISSNG THE "BE ON FOCUS" CONDITION OF
		// STORY
		// 3543
		getSelectAllMenuItem().setEnabled(selection.only(ModelTypes.tracks) && selection.has(ModelTypes.tracks));
		getRenameMenuItem().setEnabled(
				selection.only(ModelTypes.playlists, ModelTypes.folders)
						&& selection.hasAny(ModelTypes.playlists, ModelTypes.folders));
		getDeleteMenuItem().setEnabled(
				selection.only(ModelTypes.playlists, ModelTypes.folders, ModelTypes.tracks)
						&& selection.hasAny(ModelTypes.playlists, ModelTypes.folders, ModelTypes.tracks));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

}
