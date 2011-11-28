package com.all.client.view.dialog;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.MailList;
import com.all.client.view.components.ImportContactsProgressPanel;
import com.all.client.view.components.ImportEmailContactsPanel;
import com.all.client.view.components.ImportRegisteredUsersPanel;
import com.all.client.view.components.ImportUnregisteredUsersPanel;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.core.actions.Actions;
import com.all.core.actions.SendEmailInvitationAction;
import com.all.core.common.model.ApplicationActions;
import com.all.i18n.Messages;
import com.all.shared.messages.CrawlerResponse;
import com.all.shared.messages.EmailContact;
import com.all.shared.model.ContactInfo;
import com.all.shared.stats.usage.UserActions;

/**
 * Understands how to present contacts from email
 */

public class ImportContactsDialog extends AllDialog {

	private static final String REGISTERED_USERS_PANEL = "REGISTERED_USERS_PANEL";

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ImportContactsDialog.class);

	private static final int HORIZONTAL_DIALOG_FRAME = 10;

	private static final int VERTICAL_DIALOG_FRAME = 27;

	private static final String IMPORT_PROGRESS_PANEL = "IMPORT_PROGRESS_PANEL";

	private static final String IMPORT_EMAILS_PANEL = "IMPORT_EMAILS_PANEL";

	private static final long serialVersionUID = 1L;

	private static final String UNREGISTERED_USERS_PANEL = "UNREGISTERED_USERS_PANEL";

	private ImportEmailContactsPanel importEmailContactsPanel;

	private JPanel cardsPanel;

	private ImportContactsProgressPanel progressPanel;

	private CrawlerResponse result;

	private ImportRegisteredUsersPanel registeredUsersPanel;

	private ImportUnregisteredUsersPanel unregisteredUsersPanel;

	private final Collection<ContactInfo> userContacts;

	private List<MailList> mailList;

	private final ViewEngine viewEngine;

	private final DialogFactory dialogFactory;

	public ImportContactsDialog(Frame frame, Messages messages, Collection<ContactInfo> userContacts,
			LocalDescriptionPanel localDescriptionPanel, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(frame, messages);
		this.userContacts = userContacts;
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
		initializeContentPane();
		this.setBounds(new Rectangle(200 + ((localDescriptionPanel.getWidth() / 2) - (this.getWidth() / 2)),
				82 + ((localDescriptionPanel.getHeight() / 2) - (this.getHeight() / 2)), this.getWidth(), this.getHeight()));
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("ImportContacts.title");
	}

	@Override
	JComponent getContentComponent() {
		return getCardsPanel();
	}

	private JPanel getCardsPanel() {
		if (cardsPanel == null) {
			cardsPanel = new JPanel(new CardLayout());
			cardsPanel.add(getImportContactsPanel(), IMPORT_EMAILS_PANEL);
			cardsPanel.add(getProgressPanel(), IMPORT_PROGRESS_PANEL);
		}
		return cardsPanel;
	}

	private ImportEmailContactsPanel getImportContactsPanel() {
		if (importEmailContactsPanel == null) {
			importEmailContactsPanel = new ImportEmailContactsPanel();
			importEmailContactsPanel.addActionListenerToFindContactsButton(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showProgressPanel();
					viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Crawler.EXECUTE_CRAWLER);
					viewEngine.request(Actions.Application.REQUEST_CRAWLER_EMAIL_CONTACTS,
							importEmailContactsPanel.collectEmailAccounts(), new ResponseCallback<CrawlerResponse>() {

								@Override
								public void onResponse(CrawlerResponse t) {
									result = t;
									if (hasResult(result.getEmailContacts())) {
										createAndShowRegisteredUsersPanel();
									} else {
										dialogFactory.showEmailErrorDialog();
									}

								}
							});
				}
			});
			importEmailContactsPanel.addActionListenerToCancelButton(new CloseListener());
		}
		return importEmailContactsPanel;
	}

	private void showProgressPanel() {
		((CardLayout) cardsPanel.getLayout()).show(cardsPanel, IMPORT_PROGRESS_PANEL);
		progressPanel.resetSize();
		changeSize(progressPanel.getWidth() + HORIZONTAL_DIALOG_FRAME, progressPanel.getHeight() + VERTICAL_DIALOG_FRAME);
	}

	private ImportContactsProgressPanel getProgressPanel() {
		if (progressPanel == null) {
			progressPanel = new ImportContactsProgressPanel();
			progressPanel.getCancelButton().addActionListener(new CloseListener());

		}
		return progressPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		importEmailContactsPanel.internationalize(messages);
		progressPanel.internationalize(messages);
		if (registeredUsersPanel != null) {
			registeredUsersPanel.internationalize(messages);
		}
		if (unregisteredUsersPanel != null) {
			unregisteredUsersPanel.internationalize(messages);
		}
	}

	private boolean hasResult(List<EmailContact> emailContacts) {
		for (EmailContact emailContact : emailContacts) {
			if (!emailContact.getRegisteredContacts().isEmpty() || !emailContact.getUnregisteredContacts().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void createAndShowRegisteredUsersPanel() {
		ImportRegisteredUsersPanel registeredTempPanel = getRegisteredUsersPanel();
		if (registeredTempPanel != null) {
			cardsPanel.add(registeredTempPanel, REGISTERED_USERS_PANEL);
			((CardLayout) cardsPanel.getLayout()).show(cardsPanel, REGISTERED_USERS_PANEL);
			registeredTempPanel.resetSize();
			changeSize(registeredTempPanel.getWidth() + HORIZONTAL_DIALOG_FRAME, registeredTempPanel.getHeight()
					+ VERTICAL_DIALOG_FRAME);
		} else {
			createAndShowUnregisteredUsersPanel();
		}
	}

	private ImportRegisteredUsersPanel getRegisteredUsersPanel() {
		List<ContactInfo> registeredUsers = new ArrayList<ContactInfo>(result.getRegisteredUsers());
		registeredUsers.removeAll(userContacts);
		if (registeredUsers.size() > 0) {
			if (registeredUsersPanel == null) {
				registeredUsersPanel = new ImportRegisteredUsersPanel(registeredUsers, getMessages());
				registeredUsersPanel.getInviteButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						viewEngine.sendValueAction(Actions.Social.MULTIPLE_REQUEST_FRIENDSHIP, registeredUsersPanel.getSelectedContacts());
						createAndShowUnregisteredUsersPanel();
					}
				});
				registeredUsersPanel.getSkipButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						createAndShowUnregisteredUsersPanel();
					}
				});
			}
			return registeredUsersPanel;
		}
		return null;
	}

	private void createAndShowUnregisteredUsersPanel() {
		Map<String, String> unregistered = result.getUnregisteredContacts();
		Set<Entry<String, String>> entrySet = unregistered.entrySet();
		mailList = new ArrayList<MailList>();
		for (Entry<String, String> entry : entrySet) {
			mailList.add(new MailList(entry.getKey(), entry.getValue(), false));
		}
		cardsPanel.add(getUnregisteredUsersPanel(), UNREGISTERED_USERS_PANEL);
		((CardLayout) cardsPanel.getLayout()).show(cardsPanel, UNREGISTERED_USERS_PANEL);
		unregisteredUsersPanel.fillData(mailList);
		unregisteredUsersPanel.resetSize();
		changeSize(unregisteredUsersPanel.getWidth() + HORIZONTAL_DIALOG_FRAME, unregisteredUsersPanel.getHeight()
				+ VERTICAL_DIALOG_FRAME);
	}

	private ImportUnregisteredUsersPanel getUnregisteredUsersPanel() {
		if (unregisteredUsersPanel == null) {
			unregisteredUsersPanel = new ImportUnregisteredUsersPanel(getMessages());
			unregisteredUsersPanel.internationalize(getMessages());
			unregisteredUsersPanel.addActionListenerToCancelButton(new CloseListener());
			unregisteredUsersPanel.addActionListenerToInviteButton(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> recipients = new ArrayList<String>();
					for (MailList mailData : mailList) {
						if (mailData.isChecked()) {
							recipients.add(mailData.getEmail());
						}
					}
					viewEngine.send(Actions.Social.SEND_EMAIL_INVITATION, new SendEmailInvitationAction(recipients,
							unregisteredUsersPanel.getInvitationText()));
					dispose();
				}
			});
		}
		return unregisteredUsersPanel;
	}

}
