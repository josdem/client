package com.all.client.view.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;

public final class SingleExpandTree extends MouseAdapter {
	private SingleExpandTree() {
	}

	public static void apply(JTree tree) {
		tree.addMouseListener(new SingleExpandTree());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JTree tree = (JTree) e.getComponent();
		int row = tree.getRowForLocation(e.getX(), e.getY());
		if (row >= 0) {
			tree.expandRow(row);
		}
	}
}
