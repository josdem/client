package com.all.client.view.components;

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ScrollableTextPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Insets TEXT_PANE_MARGIN = new Insets(5, 5, 5, 5);

	private static final String INVALID_TEXT_PANE_NAME = "postMessageInvalidText";

	private static final String VALID_TEXT_PANE_NAME = "postMessageValidText";

	private JTextPane textPane;

	private JScrollPane scrollPane;

	public ScrollableTextPane(Rectangle bounds, int horizonatlScrollPolicy, int verticalScrollPolicy) {
		this(bounds.x, bounds.y, bounds.width, bounds.height, horizonatlScrollPolicy, verticalScrollPolicy);
	}

	public ScrollableTextPane(int x, int y, final int width, final int height, int horizonatlScrollPolicy,
			int verticalScrollPolicy) {
		super();
		setLayout(null);
		setBounds(x, y, width, height);
		initializeTextPane();
		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, width, height);
		scrollPane.setHorizontalScrollBarPolicy(horizonatlScrollPolicy);
		scrollPane.setVerticalScrollBarPolicy(verticalScrollPolicy);
		scrollPane.setViewportView(textPane);
		scrollPane.getViewport().setName(VALID_TEXT_PANE_NAME);
		add(scrollPane);
	}

	private void initializeTextPane() {
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setCaretPosition(0);
		textPane.setMargin(TEXT_PANE_MARGIN);
	}
	
	public JTextPane getTextPane() {
		return textPane;
	}
	
	public void setTextPane(JTextPane textPane) {
		this.textPane = textPane;
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}
	
	public void setError(boolean error) {
		if (error) {
			scrollPane.getViewport().setName(INVALID_TEXT_PANE_NAME);
		} else {
			scrollPane.getViewport().setName(VALID_TEXT_PANE_NAME);
		}
		scrollPane.getViewport().repaint();
	}
}
