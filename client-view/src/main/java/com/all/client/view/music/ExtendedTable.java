package com.all.client.view.music;

import java.util.ArrayList;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableModel;

import com.all.client.view.components.Table;
import com.all.client.view.components.TableStyle;
import com.all.core.common.view.JComponentToggleItem;
import com.all.core.common.view.ToggleGroupContext;
import com.all.core.common.view.ToggleGroupItem;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;
import com.all.observ.PausableObservable;

public class ExtendedTable<T, S extends TableStyle> extends Table<T, S> implements ToggleGroupItem {
	private static final long serialVersionUID = 1L;

	private PausableObservable<ObservValue<List<T>>> visibleRowsChanged = new AutoLaunchEventExtendedTable();
	private boolean loadingModel = false;

	public ExtendedTable(S style) {
		super(style);
	}

	private void notifyVisibleRowsChanged() {
		doVisibleRowsChanged();
	}

	private void doVisibleRowsChanged() {
		if (!loadingModel) {
			List<T> rows = getVisibleTracks();
			visibleRowsChanged.fire(new ObservValue<List<T>>(rows));
			doPostNotitifyVisibleRowsChanged();
		}
	}

	
	@Override
	protected void setDataModel(Iterable<T> content) {
		// Prevent too many invocations to event handlers that can adversively
		// affect performance
		try {
			loadingModel = true;
			super.setDataModel(content);
		} finally {
			loadingModel = false;
		}
		notifyVisibleRowsChanged();
	}

	protected void doPostNotitifyVisibleRowsChanged() {
	}

	private List<T> getVisibleTracks() {
		List<T> rows = new ArrayList<T>(getRowCount());
		for (int i = 0; i < getRowCount(); i++) {
			rows.add(getValue(i));
		}
		return rows;
	}

	public ObserverCollection<ObservValue<List<T>>> onVisibleRowsChanged() {
		return visibleRowsChanged;
	}

	@Override
	public void setRowSorter(RowSorter<? extends TableModel> sorter) {
		super.setRowSorter(sorter);
		sorter.addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				if (e.getType() == Type.SORTED) {
					notifyVisibleRowsChanged();
				}
			}
		});
	}

	class AutoLaunchEventExtendedTable extends PausableObservable<ObservValue<List<T>>> {
		private ObservValue<List<T>> eventObject;

		@Override
		public void add(Observer<ObservValue<List<T>>> l) {
			ObservValue<List<T>> eventObject = this.eventObject;
			if (l != null && eventObject != null) {
				l.observe(eventObject);
			}
			super.add(l);
		}

		@Override
		public boolean fire(ObservValue<List<T>> eventObject) {
			this.eventObject = eventObject;
			return super.fire(eventObject);
		}

		public boolean fire() {
			ObservValue<List<T>> eventObject = this.eventObject;
			if (eventObject != null) {
				return fire(eventObject);
			}
			return true;
		}

		@Override
		public void resume() {
			super.resume();
			doVisibleRowsChanged();
		}
	}

	protected boolean visibleRowChangedIsEnabled() {
		return visibleRowsChanged.isEnabled() && !loadingModel;
	}

	public void setToggleContext(ToggleGroupContext context) {
		JComponentToggleItem.assign(this, this, context);
	}

	@Override
	public void active() {
		visibleRowsChanged.resume();
	}

	@Override
	public void inactive() {
		visibleRowsChanged.suspend();
		clearSelection();
	}

}
