package com.all.client.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;

import com.all.client.view.listeners.SingleExpandTree;
import com.all.client.view.util.JTreeCoordinateHelper;

public class DeviceTree extends JTree {
		
		private static final long serialVersionUID = 1L;
		
		private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
		
		private static final Color SELECTED_BACKGROUND_COLOR = new Color(210, 210, 210);
		
		private static final int NODE_HEIGHT = 20;
		
		private static final int SEPARATOR_HEIGHT = 19;
		
		private Integer dragOverObject;
		
		private JTreeCoordinateHelper treeHelper;
		
		public DeviceTree(TreeNode root) {
			super(root);
			treeHelper = new JTreeCoordinateHelper(this);
			SingleExpandTree.apply(this);
		}
		
		@Override
		protected void paintComponent(Graphics gr) {
			Graphics2D g = (Graphics2D) gr;
			g.setColor(BACKGROUND_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
			int[] selectionRows = getSelectionRows();
			
			Integer highlightedRow = null;
			if (dragOverObject != null) {
				highlightedRow = dragOverObject / NODE_HEIGHT;
			}
			
			if (selectionRows != null) {
				for (int i : selectionRows) {
					int y = i * NODE_HEIGHT;
					g.setColor(SELECTED_BACKGROUND_COLOR);
					g.fillRect(0, y, getWidth(), NODE_HEIGHT);
					g.setColor(BACKGROUND_COLOR);
					g.fillRect(0, y + SEPARATOR_HEIGHT, getWidth(), 1);
				}
			}
			
			if (highlightedRow != null) {
				int y = highlightedRow * NODE_HEIGHT;
				ImageIcon imageIcon = (ImageIcon) UIManager.getDefaults().getIcon("Tree.devices.hightlight");
				Image image = imageIcon != null ? imageIcon.getImage() : null;
				g.drawImage(image, 0, y, getWidth(), NODE_HEIGHT, this);
			}
			
			setOpaque(false);
			super.paintComponent(g);
			setOpaque(true);
		}
		
		public void setDragOverObject(Integer b) {
			dragOverObject = b;
		}
		
		public Integer getDragOverObject() {
			return dragOverObject;
		}
		
		public JTreeCoordinateHelper getTreeHelper() {
			return treeHelper;
		}
		
}