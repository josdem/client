package com.all.client.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

@Component
public class UploadProgressBottomPanel extends JPanel implements Internationalizable {

	private static final Dimension CANCEL_BUTTON_SIZE = new Dimension(16, 16);
	private static final int SPACER_WIDTH = 8;
	private static final int SPACER_MAYOR_WIDTH = 10;

	private static final long serialVersionUID = 1L;

	private static final Dimension PROGRESS_PANEL_DIMENSION = new Dimension(168, 28);
	private static final Dimension SEPARATOR_PANEL_PREFERRED_SIZE = new Dimension(2, 28);
	private static final String SEPARATOR_PANEL_NAME = "verticalSeparator";

	private static final int ICON_TEXT_GAP = 7;
	private static final String ICON_NAME = "sendingMediaIcon";

	private static final int PROGRESS_BAR_DEFAULT_VALUE = 0;
	private static final int PROGRESS_BAR_MAXIMUM_VALUE = 100;
	private static final String PROGRESS_BAR_NAME = "bigProgressBar";

	private JLabel label = null;
	private JLabel uploadStatus;
	private JLabel progressLabel = null;
	private JButton cancelUploadButton;
	private JSlider progressBar = null;
	private JPanel progressBarPanel = null;

	private long uploadId;
	private Messages messages;
	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private ViewEngine viewEngine;

	public UploadProgressBottomPanel() {
		this.setName("bottomProgressPanel");
		JPanel separatorPanel = new JPanel();
		separatorPanel.setPreferredSize(SEPARATOR_PANEL_PREFERRED_SIZE);
		separatorPanel.setName(SEPARATOR_PANEL_NAME);

		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.add(BottomPanel.getSeparatorPanel());
		this.add(getSpacer(SPACER_WIDTH));
		this.add(getLabel());
		this.add(getSpacer(SPACER_WIDTH));
		this.add(getProgressBarPanel());
		this.add(getSpacer(SPACER_WIDTH));
		this.add(getCancelUploadButton());
		this.add(getSpacer(SPACER_WIDTH));
		this.add(getUploadStatus());
		this.add(getSpacer(SPACER_MAYOR_WIDTH));
		this.add(separatorPanel);
		getCancelUploadButton().setVisible(false);
		this.setVisible(false);
	}

	private JLabel getUploadStatus() {
		if (uploadStatus == null) {
			uploadStatus = new JLabel();
			uploadStatus.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);

		}
		return uploadStatus;
	}

	private final JPanel getSpacer(int width) {
		JPanel spacer = new JPanel();
		spacer.setSize(new Dimension(SPACER_WIDTH, 26));
		spacer.setMaximumSize(new Dimension(SPACER_WIDTH, 26));
		spacer.setMinimumSize(new Dimension(SPACER_WIDTH, 26));
		spacer.setPreferredSize(new Dimension(SPACER_WIDTH, 26));
		return spacer;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
			label.setIconTextGap(ICON_TEXT_GAP);
			label.setIcon(UIManager.getIcon(ICON_NAME));
		}
		return label;
	}

	private JPanel getProgressBarPanel() {
		if (progressBarPanel == null) {
			progressBarPanel = new JPanel();
			progressBarPanel.setPreferredSize(PROGRESS_PANEL_DIMENSION);
			progressBarPanel.setMinimumSize(new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE));
			progressBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
			progressBarPanel.setLayout(null);
			progressBarPanel.add(getProgressBar());
			progressBarPanel.add(getProgressLabel());
		}
		return progressBarPanel;
	}

	private JSlider getProgressBar() {
		if (progressBar == null) {
			progressBar = new JSlider();
			progressBar.setName(PROGRESS_BAR_NAME);
			progressBar.setMaximum(PROGRESS_BAR_MAXIMUM_VALUE);
			progressBar.setRequestFocusEnabled(false);
			progressBar.setValue(PROGRESS_BAR_DEFAULT_VALUE);
			progressBar.setPaintLabels(false);
			progressBar.setOpaque(false);
			progressBar.setFocusable(false);
			progressBar.setEnabled(false);
			progressBar.setBounds(new Rectangle(0, 8, 140, 12));
		}
		return progressBar;
	}

	private JLabel getProgressLabel() {
		if (progressLabel == null) {
			progressLabel = new JLabel();
			progressLabel.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
			progressLabel.setBounds(144, 7, 38, 14);
		}
		return progressLabel;
	}

	protected JButton getCancelUploadButton() {
		if (cancelUploadButton == null) {
			cancelUploadButton = new JButton();
			cancelUploadButton.setSize(CANCEL_BUTTON_SIZE);
			cancelUploadButton.setMaximumSize(CANCEL_BUTTON_SIZE);
			cancelUploadButton.setMinimumSize(CANCEL_BUTTON_SIZE);
			cancelUploadButton.setPreferredSize(CANCEL_BUTTON_SIZE);
			cancelUploadButton.setName("cancelUploadBottomButton");
			cancelUploadButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean dialog = dialogFactory.showCancelUploadDialog();
					if (dialog) {
						viewEngine.sendValueAction(Actions.Alerts.CANCEL_CONTENT_UPLOAD, uploadId);
					}
				}
			});
		}
		return cancelUploadButton;
	}

	public void setText(String text) {
		getUploadStatus().setText(text);
		this.revalidate();
	}

	protected void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public void updateProgress(int percentage) {
		progressBar.setValue(percentage);
		progressLabel.setText(messages.getMessage("bottomPanel.syncingPercentage", percentage + ""));
	}

	@Override
	public void internationalize(Messages messages) {
		progressLabel.setText(messages.getMessage("bottomPanel.syncingPercentage", "0"));
		getLabel().setText(messages.getMessage("bottomPanel.sendingMedia"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Autowired
	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

}
