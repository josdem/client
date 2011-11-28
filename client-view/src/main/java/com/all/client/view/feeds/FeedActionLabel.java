package com.all.client.view.feeds;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.apache.commons.lang.StringUtils;

import com.all.action.ActionObject;
import com.all.action.ActionType;
import com.all.appControl.control.ViewEngine;

public class FeedActionLabel<T extends ActionObject> extends JLabel {
	private static final long serialVersionUID = 6727107899430795281L;
	private final FeedLinkLabelMouseListener linkListener = new FeedLinkLabelMouseListener();
	private final ViewEngine viewEngine;
	private final ActionType<T> actionType;
	private final T parameter;

	public FeedActionLabel(final String text, String style, ActionType<T> actionType, ViewEngine viewEngine, T parameter, Icon icon) {
		super(icon);
		
		this.actionType = actionType;
		this.viewEngine = viewEngine;
		this.parameter = parameter;
		
		this.setText(text);
		this.setName(style);
		
		if (StringUtils.isNotEmpty(text)) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		this.addMouseListener(linkListener);
	}

	public FeedActionLabel(final String text, String style, ActionType<T> actionType, ViewEngine viewEngine, T parameter) {
		this(text, style, actionType, viewEngine, parameter, null);
	}

	private class FeedLinkLabelMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			if (actionType != null) {
				viewEngine.send(actionType, parameter);
			}
		}
	}

}
