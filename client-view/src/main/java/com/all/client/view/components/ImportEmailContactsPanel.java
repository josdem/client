package com.all.client.view.components;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.core.common.view.SynthFonts;
import com.all.shared.command.LoginCommand;
import com.all.shared.external.email.EmailDomain;

/**
 * Understands how to show email providers graph
 */

public final class ImportEmailContactsPanel extends JPanel implements Internationalizable {

	private static final String CANCEL_BUTTON_NAME = "buttonCancel";
	private static final String SEPARATOR_NAME = "bottomPanelSeparator";
	private static final long serialVersionUID = 1L;
	private static final Rectangle CRAWLER_PANEL_BOUNDS = new Rectangle(7, 57, 312, 348);
	private static final int HORIZONTAL_BORDER_GAP = 6;
	private static final Dimension DEFAULT_SIZE = new Dimension(326, 443);
	private JButton cancelButton;
	private JButton findContactsButton;
	private JPanel crawlerPanel;
	private Map<EmailDomain, List<EmailPanel>> emailPanels = new HashMap<EmailDomain, List<EmailPanel>>();

	private Rectangle cancelButtonBounds;
	private JTextPane instructionsLabel;

	@Autowired
	public ImportEmailContactsPanel() {
		this.setLayout(null);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.add(getInstructionsLabel());
		this.add(createTopSeparator());
		this.add(getCrawlerPanel());
		this.add(createBottomSeparator());
		this.add(getCancelButton());
		this.add(getFindContactsButton());
	}

	private JButton getFindContactsButton() {
		if (findContactsButton == null) {
			findContactsButton = new JButton();
			findContactsButton.setBounds(cancelButtonBounds.x + cancelButtonBounds.width + 10, cancelButtonBounds.y, 100, cancelButtonBounds.height);
			findContactsButton.setName("buttonFind");
		}
		return findContactsButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButtonBounds = new Rectangle((DEFAULT_SIZE.width - 190) / 2, DEFAULT_SIZE.height - 30, 80, 22);
			cancelButton.setBounds(cancelButtonBounds);
			cancelButton.setName(CANCEL_BUTTON_NAME);
		}
		return cancelButton;
	}

	private JPanel createBottomSeparator() {
		JPanel bottomSeparator = new JPanel();
		bottomSeparator.setName(SEPARATOR_NAME);
		bottomSeparator.setBounds(1, DEFAULT_SIZE.height - 38, DEFAULT_SIZE.width - 3, 2);
		return bottomSeparator;
	}

	private JPanel createTopSeparator() {
		JPanel topSeparator = new JPanel();
		topSeparator.setName(SEPARATOR_NAME);
		topSeparator.setBounds(1, 56, DEFAULT_SIZE.width - 3, 2);
		return topSeparator;
	}

	private JTextPane getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JTextPane();
			instructionsLabel.setBounds(HORIZONTAL_BORDER_GAP, 5, DEFAULT_SIZE.width - (HORIZONTAL_BORDER_GAP * 2), 45);
			instructionsLabel.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		}
		return instructionsLabel;
	}

	public Map<EmailDomain, List<LoginCommand>> collectEmailAccounts() {
		HashMap<EmailDomain, List<LoginCommand>> emailAccounts = new HashMap<EmailDomain, List<LoginCommand>>();
		for (List<EmailPanel> domainPanels : emailPanels.values()) {
			ArrayList<LoginCommand> domainAccounts = new ArrayList<LoginCommand>();
			EmailDomain key = null;
			for (EmailPanel panel : domainPanels) {
				LoginCommand emailAccount = extractPanelData(panel);
				key = panel.emailDomain;
				if (emailAccount != null) {
					domainAccounts.add(emailAccount);
				}
			}
			if (!domainAccounts.isEmpty()) {
				emailAccounts.put(key, domainAccounts);
			}
		}
		return emailAccounts;
	}

	public List<String> getEmailsList() {
		List<String> emailsList = new ArrayList<String>();
		for (List<EmailPanel> domainPanels : emailPanels.values()) {
			for (EmailPanel panel : domainPanels) {
				LoginCommand emailAccount = extractPanelData(panel);
				if (emailAccount != null) {
					emailsList.add(emailAccount.getEmail());
				}
			}
		}
		return emailsList;
	}

	private LoginCommand extractPanelData(EmailPanel panel) {
		String email = panel.getEmailField().getText();
		String password = new String(panel.getPasswordField().getPassword());
		if (password.trim().isEmpty() || email.trim().isEmpty()) {
			return null;
		}
		return new LoginCommand(email, password);
	}

	private JPanel getCrawlerPanel() {
		if (crawlerPanel == null) {
			crawlerPanel = new JPanel(null);
			crawlerPanel.setBounds(CRAWLER_PANEL_BOUNDS);
			initEmailPanels();
			resetView();
		}
		return crawlerPanel;
	}

	private void resetView() {
		crawlerPanel.removeAll();
		for (EmailDomain domain : EmailDomain.values()) {
			EmailPanel firstDomainPanel = emailPanels.get(domain).get(0);
			addPanel(firstDomainPanel);
			firstDomainPanel.reset();
		}
	}

	private void addPanel(EmailPanel panel) {
		int y = 6;
		for (java.awt.Component component : crawlerPanel.getComponents()) {
			Rectangle previousPanelBounds = component.getBounds();
			y += previousPanelBounds.height + 6;
		}
		crawlerPanel.add(panel);
		panel.setLocation(0, y);
	}

	private void initEmailPanels() {
		emailPanels.clear();
		for (EmailDomain domain : EmailDomain.values()) {
			emailPanels.put(domain, Arrays.asList(new EmailPanel[] { new EmailPanel(domain), new EmailPanel(domain) }));
		}
	}

	private void resetAndEdit(EmailDomain domain) {
		if (hasManyAccountsForDomain(domain)) {
			showDoublePanel(domain);
		} else {
			resetView();
			EmailPanel selectedPanel = emailPanels.get(domain).get(0);
			selectedPanel.edit(ButtonType.ADD);
		}
	}

	private boolean hasManyAccountsForDomain(EmailDomain domain) {
		List<EmailPanel> panelList = emailPanels.get(domain);
		int fillPanels = 0;
		for (EmailPanel panel : panelList) {
			if (panel.getEmailField().getText().trim().isEmpty() && new String(panel.getPasswordField().getPassword()).trim().isEmpty()) {
				continue;
			} else {
				fillPanels++;
			}
		}
		return fillPanels > 1;
	}

	private void showDoublePanel(EmailDomain currentDomain) {
		crawlerPanel.removeAll();
		for (EmailDomain domain : EmailDomain.values()) {
			EmailPanel firstPanel = emailPanels.get(domain).get(0);
			if (domain.equals(currentDomain)) {
				addPanel(firstPanel);
				firstPanel.edit(ButtonType.NONE);
				EmailPanel secondPanel = emailPanels.get(domain).get(1);
				addPanel(secondPanel);
				secondPanel.edit(ButtonType.HIDE);
			} else {
				addPanel(firstPanel);
				firstPanel.collapse();
			}
		}
	}

	public enum ButtonType {
		ADD, HIDE, NONE;
	}

	private final class EmailPanel extends JPanel {
		private static final String ACTION_BUTTON_HIDE_NAME = "importContactCloseButton";
		private static final String ACTION_BUTTON_ADD_NAME = "importContactAddButton";
		private static final String PASSWORD_FIELD_NAME = "textFieldPassword";
		private static final String EMAIL_FIELD_NAME = "textFieldEmail";
		private final Rectangle EMAIL_LABEL_BOUNDS = new Rectangle(5, 40, 77, 14);
		private final Rectangle PASSWORD_LABEL_BOUNDS = new Rectangle(5, 70, 77, 14);
		private final Rectangle PASSWORD_FIELD_BOUNDS = new Rectangle(95, 66, 200, 22);
		private final Rectangle EMAIL_FIELD_BOUNDS = new Rectangle(95, 36, 200, 22);
		static final String COLLAPSED = "Collapsed";
		static final String EDITING = "Editing";
		static final String DEFAULT = "Default";

		private CardLayout cardLayout = new CardLayout();
		private static final long serialVersionUID = 1L;
		protected static final String MOUSE_OVER = "_mouseOver";
		private final EmailDomain emailDomain;
		private Dimension expandedSize = new Dimension(312, 108);
		private Dimension collapsedSize = new Dimension(312, 51);
		private JPanel editingPanel;
		private JPanel collapsedPanel;
		private JPanel expandedPanel;
		private JLabel emailLabel;
		private JLabel passwordLabel;
		private JTextField emailField;
		private JPasswordField passwordField;
		private JButton actionButton;
		private ButtonType currentAction = ButtonType.NONE;

		public EmailPanel(EmailDomain emailDomain) {
			this.emailDomain = emailDomain;
			initialize();
		}

		private void initialize() {
			this.setLayout(cardLayout);
			this.add(getExpandedPanel(), DEFAULT);
			this.add(getCollapsedPanel(), COLLAPSED);
			this.add(getEditingPanel(), EDITING);
			reset();
		}

		private JPanel getEditingPanel() {
			if (editingPanel == null) {
				editingPanel = new JPanel(null);
				editingPanel.setSize(expandedSize);
				editingPanel.setName(emailDomain.getBackgroundStyle() + EDITING);
				editingPanel.add(getEmailLabel());
				getPasswordLabel();
				editingPanel.add(passwordLabel);
				if (emailDomain != EmailDomain.HOTMAIL) {
					emailLabel.setName(SynthFonts.BOLD_FONT12_WHITE);
					passwordLabel.setName(SynthFonts.BOLD_FONT12_WHITE);
				}
				emailField = new JTextField();
				emailField.setName(EMAIL_FIELD_NAME);
				emailField.setBounds(EMAIL_FIELD_BOUNDS);
				editingPanel.add(emailField);
				passwordField = new JPasswordField();
				passwordField.setName(PASSWORD_FIELD_NAME);
				passwordField.setBounds(PASSWORD_FIELD_BOUNDS);
				editingPanel.add(passwordField);
				editingPanel.add(getActionButton());
			}
			return editingPanel;
		}

		public JTextField getEmailField() {
			return emailField;
		}

		public JPasswordField getPasswordField() {
			return passwordField;
		}

		JLabel getPasswordLabel() {
			if (passwordLabel == null) {
				passwordLabel = new JLabel();
				passwordLabel.setBounds(PASSWORD_LABEL_BOUNDS);
				passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				passwordLabel.setName(SynthFonts.BOLD_FONT12_GRAY50_50_50);
			}
			return passwordLabel;
		}

		JLabel getEmailLabel() {
			if (emailLabel == null) {
				emailLabel = new JLabel();
				emailLabel.setBounds(EMAIL_LABEL_BOUNDS);
				emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				emailLabel.setName(SynthFonts.BOLD_FONT12_GRAY50_50_50);
			}
			return emailLabel;
		}

		private JButton getActionButton() {
			if (actionButton == null) {
				actionButton = new JButton();
				actionButton.setBounds(expandedSize.width - 17, expandedSize.height - 17, 14, 14);
				actionButton.setVisible(false);
				actionButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						actionButton.setVisible(false);
						switch (currentAction) {
						case HIDE:
							emailField.setText("");
							passwordField.setText("");
							resetAndEdit(emailDomain);
							break;
						case ADD:
							showDoublePanel(emailDomain);
							break;
						}
					}
				});
			}
			return actionButton;
		}

		public void showButton(ButtonType buttonType) {
			currentAction = buttonType;
			switch (buttonType) {
			case NONE:
				actionButton.setVisible(false);
				return;
			case ADD:
				actionButton.setName(ACTION_BUTTON_ADD_NAME);
				break;
			case HIDE:
				actionButton.setName(ACTION_BUTTON_HIDE_NAME);
				break;
			}
			actionButton.setVisible(true);
		}

		private JPanel getCollapsedPanel() {
			if (collapsedPanel == null) {
				collapsedPanel = new JPanel();
				collapsedPanel.setSize(collapsedSize);
				collapsedPanel.setName(emailDomain.getBackgroundStyle() + COLLAPSED);
				collapsedPanel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						collapsedPanel.setName(emailDomain.getBackgroundStyle() + COLLAPSED + MOUSE_OVER);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						collapsedPanel.setName(emailDomain.getBackgroundStyle() + COLLAPSED);
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						resetAndEdit(emailDomain);
					}
				});
			}
			return collapsedPanel;
		}

		public void collapse() {
			cardLayout.show(EmailPanel.this, COLLAPSED);
			this.setSize(collapsedSize);
		}

		public void reset() {
			cardLayout.show(EmailPanel.this, DEFAULT);
			this.setSize(expandedSize);
		}

		public void edit(ButtonType buttonType) {
			cardLayout.show(EmailPanel.this, EDITING);
			showButton(buttonType);
			this.setSize(expandedSize);
		}

		private JPanel getExpandedPanel() {
			if (expandedPanel == null) {
				expandedPanel = new JPanel();
				expandedPanel.setSize(expandedSize);
				expandedPanel.setName(emailDomain.getBackgroundStyle());
				expandedPanel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						expandedPanel.setName(emailDomain.getBackgroundStyle() + MOUSE_OVER);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						expandedPanel.setName(emailDomain.getBackgroundStyle());
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						resetAndEdit(emailDomain);
					}
				});
			}
			return expandedPanel;
		}

	}

	@Override
	public void internationalize(Messages messages) {
		findContactsButton.setText(messages.getMessage("ImportContacts.findButton"));
		cancelButton.setText(messages.getMessage("cancel"));
		instructionsLabel.setText(messages.getMessage("ImportContacts.instructions"));

		for (List<EmailPanel> panelsList : emailPanels.values()) {
			for (EmailPanel emailPanel : panelsList) {
				emailPanel.getEmailLabel().setText(messages.getMessage("ImportContacts.email"));
				emailPanel.getPasswordLabel().setText(messages.getMessage("ImportContacts.password"));
			}
		}
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public void addActionListenerToFindContactsButton(ActionListener actionListener) {
		getFindContactsButton().addActionListener(actionListener);
	}

	public void addActionListenerToCancelButton(ActionListener actionListener) {
		getCancelButton().addActionListener(actionListener);
	}

}
