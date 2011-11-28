package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class MessageDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	public enum MessageDialogType {
		NORMAL_ERROR,
		IMPORT_ERROR,
		SENT_EMAIL_CONFIRMATION
	}

	private JButton okButton;
	private JPanel panelError;
	private JPanel panelButtons;
	private JPanel labelPanel;
	private JSeparator separator;
	private String errorMessage;

	private JPanel filesWithErrorsPanel;
	public List<File> filesWithError = new ArrayList<File>();
	private Icon fileIcon = UIManager.getDefaults().getIcon("Tree.leafIcon");
	private Icon folderIcon = UIManager.getDefaults().getIcon("Tree.openIcon");
	private JLabel errorDescriptionLabel = null;
	private String title;
	private String messageLabelName;

	public MessageDialog(Frame frame, Messages messages, String errorMessage) {
		super(frame, messages);
		this.errorMessage = errorMessage;
		initialize(MessageDialogType.NORMAL_ERROR);
	}

	public MessageDialog(Frame frame, Messages messages, List<File> filesWithError) {
		super(frame, messages);
		this.filesWithError = filesWithError;
		initialize(MessageDialogType.IMPORT_ERROR);
	}

	public MessageDialog(Frame frame, Messages messages, String message, String title, String messageLabelName) {
		super(frame, messages);
		this.errorMessage = message;
		this.title = title;
		this.messageLabelName = messageLabelName;
		initialize(MessageDialogType.SENT_EMAIL_CONFIRMATION);
	}

	private void initialize(MessageDialogType type) {
		switch (type) {
		case NORMAL_ERROR:
			getNormalErrorMessage();
			break;
		case IMPORT_ERROR:
			getImportErrorMessage();
			break;
		case SENT_EMAIL_CONFIRMATION:
			getSentEmailConfirmationMessage();
			break;
		}

		GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.gridx = 0;
		labelConstraint.gridy = 0;
		getErrorPanel().add(getLabelPanel(), labelConstraint);

		GridBagConstraints separatorConstraints = new GridBagConstraints();
		separatorConstraints.gridx = 0;
		separatorConstraints.gridy = 1;
		getErrorPanel().add(getSeparator(), separatorConstraints);

		GridBagConstraints panelButtonsConstraint = new GridBagConstraints();
		panelButtonsConstraint.gridx = 0;
		panelButtonsConstraint.gridy = 2;
		getErrorPanel().add(getPanelButtons(), panelButtonsConstraint);

		initializeContentPane();

		setVisible(true);
	}

	@Override
	public String dialogTitle(Messages messages) {
		if (title != null) {
			return title;
		}
		return messages.getMessage("error");
	}

	@Override
	JComponent getContentComponent() {
		return getErrorPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private void getNormalErrorMessage() {
		this.setSize(420, 100); //480 200
		this.setPreferredSize(new Dimension(480, 100)); //480 200
		
		getErrorPanel().setSize(400, 97); //460 147
		getErrorPanel().setPreferredSize(new Dimension(400, 97));
		getErrorPanel().setMinimumSize(new Dimension(400, 97));
		getErrorPanel().setMaximumSize(new Dimension(400, 97));
		
		getLabelPanel().setSize(400, 60);
		getLabelPanel().setPreferredSize(new Dimension(400, 60));
		getLabelPanel().setMinimumSize(new Dimension(400, 60));
		getLabelPanel().setMaximumSize(new Dimension(400, 60));
		getLabelPanel().setLayout(null);
		
		JLabel errorMessageLabel = new JLabel(errorMessage);
		errorMessageLabel.setName(SynthFonts.PLAIN_FONT12_GRAY80_80_80);
		errorMessageLabel.setBounds(new Rectangle(5, 5, 400, 50));
		errorMessageLabel.setHorizontalAlignment(JLabel.CENTER);
		errorMessageLabel.setHorizontalTextPosition(JLabel.CENTER);
		getLabelPanel().add(errorMessageLabel);
		
		getSeparator().setSize(380, 2);
		getSeparator().setPreferredSize(new Dimension(380, 2));
		getSeparator().setMinimumSize(new Dimension(380, 2));
		getSeparator().setMaximumSize(new Dimension(380, 2));
	}

	private void getImportErrorMessage() {
		this.setSize(480, 200);
		this.setPreferredSize(new Dimension(480, 200));

		getErrorPanel().setSize(460, 197);
		getErrorPanel().setPreferredSize(new Dimension(460, 197));
		getErrorPanel().setMinimumSize(new Dimension(460, 197));
		getErrorPanel().setMaximumSize(new Dimension(460, 197));

		getLabelPanel().setSize(460, 150);
		getLabelPanel().setPreferredSize(new Dimension(460, 150));
		getLabelPanel().setMinimumSize(new Dimension(460, 150));
		getLabelPanel().setMaximumSize(new Dimension(460, 150));
		getLabelPanel().setLayout(null);

		errorDescriptionLabel = new JLabel();
		errorDescriptionLabel.setBounds(new Rectangle(5, 5, 450, 50));
		errorDescriptionLabel.setName(SynthFonts.PLAIN_FONT11_DARK_GRAY);
		errorDescriptionLabel.setText(getMessages().getMessage("messageImport.error"));

		getLabelPanel().add(errorDescriptionLabel, null);
		getLabelPanel().add(getFilesWithErrorsArea());

		getSeparator().setSize(460, 2);
		getSeparator().setPreferredSize(new Dimension(460, 2));
		getSeparator().setMinimumSize(new Dimension(460, 2));
		getSeparator().setMaximumSize(new Dimension(460, 2));

		getPanelButtons().setSize(460, 45);
		getPanelButtons().setPreferredSize(new Dimension(460, 45));
		getPanelButtons().setMinimumSize(new Dimension(460, 45));
		getPanelButtons().setMaximumSize(new Dimension(460, 45));
	}

	private void getSentEmailConfirmationMessage() {
		this.setSize(336, 176);
		this.setPreferredSize(new Dimension(336, 176));

		getErrorPanel().setSize(328, 150);
		getErrorPanel().setPreferredSize(new Dimension(328, 150));
		getErrorPanel().setMinimumSize(new Dimension(328, 150));
		getErrorPanel().setMaximumSize(new Dimension(328, 150));

		errorDescriptionLabel = new JLabel();
		errorDescriptionLabel.setName(messageLabelName);
		errorDescriptionLabel.setText(this.errorMessage);
		errorDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);
		errorDescriptionLabel.setHorizontalTextPosition(JLabel.CENTER);

		getLabelPanel().setSize(316, 111);
		getLabelPanel().setPreferredSize(new Dimension(316, 111));
		getLabelPanel().setMinimumSize(new Dimension(316, 111));
		getLabelPanel().setMaximumSize(new Dimension(316, 111));
		getLabelPanel().setLayout(null);
		getLabelPanel().setLayout(new BorderLayout());
		getLabelPanel().add(errorDescriptionLabel, BorderLayout.CENTER);

		getSeparator().setSize(316, 2);
		getSeparator().setPreferredSize(new Dimension(316, 2));
		getSeparator().setMinimumSize(new Dimension(316, 2));
		getSeparator().setMaximumSize(new Dimension(316, 2));

		getPanelButtons().setSize(316, 36);
		getPanelButtons().setPreferredSize(new Dimension(316, 36));
		getPanelButtons().setMinimumSize(new Dimension(316, 36));
		getPanelButtons().setMaximumSize(new Dimension(316, 36));
	}

	private JPanel getLabelPanel() {
		Dimension size = new Dimension(326, 51);
		if (labelPanel == null) {
			labelPanel = new JPanel();
			labelPanel.setSize(size);
			labelPanel.setPreferredSize(size);
			labelPanel.setMinimumSize(size);
			labelPanel.setMaximumSize(size);
		}
		return labelPanel;
	}

	private JSeparator getSeparator() {
		Dimension separatorDimension = new Dimension(318, 2);
		if (separator == null) {
			separator = new JSeparator();
			separator.setSize(separatorDimension);
			separator.setMinimumSize(separatorDimension);
			separator.setMaximumSize(separatorDimension);
			separator.setPreferredSize(separatorDimension);
		}
		return separator;
	}

	private JPanel getPanelButtons() {
		if (panelButtons == null) {
			Dimension size = new Dimension(326, 39);
			panelButtons = new JPanel();
			panelButtons.setSize(size);
			panelButtons.setPreferredSize(size);
			panelButtons.setMinimumSize(size);
			panelButtons.setMaximumSize(size);
			panelButtons.add(getOKButton());
		}
		return panelButtons;
	}

	private JComponent getErrorPanel() {
		if (panelError == null) {
			panelError = new JPanel();
			panelError.setSize(new Dimension(325, 91));
			panelError.setLayout(new GridBagLayout());
		}
		return panelError;
	}

	private JButton getOKButton() {
		if (okButton == null) {
			Dimension dimension = new Dimension(82, 22);
			okButton = new JButton();
			okButton.setText(getMessages().getMessage("ok"));
			okButton.setName("buttonOk");
			okButton.addActionListener(new CloseListener());
			okButton.setSize(dimension);
			okButton.setMaximumSize(dimension);
			okButton.setMinimumSize(dimension);
			okButton.setPreferredSize(dimension);
		}
		return okButton;
	}

	private JScrollPane getFilesWithErrorsArea() {
		JScrollPane filesWithErrorsScroll = new JScrollPane(getFilesWithErrorsPanel());
		filesWithErrorsScroll.setBounds(new Rectangle(10, 60, 440, 70));
		return filesWithErrorsScroll;
	}

	private JPanel getFilesWithErrorsPanel() {
		if (filesWithErrorsPanel == null) {
			filesWithErrorsPanel = new JPanel();
			filesWithErrorsPanel.setLayout(new BoxLayout(filesWithErrorsPanel, BoxLayout.Y_AXIS));
			filesWithErrorsPanel.setSize(new Dimension(440, 70));
			JPanel foldersPanel = getFoldersPanel();
			JPanel filePanel = getFilesPanel();
			for (int i = 0; i < filesWithError.size(); i++) {
				File file = filesWithError.get(i);
				JLabel fileName = new JLabel();
				fileName.setText(file.getName());
				fileName.setBounds(new Rectangle(new Dimension(440, 70)));
				fileName.setBorder(new EmptyBorder(0, 0, 1, 0));
				if (file.isDirectory()) {
					fileName.setIcon(folderIcon);
					foldersPanel.add(fileName);

				} else {
					fileName.setIcon(fileIcon);
					filePanel.add(fileName);
				}
			}
			filesWithErrorsPanel.add(filePanel);
			filesWithErrorsPanel.add(foldersPanel);
		}

		return filesWithErrorsPanel;
	}

	private JPanel getFilesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	private JPanel getFoldersPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}
}
