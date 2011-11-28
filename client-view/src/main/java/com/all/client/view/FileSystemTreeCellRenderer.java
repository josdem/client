/**
 * 
 */
package com.all.client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.PreviewTree;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.Model;

public class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1;

	private static final Color NORMAL_FOREGROUND_COLOR = new Color(25, 25, 25);

	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private final Icon folderIcon;

	private final Icon trackIcon;

	private final Icon fileIcon;

	private final ViewEngine viewEngine;

	public FileSystemTreeCellRenderer(ViewEngine viewEngine) {
		super();
		this.viewEngine = viewEngine;
		this.setOpaque(false);
		this.setBackground(TRANSPARENT);
		folderIcon = UIManager.getDefaults().getIcon("icons.folderGray");
		trackIcon = UIManager.getDefaults().getIcon("icons.trackBlue");
		fileIcon = UIManager.getDefaults().getIcon("icons.fileGray");
		setBackgroundNonSelectionColor(TRANSPARENT);
		setBackgroundSelectionColor(TRANSPARENT);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
		setForeground(NORMAL_FOREGROUND_COLOR);

		if (isSelected) {
			this.setName(SynthFonts.BOLD_FONT11_BLACK);
		} else {
			this.setName(SynthFonts.PLAIN_FONT11_BLACK);
		}

		Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
		if (userObject == null) {
			this.setText("");
			return this;
		}
		if (userObject instanceof File) {
			File file = (File) userObject;
			if (file.isDirectory()) {
				this.setIcon(folderIcon);
				setPreferredSize(new Dimension(PreviewTree.NON_LEAF_WIDTH, PreviewTree.NODE_HEIGHT));
			} else {
				setPreferredSize(new Dimension(PreviewTree.LEAF_WIDTH, PreviewTree.NODE_HEIGHT));
				if (viewEngine.get(Model.TRACK_REPOSITORY).isFormatSupported(file)) {
					this.setIcon(trackIcon);
				} else {
					this.setIcon(fileIcon);
				}
			}
			this.setText(file.getName());
		} else {
			this.setIcon(null);
			this.setText(userObject.toString());
		}
		return this;
	}

}