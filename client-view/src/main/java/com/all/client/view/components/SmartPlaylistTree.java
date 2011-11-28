package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.SmartPlaylist;

public class SmartPlaylistTree extends LibraryTree implements Internationalizable {
	private static final long serialVersionUID = 1L;
	
	private DefaultMutableTreeNode rootNode;
	private TreePath allMusicPath;

	public SmartPlaylistTree() {
		rootNode = new DefaultMutableTreeNode("!!!FILTERS!!!");
		allMusicPath = new TreePath(rootNode.getPath());

		Icon smartplaylistIcon = UIManager.getDefaults().getIcon("icons.smartPlaylistBlue");
		SmartPlaylistTreeCellRenderer cellRenderer = new SmartPlaylistTreeCellRenderer(smartplaylistIcon);

		this.setName("smartPlaylistTree");
		this.setEditable(false);
		this.setRootVisible(true);
		this.setRowHeight(NODE_HEIGHT);
		this.setModel(new DefaultTreeModel(rootNode));
		this.setCellRenderer(cellRenderer);
		this.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				JTree tree = (JTree) e.getSource();
				tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				Object selection = null;
				if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getLastPathComponent() != null) {
					selection = ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject();
				}
				if (selection instanceof String) {
					if (e.getOldLeadSelectionPath() != null) {
						tree.setSelectionPath(e.getOldLeadSelectionPath());
					} else {
						tree.clearSelection();
					}
				}
			}
		});
	}

	public void setSmartPlaylists(Iterable<SmartPlaylist> smartPlaylists, SmartPlaylist allMusic) {
		rootNode.removeAllChildren();
		DefaultMutableTreeNode firstNode = new DefaultMutableTreeNode(allMusic);
		rootNode.add(firstNode);
		allMusicPath = new TreePath(firstNode.getPath());
		for (SmartPlaylist smartPlaylist : smartPlaylists) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(smartPlaylist);
			rootNode.add(node);
		}
		((DefaultTreeModel) getModel()).nodeStructureChanged(rootNode);
	}

	public void selectAllMusic() {
		this.setSelectionPath(allMusicPath);
	}

	@Override
	public void internationalize(Messages messages) {
		rootNode.setUserObject(messages.getMessage("previewPanel.filters.label"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}

class SmartPlaylistTreeCellRenderer extends JPanel implements TreeCellRenderer {

	private static final long serialVersionUID = -4430385733622846351L;
	static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private JLabel label;
    private Icon leafIcon;
    
	public SmartPlaylistTreeCellRenderer(Icon smartplaylistIcon) {
		label = new JLabel();
		label.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		setLayout(new BorderLayout());
		add(label, BorderLayout.WEST);
		
		setLeafIcon(smartplaylistIcon);
	}
	
	public void setLeafIcon(Icon leafIcon) {
		this.leafIcon = leafIcon;
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
		if (entity instanceof String) {
			label.setName(SynthFonts.BOLD_FONT11_GRAY77_77_77);
			label.setText("   " + value.toString());
			label.setIcon(null);
			setPreferredSize(new Dimension(PreviewTree.NON_LEAF_WIDTH, PreviewTree.NODE_HEIGHT));
		} else {
			SmartPlaylist smart = (SmartPlaylist) entity;
			label.setText(smart.getLabel());
			label.setIcon(leafIcon);
			setPreferredSize(new Dimension(PreviewTree.LEAF_WIDTH, PreviewTree.NODE_HEIGHT));
		}
		return this;
	}

}
