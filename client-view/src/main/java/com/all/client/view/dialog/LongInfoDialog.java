package com.all.client.view.dialog;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.i18n.Messages;

public class LongInfoDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private Rectangle okButtonBounds = new Rectangle(123, 81, 80, 22);
	private Rectangle separatorPanelBounds = new Rectangle(5, 72, 316, 2);
	private Rectangle contentPanelBounds = new Rectangle(0, 0, 326, 111);
	private Rectangle infoLabelBounds = new Rectangle(5, 5, 316, 62);

	private JPanel contentPanel = null;
	private JLabel infoLabel = null;
	private JPanel separatorPanel = null;
	private JButton okButton = null;

	private String messageKey;
	private String titleKey;

	public LongInfoDialog(Frame frame, Messages messages, String messageKey, String titleKey, int width, int height) {
		super(frame, messages);
		contentPanelBounds.width = width;
		contentPanelBounds.height = height;
		infoLabelBounds.width = width - 10;
		infoLabelBounds.height = height - 49;
		separatorPanelBounds.width = width - 10;
		separatorPanelBounds.y = height - 47;
		okButtonBounds.x = width / 2 - (okButtonBounds.width / 2);
		okButtonBounds.y = height - 30;

		this.messageKey = messageKey;
		this.titleKey = titleKey;
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		if (titleKey == null) {
			return messages.getMessage("infodialog.title");
		}
		return messages.getMessage(titleKey);
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			infoLabel = new JLabel();
			infoLabel.setText(getMessages().getMessage(messageKey));
			infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			infoLabel.setBounds(infoLabelBounds);
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(contentPanelBounds);
			contentPanel.add(infoLabel, null);
			contentPanel.add(getSeparatorPanel(), null);
			contentPanel.add(getOkButton(), null);
		}
		return contentPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(separatorPanelBounds);
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setBounds(okButtonBounds);
			okButton.setText(getMessages().getMessage("infodialog.okButton"));
			okButton.setName("buttonOk");
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}

}
