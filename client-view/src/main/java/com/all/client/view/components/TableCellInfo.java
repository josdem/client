package com.all.client.view.components;

import javax.swing.table.TableColumn;

public class TableCellInfo<T> {
	private int row, column, x, y, width, height;
	private T data;
	private TableColumn tableColumn;

	public TableCellInfo(int row, int column, int x, int y, int width, int height, T data, TableColumn tableColumn) {
		super();
		this.row = row;
		this.column = column;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.data = data;
		this.tableColumn = tableColumn;
	}

	public int getRow() {
		return this.row;
	}

	public int getColumn() {
		return this.column;
	}

	public TableColumn getTableColumn() {
		return tableColumn;
	}

	public T getData() {
		return data;
	}

	public int getXinColumn() {
		return this.x;
	}

	public int getYinColumn() {
		return this.y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
