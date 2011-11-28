package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.all.core.common.view.SynthFonts;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;

public final class PreviewTreeCellRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1;
	private static final Dimension LEAF_DIMENSION = new Dimension(PreviewTree.LEAF_WIDTH, PreviewTree.NODE_HEIGHT);
	private static final Dimension NON_LEAF_DIMENSION = new Dimension(PreviewTree.NON_LEAF_WIDTH, PreviewTree.NODE_HEIGHT);
	private Icon folderIcon = UIManager.getDefaults().getIcon("Tree.openIcon");
	private Icon playlistIcon = UIManager.getDefaults().getIcon("Tree.leafIcon");
	private Icon newFolderIcon = UIManager.getDefaults().getIcon("Tree.newOpenIcon");
	private Icon newPlaylistIcon = UIManager.getDefaults().getIcon("Tree.newLeafIcon");
	
	private Icon leafIcon;
	private Icon closedIcon;
	private Icon openIcon;
	
	JLabel label;

	public PreviewTreeCellRenderer() {
		this.setPreferredSize(NON_LEAF_DIMENSION);
		label = new JLabel();
		label.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		this.setLayout(new BorderLayout());
		this.add(label, BorderLayout.WEST);
		
		setLeafIcon(playlistIcon);
		setClosedIcon(folderIcon);
		setOpenIcon(folderIcon);
	}
	
	public void setLeafIcon(Icon leafIcon) {
		this.leafIcon = leafIcon;
	}
	
	public Icon getLeafIcon() {
		return leafIcon;
	}
	
	public void setClosedIcon(Icon closedIcon) {
		this.closedIcon = closedIcon;
	}
	
	public Icon getClosedIcon() {
		return closedIcon;
	}
	
	public void setOpenIcon(Icon openIcon) {
		this.openIcon = openIcon;
	}
	
	public Icon getOpenIcon() {
		return openIcon;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		if (isSelected) {
			label.setName(SynthFonts.BOLD_FONT11_GRAY77_77_77);
		} else {
			label.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		}
		Object entity = ((DefaultMutableTreeNode) value).getUserObject();
		label.setText(entity.toString());
		if (entity instanceof Playlist) {
			setIconsAndSize((Playlist) entity);
		} else if (entity instanceof Folder) {
			setIconsAndSize((Folder) entity, leaf);
		}
		return this;
	}

	private void setIconsAndSize(Playlist playlist) {
		Icon icon = playlist.isNewContent() ? newPlaylistIcon : playlistIcon;
		setLeafIcon(icon);
		label.setIcon(icon);
		setPreferredSize(playlist.getParentFolder() == null ? NON_LEAF_DIMENSION : LEAF_DIMENSION);
	}

	private void setIconsAndSize(Folder folder, boolean leaf) {
		Icon icon = folder.isNewContent() ? newFolderIcon : folderIcon;
		label.setIcon(icon);
		setOpenIcon(icon);
		setClosedIcon(icon);
		if (leaf) {
			setLeafIcon(icon);
		}
		setPreferredSize(NON_LEAF_DIMENSION);
	}
	
	public int getIconTextGap(){
		return label.getIconTextGap();
	}
}