package com.all.client.view.toolbar.downloads;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class DownloadButtonsPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Dimension DEFAULT_SIZE = new Dimension(230, 54);

	private static final Dimension DEFAULT_SIZE_BUTTON = new Dimension(28, 28);
	
	private static final Dimension VERTICAL_SEPARATOR_PREFERRED_SIZE = new Dimension(1, 54);

	private static final Insets BUTTON_INSETS = new Insets(5, 14, 0, 14);

	private static final Insets SEPARATOR_INSETS = new Insets(0, 14, 0, 14);

	private static final String CLEAN_UP_BUTTON_NAME = "cleanUpDownloadButton";
	
	private static final String DELETE_BUTTON_NAME = "deleteDownloadButton";
	
	private static final String PAUSE_BUTTON_NAME = "pauseDownloadButton";

	private static final String RESUME_BUTTON_NAME = "resumeDownloadButton";

	private static final String VERTICAL_SEPARATOR_NAME = "backgroundPanel";
	
	private JButton cleanUpButton;

	private JButton deleteButton;
	
	private JButton pauseButton;
	
	private JButton resumeButton;
	
	private JLabel cleanUpLabel;

	private JLabel deleteLabel;
	
	private JLabel pauseLabel;
	
	private JLabel resumeLabel;
	
	private JPanel verticalSeparator;

	public DownloadButtonsPanel() {
		initialize();
	}

	private final void initialize() {
		this.setSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setLayout(new GridBagLayout());

		GridBagConstraints resumeButtonConstraints = new GridBagConstraints();
		resumeButtonConstraints.gridx = 0;
		resumeButtonConstraints.gridy = 0;
		resumeButtonConstraints.insets = BUTTON_INSETS;
		GridBagConstraints resumeLabelConstraints = new GridBagConstraints();
		resumeLabelConstraints.gridx = 0;
		resumeLabelConstraints.gridy = 1;

		GridBagConstraints pauseButtonConstraints = new GridBagConstraints();
		pauseButtonConstraints.gridx = 1;
		pauseButtonConstraints.gridy = 0;
		pauseButtonConstraints.insets = BUTTON_INSETS;
		GridBagConstraints pauseLabelConstraints = new GridBagConstraints();
		pauseLabelConstraints.gridx = 1;
		pauseLabelConstraints.gridy = 1;

		GridBagConstraints verticalSeparatorConstraints = new GridBagConstraints();
		verticalSeparatorConstraints.gridx = 2;
		verticalSeparatorConstraints.gridy = 0;
		verticalSeparatorConstraints.gridheight = 2;
		verticalSeparatorConstraints.insets = SEPARATOR_INSETS;

		GridBagConstraints cleanUpButtonConstraints = new GridBagConstraints();
		cleanUpButtonConstraints.gridx = 3;
		cleanUpButtonConstraints.gridy = 0;
		cleanUpButtonConstraints.insets = BUTTON_INSETS;
		GridBagConstraints cleanUpLabelConstraints = new GridBagConstraints();
		cleanUpLabelConstraints.gridx = 3;
		cleanUpLabelConstraints.gridy = 1;

		GridBagConstraints deleteButtonConstraints = new GridBagConstraints();
		deleteButtonConstraints.gridx = 4;
		deleteButtonConstraints.gridy = 0;
		deleteButtonConstraints.insets = BUTTON_INSETS;
		GridBagConstraints deleteLabelConstraints = new GridBagConstraints();
		deleteLabelConstraints.gridx = 4;
		deleteLabelConstraints.gridy = 1;

		this.add(getResumeButton(), resumeButtonConstraints);
		this.add(getResumeLabel(), resumeLabelConstraints);

		this.add(getPauseButton(), pauseButtonConstraints);
		this.add(getPauseLabel(), pauseLabelConstraints);

		this.add(getVerticalSeparator(), verticalSeparatorConstraints);

		this.add(getCleanUpButton(), cleanUpButtonConstraints);
		this.add(getCleanUpLabel(), cleanUpLabelConstraints);

		this.add(getDeleteButton(), deleteButtonConstraints);
		this.add(getDeleteLabel(), deleteLabelConstraints);

	}

	JButton getCleanUpButton() {
		if (cleanUpButton == null) {
			cleanUpButton = new JButton();
			cleanUpButton.setSize(DEFAULT_SIZE_BUTTON);
			cleanUpButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			cleanUpButton.setMinimumSize(DEFAULT_SIZE_BUTTON);
			cleanUpButton.setMaximumSize(DEFAULT_SIZE_BUTTON);
			cleanUpButton.setName(CLEAN_UP_BUTTON_NAME);
			cleanUpButton.setEnabled(false);
		}
		return cleanUpButton;
	}

	JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new JButton();
			deleteButton.setSize(DEFAULT_SIZE_BUTTON);
			deleteButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			deleteButton.setMinimumSize(DEFAULT_SIZE_BUTTON);
			deleteButton.setMaximumSize(DEFAULT_SIZE_BUTTON);
			deleteButton.setName(DELETE_BUTTON_NAME);
			deleteButton.setEnabled(false);
		}
		return deleteButton;
	}

	public JButton getResumeButton() {
		if (resumeButton == null) {
			resumeButton = new JButton();
			resumeButton.setSize(DEFAULT_SIZE_BUTTON);
			resumeButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			resumeButton.setMinimumSize(DEFAULT_SIZE_BUTTON);
			resumeButton.setMaximumSize(DEFAULT_SIZE_BUTTON);
			resumeButton.setName(RESUME_BUTTON_NAME);
			resumeButton.setEnabled(false);
		}
		return resumeButton;
	}

	public JButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JButton();
			pauseButton.setSize(DEFAULT_SIZE_BUTTON);
			pauseButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			pauseButton.setMinimumSize(DEFAULT_SIZE_BUTTON);
			pauseButton.setMaximumSize(DEFAULT_SIZE_BUTTON);
			pauseButton.setName(PAUSE_BUTTON_NAME);
			pauseButton.setEnabled(false);
		}
		return pauseButton;
	}

	private JLabel getDeleteLabel() {
		if (deleteLabel == null) {
			deleteLabel = new JLabel();
			deleteLabel.setText("Delete");
			deleteLabel.setName(SynthFonts.BOLD_FONT11_BLACK);
		}
		return deleteLabel;
	}

	private JLabel getCleanUpLabel() {
		if (cleanUpLabel == null) {
			cleanUpLabel = new JLabel();
			cleanUpLabel.setText("Clean Up");
			cleanUpLabel.setName(SynthFonts.BOLD_FONT11_BLACK);
		}
		return cleanUpLabel;
	}

	private JLabel getPauseLabel() {
		if (pauseLabel == null) {
			pauseLabel = new JLabel();
			pauseLabel.setText("Pause");
			pauseLabel.setName(SynthFonts.BOLD_FONT11_BLACK);
		}
		return pauseLabel;
	}

	private JLabel getResumeLabel() {
		if (resumeLabel == null) {
			resumeLabel = new JLabel();
			resumeLabel.setText("Resume");
			resumeLabel.setName(SynthFonts.BOLD_FONT11_BLACK);
		}
		return resumeLabel;
	}

	private JPanel getVerticalSeparator() {
		if (verticalSeparator == null) {
			verticalSeparator = new JPanel();
			verticalSeparator.setName(VERTICAL_SEPARATOR_NAME);
			verticalSeparator.setPreferredSize(VERTICAL_SEPARATOR_PREFERRED_SIZE);
			verticalSeparator.setSize(VERTICAL_SEPARATOR_PREFERRED_SIZE);
			verticalSeparator.setMaximumSize(VERTICAL_SEPARATOR_PREFERRED_SIZE);
			verticalSeparator.setMinimumSize(VERTICAL_SEPARATOR_PREFERRED_SIZE);
		}
		return verticalSeparator;
	}

	@Override
	public void internationalize(Messages messages) {
		getResumeButton().setToolTipText(messages.getMessage("tooltip.resumeDownloadDT"));
		getPauseButton().setToolTipText(messages.getMessage("tooltip.pauseDownloadDT"));
		getCleanUpButton().setToolTipText(messages.getMessage("tooltip.cleanDownloadDT"));
		getDeleteButton().setToolTipText(messages.getMessage("tooltip.deleteDownloadDT"));
		getDeleteLabel().setText(messages.getMessage("downloads.deleteLabel"));
		getCleanUpLabel().setText(messages.getMessage("downloads.cleanUpLabel"));
		getPauseLabel().setText(messages.getMessage("downloads.pauseLabel"));
		getResumeLabel().setText(messages.getMessage("downloads.resumeLabel"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}
