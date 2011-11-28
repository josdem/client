package com.all.client.view.dialog;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.ScrollableTextArea;
import com.all.core.actions.Actions;
import com.all.core.actions.SendEmailInvitationAction;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class SendJoinInvitationDialog extends AllDialog {
	private static final Dimension PREFERRED_SIZE = new Dimension(326, 270);
	private static final Rectangle INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(0, 4, 330, 27);
	private static final Rectangle TO_COTACT_LABEL_BOUNDS = new Rectangle(49, 33, 236, 20);
	private static final Rectangle TO_CONTACT_FIELD_BOUNDS = new Rectangle(49, 51, 228, 22);
	private static final Rectangle SUBJECT_LABEL_BOUNDS = new Rectangle(49, 73, 236, 20);
	private static final Rectangle SUBJECT_FIELD_BOUNDS = new Rectangle(49, 93, 228, 22);
	private static final Rectangle MESSAGE_LABEL_BOUNDS = new Rectangle(49, 115, 236, 20);
	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 223, 317, 2);
	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(78, 236, 80, 22);
	private static final Rectangle SEND_BUTTON_BOUNDS = new Rectangle(168, 236, 80, 22);

	private static final long serialVersionUID = 6278359513255472845L;

	private static final String FIELD_NAME = "joinInvitationtextField";
	private static final String FIELD_BOLD_NAME = "joinInvitationBoldtextField";
	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final String BUTTON_SEND_NAME = "buttonSend";
	private static final String BUTTON_CANCEL_NAME = "buttonCancel";

	private static final String BUSY_PANEL = "BUSY_PANEL";
	private static final String SEND_PANEL = "SEND_PANEL";
	private static final String EMAIL_KEY = "email";
	private static final String EMAIL_PROPERTIES = "/email.properties";
	private static final String INVITER_EMAIL = "noreply@all.com";

	private JPanel mainPanel;
	private JLabel toContactLabel;
	private JTextField toContactField;
	private JLabel subjectLabel;
	private JTextField subjectField;
	private JLabel instructionsLabel;
	private JButton sendButton;
	private JButton cancelButton;
	private JPanel separatorPanel;
	private final String toEmail;
	private JLabel messageLabel;
	private JPanel cardsPanel;
	private JPanel busyPanel;
	private JXBusyLabel busyLabel;
	private ScrollableTextArea contentArea;

	private static Log log = LogFactory.getLog(SendJoinInvitationDialog.class);
	private Properties properties;
	private final ViewEngine viewEngine;

	public SendJoinInvitationDialog(Frame window, Messages messages, String toEmail, ViewEngine viewEngine) {
		super(window, messages);
		this.viewEngine = viewEngine;
		this.toEmail = toEmail;
		this.initializeContentPane();
		loadProperties();
	}

	private void loadProperties() {
		properties = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream(EMAIL_PROPERTIES);
		if (inputStream == null) {
			properties.setProperty(EMAIL_KEY, INVITER_EMAIL);
		} else {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				log.error(e, e);
			}
		}
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
	}

	private JPanel getCardsPanel() {
		if (cardsPanel == null) {
			cardsPanel = new JPanel();
			cardsPanel.setLayout(new CardLayout());
			cardsPanel.add(getPanel(), SEND_PANEL);
			cardsPanel.add(getBusyPanel(), BUSY_PANEL);
		}
		return cardsPanel;
	}

	private void showPanel(String panel) {
		CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();
		cardLayout.show(cardsPanel, panel);
	}

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
			busyLabel.setBounds(118, 95, 100, 100);
			busyLabel.setPreferredSize(new Dimension(100, 100));
			busyLabel.setIcon(new EmptyIcon(100, 100));
			busyLabel.setBusyPainter(painter);
			busyLabel.setBusy(true);

		}
		return busyLabel;
	}

	private JPanel getPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(PREFERRED_SIZE);
			mainPanel.setLayout(null);

			instructionsLabel = new JLabel();
			instructionsLabel.setText(getMessages().getMessage("sendInvitation.instructions"));
			instructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
			instructionsLabel.setName(SynthFonts.PLAIN_FONT12_GRAY51_51_51);
			instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);

			toContactLabel = new JLabel();
			toContactLabel.setText(getMessages().getMessage("sendInvitation.to"));
			toContactLabel.setBounds(TO_COTACT_LABEL_BOUNDS);
			toContactLabel.setName(SynthFonts.PLAIN_FONT12_GRAY51_51_51);

			toContactField = new JTextField();
			toContactField.setText(toEmail);
			toContactField.setEditable(false);
			toContactField.setName(FIELD_BOLD_NAME);
			toContactField.setBounds(TO_CONTACT_FIELD_BOUNDS);

			subjectLabel = new JLabel();
			subjectLabel.setText(getMessages().getMessage("sendInvitation.subjectLabel"));
			subjectLabel.setBounds(SUBJECT_LABEL_BOUNDS);
			subjectLabel.setName(SynthFonts.PLAIN_FONT12_GRAY51_51_51);

			subjectField = new JTextField();
			subjectField.setText(getMessages().getMessage("sendInvitation.subject"));
			subjectField.setEditable(false);
			subjectField.setName(FIELD_NAME);
			subjectField.setBounds(SUBJECT_FIELD_BOUNDS);

			messageLabel = new JLabel();
			messageLabel.setText(getMessages().getMessage("sendInvitation.personalize"));
			messageLabel.setBounds(MESSAGE_LABEL_BOUNDS);
			messageLabel.setName(SynthFonts.PLAIN_FONT12_GRAY51_51_51);

			contentArea = new ScrollableTextArea(49, 135, 228, 72, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

			separatorPanel = new JPanel();
			separatorPanel.setLayout(null);
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(PANEL_SEPARATOR_NAME);
			separatorPanel.setVisible(true);

			cancelButton = new JButton();
			cancelButton.setName(BUTTON_CANCEL_NAME);
			cancelButton.setText(getMessages().getMessage("cancel"));

			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.addActionListener(new CloseListener());

			sendButton = new JButton();
			sendButton.setName(BUTTON_SEND_NAME);
			sendButton.setBounds(SEND_BUTTON_BOUNDS);
			sendButton.setText(getMessages().getMessage("sendInvitation.send"));
			sendButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showPanel(BUSY_PANEL);
					viewEngine.send(Actions.Social.SEND_EMAIL_INVITATION, new SendEmailInvitationAction(toEmail, contentArea
							.getText()));
					dispose();
				}

			});

			mainPanel.add(instructionsLabel);
			mainPanel.add(toContactLabel);
			mainPanel.add(toContactField);
			mainPanel.add(subjectLabel);
			mainPanel.add(subjectField);
			mainPanel.add(messageLabel);
			mainPanel.add(contentArea);
			mainPanel.add(separatorPanel);
			mainPanel.add(cancelButton);
			mainPanel.add(sendButton);

		}
		return mainPanel;
	}
}
