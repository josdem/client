package com.all.client.view.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JTreeCoordinateHelper {
	private JTree tree;

	public JTreeCoordinateHelper(JTree tree) {
		this.tree = tree;
	}

	public Object getTreeNodeContentFromCoordinates(Point location) {
		return getTreeNodeContentFromCoordinates(location.x, location.y);
	}

	public Object getTreeNodeContentFromCoordinates(int x, int y) {
		if (tree.getPathForLocation(x, y) == null) {
			return null;
		}
		return ((DefaultMutableTreeNode) (tree.getPathForLocation(x, y).getLastPathComponent())).getUserObject();
	}

	public DefaultMutableTreeNode getDefaultMutableTreeNodeFromCoordinates(int x, int y) {
		if (tree.getPathForLocation(x, y) == null) {
			return (DefaultMutableTreeNode) tree.getModel().getRoot();
		}
		return (DefaultMutableTreeNode) tree.getPathForLocation(x, y).getLastPathComponent();
	}

	public DefaultMutableTreeNode getDefaultMutableTreeNodeFromCoordinates(Point point) {
		return getDefaultMutableTreeNodeFromCoordinates(point.x, point.y);
	}

	public Object getValueAt(int x, int y) {
		return getDefaultMutableTreeNodeFromCoordinates(x, y).getUserObject();
	}

	public Object getValueAt(Point point) {
		return getDefaultMutableTreeNodeFromCoordinates(point).getUserObject();
	}

	public void expandNodeFromCoordinates(int x, int y) {
		tree.expandPath(tree.getPathForLocation(x, y));
	}

	public void collapseNodeFromCoordinates(int x, int y) {
		tree.collapsePath(tree.getPathForLocation(x, y));
	}

	public void select(int x, int y) {
		tree.setSelectionPath(tree.getPathForLocation(x, y));
	}

	Set<Object> selectedNodeValues = new HashSet<Object>(1);
	Set<Object> expandedNodeValues = new HashSet<Object>(1);

	public void reloadTree() {
		saveState();
		fireTreeStructureChanged();
		restoreState();
	}

	public void saveState() {
		selectedNodeValues = new HashSet<Object>();
		expandedNodeValues = new HashSet<Object>();
		int[] selection = tree.getSelectionRows();
		if (selection != null) {
			for (int x : selection) {
				selectedNodeValues.add(((DefaultMutableTreeNode) (tree.getPathForRow(x).getLastPathComponent()))
						.getUserObject());
			}
		}
		for (int x = 0; x < tree.getRowCount(); x++) {
			if (tree.isExpanded(x)) {
				expandedNodeValues.add(((DefaultMutableTreeNode) (tree.getPathForRow(x).getLastPathComponent()))
						.getUserObject());
			}
		}
	}

	public void fireTreeStructureChanged() {
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged((TreeNode) tree.getModel().getRoot());
	}

	boolean restoring = false;

	public void restoreState() {
		restoring = true;
		tree.clearSelection();
		for (int x = 0; x < tree.getRowCount(); x++) {
			Object nodeValue = ((DefaultMutableTreeNode) (tree.getPathForRow(x).getLastPathComponent())).getUserObject();
			if (expandedNodeValues.contains(nodeValue)) {
				tree.expandRow(x);
			}
			if (selectedNodeValues.contains(nodeValue)) {
				tree.addSelectionRow(x);
			}
		}
		restoring = false;
	}

	public boolean isRestoring() {
		return restoring;
	}

	public void selectRow(int y) {
		for (int x = 5; x < tree.getWidth(); x += 5) {
			TreePath path = tree.getPathForLocation(x, y);
			if (path != null) {
				tree.setSelectionPath(path);
				return;
			}
		}
		tree.setSelectionPath(null);
	}

	public void selectByValue(Object value) {
		for (int i = 0; i < tree.getRowCount(); i++) {
			TreePath path = tree.getPathForRow(i);
			if (value != null && value.equals(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject())) {
				tree.setSelectionPath(path);
				return;
			} 
		}
		selectRoot();
	}

	public void selectRoot() {
		TreePath path = tree.getPathForRow(0);
		tree.setSelectionPath(path);
	}

	public void editByValue(Object value) {
		TreePath pathForValue = getPathForValue(value);
		if (pathForValue != null) {
			tree.expandPath(pathForValue);
			tree.startEditingAtPath(pathForValue);
		}
	}

	@SuppressWarnings("unchecked")
	private TreePath getPath(Object value, TreeNode node, TreePath path) {
		if (node instanceof DefaultMutableTreeNode) {
			Object nodeValue = ((DefaultMutableTreeNode) node).getUserObject();
			if (nodeValue.equals(value)) {
				return path;
			}
		} else {
			if (value.toString().equals(node.toString())) {
				return path;
			}
		}
		Enumeration<TreeNode> en = node.children();
		while (en.hasMoreElements()) {
			TreeNode child = en.nextElement();
			TreePath possiblePath = getPath(value, child, path.pathByAddingChild(child));
			if (possiblePath != null) {
				return possiblePath;
			}
		}
		return null;
	}

	public TreePath getPathForValue(Object value) {
		TreeNode node = (TreeNode) tree.getModel().getRoot();
		return getPath(value, node, new TreePath(new Object[] { node }));
	}

	@SuppressWarnings("unchecked")
	public List getSelectedValues() {
		List l = new ArrayList();
		TreePath[] selectionPaths = tree.getSelectionPaths();
		for (TreePath p : selectionPaths) {
			Object lastPathComponent = p.getLastPathComponent();
			if (lastPathComponent instanceof DefaultMutableTreeNode) {
				l.add(((DefaultMutableTreeNode) lastPathComponent).getUserObject());
			}
		}
		return l;
	}
}
