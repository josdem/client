package com.all.client.view.components;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

public class TrashTableRowFilter extends RowFilter<TableModel, Integer> {

	public TrashTableRowFilter() {
	}

	@Override
	public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
		return false;
	}

}
