package com.all.core.common.view.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.List;

public class FocusOrderPolicy extends FocusTraversalPolicy {
	private List<Component> components; 

	public FocusOrderPolicy(List<Component> compList) {
		components = new ArrayList<Component>(compList);
	}

	@Override
	public Component getComponentAfter(Container aContainer,
			Component aComponent) {
		int idx = (components.indexOf(aComponent) + 1) % components.size();
		return components.get(idx);
	}

	@Override
	public Component getComponentBefore(Container aContainer,
			Component aComponent) {
		int idx = components.indexOf(aComponent) - 1;
		if ( idx < 0) {
			idx = components.size()-1;
		}
		return components.get(idx);
	}

	@Override
	public Component getDefaultComponent(Container aContainer) {
		return getFirstComponent(aContainer);
	}

	@Override
	public Component getFirstComponent(Container aContainer) {
		return components.get(0);
	}

	@Override
	public Component getLastComponent(Container aContainer) {
		return components.get(components.size()-1);
	}

}
