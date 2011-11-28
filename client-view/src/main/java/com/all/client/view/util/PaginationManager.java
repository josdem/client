/**
 * 
 */
package com.all.client.view.util;

import java.util.Collections;
import java.util.List;

import com.all.observ.ObserveObject;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;

public class PaginationManager<T> {

	private PageSource<T> pageSource;
	private int pageSize = 20;
	private int page = 0;
	private int size = 0;
	private List<T> elements;
	private Observable<ObserveObject> pageChange = new Observable<ObserveObject>();

	public PaginationManager() {
	}

	public void setPageSource(PageSource<T> pageSource) {
		this.pageSource = pageSource;
		setCurrentPage(0);
		elements = null;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		setCurrentPage(0);
		elements = null;
	}

	public void setCurrentPage(int page) {
		if (page >= 0 && page < getPageCount()) {
			this.page = page;
			elements = null;
			pageChange.fire(ObserveObject.EMPTY);
		}

	}

	public void setElementSize(int size) {
		this.size = size;

	}

	public void nextPage() {
		setCurrentPage(page + 1);
	}

	public void previousPage() {
		setCurrentPage(page - 1);
	}

	@SuppressWarnings("unchecked")
	public List<T> getCurrentElements() {
		if (pageSource == null) {
			return Collections.EMPTY_LIST;
		}
		if (elements == null) {
			int index = page * pageSize;
			elements = pageSource.getElements(index, index + pageSize);
		}
		return elements;
	}

	public int getCurrentPage() {
		return page;
	}

	public int getPageCount() {
		return pageSize == 0 ? 0 : (int) Math.ceil((double) size / pageSize);
	}

	public ObserverCollection<ObserveObject> onPageChange() {
		return pageChange;
	}

	public boolean isPrevPage() {
		return page > 0;
	}

	public boolean isNextPage() {
		return page < (getPageCount() - 1);
	}
}