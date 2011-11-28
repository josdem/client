package com.all.client.view.music;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.apache.commons.collections.Predicate;

import com.all.shared.model.Track;

public class DescriptionTableRowFilter extends RowFilter<TableModel, Integer> {
	private final Predicate predicate;

	public DescriptionTableRowFilter(TrackSearchType trackSearchType, String text) {
		this.predicate = trackSearchType.getPredicate(text);
	}

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
		Track track = (Track) entry.getValue(0);
		return predicate.evaluate(track);
	}
}