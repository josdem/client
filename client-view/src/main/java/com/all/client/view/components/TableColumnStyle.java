package com.all.client.view.components;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

public interface TableColumnStyle<T extends TableStyle> {

	String label();

	int defaultWidth();

	int minWidth();

	int maxWidth();

	boolean resizable();

	TableCellRenderer getRenderer(T style);

	Comparator<?> comparator(T styles);

	int index();

}
