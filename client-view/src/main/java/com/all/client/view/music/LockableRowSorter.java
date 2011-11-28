package com.all.client.view.music;

import java.util.Comparator;
import java.util.List;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class LockableRowSorter<T extends TableModel> extends TableRowSorter<T> {
	private Comparator<?>[] comparators = new Comparator<?>[0];

	public LockableRowSorter(T model) {
		super(model);
		setupComparators();
	}

	private void setupComparators() {
		Comparator<?>[] comparators = new Comparator<?>[getModel().getColumnCount()];
		System.arraycopy(this.comparators, 0, comparators, 0, Math.min(comparators.length, this.comparators.length));
		this.comparators = comparators;
	}

	@Override
	public void setComparator(int column, Comparator<?> comparator) {
		comparators[column] = comparator;
		super.setComparator(column, comparator);
	}

	@Override
	public void modelStructureChanged() {
		setupComparators();
		super.modelStructureChanged();
	}

	@Override
	public Comparator<?> getComparator(int column) {
		Comparator<?> comparator = comparators[column];
		if (comparator != null) {
			return comparator;
		}
		return super.getComparator(column);
	}

	@Override
	protected boolean useToString(int column) {
		if (column >= comparators.length) {
			return true;
		}
		return comparators[column] == null;
	}

	@Override
	public void setSortKeys(List<? extends javax.swing.RowSorter.SortKey> sortKeys) {
		if (sortKeys != null) {
			super.setSortKeys(sortKeys);
		}
	}
}
