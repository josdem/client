package com.all.client.view.dialog;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.ScrollableTextArea;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.actions.SendEmailInvitationAction;
import com.all.core.model.ContactCollection;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public class SendMultipleInvitationDialog extends AllDialog {

	private static final String JOIN_INVITATION_LABEL_NAME = "joinInvitationLabel";
	private static final long serialVersionUID = 1L;
	private static final Dimension PREFERRED_SIZE = new Dimension(336 - 10, 470 - 27);
	private static final String BUSY_PANEL = "BUSY_PANEL";
	private static final String SEND_PANEL = "SEND_PANEL";
	private JPanel mainPanel;
	private JLabel toContactLabel;
	private JLabel subjectLabel;
	private JTextField subjectField;
	private JLabel instructionsLabel;
	private JButton sendButton;
	private JButton cancelButton;
	private JPanel separatorPanel;
	private final Set<String> emails;
	private JLabel messageLabel;
	private JPanel cardsPanel;
	private JPanel busyPanel;
	private JXBusyLabel busyLabel;
	private ScrollableTextArea messageTextArea;
	private ScrollableTextArea emailsTextArea;
	private final ViewEngine viewEngine;

	public SendMultipleInvitationDialog(Frame window, Messages messages, List<String> toEmails, ViewEngine viewEngine) {
		super(window, messages);
		this.viewEngine = viewEngine;
		this.emails = new TreeSet<String>();
		this.emails.addAll(toEmails);
		MultiLayerDropTargetListener dndListener = new MultiLayerDropTargetListener();
		dndListener.addDropListener(this, new DropListener() {
			private final Class<?>[] classes = new Class<?>[] { ContactCollection.class };

			@Override
			public void doDrop(DraggedObject draggedObject, Point location) {
				ContactCollection contacts = draggedObject.get(ContactCollection.class);
				for (ContactInfo contact : contacts.getPendingContacts()) {
					emails.add(contact.getEmail());
				}
				fillEmailsTextArea();
			}

			@Override
			public boolean validateDrop(DraggedObject draggedObject, Point location) {
				ContactCollection contacts = draggedObject.get(ContactCollection.class);
				if (contacts != null && contacts.getPendingContacts().isEmpty()) {
					return false;
				}
				return true;
			}

			@Override
			public Class<?>[] handledTypes() {
				return classes;
			}
		});
		this.setDropTarget(new DropTarget(this, dndListener));
		dndListener.addDragListener(this, new MainFrameDragOverListener(this, messages));
		this.setModal(false);
		this.setAlwaysOnTop(true);
		this.initializeContentPane();
		this.setVisible(true);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("sendInvitation.title");
	}

	@Override
	JComponent getContentComponent() {
		return getCardsPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
		cancelButton.setText(messages.getMessage("cancel"));
		sendButton.setText(messages.getMessage("sendInvitation.send"));
		messageLabel.setText(messages.getMessage("sendInvitation.personalize"));
		subjectField.setText(messages.getMessage("sendInvitation.subject"));
		subjectLabel.setText(messages.getMessage("sendInvitation.subjectLabel"));
		toContactLabel.setText(messages.getMessage("sendInvitation.to"));
		instructionsLabel.setText(messages.getMessage("sendMultipleInvitation.instructions"));

	}

	private JPanel getCardsPanel() {
		if (cardsPanel == null) {
			cardsPanel = new JPanel();
			cardsPanel.setLayout(new CardLayout());
			cardsPanel.add(getMainPanel(), SEND_PANEL);
			cardsPanel.add(getBusyPanel(), BUSY_PANEL);
		}
		return cardsPanel;
	}

	// private void showPanel(String panel) {
	// CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();
	// cardLayout.show(cardsPanel, panel);
	// }

	private JPanel getBusyPanel() {
		if (busyPanel == null) {
			busyPanel = new JPanel();
			busyPanel.setPreferredSize(PREFERRED_SIZE);
			busyPanel.setLayout(null);
			busyPanel.add(getBusyLabel());
		}
		return busyPanel;
	}

	private JXBusyLabel getBusyLabel() {
		if (busyLabel == null) {
			BusyPainter painter = new BusyPainter(new Ellipse2D.Float(0, 0, 12f, 12f), new Ellipse2D.Float(6f, 6f, 88f, 88f));
			painter.setTrailLength(7);
			painter.setPoints(8);
			painter.setFrame(-1);
			painter.setBaseColor(new Color(240, 240, 240));
			painter.setHighlightColor(new Color(70, 30, 105));
			busyLabel = new JXBusyLabel(new Dimension(100, 100));
			busyLabel.setBounds((PREFERRED_SIZE.width - 100) / 2, (PREFERRED_SIZE.height - 100) / 2, 100, 100);
			busyLabel.setPreferredSize(new Dimension(100, 100));
			busyLabel.setIcon(new EmptyIcon(100, 100));
			busyLabel.setBusyPainter(painter);
			busyLabel.setBusy(true);

		}
		return busyLabel;
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(PREFERRED_SIZE);
			mainPanel.setLayout(null);
			createComponents();
			mainPanel.add(instructionsLabel);
			mainPanel.add(toContactLabel);
			mainPanel.add(emailsTextArea);
			mainPanel.add(subjectLabel);
			mainPanel.add(subjectField);
			mainPanel.add(messageLabel);
			mainPanel.add(messageTextArea);
			mainPanel.add(separatorPanel);
			mainPanel.add(cancelButton);
			mainPanel.add(sendButton);

		}
		return mainPanel;
	}

	private void createComponents() {
		createInstructionsLabel();
		createToContactLabel();
		createEmailsTextArea();
		createSubjectLabel();
		createSubjectField();
		createMessageLabel();
		createMessageArea();
		createSeparatorPanel();
		createButtons();
	}

	private void createButtons() {
		cancelButton = new JButton();
		cancelButton.setName("buttonCancel");
		cancelButton.setBounds(78, 70 + 120 + 26 + 22 + 26 + 120 + 17 + 2 + 7, 80, 22);
		cancelButton.addActionListener(new CloseListener());

		sendButton = new JButton();
		sendButton.setName("buttonSend");
		sendButton.setBounds(168, 70 + 120 + 26 + 22 + 26 + 120 + 17 + 2 + 7, 80, 22);
		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Social.SEND_EMAIL_INVITATION, new SendEmailInvitationAction(new ArrayList<String>(
						emails), messageTextArea.getText()));
				dispose();
			}

		});
	}

	private void createSeparatorPanel() {
		separatorPanel = new JPanel();
		separatorPanel.setLayout(null);
		separatorPanel.setBounds(new Rectangle(5, 70 + 120 + 26 + 22 + 26 + 120 + 17, 317, 2));
		separatorPanel.setName("bottomPanelSeparator");
		separatorPanel.setVisible(true);
	}

	private void createMessageArea() {
		messageTextArea = new ScrollableTextArea(49, 70 + 120 + 26 + 22 + 26, 228, 120,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	private void createMessageLabel() {
		messageLabel = new JLabel();
		messageLabel.setBounds(49, 70 + 120 + 26 + 22, 236, 26);
		messageLabel.setName(JOIN_INVITATION_LABEL_NAME);
	}

	private void createSubjectField() {
		subjectField = new JTextField();
		subjectField.setEditable(false);
		subjectField.setName("joinInvitationtextField");
		subjectField.setBounds(49, 70 + 120 + 26, 228, 22);
	}

	private void createSubjectLabel() {
		subjectLabel = new JLabel();
		subjectLabel.setBounds(49, 70 + 120, 236, 26);
		subjectLabel.setName(JOIN_INVITATION_LABEL_NAME);
	}

	private void createEmailsTextArea() {
		emailsTextArea = new ScrollableTextArea(49, 70, 228, 120, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		emailsTextArea.getTextArea().setDropTarget(null);
		fillEmailsTextArea();
	}

	private void createToContactLabel() {
		toContactLabel = new JLabel();
		toContactLabel.setBounds(49, 53, 236, 14);
		toContactLabel.setName(JOIN_INVITATION_LABEL_NAME);
	}

	private void createInstructionsLabel() {
		instructionsLabel = new JLabel();
		instructionsLabel.setBounds(12, 18, 319, 30);

		instructionsLabel.setName(JOIN_INVITATION_LABEL_NAME);
	}

	private void fillEmailsTextArea() {
		StringBuilder sb = new StringBuilder("");
		for (String email : emails) {
			sb.append(email).append(",\n");
		}
		emailsTextArea.setText(sb.substring(0, sb.length() - 2));
	}

}
