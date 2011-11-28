package com.all.client.view.actions;

import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;

public class TestNodeSelectedAction {
	private static final boolean DEBUG = false;
	private Log log = LogFactory.getLog(this.getClass());

	@Test
	public void shouldCreateJTreeOnFrame() throws Exception {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("one");
		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("two");
		root.add(node);
		root.add(node2);
		TreeModel treeModel = new DefaultTreeModel(root);
		JTree tree = new JTree(treeModel);

		tree.setSelectionPath(new TreePath(node.getPath()));
		assertEquals(new TreePath(node.getPath()), tree.getSelectionPath());

		panel.add(tree, BorderLayout.CENTER);
		frame.add(panel);
		frame.setSize(400, 300);
		if (DEBUG) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
	}

	@After
	public void waitIfDebug() {
		if (DEBUG) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				log.debug(e,e);
			}
		}
	}
}
