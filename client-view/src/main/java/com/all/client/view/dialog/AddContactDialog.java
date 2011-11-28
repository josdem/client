package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.contacts.ContactInfoPanel;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.events.Events;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.observ.ObservePropertyChanged;
import com.all.observ.ObservedProperty;
import com.all.observ.Observer;
import com.all.shared.model.ContactInfo;

public class AddContactDialog extends AllDialog {
	private static final int GAP_TEXT = 10;
	private static final int CONTAINER_BUTTONS_HGAP = 10;

	private static final String SEARCH_CONTACT_PANEL_NAME = "searchContactPanel";
	private static final String TEXT_FIELD_SEARCH_NAME = "textFieldSearch";
	private static final String SEARCH_BUTTON_NAME = "searchGenericButton";
	private static final String RESULTS_PANEL_NAME = "resultsPanel";
	private static final String EMPTY_PANEL_NAME = "emptypanel";
	private static final String SINGLE_RESULT_PANEL_NAME = "singleResultPanel";
	private static final String SINGLE_CONTACT_INFO_PANEL_NAME = "selectedContactInfoPanel";
	private static final String MANY_RESULTS_PANEL_NAME = "manyResultsPanel";
	private static final String BUTTONS_PANEL_NAME = "buttonsPanel";
	private static final String INVALID_TEXT_FIELD_SEARCH_NAME = "invalidTextField";
	private static final String ERROR_PANEL_NAME = "notFoundEmailPanel";
	private static final String BUTTON_SEND_INVITATION = "buttonSendInvitation";
	private static final String BUTTON_SEND_EMAIL_NAME = "buttonSendEmail";
	private static final String BUTTON_CLOSE_NAME = "buttonClose";
	private static final String BOTTOM_PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final String SEARCH_RESULTS_PANEL_NAME = "searchResultsPanel";
	private static final String RESULT_LIST_NAME = "ContactList";
	private static final String NEXT_BUTTON_NAME = "nextDrawerButton";
	private static final String PREVIOUS_BUTTON_NAME = "previousDrawerButton";
	private static final String PAGINATION_PANEL_NAME = "headerDrawer";

	private static final Rectangle INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(52, 12, 300, 14);
	private static final Rectangle SEARCH_FIELD_BOUNDS = new Rectangle(48, 41, 236, 22);
	private static final Rectangle SEARCH_BUTTON_BOUNDS = new Rectangle(127, 73, 80, 22);
	private static final Rectangle SEND_INVITATION_LABEL_BOUNDS = new Rectangle(15, 78, 286, 14);
	private static final Rectangle EMAIL_LABEL_BOUNDS = new Rectangle(15, 92, 286, 20);
	private static final Rectangle SINGLE_RESULT_LABEL_BOUNDS = new Rectangle(8, 14, 326, 32);
	private static final Rectangle SINGLE_RESULT_CONTACT_INFO_PANEL_BOUNDS = new Rectangle(7, 45, 312, 72);
	private static final Rectangle MANY_RESULTS_LABEL = new Rectangle(8, 11, 310, 32);
	private static final Rectangle MANY_RESULTS_PANEL_BOUNDS = new Rectangle(0, 0, 326, 362);
	private static final Rectangle CONTAINER_BUTTONS_BOUNDS = new Rectangle(0, 5, 326, 38);
	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 0, 318, 2);
	private static final Rectangle PAGINATION_PANEL_BOUNDS = new Rectangle(5, 291, 316, 26);
	private static final Rectangle MANY_RESULT_CONTACT_INFO_PANEL_BOUNDS = new Rectangle(7, 44, 312, 245);
	private static final Rectangle ERROR_ICON_LABEL_BOUNDS = new Rectangle(17, 4, 45, 40);
	private static final Rectangle ERROR_PANEL_BOUNDS = new Rectangle(12, 3, 302, 55);
	private static final Rectangle ERROR_LABEL_BOUNDS = new Rectangle(80, 4, 226, 40);

	private static final Dimension SEARCH_CONTACT_PANEL_NAME_SIZE = new Dimension(326, 108);
	private static final Dimension RESULTS_PANEL_SIZE = new Dimension(326, 0);
	private static final Dimension ERROR_CONTAINER_SIZE = new Dimension(306, 130);
	private static final Dimension SINGLE_RESULT_PREFERRED_SIZE = new Dimension(326, 120);
	private static final Dimension MANY_RESULTS_PANEL_SIZE = new Dimension(297, 362);
	private static final Dimension BUTTONS_PANEL_SIZE = new Dimension(326, 43);
	private static final Dimension SEND_EMAIL_BUTTON_SIZE = new Dimension(80, 22);
	private static final Dimension ADD_FRIEND_BUTTON_SIZE = new Dimension(96, 22);
	private static final Dimension DEFAULT_CLOSE_BUTTON_SIZE = new Dimension(80, 22);
	private static final Dimension ERROR_PANEL_SIZE = new Dimension(302, 55);
	private static final Dimension LIST_RESULT_DIMENSION = new Dimension(297, 245);
	private static final Dimension ERROR_C0NTAINER_NAME_CASE_SIZE = new Dimension(306, 55);
	private static final Dimension PREV_BUTTON_SIZE = new Dimension(15, 26);
	private static final Dimension ERROR_LABEL_SIZE = new Dimension(306, 35);

	private static final Color MOUSE_OVER_SEARCH_BUTTON_FG = new Color(150, 150, 150);

	private static final Color PRESSED_SEARCH_BUTTON_FG = new Color(80, 80, 80);

	private static final Color NORMAL_SEARCH_BUTOTN_FG = new Color(51, 51, 51);

	private static final Color DISABLED_SEARCH_BUTTON_FG = new Color(180, 180, 180);

	private static final long serialVersionUID = 4176623792248141754L;

	private static final int RESULTS_PANEL_HEIGHT = 347;
	private static final int RESULTS_PANEL_WIDTH = 297;
	private static final int ADD_CONTACT_PANEL_BASE_WIDTH = 326;
	private static final int DIALOG_BASE_WIDTH = 336;
	private static final int SEARCHPANEL_HEIGHT = 108;
	private static final int BUTTONSPANEL_HEIGHT = 43;
	private static final int BASE_HEIGHT = SEARCHPANEL_HEIGHT + BUTTONSPANEL_HEIGHT;
	private static final int HEIGHT_NAME_CASE = 240;
	private static final int HEIGHT_EMAIL_CASE = 307;
	private static final int RESULTS_PANEL_SINGLE_RESULT_HEIGHT = 151;

	private static final String ERROR_PANEL = "ERROR_PANEL";
	private static final String EMPTY_PANEL = "EMPTY_PANEL";
	private static final String SINGLE_RESULT_PANEL = "SINGLE_RESULT_PANEL";

	private static final String MULTIPLE_RESULT_PANEL = "MULTIPLE_RESULT_PANEL";

	private JPanel addContactPanel;
	private JPanel searchContactPanel;
	private JPanel resultsPanel;
	private JPanel separatorPanel;
	private JPanel buttonsPanel;

	private JLabel instructionsLabel;
	private JTextField searchField;
	private JButton searchButton;
	private JButton closeButton;
	private JButton sendEmailButton;
	private JButton addFriendButton;
	private boolean resultsFound = false;

	private JPanel errorPanel;
	private JPanel emptyPanel;
	private JLabel errorLabel;

	private JPanel singleResultPanel;

	private JLabel manyResultsLabel;

	private JPanel manyResultsPanel;

	private JLabel singleResultLabel;

	private ContactInfoPanel singleResultContactInfoPanel;

	private Icon errorIcon;

	private JLabel sendInvitationLabel;

	private JPanel errorContainer;

	private JLabel emailLabel;

	private DefaultListModel model = new DefaultListModel();

	private JLabel errorIconLabel;

	private JList resultList;

	private Messages messages;

	private int contactsPerPage = 20;

	private ObservedProperty<AddContactDialog, Integer> index = new ObservedProperty<AddContactDialog, Integer>(this);
	private ObservedProperty<AddContactDialog, Integer> contactCount = new ObservedProperty<AddContactDialog, Integer>(
			this);
	private ObservedProperty<AddContactDialog, List<ContactInfo>> contacts = new ObservedProperty<AddContactDialog, List<ContactInfo>>(
			this);
	private List<ContactInfo> searchResult = null;

	private JPanel paginationPanel;

	private JLabel pageLabel;

	private JButton prevPageButton;

	private JButton nextPageButton;

	private JScrollPane resultScrollPane;

	private AddContactAction action = AddContactAction.CANCEL;

	private ContactInfo selectedContact;
	private final ViewEngine viewEngine;
	private EventListener<ValueEvent<ContactInfo>> contactUpdatedListener;

	public enum AddContactAction {
		CANCEL, ADD_AS_FRIEND, SEND_EMAIL;
	}

	public AddContactDialog(JFrame frame, Messages messages, ViewEngine viewEngine) {
		super((JFrame) frame, messages);
		this.viewEngine = viewEngine;
		initializeContentPane();
		index.setValue(0);
		contactCount.setValue(0);
		contacts.setValue(null);
		setup();
	}

	public AddContactDialog(JFrame frame, Messages messages, ViewEngine viewEngine, String keyword) {
		super((JFrame) frame, messages);
		this.viewEngine = viewEngine;
		initializeContentPane();
		index.setValue(0);
		contactCount.setValue(0);
		contacts.setValue(null);
		setup();
		getSearchField().setText(keyword);
		getSearchButton().setEnabled(true);
		getSearchButton().doClick();
	}

	private void setup() {
		contactUpdatedListener = new EventListener<ValueEvent<ContactInfo>>() {
			public void handleEvent(ValueEvent<ContactInfo> event) {
				List<ContactInfo> displayedContacts = contacts.getValue();
				if(displayedContacts != null && displayedContacts.contains(event.getValue())){
					contacts.setValueAndRaiseEvent(displayedContacts);
				}
			}
		};
		viewEngine.addListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);

		contactCount.on().add(new Observer<ObservePropertyChanged<AddContactDialog, Integer>>() {
			@Override
			public void observe(ObservePropertyChanged<AddContactDialog, Integer> eventArgs) {
				enableOrDisableButtons();
				repaintTotalContactsLabel();
			}

		});

		index.on().add(new Observer<ObservePropertyChanged<AddContactDialog, Integer>>() {
			@Override
			public void observe(ObservePropertyChanged<AddContactDialog, Integer> eventArgs) {
				int fromIndex = index.getValue() * contactsPerPage;
				int toIndex = (fromIndex + contactsPerPage < contactCount.getValue()) ? fromIndex + contactsPerPage
						: contactCount.getValue();
				contacts.setValueAndRaiseEvent(searchResult.subList(fromIndex, toIndex));
				enableOrDisableButtons();
			}
		});
		contacts.on().add(new Observer<ObservePropertyChanged<AddContactDialog, List<ContactInfo>>>() {
			@Override
			public void observe(ObservePropertyChanged<AddContactDialog, List<ContactInfo>> eventArgs) {
				repaintTotalContactsLabel();
				if (contacts.getValue() == null) {
					// aun no buscas nada
					return;
				}
				if (contacts.getValue().isEmpty()) {
					resultsFound = false;
					showNoResultsPanel();
				} else {
					resultsFound = true;
					if (contactCount.getValue() > 1) {
						addFriendButton.setEnabled(false);

						fillResultsList(contacts.getValue());
						changeSize(DIALOG_BASE_WIDTH, BASE_HEIGHT + RESULTS_PANEL_HEIGHT);
						showPanel(MULTIPLE_RESULT_PANEL);

					} else {
						addFriendButton.setEnabled(true);
						ContactInfo contactInfo = contacts.getValue().get(0);
						String message = messages.getMessage("addContact.singleResult", getSearchField().getText());
						singleResultLabel.setText(message);
						singleResultContactInfoPanel.fillData(contactInfo);
						changeSize(DIALOG_BASE_WIDTH, BASE_HEIGHT + RESULTS_PANEL_SINGLE_RESULT_HEIGHT);
						showPanel(SINGLE_RESULT_PANEL);
					}
				}
				updateButtonsPanel();
			}
		});
		getPreviousPageButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				index.setValue(index.getValue() - 1);
			}
		});
		getNextPageButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				index.setValue(index.getValue() + 1);
			}
		});
	}

	private void repaintTotalContactsLabel() {
		Integer initIndex = (index.getValue() * contactsPerPage); // Initial
		// RECORD

		List<ContactInfo> value = contacts.getValue();
		Integer collectionSize = initIndex + (value != null ? value.size() : 0); // Last
		Integer totalSize = contactCount.getValue();
		Long totalPages = (long) Math.ceil((double) contactCount.getValue() / (double) contactsPerPage);
		Integer currentPage = index.getValue().intValue() + 1;
		String searchText = getSearchField().getText();
		initIndex++; // Increment the initial record of the page for visual
		// purposes

		String message = messages
				.getMessage("addContact.manyResultsLabel", initIndex.toString(), totalSize < collectionSize ? totalSize
						.toString() : collectionSize.toString(), totalSize.toString(), searchText);
		manyResultsLabel.setText(message);

		if (totalPages > 1) {
			pageLabel.setText(messages.getMessage("addContact.pagination", currentPage.toString(), totalPages.toString()));
		} else {
			pageLabel.setText("");
		}

	}

	private void enableOrDisableButtons() {
		int totalPages = contactCount.getValue() / contactsPerPage
				+ ((contactCount.getValue() % contactsPerPage != 0) ? 1 : 0);
		getNextPageButton().setEnabled(totalPages > index.getValue() + 1);
		getPreviousPageButton().setEnabled(index.getValue() > 0);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("addContact.title");
	}

	@Override
	JComponent getContentComponent() {
		return getAddContactPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
		this.messages = messages;
		sendEmailButton.setText(messages.getMessage("addContact.email"));
		addFriendButton.setText(messages.getMessage("addContact.invitation"));
		singleResultLabel.setText(messages.getMessage("addContact.singleResult"));
		instructionsLabel.setText(messages.getMessage("addContact.instructions"));
		searchButton.setText(messages.getMessage("addContact.search"));
		sendInvitationLabel.setText(messages.getMessage("addContact.sendInvitationLabel"));
		manyResultsLabel.setText(messages.getMessage("addContact.manyResultsLabel"));
		closeButton.setText(messages.getMessage("addContact.close"));
	}

	private JPanel getAddContactPanel() {
		if (addContactPanel == null) {
			addContactPanel = new JPanel();
			addContactPanel.setLayout(new BorderLayout());
			addContactPanel.setSize(ADD_CONTACT_PANEL_BASE_WIDTH, BASE_HEIGHT);
			addContactPanel.setPreferredSize(new Dimension(ADD_CONTACT_PANEL_BASE_WIDTH, BASE_HEIGHT));
			addContactPanel.add(getSearchContactPanel(), BorderLayout.NORTH);
			addContactPanel.add(getResultsPanel(), BorderLayout.CENTER);
			addContactPanel.add(getButtonsPanel(), BorderLayout.SOUTH);
			addContactPanel.setVisible(true);
		}
		return addContactPanel;
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(null);
			buttonsPanel.setName(BUTTONS_PANEL_NAME);
			buttonsPanel.setPreferredSize(BUTTONS_PANEL_SIZE);
			buttonsPanel.add(getSeparatorPanel(), null);
			JPanel containerButtons = new JPanel();
			containerButtons.setBounds(CONTAINER_BUTTONS_BOUNDS);
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.CENTER);
			flow.setHgap(CONTAINER_BUTTONS_HGAP);
			containerButtons.setLayout(flow);
			containerButtons.add(getCloseButton(), null);
			containerButtons.add(getInvitationButton(), null);
			containerButtons.add(getSendEmailButton(), null);
			containerButtons.setVisible(true);
			buttonsPanel.add(containerButtons);

		}

		return buttonsPanel;
	}

	private JPanel getSearchContactPanel() {
		if (searchContactPanel == null) {
			instructionsLabel = new JLabel();
			instructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
			instructionsLabel.setName(SynthFonts.PLAIN_FONT12_GRAY51_51_51);

			searchContactPanel = new JPanel();
			searchContactPanel.setLayout(null);
			searchContactPanel.setPreferredSize(SEARCH_CONTACT_PANEL_NAME_SIZE);
			searchContactPanel.setName(SEARCH_CONTACT_PANEL_NAME);
			searchContactPanel.add(instructionsLabel, null);
			searchContactPanel.add(getSearchField(), null);
			searchContactPanel.add(getSearchButton(), null);
			searchContactPanel.setVisible(true);
		}
		return searchContactPanel;
	}

	private JTextField getSearchField() {
		if (searchField == null) {
			searchField = new JTextField();
			searchField.setBounds(SEARCH_FIELD_BOUNDS);
			searchField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			searchField.setName(TEXT_FIELD_SEARCH_NAME);
			searchField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					validateSearch(e);
				}
			});
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					searchField.requestFocusInWindow();
				}
			});
		}
		searchField.addKeyListener(new CopyPasteKeyAdapterForMac());
		return searchField;
	}

	private void validateSearch(KeyEvent e) {
		if (KeyEvent.VK_ENTER == e.getKeyCode() && getSearchButton().isEnabled()) {
			search();
		} else {
			viewEngine.request(Actions.Social.VALIDATE_SEARCH_KEYWORD, searchField.getText().trim(),
					new ResponseCallback<Boolean>() {
						@Override
						public void onResponse(Boolean validKeyword) {
							String style = (validKeyword || searchField.getText().isEmpty()) ? TEXT_FIELD_SEARCH_NAME
									: INVALID_TEXT_FIELD_SEARCH_NAME;
							searchField.setName(style);
							getSearchButton().setEnabled(validKeyword);
							getSendEmailButton().setEnabled(false);
						}
					});
		}
	}

	private void search() {
		searchButton.setEnabled(false);
		viewEngine.request(Actions.Social.SEARCH_CONTACTS, searchField.getText().trim(),
				new ResponseCallback<List<ContactInfo>>() {
					@Override
					public void onResponse(List<ContactInfo> response) {
						searchResult = response;
						contactCount.setValue(searchResult.size());
						index.setValueAndRaiseEvent(0);
						searchButton.setEnabled(true);
						getSendEmailButton().setEnabled(true);
					}
				});
	}

	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setName(SEARCH_BUTTON_NAME);
			searchButton.setBounds(SEARCH_BUTTON_BOUNDS);
			searchButton.setIconTextGap(GAP_TEXT);
			searchButton.setEnabled(false);
			searchButton.setForeground(DISABLED_SEARCH_BUTTON_FG);
			setVisualSearchButtonListener();
			searchButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					search();
				}
			});

		}
		return searchButton;
	}

	private void setVisualSearchButtonListener() {
		searchButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Color color = searchButton.isEnabled() ? NORMAL_SEARCH_BUTOTN_FG : DISABLED_SEARCH_BUTTON_FG;
				searchButton.setForeground(color);
			}
		});
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				searchButton.setForeground(PRESSED_SEARCH_BUTTON_FG);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (searchButton.isEnabled()) {
					searchButton.setForeground(MOUSE_OVER_SEARCH_BUTTON_FG);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (searchButton.isEnabled()) {
					searchButton.setForeground(NORMAL_SEARCH_BUTOTN_FG);
				}
			}
		});
	}

	private void showNoResultsPanel() {
		String messageKey;

		if (isKeywordAnEmail()) {
			messageKey = "addContact.noResultsForEmail";
			getErrorContainer().setPreferredSize(ERROR_CONTAINER_SIZE);
			sendInvitationLabel.setVisible(true);
			emailLabel.setVisible(true);
			emailLabel.setText(getSearchField().getText());
			changeSize(DIALOG_BASE_WIDTH, HEIGHT_EMAIL_CASE);
		} else {
			messageKey = "addContact.noResultsForName";
			getErrorContainer().setPreferredSize(ERROR_C0NTAINER_NAME_CASE_SIZE);
			sendInvitationLabel.setVisible(false);
			emailLabel.setVisible(false);
			changeSize(DIALOG_BASE_WIDTH, HEIGHT_NAME_CASE);
		}
		getErrorLabel().setText(messages.getMessage(messageKey));

		showPanel(ERROR_PANEL);
	}

	private JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setPreferredSize(ERROR_LABEL_SIZE);
			errorLabel.setBounds(ERROR_LABEL_BOUNDS);
			errorLabel.setName(SynthFonts.PLAIN_FONT11_PURPLE50_15_50);
		}
		return errorLabel;
	}

	private JLabel getErrorIconLabel() {
		if (errorIconLabel == null) {
			errorIconLabel = new JLabel();
			errorIconLabel.setPreferredSize(ERROR_LABEL_SIZE);
			errorIconLabel.setBounds(ERROR_ICON_LABEL_BOUNDS);
			errorIcon = UIManager.getDefaults().getIcon("Info.alertIcon");
			errorIconLabel.setIcon(errorIcon);
		}
		return errorIconLabel;
	}

	private JPanel getEmptyPanel() {
		if (emptyPanel == null) {
			emptyPanel = new JPanel();
			emptyPanel.setLayout(null);
			emptyPanel.setName(EMPTY_PANEL_NAME);
			emptyPanel.setPreferredSize(RESULTS_PANEL_SIZE);
		}
		return emptyPanel;
	}

	private void showPanel(String panel) {
		CardLayout cardLayout = (CardLayout) getResultsPanel().getLayout();
		cardLayout.show(getResultsPanel(), panel);
	}

	private JPanel getErrorPanel() {
		if (errorPanel == null) {
			errorPanel = new JPanel();
			errorPanel.setLayout(null);
			errorPanel.setName(ERROR_PANEL_NAME);
			errorPanel.setPreferredSize(ERROR_PANEL_SIZE);
			errorPanel.add(getErrorIconLabel());
			errorPanel.add(getErrorLabel());
			errorPanel.setBounds(ERROR_PANEL_BOUNDS);
		}
		return errorPanel;
	}

	private JPanel getErrorContainer() {
		if (errorContainer == null) {
			errorContainer = new JPanel();
			errorContainer.setLayout(null);
			errorContainer.setPreferredSize(ERROR_CONTAINER_SIZE);
			errorContainer.add(getErrorPanel(), null);

			sendInvitationLabel = new JLabel();
			sendInvitationLabel.setBounds(SEND_INVITATION_LABEL_BOUNDS);
			sendInvitationLabel.setHorizontalAlignment(SwingConstants.CENTER);
			sendInvitationLabel.setVisible(false);
			sendInvitationLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE50_15_50);

			emailLabel = new JLabel();
			emailLabel.setBounds(EMAIL_LABEL_BOUNDS);
			emailLabel.setName(SynthFonts.BOLD_FONT14_PURPLE50_15_50);
			emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
			emailLabel.setVisible(false);

			errorContainer.add(sendInvitationLabel, null);
			errorContainer.add(emailLabel, null);
		}
		return errorContainer;
	}

	private JPanel getResultsPanel() {
		if (resultsPanel == null) {
			resultsPanel = new JPanel();
			resultsPanel.setLayout(new CardLayout());
			resultsPanel.setPreferredSize(RESULTS_PANEL_SIZE);
			resultsPanel.setName(RESULTS_PANEL_NAME);
			resultsPanel.add(getEmptyPanel(), EMPTY_PANEL);
			resultsPanel.add(getErrorContainer(), ERROR_PANEL);
			resultsPanel.add(getSingleResultPanel(), SINGLE_RESULT_PANEL);
			resultsPanel.add(getManyResultsPanel(), MULTIPLE_RESULT_PANEL);
			resultsPanel.setVisible(true);
		}
		return resultsPanel;
	}

	private JPanel getSingleResultPanel() {
		if (singleResultPanel == null) {
			singleResultLabel = new JLabel();
			singleResultLabel.setBounds(SINGLE_RESULT_LABEL_BOUNDS);

			singleResultPanel = new JPanel();
			singleResultPanel.setLayout(null);
			singleResultPanel.setPreferredSize(SINGLE_RESULT_PREFERRED_SIZE);
			singleResultPanel.setName(SINGLE_RESULT_PANEL_NAME);

			singleResultContactInfoPanel = new ContactInfoPanel();
			singleResultContactInfoPanel.setBounds(SINGLE_RESULT_CONTACT_INFO_PANEL_BOUNDS);
			singleResultContactInfoPanel.setName(SINGLE_CONTACT_INFO_PANEL_NAME);
			singleResultPanel.add(singleResultLabel);
			singleResultPanel.add(singleResultContactInfoPanel);
		}
		return singleResultPanel;
	}

	private JPanel getManyResultsPanel() {
		if (manyResultsPanel == null) {
			manyResultsLabel = new JLabel();
			manyResultsLabel.setBounds(MANY_RESULTS_LABEL);

			manyResultsPanel = new JPanel();
			manyResultsPanel.setLayout(null);
			manyResultsPanel.setBounds(MANY_RESULTS_PANEL_BOUNDS);
			manyResultsPanel.setPreferredSize(MANY_RESULTS_PANEL_SIZE);
			manyResultsPanel.setMinimumSize(MANY_RESULTS_PANEL_SIZE);
			manyResultsPanel.setName(MANY_RESULTS_PANEL_NAME);

			manyResultsPanel.add(manyResultsLabel);
			manyResultsPanel.add(getResultsList());
			manyResultsPanel.add(getPaginationPanel());
		}
		return manyResultsPanel;
	}

	private void fillResultsList(List<ContactInfo> contacts) {
		model.removeAllElements();
		for (ContactInfo contactInfo : contacts) {
			model.addElement(contactInfo);
		}
		resultScrollPane.getViewport().setViewPosition(new Point(0, 0));

	}

	private JPanel getPaginationPanel() {
		if (paginationPanel == null) {
			paginationPanel = new JPanel();
			paginationPanel.setLayout(new BorderLayout());
			paginationPanel.setBounds(PAGINATION_PANEL_BOUNDS);
			paginationPanel.setName(PAGINATION_PANEL_NAME);
			pageLabel = new JLabel();
			pageLabel.setName(SynthFonts.BOLD_FONT11_GRAY77_77_77);
			pageLabel.setHorizontalAlignment(JLabel.CENTER);
			pageLabel.setVerticalAlignment(JLabel.CENTER);
			pageLabel.setText("PAGINATION FOR THE NATION");
			paginationPanel.add(pageLabel, BorderLayout.CENTER);
			paginationPanel.add(getNextPageButton(), BorderLayout.EAST);
			paginationPanel.add(getPreviousPageButton(), BorderLayout.WEST);
		}
		return paginationPanel;

	}

	private JButton getPreviousPageButton() {
		if (prevPageButton == null) {
			prevPageButton = new JButton();
			prevPageButton.setPreferredSize(PREV_BUTTON_SIZE);
			prevPageButton.setName(PREVIOUS_BUTTON_NAME);
		}
		return prevPageButton;
	}

	private JButton getNextPageButton() {
		if (nextPageButton == null) {
			nextPageButton = new JButton();
			nextPageButton.setPreferredSize(PREV_BUTTON_SIZE);
			nextPageButton.setName(NEXT_BUTTON_NAME);
		}
		return nextPageButton;
	}

	private JScrollPane getResultsList() {
		resultScrollPane = null;
		resultList = new JList();
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultScrollPane = new JScrollPane(resultList);
		resultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		resultScrollPane.setPreferredSize(LIST_RESULT_DIMENSION);
		resultScrollPane.setMinimumSize(LIST_RESULT_DIMENSION);
		resultScrollPane.setMaximumSize(LIST_RESULT_DIMENSION);

		resultScrollPane.setBounds(MANY_RESULT_CONTACT_INFO_PANEL_BOUNDS);
		resultScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		resultList.setModel(this.model);
		resultList.setName(RESULT_LIST_NAME);
		resultList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				addFriendButton.setEnabled(true);

			}
		});
		resultList.setCellRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JPanel resultPanel = new JPanel();
				resultPanel.setName(SEARCH_RESULTS_PANEL_NAME);
				resultPanel.setLayout(null);
				ContactInfoPanel contactInfoPanel = new ContactInfoPanel();
				contactInfoPanel.fillData((ContactInfo) value);
				if (isSelected) {
					contactInfoPanel.setName(SINGLE_CONTACT_INFO_PANEL_NAME);
				} else {
					contactInfoPanel.setName("");
				}
				int borderGap = resultScrollPane.getVerticalScrollBar().isVisible() ? 20 : 8;
				contactInfoPanel.setBounds(3, 4, RESULTS_PANEL_WIDTH - borderGap, 72);
				resultPanel.add(contactInfoPanel);
				JPanel bottomSeparator = new JPanel();
				bottomSeparator.setBounds(3, 80, RESULTS_PANEL_WIDTH - borderGap, 1);
				bottomSeparator.setName(BOTTOM_PANEL_SEPARATOR_NAME);
				resultPanel.add(bottomSeparator);

				Dimension resultPaneDimension = new Dimension(324, 81);
				resultPanel.setPreferredSize(resultPaneDimension);
				resultPanel.setSize(resultPaneDimension);
				resultPanel.setMaximumSize(resultPaneDimension);
				resultPanel.setMinimumSize(resultPaneDimension);

				return resultPanel;
			}
		});
		return resultScrollPane;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(null);
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(BOTTOM_PANEL_SEPARATOR_NAME);
			separatorPanel.setVisible(true);
		}
		return separatorPanel;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setName(BUTTON_CLOSE_NAME);
			closeButton.setPreferredSize(DEFAULT_CLOSE_BUTTON_SIZE);
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.removeListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);
				}
			});
			closeButton.addActionListener(new CloseListener());
		}
		return closeButton;
	}

	public final void updateButtonsPanel() {
		if (resultsFound || isKeywordAnEmail()) {
			// closeButton.setBounds(RESULT_CLOSE_BUTTON_BOUNDS);
			sendEmailButton.setVisible(!resultsFound);
			addFriendButton.setVisible(resultsFound);
		} else {
			// closeButton.setBounds(NO_RESULT_CLOSE_BUTTON_BOUNDS);
			sendEmailButton.setVisible(false);
			addFriendButton.setVisible(false);
		}
	}

	private JButton getSendEmailButton() {
		if (sendEmailButton == null) {
			sendEmailButton = new JButton();
			sendEmailButton.setName(BUTTON_SEND_EMAIL_NAME);
			sendEmailButton.setPreferredSize(SEND_EMAIL_BUTTON_SIZE);
			sendEmailButton.setVisible(false);
			sendEmailButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					action = AddContactAction.SEND_EMAIL;
					viewEngine.removeListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);
					closeDialog();
				}
			});
		}
		return sendEmailButton;
	}

	private JButton getInvitationButton() {
		if (addFriendButton == null) {
			addFriendButton = new JButton();
			addFriendButton.setName(BUTTON_SEND_INVITATION);
			addFriendButton.setPreferredSize(ADD_FRIEND_BUTTON_SIZE);
			addFriendButton.setVisible(false);
			addFriendButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (contacts.getValue() == null) {
						return;
					}
					int contactIndex = contacts.getValue().size() == 1 ? 0 : resultList.getSelectedIndex();
					setSelectedContact(contacts.getValue().get(contactIndex));
					action = AddContactAction.ADD_AS_FRIEND;
					viewEngine.removeListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);
					closeDialog();
				}
			});
		}
		return addFriendButton;
	}

	private boolean isKeywordAnEmail() {
		String keyword = getSearchField().getText().trim();
		return keyword.contains("@") && keyword.contains(".") && !keyword.contains(" ");
	}

	public AddContactResult getResult() {
		return new AddContactResult(getAction(), getSelectedContact(), getEmail());
	}

	private AddContactAction getAction() {
		return action;
	}

	private ContactInfo getSelectedContact() {
		return selectedContact;
	}

	private void setSelectedContact(ContactInfo contact) {
		selectedContact = contact;
	}

	private String getEmail() {
		return isKeywordAnEmail() ? getSearchField().getText().trim() : null;
	}

	public final class AddContactResult {

		private final AddContactAction action;
		private final ContactInfo contact;
		private final String email;

		public AddContactResult(AddContactAction action, ContactInfo contact, String email) {
			this.contact = contact;
			this.action = action;
			this.email = email;
		}

		public AddContactAction getAction() {
			return this.action;
		}

		public ContactInfo getContact() {
			return this.contact;
		}

		public String getEmail() {
			return email;
		}
	}

}
