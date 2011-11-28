package com.all.client.view.toolbar.users;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.toolbar.social.FriendsScrollPane;
import com.all.client.view.toolbar.social.PaginationFriendsPanelManager;
import com.all.core.actions.Actions;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.shared.model.ContactInfo;

public final class FriendsMainPanel extends JPanel implements Internationalizable {

	private static final Log LOG = LogFactory.getLog(FriendsMainPanel.class);
	
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_PAGE_SIZE = 100;

	private static final Insets LABEL_BROWSE_INSETS = new Insets(0, 10, 0, 0);

	private static final Dimension CONTROL_FOOTER_PANEL_DEFAULT_SIZE = new Dimension(102, 30);

	private static final Dimension FRIENDS_PANEL_DEFAULT_SIZE = new Dimension(560, 168);

	private static final Dimension FRIENDS_PANEL_MINIMUM_SIZE = new Dimension(310, 168);

	private static final Dimension HEADER_PANEL_DEFAULT_SIZE = new Dimension(818, 30);

	private static final Dimension HEADER_PANEL_MINIMUM_SIZE = new Dimension(594, 30);

	private static final Dimension LABEL_BROWSE_DEFAULT_SIZE = new Dimension(70, 30);

	private static final Dimension PANEL_DEFAULT_SIZE = new Dimension(110, 30);

	private static final Dimension PANEL_MAXIMUM_SIZE = new Dimension(200, 30);

	private static final Dimension PANEL_MINIMUM_SIZE = new Dimension(60, 30);

	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(818, 2);

	private static final Insets SCROLL_PANE_INSETS = LABEL_BROWSE_INSETS;

	private static final Rectangle NEXT_BUTTON_BOUNDS = new Rectangle(70, 4, 20, 22);

	private static final Rectangle NUMBER_OF_PAGES_LABEL_BOUNDS = new Rectangle(22, 4, 50, 22);

	private static final Rectangle PREVIOUS_BUTTON_BOUNDS = new Rectangle(0, 4, 20, 22);

	private static final String NEXT_BUTTON_NAME = "nextButtonProfile";

	private static final String PANEL_NAME = "profileFriendsBackgroundPanel";

	private static final String PREVIOUS_BUTTON_NAME = "previousButtonProfile";

	private DialogFactory dialogFactory;

	private FriendsScrollPane scrollPane;

	private JButton previousButton;

	private JButton nextButton;

	private JLabel labelBrowse;

	private JLabel numberOfPagesLabel;

	private JPanel headerPanel;

	private JPanel friendsPanel;

	private JPanel footerPanel;

	private JPanel loaderPanel;

	private JSeparator bottomSeparator;

	private JSeparator topSeparator;

	private JPanel controlFooterPanel;

	private JPanel westPanel;

	private JPanel eastPanel;

	private final Observable<ObservValue<String>> onBrowseSelectedEvent = new Observable<ObservValue<String>>();

	private JPanel mainPanel;

	private static final String browseOption = "online";

	private final ViewEngine viewEngine;

	public FriendsMainPanel(DialogFactory dialogFactory, ViewEngine viewEngine, JPanel loaderPanel) {
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		this.loaderPanel = loaderPanel;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setName(PANEL_NAME);
		add(getMainPanel(), BorderLayout.CENTER);
		onBrowseSelectedEvent.add(new Observer<ObservValue<String>>() {
			@Override
			public void observe(ObservValue<String> eventArgs) {
				loaderPanel.setVisible(true);
				viewEngine.request(Actions.Social.REQUEST_ONLINE_USERS, new ResponseCallback<List<ContactInfo>>() {
					@Override
					public void onResponse(List<ContactInfo> response) {
						try {
							fillScrollPane(response);
						} catch (Exception e) {
							LOG.error(e, e);
						} finally {
							loaderPanel.setVisible(false);
						}
					}
				});
			}
		});
		// TODO Fix this, because LetterPanel has necessary functionality but is not
		// even added to this container
		fireEvent();
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getHeaderPanel(), BorderLayout.NORTH);
			mainPanel.add(getFriendsPanel(), BorderLayout.CENTER);
			mainPanel.add(getFooterPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	public JPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new JPanel();
			headerPanel.setLayout(new GridBagLayout());
			headerPanel.setSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setMinimumSize(HEADER_PANEL_MINIMUM_SIZE);
			headerPanel.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);
			GridBagConstraints westPanelConstraints = new GridBagConstraints();
			westPanelConstraints.gridx = 0;
			westPanelConstraints.gridy = 0;
			GridBagConstraints lettersPanelConstraints = new GridBagConstraints();
			lettersPanelConstraints.gridx = 1;
			lettersPanelConstraints.gridy = 0;
			GridBagConstraints eastPanelConstraints = new GridBagConstraints();
			eastPanelConstraints.gridx = 2;
			eastPanelConstraints.gridy = 0;

			headerPanel.add(getWestPanel(), westPanelConstraints);
			headerPanel.add(getEastPanel(), eastPanelConstraints);
		}
		return headerPanel;
	}

	private JPanel getEastPanel() {
		if (eastPanel == null) {
			eastPanel = new JPanel();
			eastPanel.setSize(PANEL_DEFAULT_SIZE);
			eastPanel.setPreferredSize(PANEL_DEFAULT_SIZE);
			eastPanel.setMaximumSize(PANEL_MAXIMUM_SIZE);
			eastPanel.setMinimumSize(PANEL_MINIMUM_SIZE);
		}
		return eastPanel;
	}

	private JPanel getWestPanel() {
		if (westPanel == null) {
			westPanel = new JPanel();
			westPanel.setSize(PANEL_DEFAULT_SIZE);
			westPanel.setPreferredSize(PANEL_DEFAULT_SIZE);
			westPanel.setMaximumSize(PANEL_MAXIMUM_SIZE);
			westPanel.setMinimumSize(PANEL_MINIMUM_SIZE);
		}
		return westPanel;
	}

	private JLabel getLabelBrowse() {
		if (labelBrowse == null) {
			labelBrowse = new JLabel();
			labelBrowse.setSize(LABEL_BROWSE_DEFAULT_SIZE);
			labelBrowse.setPreferredSize(LABEL_BROWSE_DEFAULT_SIZE);
			labelBrowse.setMaximumSize(LABEL_BROWSE_DEFAULT_SIZE);
			labelBrowse.setMinimumSize(LABEL_BROWSE_DEFAULT_SIZE);
		}
		return labelBrowse;
	}

	private JPanel getFriendsPanel() {
		if (friendsPanel == null) {
			friendsPanel = new JPanel();
			friendsPanel.setLayout(new GridBagLayout());
			friendsPanel.setPreferredSize(FRIENDS_PANEL_DEFAULT_SIZE);
			friendsPanel.setMinimumSize(FRIENDS_PANEL_MINIMUM_SIZE);
			GridBagConstraints topSeparatorConstraints = new GridBagConstraints();
			topSeparatorConstraints.gridx = 0;
			topSeparatorConstraints.gridy = 0;
			topSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			topSeparatorConstraints.weightx = 1.0;

			GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
			scrollPaneConstraints.gridx = 0;
			scrollPaneConstraints.gridy = 1;
			scrollPaneConstraints.fill = GridBagConstraints.BOTH;
			scrollPaneConstraints.weightx = 1.0;
			scrollPaneConstraints.weighty = 1.0;
			scrollPaneConstraints.insets = SCROLL_PANE_INSETS;

			GridBagConstraints bottomSeparatorConstraints = new GridBagConstraints();
			bottomSeparatorConstraints.gridx = 0;
			bottomSeparatorConstraints.gridy = 2;
			bottomSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			bottomSeparatorConstraints.weightx = 1.0;

			friendsPanel.add(getTopSeparator(), topSeparatorConstraints);
			friendsPanel.add(getScrollPane(), scrollPaneConstraints);
			friendsPanel.add(getBottomSeparator(), bottomSeparatorConstraints);
		}
		return friendsPanel;
	}

	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setLayout(new BorderLayout());
			footerPanel.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.setMaximumSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.setSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.setMinimumSize(HEADER_PANEL_DEFAULT_SIZE);
			footerPanel.add(new JPanel(), BorderLayout.CENTER);
			footerPanel.add(getFooterControlPanel(), BorderLayout.EAST);
		}
		return footerPanel;
	}

	private JSeparator getTopSeparator() {
		if (topSeparator == null) {
			topSeparator = new JSeparator();
			topSeparator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
			topSeparator.setSize(SEPARATOR_DEFAULT_SIZE);
			topSeparator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		}
		return topSeparator;
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

	private FriendsScrollPane getScrollPane() {
		if (scrollPane == null) {
			// List<ContactInfo> centralizedContacts =
			// contactController.getCentralizedContacts(666666);
			// Collections.sort(centralizedContacts);
			scrollPane = new FriendsScrollPane(dialogFactory, viewEngine);
			scrollPane.onLoadFriendsPanel().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					loaderPanel.setVisible(false);
				}
			});
		}
		return scrollPane;
	}

	private JPanel getFooterControlPanel() {
		if (controlFooterPanel == null) {
			controlFooterPanel = new JPanel();
			controlFooterPanel.setLayout(null);
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
			// previousButton.setEnabled(scrollPane.getPaginationManager().isPrevPage());
			previousButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					scrollPane.fillFriendsContainer(scrollPane.getPaginationManager().getPrevFriendsPage());
					previousButton.setEnabled(scrollPane.getPaginationManager().isPrevPage());
					nextButton.setEnabled(scrollPane.getPaginationManager().isNextPage());
					numberOfPagesLabel.setText(scrollPane.getPaginationManager().getPage() + "/"
							+ scrollPane.getPaginationManager().getNumberOfPages());
					scrollPane.updateSizeFriendsContainerPanel();
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
			// int numberOfPages =
			// scrollPane.getPaginationManager().getNumberOfPages() > 0 ?
			// scrollPane.getPaginationManager().getNumberOfPages() :
			// 1;
			// numberOfPagesLabel.setText(scrollPane.getPaginationManager().getPage()
			// + "/" + numberOfPages);
		}
		return numberOfPagesLabel;
	}

	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setBounds(new Rectangle(NEXT_BUTTON_BOUNDS));
			nextButton.setName(NEXT_BUTTON_NAME);
			// nextButton.setEnabled(scrollPane.getPaginationManager().isNextPage());
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<ContactInfo> nextFriendsPage = scrollPane.getPaginationManager().getNextFriendsPage();
					scrollPane.fillFriendsContainer(nextFriendsPage);
					nextButton.setEnabled(scrollPane.getPaginationManager().isNextPage());
					previousButton.setEnabled(scrollPane.getPaginationManager().isPrevPage());
					numberOfPagesLabel.setText(scrollPane.getPaginationManager().getPage() + "/"
							+ scrollPane.getPaginationManager().getNumberOfPages());
					scrollPane.updateSizeFriendsContainerPanel();
				}
			});
		}
		return nextButton;
	}

	@Override
	public void internationalize(Messages messages) {
		getLabelBrowse().setText(messages.getMessage("browse.members.label.browse"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	private void fillScrollPane(List<ContactInfo> list) {
		PaginationFriendsPanelManager paginationFriendsPanelManager = new PaginationFriendsPanelManager(list,
				DEFAULT_PAGE_SIZE);
		scrollPane.setPaginationFriendsPanelManager(paginationFriendsPanelManager);
		scrollPane.fillFriendsContainer(paginationFriendsPanelManager.getFirstFriendsPage());
		scrollPane.updateSizeFriendsContainerPanel();
		getPreviousButton().setEnabled(scrollPane.getPaginationManager().isPrevPage());
		int numberOfPages = scrollPane.getPaginationManager().getNumberOfPages() > 0 ? scrollPane.getPaginationManager()
				.getNumberOfPages() : 1;
		getNumberOfPagesLabel().setText(scrollPane.getPaginationManager().getPage() + "/" + numberOfPages);
		getNextButton().setEnabled(scrollPane.getPaginationManager().isNextPage());
		getScrollPane().setLoaded(true);
	}

	public void fireEvent() {
		onBrowseSelectedEvent.fire(new ObservValue<String>(browseOption));
	}

	public void updateContact(ContactInfo contact) {
		scrollPane.updateContact(contact);
	}
}
