package com.all.client.view.feeds;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.core.common.view.SynthFonts;

public abstract class CommonFeedPanel extends JPanel {

	private static final long serialVersionUID = -535222778466881565L;

	protected final ViewEngine viewEngine;

	private String constraints;

	public CommonFeedPanel(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

	public void appendText(String text) {
		JLabel label = new JLabel(text);
		label.setName(SynthFonts.PLAIN_FONT12_GRAY100_100_100);
		this.add(label, getContraints());
	}

	public void clear() {
		this.removeAll();
	}

	public void newLine() {
		constraints = "newline";
	}

	protected String getContraints() {
		if (constraints != null) {
			String copy = new String(constraints);
			constraints = null;
			return copy;
		}
		return null;
	}

	/**
	 * @return the width occupied by the components within this panel
	 */
	public int getInternalWidth() {
		int internalWidth = 0;
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component component = this.getComponent(i);
			int y = component.getY();
			y += component.getWidth();
			internalWidth = Math.max(y, internalWidth);
		}
		return internalWidth;
	}

}
