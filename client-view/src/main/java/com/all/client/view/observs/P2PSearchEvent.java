package com.all.client.view.observs;

import java.util.EventObject;

import com.all.client.model.DecoratedSearchData;

public class P2PSearchEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final int progress;
	private final Iterable<DecoratedSearchData> results;

	public P2PSearchEvent(Object source, int progress, Iterable<DecoratedSearchData> results) {
		super(source);
		this.progress = progress;
		this.results = results;
	}

	public int getProgress() {
		return progress;
	}

	public Iterable<DecoratedSearchData> getResults() {
		return results;
	}

}
