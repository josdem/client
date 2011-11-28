package com.all.client.view.components;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.UIManager;

import com.all.client.model.DeviceBase;

public class DevicesButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static final Dimension MINIMUM_SIZE = new Dimension(0, 20);
	private static final Dimension MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 20);
	private static final Dimension PREFERRED_SIZE = new Dimension(100, 20);
	private List<ActionListener> actionListeners;
	private final DeviceBase device;

	public DevicesButton(DeviceBase device) {
		this.device = device;
		actionListeners = new ArrayList<ActionListener>();
		this.setMinimumSize(MINIMUM_SIZE);
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setPreferredSize(PREFERRED_SIZE);
		this.setSize(PREFERRED_SIZE);
		this.setName("deviceButton");
//		this.setText(device.getDeviceRoot().getName());
		this.setText("HARD Disque!!!");
		this.setIcon(UIManager.getDefaults().getIcon(device.getDeviceIcon()));
		this.setHorizontalAlignment(LEFT);
		this.setVerticalAlignment(CENTER);
	}

	public DeviceBase getDevice() {
		return device;
	}

	@Override
	public void addActionListener(ActionListener l) {
		super.addActionListener(l);
		actionListeners.add(l);
	}

	public void clearActionListeners() {
		for (ActionListener actionListener : actionListeners) {
			super.removeActionListener(actionListener);
		}
		actionListeners.clear();
	}

	@Override
	public void removeActionListener(ActionListener l) {
		super.removeActionListener(l);
		actionListeners.remove(l);
	}

}
