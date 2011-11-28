/**
 * 
 */
package com.all.client.view.dnd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DragDataFromTree {
	private static final Log LOG = LogFactory.getLog(DragDataFromTree.class);
	private final Set<DefaultMutableTreeNode> nodesSelected = new HashSet<DefaultMutableTreeNode>();
	private DragRemoveListener listener;

	public DragDataFromTree(DragRemoveListener listener) {
		this.listener = listener;
	}

	public void add(DefaultMutableTreeNode node) {
		nodesSelected.add(node);
	}

	public void remove(DefaultTreeModel model2) {
		cleanup();
		remove();
		notifyChanges(model2);
	}

	private void cleanup() {
		Iterator<DefaultMutableTreeNode> iterator = nodesSelected.iterator();
		while (iterator.hasNext()) {
			if (parentSelected(iterator.next())) {
				iterator.remove();
			}
		}
	}

	private boolean parentSelected(TreeNode node) {
		TreeNode parent = node.getParent();
		if (parent == null) {
			return false;
		} else {
			if (nodesSelected.contains(parent)) {
				return true;
			} else {
				return parentSelected(parent);
			}
		}
	}

	public void remove() {
		for (DefaultMutableTreeNode node : nodesSelected) {
			Object parent = null;
			try {
				if (node.getParent() != null && node.getParent().getParent() != null) {
					parent = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
				}
			} catch (Exception e) {
				LOG.error(e, e);
			}
			listener.remove(parent, node.getUserObject());
		}
	}

	public void notifyChanges(DefaultTreeModel treeModel) {
		for (DefaultMutableTreeNode node : nodesSelected) {
			treeModel.removeNodeFromParent(node);
		}
	}
}
