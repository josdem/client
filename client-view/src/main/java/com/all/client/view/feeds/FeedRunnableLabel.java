package com.all.client.view.feeds;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.apache.commons.lang.StringUtils;

public class FeedRunnableLabel extends JLabel {

	private static final long serialVersionUID = 5495937068635036612L;

	private final FeedLinkLabelMouseListener linkListener = new FeedLinkLabelMouseListener();
	
	private final Runnable runnable;
	
	public FeedRunnableLabel(final String text, String style, Runnable runnable, Icon icon) {
		super(icon);
		
		this.runnable = runnable;

		this.setText(text);
		this.setName(style);

		if (StringUtils.isNotEmpty(text)) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		this.addMouseListener(linkListener);
	}
	
	public FeedRunnableLabel(final String text, String style, Runnable runnable) {
		this(text, style, runnable, null);
	}
	
	private class FeedLinkLabelMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			if(event.getClickCount() == 1){
				if (runnable != null) {
					runnable.run();
				}
			}
		}
	}

	
}
