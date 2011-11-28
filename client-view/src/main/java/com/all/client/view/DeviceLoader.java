/**
 * 
 */
package com.all.client.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.shared.model.Root;

public class DeviceLoader extends Thread implements Refreshable {

	private Log log = LogFactory.getLog(this.getClass());
	private Object lock = new Object();

	private boolean running = true;
	private final JTree tree;
	private final Root root;

	public DeviceLoader(JTree tree, Root root) {
		this.tree = tree;
		this.root = root;
		this.setDaemon(true);
		this.setName("DeviceLoaderDaemon");
	}

	public void refresh() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	@Override
	public void run() {
		try {
			loadLevel0();
			while (running) {
				checkNode((DefaultMutableTreeNode) tree.getModel().getRoot());
				synchronized (lock) {
					lock.wait(5000);
				}
			}
		} catch (InterruptedException e) {
			log.error(e, e);
		}
	}

	private void checkNode(DefaultMutableTreeNode node) throws InterruptedException {
		checkInterrupt();
		if (!tree.isExpanded(new TreePath(node.getPath()))) {
			return;
		}
		Object userObject = node.getUserObject();
		if (userObject instanceof File) {
			File nodeFile = (File) userObject;
			if (nodeFile.isDirectory()) {
				if (node.getChildCount() == 1) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(0);
					Object userObject2 = child.getUserObject();
					if (userObject2 instanceof File) {
						doDifferential(node, nodeFile);
					}
					if (userObject2 instanceof DeviceLoaderStates) {
						DeviceLoaderStates state = (DeviceLoaderStates) userObject2;
						if (state == DeviceLoaderStates.LOADING) {
							populate(node);
						}
					}
				} else {
					doDifferential(node, nodeFile);
				}

			}
		} else {
			Enumeration<?> children = node.children();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode element = (DefaultMutableTreeNode) children.nextElement();
				checkNode(element);
			}
		}
	}

	private void doDifferential(DefaultMutableTreeNode node, File nodeFile) throws InterruptedException {
		Set<File> systemFiles = new HashSet<File>();
		Set<File> childFiles = new HashSet<File>();

		for (File expectedFile : nodeFile.listFiles()) {
			if (isValid(expectedFile)) {
				systemFiles.add(expectedFile);
			}
		}

		Set<DefaultMutableTreeNode> nodesToRemove = new HashSet<DefaultMutableTreeNode>();
		Set<DefaultMutableTreeNode> nodesToAdd = new HashSet<DefaultMutableTreeNode>();

		Enumeration<?> children = node.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
			Object childObject = child.getUserObject();
			if (childObject instanceof File) {
				File childFile = (File) childObject;
				childFiles.add(childFile);
				if (!systemFiles.contains(childFile)) {
					nodesToRemove.add(child);
				}
				if (childFile.isDirectory()) {
					checkNode(child);
				}
			} else {
				nodesToRemove.add(child);
			}
		}
		for (File systemFile : systemFiles) {
			if (isValid(systemFile) && !childFiles.contains(systemFile)) {
				nodesToAdd.add(getNode(systemFile));
			}
		}

		for (DefaultMutableTreeNode defaultMutableTreeNode : nodesToAdd) {
			node.add(defaultMutableTreeNode);
		}
		for (DefaultMutableTreeNode defaultMutableTreeNode : nodesToRemove) {
			node.remove(defaultMutableTreeNode);
		}
		if (node.getChildCount() == 0) {
			node.add(new DefaultMutableTreeNode(DeviceLoaderStates.EMPTY));
		}

		if (!nodesToAdd.isEmpty() || !nodesToRemove.isEmpty()) {
			((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
		}
	}

	private boolean isValid(File file) {
		return !isInvalid(file);
	}

	private void checkInterrupt() throws InterruptedException {
		if (!running) {
			throw new InterruptedException();
		}
	}

	private void loadLevel0() {
		List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
		for (File f : root.getRootFiles()) {
			if (f.exists()) {
				nodes.add(new DefaultMutableTreeNode(f));
			}
		}
		if (nodes.isEmpty()) {
			tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(DeviceLoaderStates.EMPTY)));
			tree.setRootVisible(false);
		} else if (nodes.size() == 1) {
			populate(nodes.get(0));
			tree.setModel(new DefaultTreeModel(nodes.get(0)));
			tree.setRootVisible(false);
		} else {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(DeviceLoaderStates.UNNAMED);
			for (DefaultMutableTreeNode defaultMutableTreeNode : nodes) {
				root.add(defaultMutableTreeNode);
			}
			tree.setModel(new DefaultTreeModel(root));
			tree.setRootVisible(false);
		}
	}

	public void populate(DefaultMutableTreeNode node) {
		Object userObject = node.getUserObject();
		if (userObject instanceof File) {
			File file = (File) userObject;
			if (file.isDirectory()) {
				node.removeAllChildren();
				for (File f : file.listFiles()) {
					addNode(f, node);
				}
				if (node.getChildCount() == 0) {
					node.add(new DefaultMutableTreeNode(DeviceLoaderStates.EMPTY));
				}
			}
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.nodeStructureChanged(node);
		}
	}

	public void clear(DefaultMutableTreeNode node) {
		Object userObject = node.getUserObject();
		if (userObject instanceof File) {
			File file = (File) userObject;
			if (file.isDirectory()) {
				node.removeAllChildren();
				node.add(new DefaultMutableTreeNode(DeviceLoaderStates.LOADING));
			}
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.nodeStructureChanged(node);
		}
	}

	private void addNode(File file, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode node = getNode(file);
		if (node != null) {
			parent.add(node);
		}
	}

	private DefaultMutableTreeNode getNode(File file) {
		if (isInvalid(file)) {
			return null;
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
		if (file.isDirectory()) {
			node.add(new DefaultMutableTreeNode(DeviceLoaderStates.LOADING));
		}
		return node;

	}

	private boolean isInvalid(File file) {
		return file.isHidden() || ".".equals(file.getName()) || "..".equals(file.getName());
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}