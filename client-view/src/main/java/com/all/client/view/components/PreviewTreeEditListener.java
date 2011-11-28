package com.all.client.view.components;

import javax.swing.tree.DefaultMutableTreeNode;

public interface PreviewTreeEditListener {
	boolean edited(Object editedObject, String cellValue, DefaultMutableTreeNode editingNode);

}
