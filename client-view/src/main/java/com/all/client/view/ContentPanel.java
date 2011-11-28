package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class ContentPanel extends JPanel implements Internationalizable, View {
	private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
	private static final Dimension BOTTOM_PANEL_DEFAULT_SIZE = new Dimension(198, 18);
	private static final long serialVersionUID = 1L;
	protected JPanel bottomLibPanel = null;
	private final ContentTitlePanel titlePanel;
	private final SimplePanel contentPanel;

	public ContentPanel(ContentTitlePanel titlePanel, SimplePanel contentPanel) {
		this.titlePanel = titlePanel;
		this.contentPanel = contentPanel;
		
		initialize();
	}

	public ContentPanel(ContentTitlePanel titlePanel, PreviewPanel contentPanel) {
		this.titlePanel = titlePanel;
		this.contentPanel = contentPanel;
		
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setName("previewTreeBackground");
		this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
		if (titlePanel != null) {
			this.add(titlePanel, BorderLayout.NORTH);
			if (!(titlePanel instanceof DeviceContentTitlePanel)) {
				this.add(getBottomLibPanel(), BorderLayout.SOUTH);
			}
		}
		this.add(contentPanel, BorderLayout.CENTER);
	}

	public void initialize(ViewEngine viewEngine) {
		contentPanel.initialize(viewEngine);
	}

	public void destroy(ViewEngine viewEngine) {
		if (titlePanel != null) {
			titlePanel.destroy(viewEngine);
		}
		contentPanel.destroy(viewEngine);
	}

	protected JPanel getBottomLibPanel() {
		if (bottomLibPanel == null) {
			bottomLibPanel = new JPanel();
			bottomLibPanel.setLayout(null);
			bottomLibPanel.setBorder(EMPTY_BORDER);
			bottomLibPanel.setName("myMusicBottomLibPanel");
			bottomLibPanel.setPreferredSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomLibPanel.setMaximumSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomLibPanel.setMinimumSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomLibPanel.setSize(BOTTOM_PANEL_DEFAULT_SIZE);
		}
		return bottomLibPanel;
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void removeMessages(Messages messages) {
		contentPanel.removeMessages(messages);
		if (titlePanel != null) {
			titlePanel.removeMessages(messages);
		}
	}

	@Override
	public void setMessages(Messages messages) {
		contentPanel.setMessages(messages);
		if (titlePanel != null) {
			titlePanel.setMessages(messages);
		}
	}

}
