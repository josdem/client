package com.all.core.common.view;

import java.util.ArrayList;
import java.util.List;

public class ToggleGroupContext {
	private List<ToggleGroupItem> items = new ArrayList<ToggleGroupItem>();
	private ToggleGroupItem item;

	public void activate(ToggleGroupItem item) {
		if (item == this.item || (item != null && item.equals(this.item))) {
			return;
		}
		this.item = item;
		for (ToggleGroupItem toggleItem : items) {
			if (toggleItem == items || toggleItem.equals(item)) {
				toggleItem.active();
			} else {
				toggleItem.inactive();
			}
		}
	}

	public void add(ToggleGroupItem item) {
		if (item != null && !items.contains(item)) {
			items.add(item);
			activate(this.item);
		}
	}
}
