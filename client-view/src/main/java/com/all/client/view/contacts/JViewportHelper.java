package com.all.client.view.contacts;

import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class JViewportHelper {
	private JScrollPane jScrollPane;

	public JViewport getViewport(JScrollPane jScrollPane) {
		this.jScrollPane = jScrollPane;
		return jScrollPane.getViewport();
	}

	public void setViewPosition(Point viewPosition) {
		this.jScrollPane.getViewport().setViewPosition(viewPosition);
	}
	
}
