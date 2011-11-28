package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import com.all.appControl.control.ViewEngine;
import com.all.client.util.Formatters;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;

public final class DeviceCopyDialog extends AllDialog {

	private static final Rectangle INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(12, 10, 300, 30);

	private static final long serialVersionUID = 1L;

	private static final int PROGRESS_BAR_DEFAULT_VALUE = 0;

	private static final int PROGRESS_BAR_MAXIMUM_VALUE = 100;

	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(324, 150);

	private static final Dimension DEFAULT_SIZE = new Dimension(300, 100);

	private static final Rectangle BACKGROUND_PROGRESS_PANEL_BOUNDS = new Rectangle(12, 44, 300, 56);

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(122, 119, 80, 22);

	private static final Rectangle PROGRESS_LABEL_BOUNDS = new Rectangle(10, 5, 280, 20);

	private static final Rectangle SIZE_LABEL_BOUNDS = new Rectangle(10, 55, 280, 20);

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(5, 110, 313, 2);

	private static final Rectangle PROGRESS_BAR_BOUNDS = new Rectangle(10, 34, 280, 10);

	private static final String BACKGROUND_PROGRESS_PANEL_NAME = "grayRoundedBorderPanel";

	private static final String CANCEL_BUTTON_NAME = "actionCancelButton";

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private static final String PROGRESS_BAR_NAME = "deviceSilderBar";

	private static final long KILOBYTE = 1024;

	private static final long MEGABYTE = 1048576;

	private static final long GIGABYTE = 1073741824;

	//private Cancelable cancelable;

	private JButton cancelButton;

	private JLabel instructionsLabel;

	private JLabel progressLabel;

	private JLabel sizeLabel;

	private JPanel contentComponent;

	private JSlider progressBar;

	private JPanel panelSeparator;

	private JPanel backgroundProgressPanel;

	private long size;

	private long totalSize;

	private int files;

	private int totalFiles;

	private final ViewEngine viewEngine;

	public DeviceCopyDialog(Frame parent, Messages messages, ViewEngine viewEngine) {
		super((JFrame) parent, messages);
		this.viewEngine = viewEngine;
		//this.setCancelAction(null);
		this.setModal(false);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.initializeContentPane();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DeviceCopyDialog.this.setVisible(true);
			}
		});
		this.addWindowListener(new CloseWindowListener());
	}

	public void onFinish() {
		//It is necessary to wait a little before dispatching the event specially when the user is trying to add only gray references
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public void onByteProgress(int progress, long size, long totalSize) {
		getProgressBar().setValue(progress);
		this.size = size;
		this.totalSize = totalSize;
		updateProgresLabel();
	}

	public void onFileProgress(int progress, int files, int totalFiles, String name) {
		this.files = files;
		this.totalFiles = totalFiles;
		updateProgresLabel();
	}

	private void updateProgresLabel() {
		String formatSize = formatBytes(getMessages(), size);
		String formatTotalSize = formatBytes(getMessages(), totalSize);
		String text = getMessages().getMessage("devices.copy.progress", "" + files, "" + totalFiles, formatSize,
				formatTotalSize);
		getProgressLabel().setText(text);
	}

//	public void setCancelAction(final Cancelable cancelable) {
//		this.cancelable = cancelable == null ? Cancelable.EMPTY : cancelable;
//	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("devices.copy.title");
	}

	@Override
	JComponent getContentComponent() {
		if (contentComponent == null) {
			contentComponent = new JPanel();
			contentComponent.setSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setPreferredSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setMinimumSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setMaximumSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setLayout(null);
			contentComponent.add(getInstructionsLabel());
			contentComponent.add(getBackgroundProgressPanel());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getCancelButton());
		}
		return contentComponent;
	}

	private JPanel getBackgroundProgressPanel() {
		if (backgroundProgressPanel == null) {
			backgroundProgressPanel = new JPanel();
			backgroundProgressPanel.setLayout(null);
			backgroundProgressPanel.setBounds(BACKGROUND_PROGRESS_PANEL_BOUNDS);
			backgroundProgressPanel.setName(BACKGROUND_PROGRESS_PANEL_NAME);
			backgroundProgressPanel.add(getProgressLabel());
			backgroundProgressPanel.add(getSizeLabel());
			backgroundProgressPanel.add(getProgressBar());
		}
		return backgroundProgressPanel;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setBounds(PANEL_SEPARATOR_BOUNDS);
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
		}
		return panelSeparator;
	}

	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			instructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
			instructionsLabel.setText(getMessages().getMessage("devices.copy.instructions"));
		}
		return instructionsLabel;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(getMessages().getMessage("devices.copy.cancel"));
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.setName(CANCEL_BUTTON_NAME);
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.send(Actions.Devices.CANCEL_COPY);
				}
			});
		}
		return cancelButton;
	}

	private JLabel getProgressLabel() {
		if (progressLabel == null) {
			progressLabel = new JLabel();
			progressLabel.setName("");
			progressLabel.setBounds(PROGRESS_LABEL_BOUNDS);
		}
		return progressLabel;
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
			progressBar.setBounds(PROGRESS_BAR_BOUNDS);
		}
		return progressBar;
	}

	private JLabel getSizeLabel() {
		if (sizeLabel == null) {
			sizeLabel = new JLabel();
			sizeLabel.setBounds(SIZE_LABEL_BOUNDS);
		}
		return sizeLabel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	protected String formatBytes(Messages messages, long bytes) {
		String formattedString = "";
		Float convertedBytes = 0F;
		Float floatBytes = new Float(bytes);
		if (bytes > GIGABYTE) {
			convertedBytes = floatBytes / GIGABYTE;
			formattedString = messages.getMessage("externalDevices.gigaBytes", Formatters
					.formatFloat(convertedBytes, 1));
		} else if (bytes > MEGABYTE) {
			convertedBytes = floatBytes / MEGABYTE;
			formattedString = messages.getMessage("externalDevices.megaBytes", Formatters
					.formatFloat(convertedBytes, 1));
		} else {
			convertedBytes = floatBytes / KILOBYTE;
			formattedString = messages.getMessage("externalDevices.kiloBytes", Formatters
					.formatFloat(convertedBytes, 1));
		}
		return formattedString;
	}

	
    private class CloseWindowListener extends WindowAdapter  {
		@Override
		public void windowClosing(WindowEvent e) {
			viewEngine.send(Actions.Devices.CANCEL_COPY);
		}
	};
}
