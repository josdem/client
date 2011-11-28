package com.all.client.view.components;

import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.all.client.model.Trash;
import com.all.shared.model.ModelCollection;

public class TrashTable extends Table<Object, TrashTableStyle> {
	private static final long serialVersionUID = 1L;

	public TrashTable() {
		super(new TrashTableStyle());
		 this.setName("descriptionTable");
		TrashTableHeader tableHeader = new TrashTableHeader(columnModel);
		this.setTableHeader(tableHeader);
		this.setHeaderRowHeight(getRowHeight());

		for (TrashTableColumns col : TrashTableColumns.values()) {
			TableColumn column = setupColumn(col);
			tableColumnModel.addColumn(column);
		}

		TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(model);
		rowSorter.setMaxSortKeys(1);
		for (TrashTableColumns col : TrashTableColumns.values()) {
			rowSorter.setComparator(col.index(), col.comparator(style));
		}
		this.setRowSorter(rowSorter);

	}

	public ModelCollection getSelectedItems() {
		return null;

	}

	public void setModel(Trash trash) {
	}

}
