package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.feeds.FeedViewFactory;
import com.all.core.actions.Actions;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public class ProfileCardPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension BUTTON_PANEL_DEFAULT_SIZE = new Dimension(39, 24);

	private static final Dimension BLANK_PANEL_DEFAULT_SIZE = new Dimension(14, 32);

	private static final Dimension CONTROL_FOOTER_PANEL_DEFAULT_SIZE = new Dimension(102, 30);

	private static final Dimension FOOTER_PANEL_DEFAULT_SIZE = new Dimension(Integer.MAX_VALUE, 32);

	private static final Dimension HEADER_LABEL_DEFAULT_SIZE = new Dimension(80, 32);

	private static final Dimension HEADER_PANEL_DEFAULT_SIZE = new Dimension(Integer.MAX_VALUE, 32);

	private static final Dimension LABELS_PANEL_DEFAULT_SIZE = new Dimension(Integer.MAX_VALUE, 32);

	private static final Dimension MAIN_PANEL_DEFAULT_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

	private static final Dimension RELOAD_BUTTON_DEFAULT_SIZE = new Dimension(15, 15);

	private static final Dimension SEPARATOR_PANEL_DEFAULT_SIZE = new Dimension(8, 8);

	private static final FlowLayout LABELS_PANEL_LAYOUT = new FlowLayout(FlowLayout.LEFT, 0, 0);

	private static final Rectangle NEXT_BUTTON_BOUNDS = new Rectangle(70, 4, 20, 22);

	private static final Rectangle NUMBER_OF_PAGES_LABEL_BOUNDS = new Rectangle(22, 4, 50, 22);

	private static final Rectangle PREVIOUS_BUTTON_BOUNDS = new Rectangle(0, 4, 20, 22);

	private static final String FEED_BUTTON_NAME = "profileFriendsFeedButton";

	private static final String FEED_PANEL_NAME = "feedPanel";

	private static final String FOOTER_PANEL_NAME = "socialFooterPanelBackground";

	private static final String FRIENDS_PANEL_NAME = "friendsPanel";

	private static final String HEADER_PANEL_NAME = "socialHeaderPanelBackground";

	private static final String NEXT_BUTTON_NAME = "nextButtonProfile";

	private static final String PAGE_SEPARATOR = "/";

	private static final String PREVIOUS_BUTTON_NAME = "previousButtonProfile";

	private static final String RELOAD_BUTTON_NAME = "reloadButton";

	private static final String SEPARATOR_PANEL_NAME = "separatorSocialPanelBackground";

	private FriendsPanel friendsPanel;

	private JLabel numberOfPagesLabel;

	private JButton reloadButton;

	private JButton previousButton;

	private JButton nextButton;

	private JToggleButton feedsToggleButton;

	private JToggleButton friendsToggleButton;

	private JPanel footerPanel;

	private JPanel headerPanel;

	private JPanel mainPanel;

	private JPanel labelsPanel;

	private JPanel buttonPanel;

	private JPanel controlFooterPanel;

	private final Messages messages;

	private ViewEngine viewEngine;

	private final DialogFactory dialogFactory;

	private FeedPanel feedPanel;

	private ButtonGroup buttonGroup;

	private PaginationFriendsPanelManager paginationManager;

	private JPanel separatorPanel;

	private JPanel blankPanel;

	private final FeedViewFactory feedViewFactory;

	public ProfileCardPanel(Messages messages, DialogFactory dialogFactory, FeedViewFactory feedViewFactory) {
		this.messages = messages;
		this.dialogFactory = dialogFactory;
		this.feedViewFactory = feedViewFactory;
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		buttonGroup = new ButtonGroup();
		this.add(getHeaderPanel(), BorderLayout.NORTH);
		this.add(getMainPanel(), BorderLayout.CENTER);
		this.add(getFooterPanel(), BorderLayout.SOUTH);
		internationalize(messages);
		buttonGroup.setSelected(getFeedsToggleButton().getModel(), true);
	}

	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setLayout(new BorderLayout());
			footerPanel.setName(FOOTER_PANEL_NAME);
			footerPanel.setPreferredSize(FOOTER_PANEL_DEFAULT_SIZE);
			footerPanel.setMaximumSize(FOOTER_PANEL_DEFAULT_SIZE);
			footerPanel.setMinimumSize(FOOTER_PANEL_DEFAULT_SIZE);
			footerPanel.add(new JPanel(), BorderLayout.CENTER);
			footerPanel.add(getFooterControlPanel(), BorderLayout.EAST);
		}
		return footerPanel;
	}

	private JPanel getFooterControlPanel() {
		if (controlFooterPanel == null) {
			controlFooterPanel = new JPanel();
			controlFooterPanel.setLayout(null);
			controlFooterPanel.setVisible(false);
			controlFooterPanel.setPreferredSize(CONTROL_FOOTER_PANEL_DEFAULT_SIZE);
			controlFooterPanel.setSize(CONTROL_FOOTER_PANEL_DEFAULT_SIZE);
			controlFooterPanel.setMinimumSize(CONTROL_FOOTER_PANEL_DEFAULT_SIZE);
			controlFooterPanel.setMaximumSize(CONTROL_FOOTER_PANEL_DEFAULT_SIZE);
			controlFooterPanel.add(getPreviousButton());
			controlFooterPanel.add(getNumberOfPagesLabel());
			controlFooterPanel.add(getNextButton());
		}
		return controlFooterPanel;
	}

	private JButton getPreviousButton() {
		if (previousButton == null) {
			previousButton = new JButton();
			previousButton.setBounds(new Rectangle(PREVIOUS_BUTTON_BOUNDS));
			previousButton.setName(PREVIOUS_BUTTON_NAME);
			previousButton.setEnabled(paginationManager.isPrevPage());
			previousButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					friendsPanel.getScrollPane().fillFriendsContainer(paginationManager.getPrevFriendsPage());
					previousButton.setEnabled(paginationManager.isPrevPage());
					nextButton.setEnabled(paginationManager.isNextPage());
					numberOfPagesLabel.setText(paginationManager.getPage() + PAGE_SEPARATOR
							+ paginationManager.getNumberOfPages());
					friendsPanel.updateScrollPaneSize();
				}
			});
		}
		return previousButton;
	}

	private JLabel getNumberOfPagesLabel() {
		if (numberOfPagesLabel == null) {
			numberOfPagesLabel = new JLabel();
			numberOfPagesLabel.setBounds(NUMBER_OF_PAGES_LABEL_BOUNDS);
			numberOfPagesLabel.setHorizontalAlignment(SwingConstants.CENTER);
			int numberOfPages = paginationManager.getNumberOfPages() > 0 ? paginationManager.getNumberOfPages() : 1;
			numberOfPagesLabel.setText(paginationManager.getPage() + PAGE_SEPARATOR + numberOfPages);
		}
		return numberOfPagesLabel;
	}

	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setBounds(new Rectangle(NEXT_BUTTON_BOUNDS));
			nextButton.setName(NEXT_BUTTON_NAME);
			nextButton.setEnabled(paginationManager.isNextPage());
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<ContactInfo> nextFriendsPage = paginationManager.getNextFriendsPage();
					friendsPanel.getScrollPane().fillFriendsContainer(nextFriendsPage);
					nextButton.setEnabled(paginationManager.isNextPage());
					previousButton.setEnabled(paginationManager.isPrevPage());
					numberOfPagesLabel.setText(paginationManager.getPage() + PAGE_SEPARATOR
							+ paginationManager.getNumberOfPages());
					friendsPanel.updateScrollPaneSize();
				}
			});
		}
		return nextButton;
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new CardLayout());
			mainPanel.setPreferredSize(MAIN_PANEL_DEFAULT_SIZE);
			mainPanel.add(getFeedPanel(), FEED_PANEL_NAME);
			mainPanel.add(getFriendsPanel(), FRIENDS_PANEL_NAME);
		}
		return mainPanel;
	}

	private FeedPanel getFeedPanel() {
		if (feedPanel == null) {
			feedPanel = new FeedPanel(feedViewFactory, messages);
		}
		return feedPanel;
	}

	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new JPanel();
			headerPanel.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setName(HEADER_PANEL_NAME);

			headerPanel.setLayout(new GridBagLayout());

			GridBagConstraints labelsPanelConstraints = new GridBagConstraints();
			labelsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			labelsPanelConstraints.weightx = 1.0;

			GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
			buttonPanelConstraints.gridx = 1;

			headerPanel.add(getLabelsPanel(), labelsPanelConstraints);
			headerPanel.add(getButtonPanel(), buttonPanelConstraints);
		}
		return headerPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setPreferredSize(SEPARATOR_PANEL_DEFAULT_SIZE);
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
		}
		return separatorPanel;
	}

	private JPanel getLabelsPanel() {
		if (labelsPanel == null) {
			labelsPanel = new JPanel();
			labelsPanel.setLayout(LABELS_PANEL_LAYOUT);
			labelsPanel.setPreferredSize(LABELS_PANEL_DEFAULT_SIZE);
			labelsPanel.add(getBlankPanel());
			labelsPanel.add(getFeedsToggleButton());
			labelsPanel.add(getSeparatorPanel());
			labelsPanel.add(getFriendsToggleButton());
			buttonGroup.add(getFeedsToggleButton());
			buttonGroup.add(getFriendsToggleButton());
		}
		return labelsPanel;
	}

	private JPanel getBlankPanel() {
		if (blankPanel == null) {
			blankPanel = new JPanel();
			blankPanel.setPreferredSize(BLANK_PANEL_DEFAULT_SIZE);
		}
		return blankPanel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setPreferredSize(BUTTON_PANEL_DEFAULT_SIZE);
			buttonPanel.setMaximumSize(BUTTON_PANEL_DEFAULT_SIZE);
			buttonPanel.setMinimumSize(BUTTON_PANEL_DEFAULT_SIZE);
			buttonPanel.add(getReloadButton());
		}
		return buttonPanel;
	}

	private JToggleButton getFeedsToggleButton() {
		if (feedsToggleButton == null) {
			feedsToggleButton = new JToggleButton();
			feedsToggleButton.setPreferredSize(HEADER_LABEL_DEFAULT_SIZE);
			feedsToggleButton.setName(FEED_BUTTON_NAME);
			feedsToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
			feedsToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					changePanel(FEED_PANEL_NAME);
					reloadButton.setVisible(true);
					controlFooterPanel.setVisible(false);
				}
			});
		}
		return feedsToggleButton;
	}

	private JToggleButton getFriendsToggleButton() {
		if (friendsToggleButton == null) {
			friendsToggleButton = new JToggleButton();
			friendsToggleButton.setPreferredSize(HEADER_LABEL_DEFAULT_SIZE);
			friendsToggleButton.setName(FEED_BUTTON_NAME);
			friendsToggleButton.setHorizontalAlignment(SwingConstants.RIGHT);
			friendsToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					changePanel(FRIENDS_PANEL_NAME);
					reloadButton.setVisible(false);
					controlFooterPanel.setVisible(true);
				}
			});
		}
		return friendsToggleButton;
	}

	private JButton getReloadButton() {
		if (reloadButton == null) {
			reloadButton = new JButton();
			reloadButton.setPreferredSize(RELOAD_BUTTON_DEFAULT_SIZE);
			reloadButton.setName(RELOAD_BUTTON_NAME);
			reloadButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.send(Actions.Feeds.REQUEST_LAST_FEEDS);
				}
			});
		}
		return reloadButton;
	}

	private FriendsPanel getFriendsPanel() {
		if (friendsPanel == null) {
			friendsPanel = new FriendsPanel(viewEngine, dialogFactory);
			paginationManager = friendsPanel.getPaginationManager();
		}
		return friendsPanel;
	}

	private void changePanel(String panelName) {
		CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
		cardLayout.show(mainPanel, panelName);
	}

	@Override
	public void internationalize(Messages messages) {
		feedsToggleButton.setText(messages.getMessage("profile.feeds.title"));
		friendsToggleButton.setText(messages.getMessage("profile.friends.title"));
		getNextButton().setToolTipText(messages.getMessage("profile.next.button.tooltip"));
		getPreviousButton().setToolTipText(messages.getMessage("profile.previous.button.tooltip"));
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		getFeedPanel().destroy(viewEngine);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
		getFeedPanel().initialize(viewEngine);
	}
}