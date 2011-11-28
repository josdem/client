package com.all.client.view.toolbar.social;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;

import com.all.twitter.TwitterStatus;

public final class TwitterStatusButton extends JButton {

	private static final long serialVersionUID = 1L;

	private static final String TWITTER_STATUS_BUTTON_NAME = "replyWallButton";

	private static final Dimension TWITTER_STATUS_BUTTON_DEFAULT_SIZE = new Dimension(60, 16);

	private static final Insets TWITTER_STATUS_BUTTON_MARGIN = new Insets(0, 6, 0, 7);

	private final TwitterStatus twitterStatus;

	public TwitterStatusButton(TwitterStatus twitterStatus) {
		this.twitterStatus = twitterStatus;
		setName(TWITTER_STATUS_BUTTON_NAME);
		setPreferredSize(TWITTER_STATUS_BUTTON_DEFAULT_SIZE);
		setSize(TWITTER_STATUS_BUTTON_DEFAULT_SIZE);
		setMaximumSize(TWITTER_STATUS_BUTTON_DEFAULT_SIZE);
		setMinimumSize(TWITTER_STATUS_BUTTON_DEFAULT_SIZE);
		setMargin(TWITTER_STATUS_BUTTON_MARGIN);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public TwitterStatus getTwitterStatus() {
		return twitterStatus;
	}
}
