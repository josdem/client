package com.all.client.view.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.HipecotechTopPanel;
import com.all.client.view.MainFrame;
import com.all.client.view.VolumePanel;
import com.all.commons.Environment;
import com.all.core.actions.Actions;

@Controller
public class MusicPlayerShortcutController {

	private static final String PLAY_PAUSE_KEY = "playPause";

	private static final String PREVIOUS_KEY = "previous";

	private static final String FORWARD_KEY = "forward";

	private static final String VOLUME_UP_KEY = "volumeUp";

	private static final String VOLUME_DOWN_KEY = "volumeDown";

	private static final String VOLUME_MUTE_KEY = "volumeMute";

	@Autowired
	private MainFrame mainFrame;

	@Autowired
	private HipecotechTopPanel hipecotechTopPanel;

	@Autowired
	private VolumePanel volumePanel;
	@Autowired
	private ViewEngine viewEngine;

	private KeyStroke playPauseKey;
	private KeyStroke forwardKey;
	private KeyStroke previousKey;
	private KeyStroke volumeUpKey;
	private KeyStroke volumeDownKey;
	private KeyStroke volumeMuteKey;

	private Action playPauseAction;
	private Action playerForwardAction;
	private Action playerPreviousAction;
	private Action playerVolumeUpAction;
	private Action playerVolumeDownAction;
	private Action playerVolumeMuteAction;

	@SuppressWarnings("serial")
	public MusicPlayerShortcutController() {
		int optionMask = (Environment.isMac() ? KeyEvent.ALT_DOWN_MASK : KeyEvent.ALT_DOWN_MASK);
		playPauseKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_ENTER, optionMask);
		previousKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_LEFT, optionMask);
		forwardKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_RIGHT, optionMask);
		volumeUpKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_UP, optionMask);
		volumeDownKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_DOWN, optionMask);
		volumeMuteKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_M, optionMask);

		playPauseAction = new AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				hipecotechTopPanel.playOnShorCut();
				mainFrame.requestFocus();
			}
		};

		playerForwardAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Player.FORWARD);
				mainFrame.requestFocus();
			}
		};

		playerPreviousAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Player.PREVIOUS);
				mainFrame.requestFocus();
			}
		};

		playerVolumeUpAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				volumePanel.volumeUp();
				mainFrame.requestFocus();
			}
		};

		playerVolumeDownAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				volumePanel.volumeDown();
				mainFrame.requestFocus();
			}
		};

		playerVolumeMuteAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Player.TOGGLE_MUTE);
				mainFrame.requestFocus();
			}
		};
	}

	@PostConstruct
	public void bindShortcuts() {
		bindShortcuts(mainFrame.getRootPane());
	}

	private void bindShortcuts(JComponent component) {

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(forwardKey, FORWARD_KEY);
		component.getActionMap().put(FORWARD_KEY, playerForwardAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(previousKey, PREVIOUS_KEY);
		component.getActionMap().put(PREVIOUS_KEY, playerPreviousAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(playPauseKey, PLAY_PAUSE_KEY);
		component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(playPauseKey, PLAY_PAUSE_KEY);
		component.getInputMap(JComponent.WHEN_FOCUSED).put(playPauseKey, "none");
		component.getActionMap().put(PLAY_PAUSE_KEY, playPauseAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(volumeUpKey, VOLUME_UP_KEY);
		component.getActionMap().put(VOLUME_UP_KEY, playerVolumeUpAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(volumeDownKey, VOLUME_DOWN_KEY);
		component.getActionMap().put(VOLUME_DOWN_KEY, playerVolumeDownAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(volumeMuteKey, VOLUME_MUTE_KEY);
		component.getActionMap().put(VOLUME_MUTE_KEY, playerVolumeMuteAction);
	}

}
