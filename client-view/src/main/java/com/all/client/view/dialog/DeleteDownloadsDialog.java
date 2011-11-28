package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.all.i18n.DefaultMessages;
import com.all.i18n.Messages;

public class DeleteDownloadsDialog extends AllDialog {
	private static final int CANCEL_BUTTON_WIDTH = 80;
	private static final int CANCEL_BUTTON_HEIGHT = 22;
	private static final int SEPARATOR_HEIGHT = 2;
	private static final int BUTTON_HEIGHT = 40;
	private static final int HORIZONTAL_BORDER_GAP = 8;
	private static final int VERTICAL_GAP = 7;
	private static final int BUTTONS_VERTICAL_GAP = 12;
	private static final int WIDTH = 242;
	private static final int HEIGHT = 225;
	private static final int MAX_COMPONENT_WIDTH = WIDTH - (2 * HORIZONTAL_BORDER_GAP);
	private static final int ICON_TEXT_GAP = 10;

	public enum DeleteDownloadsAction {
		CANCEL, DELETE_ALL, DELETE_DOWNLOAD;
	}

	public DeleteDownloadsDialog(Frame frame, Messages messages) {
		super(frame, messages);
		initializeContentPane();
	}

	private static final long serialVersionUID = 1L;
	private JLabel descriptionLabel;
	private JButton deleteAllButton;
	private JButton deleteDownloadButton;
	private JButton cancelButton;
	private JPanel contentPanel;
	private DeleteDownloadsAction action = DeleteDownloadsAction.CANCEL;

	public DeleteDownloadsAction getAction() {
		return action;
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("DeleteDownloadsDialog.title");
	}

	@Override
	JComponent getContentComponent() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.add(getDescriptionLabel());
			contentPanel.add(getDeleteAllButton());
			contentPanel.add(getDeleteDownloadButton());
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCancelButton());
			contentPanel.setSize(new Dimension(WIDTH, HEIGHT));
		}
		return contentPanel;
	}

	private JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel();
			descriptionLabel.setBounds(HORIZONTAL_BORDER_GAP, 18, MAX_COMPONENT_WIDTH, 40);
		}
		return descriptionLabel;
	}

	private JButton getDeleteAllButton() {
		if (deleteAllButton == null) {
			deleteAllButton = new JButton();
			deleteAllButton.setBounds(HORIZONTAL_BORDER_GAP, getDescriptionLabel().getY() + getDescriptionLabel().getHeight()
					+ 10, MAX_COMPONENT_WIDTH, BUTTON_HEIGHT);
			deleteAllButton.setMargin(new Insets(0, 0, 0, 0));
			deleteAllButton.setName("deleteEverythingButton");
			deleteAllButton.setHorizontalAlignment(SwingConstants.LEFT);
			deleteAllButton.setIcon(UIManager.getDefaults().getIcon("invisibleIcon"));
			deleteAllButton.setIconTextGap(ICON_TEXT_GAP);
			deleteAllButton.addActionListener(new CloseListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					action = DeleteDownloadsAction.DELETE_ALL;
					super.actionPerformed(e);
				}
			});
		}
		return deleteAllButton;
	}

	private JButton getDeleteDownloadButton() {
		if (deleteDownloadButton == null) {
			deleteDownloadButton = new JButton();
			deleteDownloadButton.setName("deleteDownloadRefButton");
			deleteDownloadButton.setHorizontalAlignment(SwingConstants.LEFT);
			deleteDownloadButton.setIcon(UIManager.getDefaults().getIcon("invisibleIcon"));
			deleteDownloadButton.setIconTextGap(ICON_TEXT_GAP);
			deleteDownloadButton.setBounds(HORIZONTAL_BORDER_GAP, getDeleteAllButton().getY()
					+ getDeleteAllButton().getHeight() + BUTTONS_VERTICAL_GAP, MAX_COMPONENT_WIDTH, BUTTON_HEIGHT);
			deleteDownloadButton.addActionListener(new CloseListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					action = DeleteDownloadsAction.DELETE_DOWNLOAD;
					super.actionPerformed(e);
				}
			});
		}
		return deleteDownloadButton;
	}

	private JPanel getSeparatorPanel() {
		JPanel separator = new JPanel();
		separator.setName("bottomPanelSeparator");
		separator.setBounds(HORIZONTAL_BORDER_GAP / 2, getDeleteDownloadButton().getY()
				+ getDeleteDownloadButton().getHeight() + 24, WIDTH - HORIZONTAL_BORDER_GAP, SEPARATOR_HEIGHT);
		return separator;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName("buttonCancel");
			cancelButton.setBounds(WIDTH / 2 - CANCEL_BUTTON_WIDTH / 2, getSeparatorPanel().getY()
					+ getSeparatorPanel().getHeight() + VERTICAL_GAP, CANCEL_BUTTON_WIDTH, CANCEL_BUTTON_HEIGHT);
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		descriptionLabel.setText(messages.getMessage("DeleteDownloadsDialog.description"));
		deleteAllButton.setText(messages.getMessage("DeleteDownloadsDialog.deleteAll"));
		deleteDownloadButton.setText(messages.getMessage("DeleteDownloadsDialog.deleteDownload"));
		cancelButton.setText(messages.getMessage("cancel"));
	}

	public static void main(String[] args) {
		Messages messages = new DefaultMessages(new ReloadableResourceBundleMessageSource());
		new DeleteDownloadsDialog(null, messages).setVisible(true);
	}

}
