/**
 * 
 */
package com.all.client.view.toolbar.downloads;

import java.util.ArrayList;
import java.util.List;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;

import com.all.client.model.Download;
import com.all.client.model.StringPredicate;

public class DownloadTableRowFilter extends RowFilter<TableModel, Integer> {
	private Predicate namePredicate;

	public DownloadTableRowFilter(String text) {
		String[] names = text.split(" ");
		List<Predicate> namePredicates = new ArrayList<Predicate>();

		for (String nameSplitted : names) {
			namePredicates.add(new DownloadInfoNamePredicate(nameSplitted));
		}

		if (namePredicates.size() > 1) {
			Predicate andPredicate = new AndPredicate(namePredicates.get(0), namePredicates.get(1));
			for (int i = 2; i < namePredicates.size(); i++) {
				andPredicate = new AndPredicate(andPredicate, namePredicates.get(i));
			}
			this.namePredicate = andPredicate;
		} else {
			this.namePredicate = namePredicates.get(0);
		}

	}

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
		Download downloadInfo = (Download) entry.getValue(0);
		return namePredicate.evaluate(downloadInfo);
	}
}

class DownloadInfoNamePredicate extends StringPredicate<Download> {
	public DownloadInfoNamePredicate(String target) {
		super(target);
	}

	@Override
	protected String sourceString(Download track) {
		return track.getDisplayName();
	}

}
