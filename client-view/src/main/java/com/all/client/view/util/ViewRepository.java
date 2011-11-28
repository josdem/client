package com.all.client.view.util;

import org.springframework.stereotype.Repository;

import com.all.core.common.view.ToggleGroupContext;

@Repository
public class ViewRepository {
	private ToggleGroupContext tableGroupContext = new ToggleGroupContext();

	public ToggleGroupContext getTableGroupContext() {
		return tableGroupContext;
	}
}
