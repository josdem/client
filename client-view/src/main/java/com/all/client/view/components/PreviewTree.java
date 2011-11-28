package com.all.client.view.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.all.client.view.listeners.SingleExpandTree;
import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;

public class PreviewTree extends LibraryTree {
	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode firstNodeSelected;
	private boolean isSelectedOnDragOver;
	private Object objectSelectedOnDrag;

	private DefaultMutableTreeNode editingNode;
	private PreviewTreeEditListener editListener;

	public PreviewTree() {
		this.toggleClickCount = Integer.MAX_VALUE;
		this.setName("previewTree");
		this.setDragEnabled(true);
		this.setRootVisible(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		TreeCellRenderer treeCellRenderer = new PreviewTreeCellRenderer();
		this.setCellRenderer(treeCellRenderer);
		SingleExpandTree.apply(this);
	}

	public void addToModel(Object valueAdded) {
		JTreeCoordinateHelper helper = new JTreeCoordinateHelper(this);
		if (getSelectionPath() == null) {
			helper.selectRoot();
		}

		if (valueAdded instanceof Playlist) {
			Playlist playlistAdded = (Playlist) valueAdded;
			if (playlistAdded.getParentFolder() != null) {
				expandPath(getSelectionPath());
			}
		}

		helper.selectByValue(valueAdded);
		startEditingAtPath(getSelectionPath());
	}

	@SuppressWarnings({ "unchecked" })
	public void reorderFolderNode(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode folderNode = (DefaultMutableTreeNode) node.getParent();
		List<DefaultMutableTreeNode> children = Collections.list(folderNode.children());
		Collections.sort(children, new Comparator<DefaultMutableTreeNode>() {
			@Override
			public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
				if (o1.getUserObject() instanceof Folder && o2.getUserObject() instanceof Playlist) {
					return -1;
				}
				if (o2.getUserObject() instanceof Folder && o1.getUserObject() instanceof Playlist) {
					return 1;
				}
				return ((Comparable) o1.getUserObject()).compareTo((Comparable) o2.getUserObject());
			}
		});
		folderNode.removeAllChildren();
		Iterator<DefaultMutableTreeNode> childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			folderNode.add(childrenIterator.next());
		}
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.nodeStructureChanged(folderNode);
	}

	@Override
	public void startEditingAtPath(TreePath path) {
		if (path == null || path.getLastPathComponent() == null) {
			return;
		}
		setEditable(true);
		setEditingNode((DefaultMutableTreeNode) path.getLastPathComponent());
		super.startEditingAtPath(path);
	}

	public void setEditListener(PreviewTreeEditListener editListener) {
		this.editListener = editListener;
	}

	public boolean isSelectedOnDragOver() {
		return isSelectedOnDragOver;
	}

	public void setSelectedOnDragOver(boolean isSelectedOnDragOver) {
		this.isSelectedOnDragOver = isSelectedOnDragOver;
	}

	public Object getObjectSelectedOnDrag() {
		return objectSelectedOnDrag;
	}

	public void setObjectSelectedOnDrag(Object objectSelectedOnDrag) {
		this.objectSelectedOnDrag = objectSelectedOnDrag;
	}

	public DefaultMutableTreeNode getFirstNodeSelected() {
		return firstNodeSelected;
	}

	public PreviewTreeEditListener getEditListener() {
		return editListener;
	}

	public void setEditingNode(DefaultMutableTreeNode editingNode) {
		this.editingNode = editingNode;
	}

	public DefaultMutableTreeNode getEditingNode() {
		return editingNode;
	}
}

