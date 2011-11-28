package com.all.client.view.components;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;

import com.all.commons.SoundPlayer.Sound;
import com.all.shared.model.Playlist;

public class PreviewCellEditor extends DefaultCellEditor {
	private static final int ICON_SIZE = 20;
	private static final long serialVersionUID = 1L;
	private boolean canceling = true;
	private StubbornComponentResizeListener stubbornListener = new StubbornComponentResizeListener(100,
			PreviewTree.NODE_HEIGHT, "TEXTFIELD");
	private final PreviewTree previewTree;
	
	public PreviewCellEditor(final JTextField textField, PreviewTree previewTree) {
		super(textField);
		this.previewTree = previewTree;
		textField.addComponentListener(stubbornListener);
		
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				JTextField textField2 = textField;
				textField2.setCaretPosition(textField2.getText().length());
				textField2.selectAll();
			}
		});
		
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					canceling = true;
				}
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				if (textField.getText().length() >= Playlist.NAME_MAXLENGTH
						&& (null == textField.getSelectedText() || textField.getSelectedText().length() == 0)) {
					Sound.LIBRARY_NAME_TOO_LONG.play();
					e.consume();
				}
			}
		});
		canceling = false;
	}
	
	@Override
	public boolean stopCellEditing() {
		canceling = true;
		return updateComplete();
	}
	
	@Override
	public void cancelCellEditing() {
		if (!canceling) {
			// no se esta cancelando, si se quiere renombrar
			if (!updateComplete()) {
				previewTree.startEditingAtPath(previewTree.getEditingPath());
			}
		} else {
			canceling = false;
		}
	}
	
	private boolean updateComplete() {
		if (previewTree.getEditListener() == null) {
			return false;
		}
		Object userObject = previewTree.getEditingNode().getUserObject();
		String newValue = (String) previewTree.getCellEditor().getCellEditorValue();
		return previewTree.getEditListener().edited(userObject, newValue, previewTree.getEditingNode());
	}
	
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		Component component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		stubbornListener.setWidth(tree.getRowBounds(row).width - ICON_SIZE);
		return component;
	}
}