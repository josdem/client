package com.all.core.common.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

public class JComponentToggleItem implements PropertyChangeListener {

	private static final String OPAQUE = "opaque";
	private final ToggleGroupItem item;
	private final ToggleGroupContext context;

	public static void assign(JComponent component, ToggleGroupItem item, ToggleGroupContext context) {
		JComponentToggleItem listener = new JComponentToggleItem(item, context);
		context.add(item);
		component.addPropertyChangeListener(OPAQUE, listener);
		if (component.isOpaque()) {
			context.activate(item);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (OPAQUE.equals(evt.getPropertyName())) {
			if (evt.getNewValue() instanceof Boolean) {
				Boolean val = (Boolean) evt.getNewValue();
				if (val != null && val.booleanValue()) {
					context.activate(item);
				}
			}
		}
	}

	public JComponentToggleItem(ToggleGroupItem item, ToggleGroupContext context) {
		this.item = item;
		this.context = context;
	}
}
