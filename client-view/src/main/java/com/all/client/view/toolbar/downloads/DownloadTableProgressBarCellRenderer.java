/**
 * 
 */
package com.all.client.view.toolbar.downloads;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.all.client.model.Download;
import com.all.client.view.components.GridBagConstraintsFactory;
import com.all.client.view.components.GridBagConstraintsFactory.FillMode;
import com.all.client.view.format.RateFormater;
import com.all.core.common.view.SynthFonts;
import com.all.downloader.bean.DownloadState;

class DownloadTableProgressBarCellRenderer implements TableCellRenderer {
	private static final Dimension MAX_PERCENT_LABEL_SIZE = new Dimension(35, 14);
	private static final Dimension MAX_RATE_LABEL_SIZE = new Dimension(75, 14);

	private static final Dimension MINIMUM_PROGRESS_BAR_SIZE = new Dimension(60, 10);
	private static final Dimension MAXIMUM_PROGRESS_BAR_SIZE = new Dimension(256, 10);

	private JSlider progressBar;
	private JLabel percent;
	private JLabel rate;
	private JPanel panel;
	private RateFormater rateFormater;
	private GridBagConstraints progressBarConstraints;
	private GridBagConstraints percentConstraints;
	private GridBagConstraints rateConstraints;

	public DownloadTableProgressBarCellRenderer() {
		progressBar = new JSlider();
		progressBar.setName("syncProgressBar");
		progressBar.setMinimumSize(MINIMUM_PROGRESS_BAR_SIZE);
		progressBar.setMaximumSize(MAXIMUM_PROGRESS_BAR_SIZE);
		progressBar.setEnabled(false);

		percent = new JLabel();
		percent.setMaximumSize(MAX_PERCENT_LABEL_SIZE);
		percent.setMinimumSize(MAX_PERCENT_LABEL_SIZE);
		percent.setPreferredSize(MAX_PERCENT_LABEL_SIZE);
		percent.setSize(MAX_PERCENT_LABEL_SIZE);
		percent.setHorizontalAlignment(JLabel.RIGHT);

		rate = new JLabel();
		rate.setMaximumSize(MAX_RATE_LABEL_SIZE);
		rate.setMinimumSize(MAX_RATE_LABEL_SIZE);
		rate.setPreferredSize(MAX_RATE_LABEL_SIZE);
		rate.setSize(MAX_RATE_LABEL_SIZE);
		rate.setHorizontalAlignment(JLabel.RIGHT);

		rateFormater = new RateFormater();

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraintsFactory factory = GridBagConstraintsFactory.create();
		progressBarConstraints = factory.grid(0, 0).fill(1, 1, FillMode.HORIZONTAL).insets(0, 5, 0, 0).get();
		percentConstraints = factory.grid(1, 0).fill(0, 0, FillMode.NONE).get();
		rateConstraints = factory.grid(2, 0).fill(0, 0, FillMode.NONE).get();

		panel.add(progressBar, progressBarConstraints);
		panel.add(percent, percentConstraints);
		panel.add(rate, rateConstraints);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		progressBar.setVisible(false);
		percent.setVisible(false);
		rate.setVisible(false);

		Download downloadBean = (Download) value;
		DownloadState currentStatus = downloadBean.getStatus();
		String rateText = "";
		if (currentStatus != DownloadState.Queued) {
			progressBar.setVisible(true);
			percent.setVisible(true);
			rate.setVisible(true);
		}
		if (currentStatus == DownloadState.Downloading) {
			progressBar.setEnabled(true);
			if (downloadBean.getRate() > 0) {
				rateText = rateFormater.getFormat(downloadBean.getRate());
			}
		} else {
			progressBar.setEnabled(false);
		}
		rate.setText(rateText);
		if (isSelected) {
			percent.setName(SynthFonts.BOLD_FONT11_BLACK);
			rate.setName(SynthFonts.BOLD_FONT11_BLACK);
		} else {
			percent.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
			rate.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		}
		progressBar.setValue(downloadBean.getProgress());
		percent.setText(downloadBean.getProgress() + "%");

		panel.invalidate();
		panel.validate();
		return panel;
	}
}