package com.all.client.view.listeners;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.Table;
import com.all.core.actions.Actions;

public class RestoreSelectionFocusListener implements FocusListener {
	private ViewEngine viewEngine;

	public RestoreSelectionFocusListener(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void focusGained(FocusEvent e) {
		Component component = e.getComponent();
		if (component instanceof JTree) {
			restoreSelection((JTree) component);
			return;
		}
		if (component instanceof Table) {
			restoreSelection((Table) component);
			return;
		}
		if (component instanceof JTable) {
			restoreSelection((JTable) component);
			return;
		}

	}

	private void restoreSelection(JTable table) {
		if (table.getSelectedRowCount() > 0) {
			int[] selectedRows = table.getSelectedRows();
			List<Object> selectedItems = new ArrayList<Object>(selectedRows.length);
			for (int row : selectedRows) {
				selectedItems.add(table.getValueAt(row, 0));
			}
			viewEngine.sendValueAction(Actions.View.SET_CLIPBOARD_SELECTION, selectedItems);
		}
	}

	private void restoreSelection(Table<?, ?> table) {
		if (table.getSelectedRowCount() > 0) {
			int[] selectedRows = table.getSelectedRows();
			List<Object> selectedItems = new ArrayList<Object>(selectedRows.length);
			for (int row : selectedRows) {
				selectedItems.add(table.getValue(row));
			}
			viewEngine.sendValueAction(Actions.View.SET_CLIPBOARD_SELECTION, selectedItems);
		}
	}

	private void restoreSelection(JTree tree) {
		if (tree.getSelectionCount() > 0) {
			TreePath[] selectedRows = tree.getSelectionPaths();
			List<Object> selectedItems = new ArrayList<Object>(selectedRows.length);
			for (TreePath row : selectedRows) {
				selectedItems.add(((DefaultMutableTreeNode) row.getLastPathComponent()).getUserObject());
			}
			viewEngine.sendValueAction(Actions.View.SET_CLIPBOARD_SELECTION, selectedItems);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

}
