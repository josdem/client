package com.all.client.view.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJTreeCoordinateHelper {
	private static final boolean DEBUG = false;
	private static final String rootValue = "root";
	private static final String playlistInsideRootValue = "playlistRoot";
	private static final String folderValue = "folder";
	private static final String playlistInsideFolderValue = "playlist";
	private JTree tree;
	private JTreeCoordinateHelper jTreeCoordinateHelper;
	DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootValue);

	private Log log = LogFactory.getLog(this.getClass());
	
	private TreePath playlistInsideFolderPath;

	@BeforeClass
	public static void restoreLnF() throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	}

	@Before
	public void initializeTree() {
		DefaultMutableTreeNode folder = new DefaultMutableTreeNode(folderValue);
		DefaultMutableTreeNode playlistRoot = new DefaultMutableTreeNode(playlistInsideRootValue);
		DefaultMutableTreeNode playlist = new DefaultMutableTreeNode(playlistInsideFolderValue);
		root.add(folder);
		folder.add(playlist);
		root.add(playlistRoot);
		tree = new JTree(root);

		playlistInsideFolderPath = new TreePath(playlist.getPath());

		tree.setSize(50, 200);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setName("TREE");
		jTreeCoordinateHelper = new JTreeCoordinateHelper(tree);
		if (DEBUG) {
			JFrame frame = new JFrame();
			frame.setSize(50, 200);
			frame.add(tree);
			frame.setVisible(true);
		}
	}

	@Test
	public void shouldGetValueInsideTreeNodeOnGivenCoordinates() throws Exception {
		assertEquals(rootValue, jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(10, 5));
		assertEquals(folderValue, jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(40, 20));
		assertEquals(playlistInsideRootValue, jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(40, 40));
		assertNull(jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(40, 120));
	}

	@Test
	public void shouldExpandNodeByCoordinates() throws Exception {
		jTreeCoordinateHelper.expandNodeFromCoordinates(40, 20);
		assertEquals(playlistInsideFolderValue, jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(40, 40));
	}

	@Test
	public void shouldCollapseNodeByCoordinates() throws Exception {
		jTreeCoordinateHelper.expandNodeFromCoordinates(40, 20);
		assertEquals(playlistInsideFolderValue, jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(40, 40));
		jTreeCoordinateHelper.collapseNodeFromCoordinates(40, 20);
		assertEquals(playlistInsideRootValue, jTreeCoordinateHelper.getTreeNodeContentFromCoordinates(40, 40));
	}

	@Test
	public void shouldNotDieCollapsingOrExpanding() {
		jTreeCoordinateHelper.expandNodeFromCoordinates(40, 40);
		jTreeCoordinateHelper.collapseNodeFromCoordinates(40, 40);
		jTreeCoordinateHelper.expandNodeFromCoordinates(40, 120);
		jTreeCoordinateHelper.collapseNodeFromCoordinates(40, 120);
	}

	@Test
	public void shouldSelectNodeInGivenCoordinates() throws Exception {
		jTreeCoordinateHelper.select(40, 40);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
		assertEquals(playlistInsideRootValue, node.getUserObject());
	}

	@Test
	public void shouldGetTheDefaultMutableTreeNodeInsideTheTree() throws Exception {
		DefaultMutableTreeNode node = jTreeCoordinateHelper.getDefaultMutableTreeNodeFromCoordinates(40, 40);
		assertEquals(playlistInsideRootValue, node.getUserObject());
	}

	@Test
	public void shouldGetTheRootDefaultMutableTreeNodeInsideTheTreeIfCoordinatesFallOutsideTheTree() throws Exception {
		DefaultMutableTreeNode node = jTreeCoordinateHelper.getDefaultMutableTreeNodeFromCoordinates(40, 150);
		assertEquals(rootValue, node.getUserObject());
	}

	@Test
	public void shouldReloadTreeAfterStructureHasChanged() throws Exception {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("anotherNode");
		root.add(node);
		jTreeCoordinateHelper.reloadTree();
		assertEquals(node, jTreeCoordinateHelper.getDefaultMutableTreeNodeFromCoordinates(40, 60));
	}

	@Test
	public void shouldReloadTreeAndKeepExpandedStateAfterStructureHasChanged() throws Exception {
		tree.expandRow(1);
		jTreeCoordinateHelper.select(40, 40);
		DefaultMutableTreeNode selectednode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("anotherNode");
		root.add(node);
		jTreeCoordinateHelper.reloadTree();
		assertEquals(node, jTreeCoordinateHelper.getDefaultMutableTreeNodeFromCoordinates(40, 80));
		assertEquals(selectednode, tree.getSelectionPath().getLastPathComponent());
	}

	@Test
	public void shouldSelectElementByValue() throws Exception {
		jTreeCoordinateHelper.selectByValue(null);

		assertEquals(rootValue, ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject());

		jTreeCoordinateHelper.selectByValue(folderValue);

		assertEquals(folderValue, ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject());
	}

	@Test
	public void shouldSelectRoot() throws Exception {
		jTreeCoordinateHelper.selectRoot();
		assertEquals(rootValue, ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject());
	}

	@Test
	public void shouldSelectEditNodeByValue() throws Exception {
		assertFalse(tree.isVisible(playlistInsideFolderPath));
		jTreeCoordinateHelper.editByValue(playlistInsideFolderValue);
		assertTrue(tree.isVisible(playlistInsideFolderPath));
		assertTrue(tree.isEditing());
		assertEquals(playlistInsideFolderPath, tree.getEditingPath());
	}

	@Test
	public void shouldGetPathByValue() throws Exception {
		TreePath pathForValue = jTreeCoordinateHelper.getPathForValue(playlistInsideFolderValue);
		assertNotNull(pathForValue);
		assertEquals(playlistInsideFolderPath, pathForValue);

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