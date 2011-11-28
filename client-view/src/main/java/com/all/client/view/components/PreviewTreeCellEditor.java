package com.all.client.view.components;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.all.core.common.view.util.SpacerKeyListener;
import com.all.shared.model.Folder;

public class PreviewTreeCellEditor extends PreviewTreeDefaultCellEditor {

	private Icon openIcon;
	private Icon closedIcon;
	private Icon leafIcon;
	private StubbornComponentResizeListener stubbornListener = new StubbornComponentResizeListener(100,
			PreviewTree.NODE_HEIGHT, "TEXTFIELD");
	private static final SpacerKeyListener spacerListener = new SpacerKeyListener();

	public PreviewTreeCellEditor(PreviewTree tree, PreviewTreeCellRenderer defaultTreeCellRenderer) {
		super(tree, defaultTreeCellRenderer, new PreviewCellEditor(new JTextField(), tree));
		editingContainer.addComponentListener(stubbornListener);
		this.openIcon = defaultTreeCellRenderer.getOpenIcon();
		this.closedIcon = defaultTreeCellRenderer.getClosedIcon();
		this.leafIcon = defaultTreeCellRenderer.getLeafIcon();
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		editingIcon = closedIcon;
		Component treeCellEditorComponent = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		EditorContainer editorContainer = (EditorContainer) treeCellEditorComponent;
		addSpacerListener(editorContainer);
		stubbornListener.setWidth(tree.getRowBounds(row).width);
		return treeCellEditorComponent;
	}

	private void addSpacerListener(EditorContainer editorContainer) {
		if(editorContainer.getComponentCount() > 0){
			editorContainer.getComponent(0).removeKeyListener(spacerListener);
			editorContainer.getComponent(0).addKeyListener(spacerListener);
		}
	}

	@Override
	protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (((DefaultMutableTreeNode) value).getUserObject() instanceof Folder) {
			editingIcon = openIcon;
		} else {
			editingIcon = leafIcon;
		}
		if (editingIcon != null) {
			offset = renderer.getIconTextGap() + editingIcon.getIconWidth();
		} else {
			offset = renderer.getIconTextGap();
		}
	}
}