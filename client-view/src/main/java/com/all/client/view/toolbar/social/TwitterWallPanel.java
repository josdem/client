package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.DecoratedTwitterStatus;
import com.all.client.model.TwitterProfile;
import com.all.client.util.TwitterUtil;
import com.all.client.view.listeners.TwitterActionListener;
import com.all.client.view.toolbar.social.TwitterStatusPanel.TwitterUserLabel;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.common.view.util.SpacerKeyListener;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.twitter.TwitterStatus;

public class TwitterWallPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(TwitterWallPanel.class);

	private static final String ENTER_BUTTON_LABEL_DEFAULT_TEXT_SIZE = "3";

	private static final String ENTER_BUTTON_LABEL_NUMBER_TEXT_SIZE = "5";

	private static final String DIRECT_MESSAGES_TWITTER_TOGGLE_BUTTON = "directMessagesTwitterToggleButton";

	private static final String MENTIONS_TWITTER_TOGGLE_BUTTON = "mentionsTwitterToggleButton";

	private static final String HOME_TWITTER_TOGGLE_BUTTON = "homeTwitterToggleButton";

	private static final Dimension BUTTON_PREFERED_SIZE = new Dimension(20, 20);

	private static final Dimension CONTROL_PANEL_DEFAULT_SIZE = new Dimension(100, 30);

	private static final Dimension ENTER_BUTTON_DEFAULT_SIZE = new Dimension(50, 50);

	private static final Dimension HEADER_PANEL_DEFAULT_SIZE = new Dimension(560, 30);

	private static final Dimension MESSAGE_LABEL_DEFAULT_SIZE = new Dimension(248, 16);

	private static final Dimension NAME_PANEL_DEFAULT_SIZE = new Dimension(402, 30);

	private static final Dimension SCROLL_PANE_DEFAULT_SIZE = new Dimension(200, 160);

	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(310, 2);

	private static final Dimension TEXT_FIELD_PANEL_DEFAULT_SIZE = new Dimension(284, 50);

	private static final Dimension WALL_PANEL_DEFAULT_SIZE = new Dimension(560, 168);

	private static final Dimension WALL_PANEL_MINIMUM_SIZE = new Dimension(310, 168);

	private static final Border TEXT_AREA_BORDER = BorderFactory.createEmptyBorder(3, 6, 6, 6);

	private static final Insets MESSAGE_LABEL_INSETS = new Insets(4, 14, 4, 14);

	private static final Insets TEXT_FIELD_PANEL_INSETS = new Insets(0, 14, 8, 14);

	private static final Rectangle CLOSE_BUTTON_BOUNDS = new Rectangle(74, 8, 14, 14);

	private static final Rectangle PROFILE_NAME_LABEL_BOUNDS = new Rectangle(14, 0, 300, 30);

	private static final String ENTER_BUTTON_NAME = "enterWallTextProfileButton";

	private static final String PANEL_NAME = "profileWallBackgroundPanel";

	private static final String TEXT_AREA_PANEL_NAME = "wallTextArea";

	private static final String INVALID_TEXT_AREA_PANEL_NAME = "invalidWallTextArea";

	private static final String CLOSE_BUTTON_NAME = "closeLibraryButton";

	private static final int DEFAULT_TEXT_AREA_LINE_HEIGHT = 19;

	private final Map<DecoratedTwitterStatus, TwitterStatusPanel> cachedPanels = new HashMap<DecoratedTwitterStatus, TwitterStatusPanel>();

	private final ExecutorService tweetExecutor = Executors.newSingleThreadExecutor();

	private final MouseListener retweetListener = new RetweetListener();

	private final MouseListener replyTweetListener = new ReplyTweetListener();

	private final MouseListener directListener = new DirectTweetListener();

	private final MouseListener userClickedListener = new UserClickedListener();

	private JPanel footerPanel;

	private JPanel wallPanel;

	private JSeparator bottomSeparator;

	private Timer inputTimer;

	private JPanel headerPanel;

	private JLabel messageLabel;

	private JPanel textFieldPanel;

	private JSeparator topSeparator;

	private JButton enterButton;

	private JScrollPane scrollPane;

	private JPanel messageContainer;

	private JPanel textAreaPanel;

	private JScrollPane textAreaScrollPane;

	private JTextArea textArea;

	private JPanel namePanel;

	private JPanel controlPanel;

	private JButton closeButton;

	private JLabel profileNameLabel;

	private final Messages messages;

	private final TwitterStatusDateComparator twitterStatusDateComparator = new TwitterStatusDateComparator();

	private final TwitterActionListener twitterActionListener;

	private TwitterProfile currentProfile;

	private List<DecoratedTwitterStatus> currentTimeline = new ArrayList<DecoratedTwitterStatus>();

	private Log log = LogFactory.getLog(this.getClass());

	private JToggleButton directMessagesButton;

	private JToggleButton mentionsButton;

	private JToggleButton homeButton;

	public TwitterWallPanel(TwitterActionListener twitterListener, Messages messages) {
		this.twitterActionListener = twitterListener;
		this.messages = messages;
		initialize();
		internationalize(messages);
		bindActions();
	}

	private void bindActions() {
		getCloseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				twitterActionListener.onHome();
			}
		});
	}

	public synchronized void showHome(List<DecoratedTwitterStatus> newTimeline, TwitterProfile userProfile) {
		if (userProfile.isLoggedInUser() || !userProfile.equals(currentProfile)) {

			Collections.sort(newTimeline, twitterStatusDateComparator);

			currentProfile = userProfile;
			currentTimeline = new ArrayList<DecoratedTwitterStatus>(newTimeline);
			
			setButtonsVisibility(currentProfile.isLoggedInUser());
			setTitleWith(currentProfile.getScreenName());
			
			switchTimeline();
			
			getHomeButton().setSelected(true);
		}
	}

	public synchronized void showMentionsOrDirectMessages(List<DecoratedTwitterStatus> newTimeline, String screenName) {
		Collections.sort(newTimeline, twitterStatusDateComparator);
		currentTimeline = new ArrayList<DecoratedTwitterStatus>(newTimeline);

		setButtonsVisibility(true);
		setTitleWith(screenName);

		switchTimeline();
	}

	private void setTitleWith(String screenName) {
		getProfileNameLabel().setText(messages.getMessage("twitter.wall.titlePanel", screenName));
	}

	private void setButtonsVisibility(boolean visible) {
		getCloseButton().setVisible(!visible);
		getTextFieldPanel().setVisible(visible);
		getHomeButton().setVisible(visible);
		getMentionsButton().setVisible(visible);
		getDirectMessagesButton().setVisible(visible);
	}

	public synchronized void updateTimeLine(List<DecoratedTwitterStatus> newTimeline, TwitterProfile userProfile) {
		Collections.sort(newTimeline, twitterStatusDateComparator);
		updateTimeline(newTimeline);
		getScrollPane().revalidate();
	}

	private void switchTimeline() {
		getMessageContainer().removeAll();
		getMessageContainer().validate();
		for (DecoratedTwitterStatus twitterStatus : currentTimeline) {
			getMessageContainer().add(getTwitterStatusPanel(twitterStatus));
			getMessageContainer().validate();
		}
		getScrollPane().revalidate();
	}

	private void updateTimeline(List<DecoratedTwitterStatus> newTimeline) {
		List<DecoratedTwitterStatus> added = new ArrayList<DecoratedTwitterStatus>(newTimeline);
		added.removeAll(currentTimeline);
		List<DecoratedTwitterStatus> removed = new ArrayList<DecoratedTwitterStatus>(currentTimeline);
		removed.removeAll(newTimeline);
		for (TwitterStatus twitterStatus : removed) {
			int index = currentTimeline.indexOf(twitterStatus);
			TwitterStatusPanel panel = (TwitterStatusPanel) getMessageContainer().getComponent(index);
			panel.removeMessages(messages);
			currentTimeline.remove(index);
			getMessageContainer().remove(index);
		}
		Collections.sort(added, twitterStatusDateComparator);
		currentTimeline.addAll(added);
		Collections.sort(currentTimeline, twitterStatusDateComparator);
		for (DecoratedTwitterStatus twitterStatus : added) {
			getMessageContainer().add(getTwitterStatusPanel(twitterStatus), currentTimeline.indexOf(twitterStatus));
			getMessageContainer().validate();
		}
	}

	private TwitterStatusPanel getTwitterStatusPanel(DecoratedTwitterStatus twitterStatus) {
		if (!cachedPanels.containsKey(twitterStatus)) {
			long startTime = System.currentTimeMillis();
			TwitterStatusPanel statusPanel = new TwitterStatusPanel(twitterStatus, messages);
			long creationTime = System.currentTimeMillis() - startTime;
			if (creationTime > 300) {
				LOG.warn("Creation of twitterStatus took " + creationTime + " ms.\n" + twitterStatus.getText());
			}
			statusPanel.addReplyTweetListener(replyTweetListener);
			statusPanel.addRetweetListener(retweetListener);
			statusPanel.addDirectListener(directListener);
			statusPanel.addUserClickedListener(userClickedListener);
			cachedPanels.put(twitterStatus, statusPanel);
		}
		return cachedPanels.get(twitterStatus);
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setName(PANEL_NAME);
		this.add(getHeaderPanel(), BorderLayout.NORTH);
		this.add(getWallPanel(), BorderLayout.CENTER);
		this.add(getFooterPanel(), BorderLayout.SOUTH);
	}

	private JPanel getWallPanel() {
		if (wallPanel == null) {
			wallPanel = new JPanel();
			wallPanel.setLayout(new GridBagLayout());
			wallPanel.setPreferredSize(WALL_PANEL_DEFAULT_SIZE);
			wallPanel.setMinimumSize(WALL_PANEL_MINIMUM_SIZE);
			GridBagConstraints messageLabelConstraints = new GridBagConstraints();
			messageLabelConstraints.gridx = 0;
			messageLabelConstraints.gridy = 0;
			messageLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			messageLabelConstraints.weightx = 1.0;
			messageLabelConstraints.insets = MESSAGE_LABEL_INSETS;

			GridBagConstraints textFieldPanelConstraints = new GridBagConstraints();
			textFieldPanelConstraints.gridx = 0;
			textFieldPanelConstraints.gridy = 1;
			textFieldPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			textFieldPanelConstraints.weightx = 1.0;
			textFieldPanelConstraints.insets = TEXT_FIELD_PANEL_INSETS;

			GridBagConstraints topSeparatorConstraints = new GridBagConstraints();
			topSeparatorConstraints.gridx = 0;
			topSeparatorConstraints.gridy = 2;
			topSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			topSeparatorConstraints.weightx = 1.0;

			GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
			scrollPaneConstraints.gridx = 0;
			scrollPaneConstraints.gridy = 3;
			scrollPaneConstraints.fill = GridBagConstraints.BOTH;
			scrollPaneConstraints.weightx = 1.0;
			scrollPaneConstraints.weighty = 1.0;
			scrollPaneConstraints.insets = new Insets(0, 0, 0, 0);

			GridBagConstraints bottomSeparatorConstraints = new GridBagConstraints();
			bottomSeparatorConstraints.gridx = 0;
			bottomSeparatorConstraints.gridy = 4;
			bottomSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			bottomSeparatorConstraints.weightx = 1.0;

			wallPanel.add(getMessageLabel(), messageLabelConstraints);
			wallPanel.add(getTextFieldPanel(), textFieldPanelConstraints);
			wallPanel.add(getTopSeparator(), topSeparatorConstraints);
			wallPanel.add(getScrollPane(), scrollPaneConstraints);
			wallPanel.add(getBottomSeparator(), bottomSeparatorConstraints);
		}
		return wallPanel;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setSize(MESSAGE_LABEL_DEFAULT_SIZE);
			messageLabel.setPreferredSize(MESSAGE_LABEL_DEFAULT_SIZE);
			messageLabel.setMaximumSize(MESSAGE_LABEL_DEFAULT_SIZE);
			messageLabel.setMinimumSize(MESSAGE_LABEL_DEFAULT_SIZE);
		}
		return messageLabel;
	}

	private JPanel getTextFieldPanel() {
		if (textFieldPanel == null) {
			textFieldPanel = new JPanel();
			textFieldPanel.setMinimumSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textFieldPanel.setPreferredSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textFieldPanel.setSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textFieldPanel.setLayout(new BorderLayout());
			textFieldPanel.add(getEnterButton(), BorderLayout.EAST);
			textFieldPanel.add(getTextAreaPanel(), BorderLayout.CENTER);
		}
		return textFieldPanel;
	}

	private JButton getEnterButton() {
		if (enterButton == null) {
			enterButton = new JButton();
			enterButton.setSize(ENTER_BUTTON_DEFAULT_SIZE);
			enterButton.setPreferredSize(ENTER_BUTTON_DEFAULT_SIZE);
			enterButton.setMaximumSize(ENTER_BUTTON_DEFAULT_SIZE);
			enterButton.setMinimumSize(ENTER_BUTTON_DEFAULT_SIZE);
			enterButton.setName(ENTER_BUTTON_NAME);
			enterButton.setEnabled(false);
			enterButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					tweetExecutor.execute(new Runnable() {
						@Override
						public void run() {
							twitterActionListener.onTwitterStatusUpdated(getTextArea().getText());
						}

					});
				}
			});
			enterButton.setForeground(Color.WHITE);
		}
		return enterButton;
	}

	private JPanel getTextAreaPanel() {
		if (textAreaPanel == null) {
			textAreaPanel = new JPanel(new BorderLayout());
			textAreaPanel.setPreferredSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textAreaPanel.setMaximumSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textAreaPanel.setMinimumSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textAreaPanel.setSize(TEXT_FIELD_PANEL_DEFAULT_SIZE);
			textAreaPanel.add(getTextAreaScrollPane(), BorderLayout.CENTER);

		}
		return textAreaPanel;
	}

	private JScrollPane getTextAreaScrollPane() {
		if (textAreaScrollPane == null) {
			textAreaScrollPane = new JScrollPane();
			textAreaScrollPane.getViewport().setName(TEXT_AREA_PANEL_NAME);
			textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			textAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			textAreaScrollPane.setViewportView(getTextArea());
			// hack to fix last line in text area when autoscrolling...
			textAreaScrollPane.getViewport().addChangeListener(new ChangeListener() {

				private AtomicBoolean fixedScroll = new AtomicBoolean(false);

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (!fixedScroll.get() && textArea.getHeight() > TEXT_FIELD_PANEL_DEFAULT_SIZE.getHeight()) {
						Rectangle visibleRect = textAreaScrollPane.getViewport().getViewRect();
						Rectangle newVisibleRect = null;
						if (textArea.getHeight() < TEXT_FIELD_PANEL_DEFAULT_SIZE.getHeight() + DEFAULT_TEXT_AREA_LINE_HEIGHT) {
							newVisibleRect = new Rectangle(visibleRect.x, 0, visibleRect.width, visibleRect.height);
						} else {
							newVisibleRect = new Rectangle(visibleRect.x, visibleRect.y + 1, visibleRect.width, visibleRect.height);
						}
						fixedScroll.set(true);
						textArea.scrollRectToVisible(newVisibleRect);
					} else {
						fixedScroll.set(false);
					}
				}
			});
		}
		return textAreaScrollPane;
	}

	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setBorder(TEXT_AREA_BORDER);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEnabled(true);
			textArea.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			textArea.addKeyListener(new CopyPasteKeyAdapterForMac());
			textArea.getDocument().addDocumentListener(new TwitterStatusListener());
			((AbstractDocument) textArea.getDocument()).setDocumentFilter(new TwitterDocumentFilter());
			textArea.addKeyListener(new SpacerKeyListener());
		}
		return textArea;
	}

	private JSeparator getTopSeparator() {
		if (topSeparator == null) {
			topSeparator = new JSeparator();
			topSeparator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
			topSeparator.setSize(SEPARATOR_DEFAULT_SIZE);
			topSeparator.setMaximumSize(SEPARATOR_DEFAULT_SIZE);
			topSeparator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		}
		return topSeparator;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(SCROLL_PANE_DEFAULT_SIZE);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setViewportView(getMessageContainer());
			scrollPane.getViewport().setBackground(new Color(252, 252, 252));
		}
		return scrollPane;
	}

	private JPanel getMessageContainer() {
		if (messageContainer == null) {
			messageContainer = new MessageContainerPanel();
		}
		return messageContainer;
	}

	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new JPanel();
			headerPanel.setLayout(new BorderLayout());
			headerPanel.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setMaximumSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setMinimumSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.add(getNamePanel(), BorderLayout.CENTER);
			headerPanel.add(getControlPanel(), BorderLayout.EAST);
		}
		return headerPanel;
	}

	private JPanel getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JPanel();
			controlPanel.setLayout(null);
			controlPanel.setPreferredSize(CONTROL_PANEL_DEFAULT_SIZE);
			controlPanel.add(getCloseButton());
		}
		return controlPanel;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setName(CLOSE_BUTTON_NAME);
			closeButton.setBounds(CLOSE_BUTTON_BOUNDS);
			closeButton.setVisible(false);
		}
		return closeButton;
	}

	private JPanel getNamePanel() {
		if (namePanel == null) {
			namePanel = new JPanel();
			namePanel.setLayout(null);
			namePanel.setPreferredSize(NAME_PANEL_DEFAULT_SIZE);
			namePanel.setSize(NAME_PANEL_DEFAULT_SIZE);
			namePanel.setMinimumSize(CONTROL_PANEL_DEFAULT_SIZE);
			namePanel.add(getProfileNameLabel());
		}
		return namePanel;
	}

	private JLabel getProfileNameLabel() {
		if (profileNameLabel == null) {
			profileNameLabel = new JLabel();
			profileNameLabel.setBounds(PROFILE_NAME_LABEL_BOUNDS);
			profileNameLabel.setName(SynthFonts.BOLD_FONT14_PURPLE87_63_106);
		}
		return profileNameLabel;
	}

	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 5));
			footerPanel.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.setMaximumSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.setSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.setMinimumSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.add(getHomeButton());
			footerPanel.add(getMentionsButton());
			footerPanel.add(getDirectMessagesButton());

			ButtonGroup twitterButtonGroup = new ButtonGroup();
			twitterButtonGroup.add(getHomeButton());
			twitterButtonGroup.add(getMentionsButton());
			twitterButtonGroup.add(getDirectMessagesButton());
		}
		return footerPanel;
	}

	private JToggleButton getDirectMessagesButton() {
		if (directMessagesButton == null) {
			directMessagesButton = new JToggleButton();
			directMessagesButton.setName(DIRECT_MESSAGES_TWITTER_TOGGLE_BUTTON);
			directMessagesButton.setPreferredSize(BUTTON_PREFERED_SIZE);
			directMessagesButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					twitterActionListener.onDirectMessages();
				}
			});
		}
		return directMessagesButton;
	}

	private JToggleButton getMentionsButton() {
		if (mentionsButton == null) {
			mentionsButton = new JToggleButton();
			mentionsButton.setName(MENTIONS_TWITTER_TOGGLE_BUTTON);
			mentionsButton.setPreferredSize(BUTTON_PREFERED_SIZE);
			mentionsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					twitterActionListener.onMentions();
				}
			});
		}
		return mentionsButton;
	}

	private JToggleButton getHomeButton() {
		if (homeButton == null) {
			homeButton = new JToggleButton();
			homeButton.setName(HOME_TWITTER_TOGGLE_BUTTON);
			homeButton.setPreferredSize(BUTTON_PREFERED_SIZE);
			homeButton.setSelected(true);
			homeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					twitterActionListener.onHome();
				}
			});
		}
		return homeButton;
	}

	private JSeparator getBottomSeparator() {
		if (bottomSeparator == null) {
			bottomSeparator = new JSeparator();
			bottomSeparator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
			bottomSeparator.setSize(SEPARATOR_DEFAULT_SIZE);
			bottomSeparator.setMaximumSize(SEPARATOR_DEFAULT_SIZE);
			bottomSeparator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		}
		return bottomSeparator;
	}

	@Override
	public void internationalize(Messages messages) {
		getEnterButton().setText(
				messages.getMessage("profile.wall.enterButton", ENTER_BUTTON_LABEL_DEFAULT_TEXT_SIZE,
						messages.getMessage("profile.wall.send")));
		if (currentProfile != null) {
			getProfileNameLabel().setText(messages.getMessage("twitter.wall.titlePanel", currentProfile.getScreenName()));
		}
		getMessageLabel().setText(messages.getMessage("twitter.wall.leavemessage"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	private void validateMessage() {
		if (inputTimer != null) {
			inputTimer.cancel();
		}
		if (getTextArea().getText().length() == 0) {
			getEnterButton().setEnabled(false);
			getEnterButton().setForeground(Color.WHITE);
			getEnterButton().setText(
					messages.getMessage("profile.wall.enterButton", ENTER_BUTTON_LABEL_DEFAULT_TEXT_SIZE,
							messages.getMessage("profile.wall.send")));
			getTextAreaScrollPane().getViewport().setName(TEXT_AREA_PANEL_NAME);
		} else {
			int remainingChars = TwitterUtil.getRemainingChars(getTextArea().getText());
			getEnterButton().setText(
					messages.getMessage("profile.wall.enterButton", ENTER_BUTTON_LABEL_NUMBER_TEXT_SIZE, "" + remainingChars));
			if (remainingChars < 0) {
				getEnterButton().setEnabled(false);
				getEnterButton().setForeground(Color.RED);
				getTextAreaScrollPane().getViewport().setName(INVALID_TEXT_AREA_PANEL_NAME);
			} else {
				getEnterButton().setEnabled(true);
				getEnterButton().setForeground(Color.WHITE);
				getTextAreaScrollPane().getViewport().setName(TEXT_AREA_PANEL_NAME);
			}
		}
		getTextAreaScrollPane().getViewport().repaint();
		inputTimer = new Timer(EnterButtonTimerTask.NAME);
		inputTimer.schedule(new EnterButtonTimerTask(), EnterButtonTimerTask.INPUT_DELAY);
	}

	private final class TwitterStatusListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			validateMessage();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			validateMessage();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			validateMessage();
		}
	}

	private final class EnterButtonTimerTask extends TimerTask {
		public static final String NAME = "WALL ENTER BUTTON TIMER TASK";
		public static final long INPUT_DELAY = 500;

		@Override
		public void run() {
			if (getEnterButton().isEnabled()) {
				getEnterButton().setText(
						messages.getMessage("profile.wall.enterButton", ENTER_BUTTON_LABEL_DEFAULT_TEXT_SIZE,
								messages.getMessage("profile.wall.send")));
			}
		}
	}

	private final class TwitterDocumentFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			if (string == null) {
				return;
			} else {
				replace(fb, offset, 0, string, attr);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			replace(fb, offset, length, "", null);
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int selectedLength, String text, AttributeSet attrs)
				throws BadLocationException {
			text = text.replaceAll("\\\\n", " ");
			fb.replace(offset, selectedLength, text, attrs);
		}
	}

	private final class RetweetListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			Object source = e.getSource();
			if (source instanceof TwitterStatusButton) {
				TwitterStatusButton statusButton = (TwitterStatusButton) source;
				twitterActionListener.onRetweetedStatus(statusButton.getTwitterStatus());
			} else {
				LOG.warn("An retweet event was received from a component different than TwitterStatusButton: " + source);
			}
		}
	}

	private final class ReplyTweetListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			Object source = e.getSource();
			if (source instanceof TwitterStatusButton) {
				TwitterStatusButton statusButton = (TwitterStatusButton) source;
				twitterActionListener.onTwitterStatustReplied(statusButton.getTwitterStatus());
			} else {
				LOG.warn("An retweet event was received from a component different than TwitterStatusButton: " + source);
			}
		}

	}

	private final class DirectTweetListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			log.debug("DirectTweetListener");
			Object source = e.getSource();
			if (source instanceof TwitterStatusButton) {
				TwitterStatusButton statusButton = (TwitterStatusButton) source;
				twitterActionListener.onTwitterStatustDirect(statusButton.getTwitterStatus());
			} else {
				LOG.warn("An retweet event was received from a component different than TwitterStatusButton: " + source);
			}
		}

	}

	private final class UserClickedListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			TwitterUserLabel userLabel = (TwitterUserLabel) e.getSource();
			twitterActionListener.onUserProfileRequested(userLabel.getScreenName());
		}
	}

	private final class TwitterStatusDateComparator implements Comparator<TwitterStatus> {

		@Override
		public int compare(TwitterStatus ts1, TwitterStatus ts2) {
			return ts2.getDateCreatedAt().compareTo(ts1.getDateCreatedAt());
		}

	}

	public void setStatus(String status) {
		getTextArea().setText(status);
	}

	protected void disableTextArea() {
		textArea.setEditable(false);
		enterButton.setEnabled(false);
	}

	protected void enableTextArea(boolean deleteText) {
		textArea.setEditable(true);
		enterButton.setEnabled(true);
		if (deleteText) {
			textArea.setText("");
			textArea.requestFocus();
		}
	}

}
