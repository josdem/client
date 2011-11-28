package com.all.client.view.dnd;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.junit.Before;
import org.junit.Test;

public class TestDragDataFromTree {

	// * This tree structure is:
	// *A
	// *|-B
	// *|-C
	// ***|-D
	// ***|-E
	// *|-F
	// *|-G
	// ***|-H
	// ***|-I
	// *****|-J
	// *****|-K

	private DefaultMutableTreeNode A = new DefaultMutableTreeNode("A");
	private DefaultMutableTreeNode B = new DefaultMutableTreeNode("B");
	private DefaultMutableTreeNode C = new DefaultMutableTreeNode("C");
	private DefaultMutableTreeNode D = new DefaultMutableTreeNode("D");
	private DefaultMutableTreeNode E = new DefaultMutableTreeNode("E");
	private DefaultMutableTreeNode F = new DefaultMutableTreeNode("F");
	private DefaultMutableTreeNode G = new DefaultMutableTreeNode("G");
	private DefaultMutableTreeNode H = new DefaultMutableTreeNode("H");
	private DefaultMutableTreeNode I = new DefaultMutableTreeNode("I");
	private DefaultMutableTreeNode J = new DefaultMutableTreeNode("J");
	private DefaultMutableTreeNode K = new DefaultMutableTreeNode("K");

	@Before
	public void setup() {
		A.add(B);
		A.add(C);
		A.add(F);
		A.add(G);
		C.add(D);
		C.add(E);
		G.add(H);
		G.add(I);
		I.add(J);
		I.add(K);
	}

	private DragRemoveListener dragRemoveListener = mock(DragRemoveListener.class);
	private DefaultTreeModel treeModel = mock(DefaultTreeModel.class);

	@Test
	public void shouldCheckThatTheTreeIsGood() throws Exception {
		root(A).child(B, root(C).child(D, E), F, root(G).child(H, root(I).child(J, K)));
	}

	@Test
	public void shouldRemoveFromTreeData() throws Exception {
		DragDataFromTree dragDataFromTree = new DragDataFromTree(dragRemoveListener);
		dragDataFromTree.add(C);
		dragDataFromTree.remove(treeModel);
		verify(treeModel).removeNodeFromParent(C);
		verifyNoMoreInteractions(treeModel);

		// its null since its root.
		verify(dragRemoveListener).remove(null, "C");
		verifyNoMoreInteractions(dragRemoveListener);
	}

	@Test
	public void shouldJustRemoveTwoNodes() throws Exception {
		DragDataFromTree dragDataFromTree = new DragDataFromTree(dragRemoveListener);
		dragDataFromTree.add(D);
		dragDataFromTree.add(E);
		dragDataFromTree.remove(treeModel);
		verify(treeModel).removeNodeFromParent(D);
		verify(treeModel).removeNodeFromParent(E);
		verifyNoMoreInteractions(treeModel);

		verify(dragRemoveListener).remove("C", "D");
		verify(dragRemoveListener).remove("C", "E");
		verifyNoMoreInteractions(dragRemoveListener);

	}

	@Test
	public void shouldJustRemoveTheParent() throws Exception {
		DragDataFromTree dragDataFromTree = new DragDataFromTree(dragRemoveListener);
		dragDataFromTree.add(C);
		dragDataFromTree.add(D);
		dragDataFromTree.remove(treeModel);
		verify(treeModel).removeNodeFromParent(C);
		verifyNoMoreInteractions(treeModel);

		verify(dragRemoveListener).remove(null, "C");
		verifyNoMoreInteractions(dragRemoveListener);

	}

	@Test
	public void shouldJustRemoveTheParentOfTheParent() throws Exception {
		DragDataFromTree dragDataFromTree = new DragDataFromTree(dragRemoveListener);
		dragDataFromTree.add(K);
		dragDataFromTree.add(G);
		dragDataFromTree.remove(treeModel);
		verify(treeModel).removeNodeFromParent(G);
		verifyNoMoreInteractions(treeModel);

		verify(dragRemoveListener).remove(null, "G");
		verifyNoMoreInteractions(dragRemoveListener);
	}

	@Test
	public void shouldJustRemoveTheParentOfTheParent2IntenseTest() throws Exception {
		DragDataFromTree dragDataFromTree = new DragDataFromTree(dragRemoveListener);
		dragDataFromTree.add(K);
		dragDataFromTree.add(G);
		dragDataFromTree.add(H);
		dragDataFromTree.add(I);
		dragDataFromTree.add(J);
		dragDataFromTree.add(K);
		dragDataFromTree.remove(treeModel);
		verify(treeModel).removeNodeFromParent(G);
		verifyNoMoreInteractions(treeModel);

		verify(dragRemoveListener).remove(null, "G");
		verifyNoMoreInteractions(dragRemoveListener);
	}

	public DefaultMutableTreeNodeCheck root(DefaultMutableTreeNode node) {
		return new DefaultMutableTreeNodeCheck(node);
	}
}

class DefaultMutableTreeNodeCheck {
	private final DefaultMutableTreeNode node;

	public DefaultMutableTreeNodeCheck(DefaultMutableTreeNode node) {
		this.node = node;

	}

	public DefaultMutableTreeNode child(DefaultMutableTreeNode... children) {
		assertEquals(node.getChildCount(), children.length);
		int index = 0;
		for (DefaultMutableTreeNode defaultMutableTreeNode : children) {
			assertEquals(node.getChildAt(index), defaultMutableTreeNode);
			index++;
		}
		return node;
	}
}