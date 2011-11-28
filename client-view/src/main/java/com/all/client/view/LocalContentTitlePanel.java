package com.all.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.MenuItems;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

@Component
public class LocalContentTitlePanel extends ContentTitlePanel implements Internationalizable {
	private static final long serialVersionUID = 1L;

	private static final Rectangle ADD_BUTTON_BOUNDS = new Rectangle(168, 1, 28, 22);
	private static final Color MY_MUSIC_LABEL_FG_COLOR = new Color(77, 77, 77);
	private static final Rectangle MY_MUSIC_LABEL_BOUNDS = new Rectangle(30, 0, 138, 22);
	private static final Dimension DEFAULT_SIZE = new Dimension(198, 24);
	private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

	private JLabel myMusicLabel;
	private JButton addButton;
	private JPopupMenu myMusicPopup;
	private JMenuItem createFolderMenuItem = null;
	private JMenuItem createPlaylistMenuItem = null;

	public LocalContentTitlePanel() {
	}

	@Override
	@PostConstruct
	public void initGui() {
		this.setLayout(null);
		this.setBorder(EMPTY_BORDER);
		this.setName("myMusicPanel");
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setSize(DEFAULT_SIZE);
		this.add(getMyMusicLabel());
		this.add(getAddButton());
	}

	@Autowired
	public void wire(final ViewEngine viewEngine) {
		getAddButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getArrowPopup().show(getAddButton(), 0, getAddButton().getHeight());
				Sound.LIBRARY_EXPAND.play();
			}
		});

		getCreatePlaylist().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_CREATE_PLAYLIST);
			}
		});

		getCreateFolder().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_CREATE_FOLDER);
			}
		});
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void internationalize(Messages messages) {
		getMyMusicLabel().setText(messages.getMessage("previewPanel.mymusic.label"));
		getAddButton().setToolTipText(messages.getMessage("tooltip.addLib"));
		MenuItems.CREATE_PLAYLIST.internationalize(getCreatePlaylist(), messages);
		MenuItems.CREATE_FOLDER.internationalize(getCreateFolder(), messages);
	}

	public JLabel getMyMusicLabel() {
		if (myMusicLabel == null) {
			myMusicLabel = new JLabel();
			myMusicLabel.setHorizontalAlignment(JLabel.CENTER);
			myMusicLabel.setForeground(MY_MUSIC_LABEL_FG_COLOR);
			myMusicLabel.setVerticalAlignment(JLabel.CENTER);
			myMusicLabel.setBounds(MY_MUSIC_LABEL_BOUNDS);
			myMusicLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
		}
		return myMusicLabel;
	}

	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setName("libraryAddButton");
			addButton.setBounds(ADD_BUTTON_BOUNDS);
		}
		return addButton;
	}

	private JPopupMenu getArrowPopup() {
		myMusicPopup = new JPopupMenu();
		myMusicPopup.setAlignmentX(196);
		myMusicPopup.add(getCreateFolder());
		myMusicPopup.add(getCreatePlaylist());
		return myMusicPopup;
	}

	private JMenuItem getCreateFolder() {
		if (createFolderMenuItem == null) {
			createFolderMenuItem = MenuItems.CREATE_FOLDER.getItem();
		}
		return createFolderMenuItem;
	}

	private JMenuItem getCreatePlaylist() {
		if (createPlaylistMenuItem == null) {
			createPlaylistMenuItem = MenuItems.CREATE_PLAYLIST.getItem();
		}
		return createPlaylistMenuItem;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

}
