package com.all.client.view.components;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScrollableTextArea extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String INVALID_TEXT_PANE_NAME = "postMessageInvalidText";

	private static final String VALID_TEXT_PANE_NAME = "postMessageValidText";
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private static final int VERTICAL_SCROLL_BAR_WIDTH = 18;
	private static final int HORIZONTAL_GAP = 10;
	private static final int VERTICAL_GAP = 1;

	public ScrollableTextArea(Rectangle bounds, int horizonatlScrollPolicy, int verticalScrollPolicy) {
		this(bounds.x, bounds.y, bounds.width, bounds.height, horizonatlScrollPolicy, verticalScrollPolicy);
	}

	public ScrollableTextArea(int x, int y, final int width, final int height, int horizonatlScrollPolicy,
			int verticalScrollPolicy) {
		super();
		setLayout(null);
		setBounds(x, y, width, height);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setMargin(new Insets(5, 5, 5, 5));
		scrollPane = new JScrollPane();
		scrollPane.setBounds(HORIZONTAL_GAP, VERTICAL_GAP, width - VERTICAL_SCROLL_BAR_WIDTH, height - VERTICAL_GAP * 2);
		scrollPane.setHorizontalScrollBarPolicy(horizonatlScrollPolicy);
		scrollPane.setVerticalScrollBarPolicy(verticalScrollPolicy);
		scrollPane.setViewportView(textArea);
		scrollPane.getVerticalScrollBar().addComponentListener(new ComponentAdapter() {
			Rectangle scrollVisibleBounds = new Rectangle(HORIZONTAL_GAP, VERTICAL_GAP, width - 10, height - VERTICAL_GAP * 2);
			Rectangle scrollNotVisibleBounds = new Rectangle(HORIZONTAL_GAP, VERTICAL_GAP, width - VERTICAL_SCROLL_BAR_WIDTH,
					height - VERTICAL_GAP * 2);

			@Override
			public void componentHidden(ComponentEvent e) {
				updateScrollPaneBounds(scrollNotVisibleBounds);
			}

			@Override
			public void componentShown(ComponentEvent e) {
				updateScrollPaneBounds(scrollVisibleBounds);
			}
		});
		scrollPane.getViewport().setName(VALID_TEXT_PANE_NAME);
		add(scrollPane);
	}

	private void updateScrollPaneBounds(Rectangle bounds) {
		scrollPane.setBounds(bounds);
	}

	public void setText(String text) {
		textArea.setText(text);
	}

	public String getText() {
		return textArea.getText();
	}

	public void setEditable(boolean isEditable) {
		textArea.setEditable(isEditable);
	}

	public JTextArea getTextArea() {
		return textArea;
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
