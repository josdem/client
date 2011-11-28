package com.all.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.springframework.stereotype.Component;

import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.events.ImportProgressEvent;
import com.all.event.EventMethod;

@Component
public class ProgressBarPanel extends JPanel {

	private static final long serialVersionUID = 6433231001179939499L;

	private static final Dimension SEPARATOR_PANEL_SIZE = new Dimension(2, 28);
	private static final Dimension MINIMUM_DIMENSION = new Dimension(35, 28);
	private static final Dimension SPACER_SIZE = new Dimension(8, 28);
	private static final Dimension MUSIC_ICON_SIZE = new Dimension(16, 16);
	private static final Dimension PROGRESS_BAR_SIZE = new Dimension(140, 12);

	private JSlider progressBar;
	private JLabel processLabel;
	private JLabel mediaIconLabel;
	private JLabel percentageLabel;
	private JPanel percentageLabelPanel;
	private JPanel separatorPanel;
	private JPanel processLabelPanel;

	public ProgressBarPanel() {
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints separatorConstraints = new GridBagConstraints();
		separatorConstraints.gridx = 1;
		GridBagConstraints musicIconConstraints = new GridBagConstraints();
		musicIconConstraints.gridx = 3;

		GridBagConstraints textMusicConstraints = new GridBagConstraints();
		textMusicConstraints.fill = GridBagConstraints.HORIZONTAL;
		textMusicConstraints.weightx = 0.5;
		textMusicConstraints.gridx = 5;

		GridBagConstraints progressBarConstraints = new GridBagConstraints();
		progressBarConstraints.gridx = 7;
		progressBarConstraints.fill = GridBagConstraints.NONE;

		GridBagConstraints percentageConstraints = new GridBagConstraints();
		percentageConstraints.gridx = 9;
		percentageConstraints.weightx = 0.5;
		percentageConstraints.fill = GridBagConstraints.HORIZONTAL;

		this.add(BottomPanel.getSeparatorPanel(), separatorConstraints);
		this.add(getFixedSpacer(), getFixedSpacerConstraints(2));
		this.add(getMusicIconLabel(), musicIconConstraints);
		this.add(getFixedSpacer(), getFixedSpacerConstraints(4));
		this.add(getProcessLabelPanel(), textMusicConstraints);
		this.add(getFixedSpacer(), getFixedSpacerConstraints(6));
		this.add(getProgressBar(), progressBarConstraints);
		this.add(getFixedSpacer(), getFixedSpacerConstraints(8));
		this.add(getPercentageLabelPanel(), percentageConstraints);
		this.add(getFixedSpacer(), getFixedSpacerConstraints(10));
		this.add(getSeparatorPanel(), getFixedSpacerConstraints(11));
		this.setVisible(false);
		this.setName("bottomProgressPanel");
	}

	private JLabel getMusicIconLabel() {
		if (mediaIconLabel == null) {
			mediaIconLabel = new JLabel();
			mediaIconLabel.setSize(MUSIC_ICON_SIZE);
			mediaIconLabel.setPreferredSize(MUSIC_ICON_SIZE);
			mediaIconLabel.setMinimumSize(MUSIC_ICON_SIZE);
			mediaIconLabel.setMaximumSize(MUSIC_ICON_SIZE);
			mediaIconLabel.setName("syncMusicIcon");
		}
		return mediaIconLabel;
	}

	private JPanel getProcessLabelPanel() {
		if (processLabelPanel == null) {
			processLabelPanel = new JPanel();
			processLabelPanel.add(getProcessLabel());
		}
		return processLabelPanel;
	}

	private JLabel getProcessLabel() {
		if (processLabel == null) {
			processLabel = new JLabel();
			processLabel.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
		}
		return processLabel;
	}

	private JPanel getFixedSpacer() {
		JPanel spacer = new JPanel();
		spacer.setBackground(Color.CYAN);
		spacer.setSize(SPACER_SIZE);
		spacer.setMaximumSize(SPACER_SIZE);
		spacer.setMinimumSize(SPACER_SIZE);
		spacer.setPreferredSize(SPACER_SIZE);
		return spacer;
	}

	private GridBagConstraints getFixedSpacerConstraints(int element) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = element;
		return gridBagConstraints;
	}

	private JSlider getProgressBar() {
		if (progressBar == null) {
			progressBar = new JSlider();
			progressBar.setName("bigProgressBar");
			progressBar.setPreferredSize(PROGRESS_BAR_SIZE);
			progressBar.setMaximum(100);
			progressBar.setRequestFocusEnabled(false);
			progressBar.setValue(0);
			progressBar.setPaintLabels(false);
			progressBar.setOpaque(false);
			progressBar.setFocusable(false);
		}
		return progressBar;
	}

	@EventMethod(Events.Library.IMPORT_COMPLETED_ID)
	public void onImportCompleted() {
		ProgressBarPanel.this.setVisible(false);
		getProgressBar().setValue(0);
		recalculateLabelSize(getProcessLabel());
		if (ProgressBarPanel.this.getParent() != null) {
			((JPanel) ProgressBarPanel.this.getParent().getParent()).revalidate();
		}
	}

	@EventMethod(Events.Library.IMPORT_PROGRESS_ID)
	public void onImportProgress(ImportProgressEvent event) {
		int progress = event.getProgress();
		getProcessLabel().setText(event.getMessage());
		if (getProgressBar().getValue() < progress) {
			getProgressBar().setValue(progress);
			getPercentageLabel().setText(progress + "%");
			ProgressBarPanel.this.setVisible(true);
		}
		recalculateLabelSize(getProcessLabel());
		if (ProgressBarPanel.this.getParent() != null) {
			((JPanel) ProgressBarPanel.this.getParent().getParent()).revalidate();
		}
	}

	private void recalculateLabelSize(JLabel label) {
		FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
		int labelWidth = fontMetrics.stringWidth(label.getText()) + 2;
		label.setSize(new Dimension(labelWidth, 14));
		label.setPreferredSize(new Dimension(labelWidth, 14));
		label.setMinimumSize(new Dimension(labelWidth, 14));
		label.setMaximumSize(new Dimension(labelWidth, 14));
		JPanel parent = (JPanel) label.getParent();
		parent.setSize(new Dimension(labelWidth, 26));
		parent.setPreferredSize(new Dimension(labelWidth, 26));
		parent.setMaximumSize(new Dimension(labelWidth, 26));
		parent.setMinimumSize(new Dimension(labelWidth, 26));
		parent.validate();
	}

	private JPanel getPercentageLabelPanel() {
		if (percentageLabelPanel == null) {
			percentageLabelPanel = new JPanel();
			percentageLabelPanel.setBackground(Color.MAGENTA);
			percentageLabelPanel.setSize(MINIMUM_DIMENSION);
			percentageLabelPanel.setPreferredSize(MINIMUM_DIMENSION);
			percentageLabelPanel.setMinimumSize(MINIMUM_DIMENSION);
			percentageLabelPanel.setMaximumSize(MINIMUM_DIMENSION);
			percentageLabelPanel.add(getPercentageLabel());
		}
		return percentageLabelPanel;
	}

	private JLabel getPercentageLabel() {
		if (percentageLabel == null) {
			percentageLabel = new JLabel();
			percentageLabel.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
		}
		return percentageLabel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setOpaque(false);
			separatorPanel.setSize(SEPARATOR_PANEL_SIZE);
			separatorPanel.setPreferredSize(SEPARATOR_PANEL_SIZE);
			separatorPanel.setMinimumSize(SEPARATOR_PANEL_SIZE);
			separatorPanel.setMaximumSize(SEPARATOR_PANEL_SIZE);
			separatorPanel.setName("verticalSeparator");
		}
		return separatorPanel;
	}

}
