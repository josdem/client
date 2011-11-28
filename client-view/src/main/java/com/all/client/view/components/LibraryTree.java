package com.all.client.view.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JTree;

import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.core.common.view.SynthColors;
import com.all.core.common.view.SynthIcons;

public abstract class LibraryTree extends JTree {
	private static final long serialVersionUID = 1L;
	public static final int NODE_HEIGHT = 20;
	public static final int SEPARATOR_HEIGHT = 19;
	public static final int NON_LEAF_WIDTH = 165;
	public static final int LEAF_WIDTH = 150;

	private JTreeCoordinateHelper treeHelper;
	private Color treeBackgroundColor;
	private Color selectionBGColor;
	private Integer dragOverObject;

	public LibraryTree() {
		super(new Object[] {});
		treeHelper = new JTreeCoordinateHelper(this);
		treeBackgroundColor = SynthColors.WHITEBLUE15462645;
		// Fonts need to be derived (created new) due a bug in Synth (another
		// one bites the dust)
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				selectionBGColor = SynthColors.SKYBLUE11521505;
				repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				selectionBGColor = SynthColors.SKYBLUE12508400;
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(treeBackgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		int[] selectionRows = getSelectionRows();

		Integer highlightedRow = null;
		if (dragOverObject != null) {
			highlightedRow = dragOverObject / NODE_HEIGHT;
		}

		if (selectionRows != null) {
			for (int i : selectionRows) {
				if (highlightedRow == null || i != highlightedRow.intValue()) {
					int y = i * NODE_HEIGHT;
					g.setColor(selectionBGColor);
					g.fillRect(0, y, getWidth(), NODE_HEIGHT);
					g.setColor(treeBackgroundColor);
					g.fillRect(0, y + SEPARATOR_HEIGHT, getWidth(), 1);
				}
			}
		}

		if (highlightedRow != null) {
			int y = highlightedRow * NODE_HEIGHT;
			ImageIcon imageIcon = (ImageIcon) SynthIcons.HIGHLIGHT_ICON;
			Image image = imageIcon != null ? imageIcon.getImage() : null;
			g.drawImage(image, 0, y, getWidth(), NODE_HEIGHT, this);
		}

		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	public JTreeCoordinateHelper getTreeHelper() {
		return treeHelper;
	}

	public void setDragOverObject(Integer b) {
		dragOverObject = b;
	}

	public Integer getDragOverObject() {
		return dragOverObject;
	}

}
