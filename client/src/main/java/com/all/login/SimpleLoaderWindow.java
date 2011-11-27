package com.all.login;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimpleLoaderWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	public SimpleLoaderWindow() {
		initialize();
	}

	private void initialize() {
		JPanel loadingPanel = new JPanel(new BorderLayout());
		JLabel loadingLabel = new JLabel();
		loadingLabel.setText("LOADING!!");
		loadingPanel.add(loadingLabel, BorderLayout.CENTER);
		this.getContentPane().add(loadingPanel, BorderLayout.CENTER);
		this.setSize(200, 65);
	}
}
