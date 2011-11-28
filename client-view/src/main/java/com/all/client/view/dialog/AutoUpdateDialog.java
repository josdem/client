package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.StyleConstants;

import org.springframework.context.support.ResourceBundleMessageSource;

import com.all.client.view.components.JTextPaneAligned;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.DefaultMessages;
import com.all.i18n.Messages;

public class AutoUpdateDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE_PANEL_NAME = "autoUpdatePopupPanel";
	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final String BUTTON_CLOSE_NAME = "buttonClose";

	private static final Rectangle HEADER_LABEL_BOUNDS = new Rectangle(0, 16, 244, 50);
	private static final Rectangle TITTLE_LABEL_BOUNDS = new Rectangle(0, 65, 244, 40);
	private static final Rectangle BODY_LABEL_BOUNDS = new Rectangle(0, 110, 244, 70);
	private static final Rectangle LABELS_PANEL_BOUNDS = new Rectangle(206, 0, 244, 165);

	private static final Dimension BUTTONS_SIZE = new Dimension(98, 22);
	private static final Dimension BUTTONS_PANEL_SIZE = new Dimension(450, 38);
	private static final Dimension DIALOG_SIZE = new Dimension(460, 234);
	private static final Dimension MESSAGE_PANEL_SIZE = new Dimension(446, 165);
	private static final Dimension SEPARATOR_SIZE = new Dimension(446, 2);;

	private JPanel contentComponent;
	private JPanel messagePanel;
	private JPanel labelsPanel;
	private JPanel separator;
	private JPanel buttonsPanel;
	private JButton updateButton;
	private JTextPane headerLabel;
	private JTextPane tittleLabel;
	private JTextPane bodyLabel;

	public AutoUpdateDialog(Frame mainFrame, Messages messages) {
		super(mainFrame, messages);
		this.setSize(DIALOG_SIZE);
		this.initializeContentPane();
		internationalize(messages);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("dialog.autoupdate.tittle");
	}

	@Override
	JComponent getContentComponent() {
		if (contentComponent == null) {
			contentComponent = new JPanel();
			contentComponent.setLayout(new BoxLayout(contentComponent, BoxLayout.Y_AXIS));
			contentComponent.add(getMessagePanel());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getButtonsPanel());

			// this.getRootPane().setDefaultButton(getUpdateButton());
		}
		return contentComponent;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		getHeaderLabel().setText(messages.getMessage("dialog.autoupdate.body.message.header"));
		getTittleLabel().setText(messages.getMessage("dialog.autoupdate.body.message.tittle"));
		getBodyLabel().setText(messages.getMessage("dialog.autoupdate.body.message.body"));
		getUpdateButton().setText(messages.getMessage("dialog.autoupdate.button.ok"));
	}

	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel();
			messagePanel.setLayout(null);
			messagePanel.setPreferredSize(MESSAGE_PANEL_SIZE);
			messagePanel.setMinimumSize(MESSAGE_PANEL_SIZE);
			messagePanel.setMaximumSize(MESSAGE_PANEL_SIZE);
			messagePanel.setName(MESSAGE_PANEL_NAME);
			messagePanel.add(getLabelsPanel());
		}
		return messagePanel;
	}

	private JPanel getPanelSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setPreferredSize(SEPARATOR_SIZE);
			separator.setMinimumSize(SEPARATOR_SIZE);
			separator.setMaximumSize(SEPARATOR_SIZE);
			separator.setName(PANEL_SEPARATOR_NAME);
		}
		return separator;
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttonsPanel.setSize(BUTTONS_PANEL_SIZE);
			buttonsPanel.setPreferredSize(BUTTONS_PANEL_SIZE);
			buttonsPanel.add(getUpdateButton());
		}
		return buttonsPanel;
	}

	private JButton getUpdateButton() {
		if (updateButton == null) {
			updateButton = new JButton();
			updateButton.setName(BUTTON_CLOSE_NAME);
			updateButton.setPreferredSize(BUTTONS_SIZE);
			updateButton.addActionListener(new CloseListener());
		}
		return updateButton;
	}

	private JTextPane getHeaderLabel() {
		if (headerLabel == null) {
			headerLabel = new JTextPaneAligned(StyleConstants.ALIGN_CENTER);
			headerLabel.setBounds(HEADER_LABEL_BOUNDS);
			headerLabel.setName(SynthFonts.BOLD_ITALIC_FONT16_PURPLE50_15_50);
			headerLabel.setEditable(false);
		}
		return headerLabel;
	}

	private JPanel getLabelsPanel() {
		if (labelsPanel == null) {
			labelsPanel = new JPanel();
			labelsPanel.setBounds(LABELS_PANEL_BOUNDS);
			labelsPanel.setLayout(null);
			labelsPanel.add(getHeaderLabel());
			labelsPanel.add(getTittleLabel());
			labelsPanel.add(getBodyLabel());
		}
		return labelsPanel;
	}

	private JTextPane getTittleLabel() {
		if (tittleLabel == null) {
			tittleLabel = new JTextPaneAligned(StyleConstants.ALIGN_CENTER);
			tittleLabel.setBounds(TITTLE_LABEL_BOUNDS);
			tittleLabel.setEditable(false);
			tittleLabel.setName(SynthFonts.PLAIN_FONT13_GRAY50_50_50);
		}
		return tittleLabel;
	}

	private JTextPane getBodyLabel() {
		if (bodyLabel == null) {
			bodyLabel = new JTextPaneAligned(StyleConstants.ALIGN_CENTER);
			bodyLabel.setBounds(BODY_LABEL_BOUNDS);
			bodyLabel.setEditable(false);
			bodyLabel.setName(SynthFonts.PLAIN_FONT10_GRAY100_100_100);
		}
		return bodyLabel;
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("i18n.messages");
		Messages messages = new DefaultMessages(messageSource);
		// messages.setLocale(new Locale("es", "MX"));

		UIManager.setLookAndFeel("com.all.plaf.hipecotech.HipecotechLookAndFeel");

		new AutoUpdateDialog(null, messages).setVisible(true);
	}
}
