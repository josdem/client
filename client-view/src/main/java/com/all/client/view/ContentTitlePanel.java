package com.all.client.view;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.i18n.Internationalizable;

public abstract class ContentTitlePanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension(198, 24);

	private static final Rectangle TITLE_LABEL_BOUNDS = new Rectangle(20, 2, 158, 18);

	protected JLabel titleLabel;

	public void initialize() {
		this.setLayout(null);
		this.setPreferredSize(PREFERRED_SIZE);
		this.setMaximumSize(PREFERRED_SIZE);
		this.setMinimumSize(PREFERRED_SIZE);
		this.setSize(PREFERRED_SIZE);
		this.add(getTitleLabel());
	}

	protected JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setVerticalAlignment(JLabel.CENTER);
			titleLabel.setHorizontalAlignment(JLabel.CENTER);
			titleLabel.setBounds(TITLE_LABEL_BOUNDS);
		}
		return titleLabel;
	}
	
	public abstract void initGui(); 

}
