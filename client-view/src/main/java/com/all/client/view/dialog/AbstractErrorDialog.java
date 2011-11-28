package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.i18n.Messages;

public abstract class AbstractErrorDialog extends AllDialog {
	private static final int HEIGHT = 113;

	private static final int WIDTH = 326;

	private static final Rectangle MESSAGE_LABEL_BOUNDS = new Rectangle(0, 16, WIDTH, 44);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 72, 316, 2);

	private static final Rectangle BUTTON_PANEL_BOUNDS = new Rectangle(0, 75, WIDTH, 36);

	private static final Rectangle MESSAGE_PANEL_BOUNDS = new Rectangle(0, 0, WIDTH, 71);

	private static final Dimension CONTENT_PANEL_SIZE = new Dimension(WIDTH, HEIGHT);

	private static final long serialVersionUID = 1L;

	private JPanel messagePanel;

	private JPanel contentPanel;

	private JPanel separatorPanel;

	private final String errorKey;

	private JLabel messageLabel;

	private final Object[] parameters;

	public AbstractErrorDialog(Frame frame, Messages messages, String errorKey, String... parameters) {
		super(frame, messages);
		this.errorKey = errorKey;
		this.parameters = parameters;
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("errorDialog.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel(null);
			contentPanel.add(getMessagePanel());
			contentPanel.setSize(CONTENT_PANEL_SIZE);
			getMessagePanel().setBounds(MESSAGE_PANEL_BOUNDS);
			contentPanel.add(getSeparatorPanel());
			getSeparatorPanel().setBounds(SEPARATOR_PANEL_BOUNDS);
			contentPanel.add(getButtonPanel());
			getButtonPanel().setBounds(BUTTON_PANEL_BOUNDS);
		}
		return contentPanel;
	}

	protected abstract JPanel getButtonPanel();

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel(null);
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel(null);
			messagePanel.add(getMessageLabel());
		}
		return messagePanel;
	}

	protected JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setHorizontalAlignment(JLabel.CENTER);
			messageLabel.setBounds(MESSAGE_LABEL_BOUNDS);
		}
		return messageLabel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		getMessageLabel().setText(messages.getMessage(errorKey, parameters));
	}

}
