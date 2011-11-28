package com.all.client.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.core.common.view.SynthFonts;
import com.all.shared.command.LoginCommand;
import com.all.shared.external.email.EmailDomain;

public final class ImportContactsProgressPanel extends JPanel implements Internationalizable {
	private static final long serialVersionUID = 1L;

	private static final Rectangle HOTMAIL_ICON_BOUNDS = new Rectangle(30, 14, 66, 20);

	private static final Dimension PROGRESS_PANEL_SIZE = new Dimension(326, 295);

	private JLabel hotmailIcon;
	private JLabel gmailIcon;
	private JLabel yahooIcon;
	private JLabel mainLabel;
	private JTextPane instructionsPanel;
	private JXBusyLabel busyLabel = null;
	private JPanel separatorPanel;
	private JButton cancelButton;


	public ImportContactsProgressPanel() {
		resetSize();
		this.setLayout(null);
		this.add(getHotmailIcon());
		this.add(getGmailIcon());
		this.add(getYahooIcon());
		this.add(getMainLabel());
		this.add(getInstructionPanel());
		this.add(getBusyLabel());
		this.add(getSeparatorPanel());
		this.add(getCancelButton());
	}

	private JLabel getMainLabel() {
		if (mainLabel == null) {
			mainLabel = new JLabel();
			mainLabel.setBounds(25, 53, this.getWidth() - 10, 20);
			mainLabel.setName(SynthFonts.BOLD_FONT12_GRAY50_50_50);
		}
		return mainLabel;
	}
	
	private JTextPane getInstructionPanel() {
		if (instructionsPanel == null) {
			instructionsPanel = new JTextPane();
			instructionsPanel.setBounds(25, 80, this.getWidth() - 10, 150);
			instructionsPanel.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		}
		return instructionsPanel;
	}
	
	private JLabel getBusyLabel() {
		if (busyLabel == null) {
			BusyPainter painter = new BusyPainter(new Ellipse2D.Float(0, 0, 7f, 7f), new Ellipse2D.Float(3f, 3f, 29f,
					29f));
			painter.setTrailLength(7);
			painter.setPoints(8);
			painter.setFrame(-1);
			painter.setBaseColor(new Color(187, 189, 191));
			painter.setHighlightColor(new Color(35, 31, 32));
			busyLabel = new JXBusyLabel(new Dimension(36, 36));
			busyLabel.setBounds((this.getWidth() - 36) / 2, 210, 36, 36);
			busyLabel.setPreferredSize(new Dimension(36, 36));
			busyLabel.setIcon(new EmptyIcon(36, 36));
			busyLabel.setBusyPainter(painter);
			busyLabel.setBusy(true);
			busyLabel.setVisible(true);
		}
		return busyLabel;
	}


	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(null);
			separatorPanel.setBounds(new Rectangle(8, 250, this.getWidth() - 16, 2));
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds((this.getWidth() - 80) / 2, 260, 80, 22);
			cancelButton.setName("buttonCancel");
		}
		return cancelButton;
	}

	




	private JLabel getHotmailIcon() {
		if (hotmailIcon == null) {
			hotmailIcon = new JLabel();
			hotmailIcon.setBounds(HOTMAIL_ICON_BOUNDS);
			hotmailIcon.setName("hotmailIcon");
		}
		return hotmailIcon;
	}

	private JLabel getYahooIcon() {
		if (yahooIcon == null) {
			yahooIcon = new JLabel();
			yahooIcon.setBounds(29 + 66 + 34, 14, 66, 20);
			yahooIcon.setName("yahooIcon");
		}
		return yahooIcon;
	}

	private JLabel getGmailIcon() {
		if (gmailIcon == null) {
			gmailIcon = new JLabel();
			gmailIcon.setBounds(this.getWidth() - 29 - 66, 14, 66, 20);
			gmailIcon.setName("gmailIcon");
		}
		return gmailIcon;
	}

	protected List<String> extractEmails(Map<EmailDomain, List<LoginCommand>> emailAccounts) {
		List<String> emails = new ArrayList<String>();
		for (List<LoginCommand> listLogin : emailAccounts.values()) {
			for (LoginCommand login : listLogin) {
				emails.add(login.getEmail());
			}
		}
		return emails;
	}

	@Override
	public void internationalize(Messages messages) {
		cancelButton.setText(messages.getMessage("cancel"));
		mainLabel.setText(messages.getMessage("ImportContacts.findContactProgress"));
		instructionsPanel.setText(messages.getMessage("ImportContactsProgress.instructions"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public void resetSize() {
		this.setSize(PROGRESS_PANEL_SIZE);
		this.setPreferredSize(PROGRESS_PANEL_SIZE);
		this.setMinimumSize(PROGRESS_PANEL_SIZE);
		this.setMaximumSize(PROGRESS_PANEL_SIZE);

	}

}
