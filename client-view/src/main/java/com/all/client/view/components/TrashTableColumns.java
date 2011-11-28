package com.all.client.view.components;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

public enum TrashTableColumns implements TableColumnStyle<TrashTableStyle> {
	ICON("", 24, 24, 24, false),
	NAME("Name", 350, 70, Integer.MAX_VALUE, true),
	TYPE("Type", 60, 60, 100, true),
	DELETE_DATE("Delete date", 120, 100, 160, true);
	private final String name;
	private final int defaultWidth;
	private final int minWidth;
	private final int maxWidth;
	private final boolean resizable;
	private final int index;

	private TrashTableColumns(String name, int defaultWidth, int minWidth, int maxWidth, boolean resizable) {
		this.name = name;
		this.defaultWidth = defaultWidth;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.resizable = resizable;
		this.index = ordinal();
	}

	@Override
	public Comparator<?> comparator(TrashTableStyle styles) {
		return null;
	}

	@Override
	public TableCellRenderer getRenderer(TrashTableStyle style) {
		return null;
	}

	@Override
	public int defaultWidth() {
		return defaultWidth;
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public String label() {
		return name;
	}

	@Override
	public int maxWidth() {
		return maxWidth;
	}

	@Override
	public int minWidth() {
		return minWidth;
	}

	@Override
	public boolean resizable() {
		return resizable;
	}
}
