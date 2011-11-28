package com.all.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.PreviewTree;
import com.all.client.view.components.SmartPlaylistTree;
import com.all.client.view.music.DescriptionTable;
import com.all.commons.Environment;
import com.all.core.actions.Actions;

/**
 * Binds Shortcut with Action at some specific level in the Hierarchical JComponent tree
 * 
 * @author User
 * 
 */
@Component
public class ShortcutBinder {
	@Autowired
	private ViewEngine viewEngine;

	// only needed to bind inputMap with actionMap
	private enum Key {
		copyKey, cutKey, pasteKey, doNothing, none
	}

	// private static final Log log = LogFactory.getLog(ShortcutBinder.class);

	public void whenCopyInPreviewTree(PreviewTree previewTree) {

		KeyStroke ctrlC;
		if (Environment.isMac()) {
			ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK);
		} else {
			ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlC, previewTree);
		doNothingWhenKeyStrokeInJComponent(ctrlC, previewTree);
	}

	public void whenPasteInPreviewTree(PreviewTree previewTree) {
		KeyStroke ctrlV;
		if (Environment.isMac()) {
			ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK);
		} else {
			ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlV, previewTree);
		pasteWhenKeyStrokeInJComponent(ctrlV, previewTree);
	}

	public void whenCopyInSmartPlaylistTree(SmartPlaylistTree smartPlaylistTree) {
		KeyStroke ctrlC;
		if (Environment.isMac()) {
			ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK);
		} else {
			ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlC, smartPlaylistTree);
		doNothingWhenKeyStrokeInJComponent(ctrlC, smartPlaylistTree);
	}

	public void whenCutInSmartPlaylistTree(SmartPlaylistTree smartPlaylistTree) {
		KeyStroke ctrlX;
		if (Environment.isMac()) {
			ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.META_DOWN_MASK);
		} else {
			ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlX, smartPlaylistTree);
		doNothingWhenKeyStrokeInJComponent(ctrlX, smartPlaylistTree);
	}

	public void whenPasteInSmartPlaylistTree(SmartPlaylistTree smartPlaylistTree) {
		KeyStroke ctrlV;
		if (Environment.isMac()) {
			ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK);
		} else {
			ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlV, smartPlaylistTree);
		doNothingWhenKeyStrokeInJComponent(ctrlV, smartPlaylistTree);

	}

	public void whenCopyInDescriptionTable(DescriptionTable descriptionTable) {
		KeyStroke ctrlC;
		if (Environment.isMac()) {
			ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK);
		} else {
			ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlC, descriptionTable);
		copyWhenKeyStrokeInJComponent(ctrlC, descriptionTable);
	}

	public void whenCutInDescriptionTable(DescriptionTable descriptionTable) {
		KeyStroke ctrlX;
		if (Environment.isMac()) {
			ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.META_DOWN_MASK);
		} else {
			ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlX, descriptionTable);
		doNothingWhenKeyStrokeInJComponent(ctrlX, descriptionTable);
	}

	public void whenPasteInDescriptionTable(DescriptionTable descriptionTable) {
		KeyStroke ctrlV;
		if (Environment.isMac()) {
			ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK);
		} else {
			ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
		}
		propagateWhenKeyStrokeInJComponent(ctrlV, descriptionTable);
		pasteWhenKeyStrokeInJComponent(ctrlV, descriptionTable);
	}

	private void propagateWhenKeyStrokeInJComponent(KeyStroke keyStroke, JComponent component) {
		InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);
		inputMap.put(keyStroke, Key.none.toString());
	}

	private void doNothingWhenKeyStrokeInJComponent(KeyStroke keyStroke, JComponent component) {
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		};
		InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(keyStroke, Key.doNothing.toString());
		ActionMap actionMap = component.getActionMap();
		actionMap.put(Key.doNothing.toString(), action);
	}

	private void copyWhenKeyStrokeInJComponent(KeyStroke keyStroke, JComponent component) {
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.CLIPBOARD_COPY);
			}
		};
		InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(keyStroke, Key.copyKey.toString());
		ActionMap actionMap = component.getActionMap();
		actionMap.put(Key.copyKey.toString(), action);
	}

	private void pasteWhenKeyStrokeInJComponent(KeyStroke keyStroke, JComponent component) {
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.CLIPBOARD_PASTE);
			}
		};
		InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(keyStroke, Key.pasteKey.toString());
		ActionMap actionMap = component.getActionMap();
		actionMap.put(Key.pasteKey.toString(), action);
	}

}
