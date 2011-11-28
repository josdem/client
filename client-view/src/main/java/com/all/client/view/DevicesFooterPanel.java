package com.all.client.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.DeviceRoot;
import com.all.client.util.Formatters;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public final class DevicesFooterPanel extends FooterPanel {

	private static final long serialVersionUID = 5895241546700472606L;

	private static final Dimension BOTTOM_PANEL_SIZE = new Dimension(200, 26);
	private static final Dimension DEFAULT_TOP_PANEL_SIZE = new Dimension(200, 25);
	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(100, 1);
	private static final Insets BAR_INSETS = new Insets(7, 6, 8, 6);
	private static final Insets BOTTOM_PANEL_INSETS = new Insets(0, 0, 1, 0);
	private static final Insets SPACE_INSETS = new Insets(0, 2, 0, 5);
	private static final Insets TITLE_INSETS = new Insets(0, 12, 0, 0);
	private static final Insets TOP_PANEL_INSETS = new Insets(2, 0, 0, 0);
	private static final String BOTTOM_PANEL_NAME = "bottomDevicePanel";
	private static final String NAME = "previewTreeBackground";
	private static final String SPACE_BAR_NAME = "deviceSilderBar";
	private static final String TOP_PANEL_NAME = "footerDevicesTopPanel";
	private static final String DEFAULT_DEVICES_ICON = "ExternalDevices.icon";
	private static final long KILOBYTE = 1024;
	private static final long MEGABYTE = 1048576;
	private static final long GIGABYTE = 1073741824;

	private JLabel spaceLabel;
	private JSlider spaceBar;
	private JPanel bottomPanel;
	private JPanel separator;
	private JLabel titleLabel;
	private JPanel topPanel;
	private File rootFile = null;

	private Thread updateThread = null;

	private Log log = LogFactory.getLog(this.getClass());

	private Messages messages;

	public DevicesFooterPanel(DeviceRoot deviceRoot, Messages messages) {
		setDeviceRootFile(deviceRoot);
		setMessages(messages);
	}

	private void setDeviceRootFile(DeviceRoot deviceRoot) {
		try {
			Iterable<File> rootFiles = deviceRoot.getRootFiles();
			for (File file : rootFiles) {
				rootFile = file;
				break;
			}
		} catch (Exception e) {
			log.error(e, e);
			rootFile = null;
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		updateThread = null;
		removeMessages(messages);
	}

	@Override
	public void internationalize(Messages messages) {
		String freeSpaceMessage = messages.getMessage("externalDevices.freeSpaceTitle");
		getTitleLabel().setText(freeSpaceMessage);

		Object[] capacityArray = new Object[] { getFormatedAvailableSpace(messages), getFormatedCapacity(messages) };
		String availableSpaceMessage = messages.getMessage("externalDevices.freeSpaceLabel", capacityArray);
		getSpaceLabel().setText(availableSpaceMessage);

		getSpaceBar().setToolTipText(freeSpaceMessage + " " + availableSpaceMessage);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.setName(NAME);
		this.setLayout(new GridBagLayout());

		GridBagConstraints topPanelConstraints = new GridBagConstraints();
		topPanelConstraints.gridx = 0;
		topPanelConstraints.gridy = 0;
		topPanelConstraints.weightx = 1;
		topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		topPanelConstraints.insets = TOP_PANEL_INSETS;

		GridBagConstraints separatorConstraints = new GridBagConstraints();
		separatorConstraints.gridx = 0;
		separatorConstraints.gridy = 1;
		separatorConstraints.weightx = 1;
		separatorConstraints.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints bottomConstraints = new GridBagConstraints();
		bottomConstraints.gridx = 0;
		bottomConstraints.gridy = 2;
		bottomConstraints.weightx = 1;
		bottomConstraints.fill = GridBagConstraints.HORIZONTAL;
		bottomConstraints.insets = BOTTOM_PANEL_INSETS;

		this.add(getTopPanel(), topPanelConstraints);
		this.add(getSeparator(), separatorConstraints);
		this.add(getBottomPanel(), bottomConstraints);

		updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (Thread.currentThread() == updateThread) {
					try {
						getSpaceBar().setValue(getUsedSpacePercentage());
						internationalize(messages);
					} catch (Exception e) {
						log.debug(e, e);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						log.error(e, e);
					}
				}
			}
		});
		updateThread.setName("DeviceSpaceUpdater");
		updateThread.setDaemon(true);
		updateThread.start();

	}

	private int getUsedSpacePercentage() {
		int percentage = 0;
		if (getDeviceCapacity() > 0) {
			percentage = (int) ((getDeviceUsedSpace() * 100) / getDeviceCapacity());
		}
		return percentage;
	}

	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setMinimumSize(DEFAULT_TOP_PANEL_SIZE);
			topPanel.setPreferredSize(DEFAULT_TOP_PANEL_SIZE);
			topPanel.setMaximumSize(DEFAULT_TOP_PANEL_SIZE);
			topPanel.setSize(DEFAULT_TOP_PANEL_SIZE);
			topPanel.setName(TOP_PANEL_NAME);
			topPanel.setLayout(new GridBagLayout());
			GridBagConstraints titleLabelConstraints = new GridBagConstraints();
			titleLabelConstraints.gridx = 0;
			titleLabelConstraints.gridy = 0;
			titleLabelConstraints.weightx = 1;
			titleLabelConstraints.weighty = 1;
			titleLabelConstraints.fill = GridBagConstraints.NONE;
			titleLabelConstraints.insets = TITLE_INSETS;
			GridBagConstraints spaceLabelConstraints = new GridBagConstraints();
			spaceLabelConstraints.gridx = 1;
			spaceLabelConstraints.gridy = 0;
			spaceLabelConstraints.weightx = 1;
			spaceLabelConstraints.weighty = 1;
			spaceLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			spaceLabelConstraints.insets = SPACE_INSETS;
			topPanel.add(getTitleLabel(), titleLabelConstraints);
			topPanel.add(getSpaceLabel(), spaceLabelConstraints);
		}
		return topPanel;
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setIcon(getDeviceIcon());
			Dimension preferredSize = new Dimension(85, 15);
			titleLabel.setPreferredSize(preferredSize);
			titleLabel.setMaximumSize(preferredSize);
			titleLabel.setMinimumSize(preferredSize);
			titleLabel.setSize(preferredSize);
			titleLabel.setName(SynthFonts.BOLD_FONT11_GRAY72_72_72);
		}
		return titleLabel;
	}

	private Icon getDeviceIcon() {
		try {
			// TODO: Define which icon will be shown, the default one or the one of the device
			// return UIManager.getDefaults().getIcon(device.getDeviceIcon());
			return UIManager.getDefaults().getIcon(DEFAULT_DEVICES_ICON);
		} catch (Exception e) {
			return null;
		}
	}

	private long getDeviceCapacity() {
		if (rootFile != null) {
			return rootFile.getTotalSpace();
		}
		return 0L;
	}

	private long getDeviceAvailableSpace() {
		if (rootFile != null) {
			return rootFile.getFreeSpace();
		}
		return 0L;
	}

	private long getDeviceUsedSpace() {
		return getDeviceCapacity() - getDeviceAvailableSpace();
	}

	private JLabel getSpaceLabel() {
		if (spaceLabel == null) {
			spaceLabel = new JLabel();
			spaceLabel.setHorizontalTextPosition(SwingConstants.LEFT);
			spaceLabel.setName(SynthFonts.PLAIN_FONT11_GRAY72_72_72);
		}
		return spaceLabel;
	}

	private JPanel getSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
			separator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
			separator.setMaximumSize(SEPARATOR_DEFAULT_SIZE);
			separator.setSize(SEPARATOR_DEFAULT_SIZE);
			separator.setName(NAME);
		}
		return separator;
	}

	private String getFormatedAvailableSpace(Messages messages) {
		return formatBytes(messages, getDeviceAvailableSpace());
	}

	private String getFormatedCapacity(Messages messages) {
		return formatBytes(messages, getDeviceCapacity());
	}

	protected String formatBytes(Messages messages, long bytes) {
		String formattedString = "";
		Float convertedBytes = 0F;
		Float floatBytes = new Float(bytes);
		if (bytes > GIGABYTE) {
			convertedBytes = floatBytes / GIGABYTE;
			formattedString = messages.getMessage("externalDevices.gigaBytes", Formatters.formatFloat(convertedBytes, 1));
		} else if (bytes > MEGABYTE) {
			convertedBytes = floatBytes / MEGABYTE;
			formattedString = messages.getMessage("externalDevices.megaBytes", Formatters.formatFloat(convertedBytes, 1));
		} else {
			convertedBytes = floatBytes / KILOBYTE;
			formattedString = messages.getMessage("externalDevices.kiloBytes", Formatters.formatFloat(convertedBytes, 1));
		}
		return formattedString;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setPreferredSize(BOTTOM_PANEL_SIZE);
			bottomPanel.setMaximumSize(BOTTOM_PANEL_SIZE);
			bottomPanel.setSize(BOTTOM_PANEL_SIZE);
			bottomPanel.setMinimumSize(BOTTOM_PANEL_SIZE);
			bottomPanel.setName(BOTTOM_PANEL_NAME);
			bottomPanel.setLayout(new GridBagLayout());
			GridBagConstraints sliderBarInsets = new GridBagConstraints();
			sliderBarInsets.gridx = 0;
			sliderBarInsets.gridy = 0;
			sliderBarInsets.weightx = 1;
			sliderBarInsets.weighty = 1;
			sliderBarInsets.fill = GridBagConstraints.HORIZONTAL;
			sliderBarInsets.insets = BAR_INSETS;
			bottomPanel.add(getSpaceBar(), sliderBarInsets);
		}
		return bottomPanel;
	}

	private JSlider getSpaceBar() {
		if (spaceBar == null) {
			spaceBar = new JSlider();
			spaceBar.setValue(0);
			spaceBar.setName(SPACE_BAR_NAME);
			spaceBar.setMaximum(100);
			spaceBar.setRequestFocusEnabled(false);
			spaceBar.setFocusable(false);
			spaceBar.setEnabled(false);
		}
		return spaceBar;
	}

}
