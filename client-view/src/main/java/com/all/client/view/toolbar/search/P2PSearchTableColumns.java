package com.all.client.view.toolbar.search;

import static com.all.client.view.toolbar.search.P2PSearchTableComparators.indexComparator;
import static com.all.client.view.toolbar.search.P2PSearchTableComparators.nameComparator;
import static com.all.client.view.toolbar.search.P2PSearchTableComparators.peersComparator;
import static com.all.client.view.toolbar.search.P2PSearchTableComparators.sizeComparator;
import static com.all.client.view.toolbar.search.P2PSearchTableComparators.typeComparator;
import static com.all.client.view.toolbar.search.P2PSearchTableRenderers.indexRenderer;
import static com.all.client.view.toolbar.search.P2PSearchTableRenderers.nameRenderer;
import static com.all.client.view.toolbar.search.P2PSearchTableRenderers.peersRenderer;
import static com.all.client.view.toolbar.search.P2PSearchTableRenderers.sizeRenderer;
import static com.all.client.view.toolbar.search.P2PSearchTableRenderers.typeRenderer;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

import com.all.client.view.components.TableColumnStyle;

public enum P2PSearchTableColumns implements TableColumnStyle<P2PSearchTableStyle> {
	INDEX("", 28, 28, 28, false),
	NAME("Name", 320, 200, Integer.MAX_VALUE, false),
	PEERS("Peers", 58, 58, 58, false),
	SIZE("Size", 58, 58, 58, false),
	TYPE("Type", 58, 58, 58, false);

	private final String name;
	private final int defaultWidth;
	private final int minWidth;
	private final int maxWidth;
	private final boolean resizable;
	private final int index;

	private P2PSearchTableColumns(String name, int defaultWidth, int minWidth, int maxWidth, boolean resizable) {
		this.name = name;
		this.defaultWidth = defaultWidth;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.resizable = resizable;
		this.index = ordinal();
	}

	public String label() {
		return name;
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

	@Override
	public TableCellRenderer getRenderer(P2PSearchTableStyle style) {
		switch (this) {
		case INDEX:
			return indexRenderer(style);
		case NAME:
			return nameRenderer(style);
		case PEERS:
			return peersRenderer(style);
		case SIZE:
			return sizeRenderer(style);
		case TYPE:
			return typeRenderer(style);

		}
		return null;
	}

	@Override
	public Comparator<?> comparator(P2PSearchTableStyle style) {
		switch (this) {
		case INDEX:
			return indexComparator(style);
		case NAME:
			return nameComparator(style);
		case PEERS:
			return peersComparator(style);
		case SIZE:
			return sizeComparator(style);
		case TYPE:
			return typeComparator(style);
		}
		return null;
	}

}
