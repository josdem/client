package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogoutFrame {
	private static final Log LOG = LogFactory.getLog(LogoutFrame.class);
	private static final long serialVersionUID = 1L;
	private JPanel centerPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel messageLabel;
	private JFrame frame;
	private JLabel headerLabel;

	public void hide() {
		try {
			getFrame().dispatchEvent(new WindowEvent(getFrame(), WindowEvent.WINDOW_CLOSING));
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	public void show(Window window) {
		Point location = new Point(0, 0);
		try {
			location.x = frame.getLocation().x + (frame.getWidth() / 2);
			location.y = frame.getLocation().y + (frame.getHeight() / 2);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		show(location);
	}

	public void show(Point location) {
		getFrame().setLocation(location);
		getFrame().setVisible(true);
	}

	public JFrame getFrame() {
		if (frame == null) {
			frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.add(getCenterPanel(), BorderLayout.CENTER);
			Dimension size = new Dimension(250, 90);
			frame.setPreferredSize(size);
			frame.setMinimumSize(size);
			frame.setMaximumSize(size);
			frame.setSize(size);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setUndecorated(true);
		}
		return frame;
	}

	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel(new BorderLayout());
			centerPanel.setName("logoutPanel");
			centerPanel.add(getBottomPanel(), BorderLayout.SOUTH);
			centerPanel.add(getTopPanel(), BorderLayout.NORTH);
		}
		return centerPanel;
	}

	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new GridBagLayout());
			Dimension size = new Dimension(100, 30);
			topPanel.setPreferredSize(size);
			topPanel.setMinimumSize(size);
			topPanel.setMaximumSize(size);
			topPanel.setSize(size);
			GridBagConstraints headerLabelConstraints = new GridBagConstraints();
			topPanel.add(getHeaderLabel(), headerLabelConstraints);
		}
		return topPanel;
	}

	private JLabel getHeaderLabel() {
		if (headerLabel == null) {
			headerLabel = new JLabel();
		}
		return headerLabel;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridBagLayout());
			Dimension size = new Dimension(100, 30);
			bottomPanel.setPreferredSize(size);
			bottomPanel.setMinimumSize(size);
			bottomPanel.setMaximumSize(size);
			bottomPanel.setSize(size);
			GridBagConstraints messageLabelConstraints = new GridBagConstraints();
			bottomPanel.add(getMessageLabel(), messageLabelConstraints);
		}
		return bottomPanel;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel("LOGGING OUT");

		}
		return messageLabel;
	}

	public void showMessage(String message) {
		getMessageLabel().setText(message);
	}

	public void showTitle(String title) {
		getHeaderLabel().setText(title);
	}

}
