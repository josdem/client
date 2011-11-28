package com.all.client.view.components;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.DeviceBase;
import com.all.client.view.View;
import com.all.core.actions.Actions;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.common.view.SynthFonts;
import com.all.shared.model.Root;

public class ExternalDevicePanel extends JPanel implements View {

	private static final long serialVersionUID = -479956329058815902L;

	private static final Dimension PANEL_SIZE = new Dimension(198, 25);

	private static final Rectangle DEVICE_LABEL_BOUNDS = new Rectangle(29, 4, 135, 16);

	private static final Rectangle EJECT_BUTTON_BOUNDS = new Rectangle(169, 4, 16, 16);

	private static final String DEVICE_NAME_UNAVAILABLE = "Device Name Unavailable";

	private static final String EJECT_DEVICE_BUTTON_NAME = "ejectDeviceButton";

	private static final String NAME = "externalDeviceNormalPanel";

	private final DeviceBase device;

	private JButton ejectDeviceButton;

	private JLabel deviceLabel;

	private Log log = LogFactory.getLog(this.getClass());

	private final ViewEngine viewEngine;

	public ExternalDevicePanel(DeviceBase device, ViewEngine viewEngine) {
		super(null);
		this.device = device;
		this.viewEngine = viewEngine;
		this.setMinimumSize(PANEL_SIZE);
		this.setMaximumSize(PANEL_SIZE);
		this.setPreferredSize(PANEL_SIZE);
		this.setSize(PANEL_SIZE);
		this.setName(NAME);
		this.add(getDeviceLabel());
		this.add(getEjectDeviceButton());
	}

	public JLabel getDeviceLabel() {
		if (deviceLabel == null) {
			deviceLabel = new JLabel();
			deviceLabel.setIcon(getDeviceIcon());
			deviceLabel.setText(getDeviceName());
			deviceLabel.setName(SynthFonts.BOLD_FONT11_GRAY64_64_64);
			deviceLabel.setBounds(DEVICE_LABEL_BOUNDS);
			deviceLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					selectDevice();
				}
			});
		}
		return deviceLabel;
	}

	private String getDeviceName() {
		try {
			return device.getDeviceRoot().getName();
		} catch (Exception e) {
			return DEVICE_NAME_UNAVAILABLE;
		}
	}

	private Icon getDeviceIcon() {
		try {
			return UIManager.getDefaults().getIcon(device.getDeviceIcon());
		} catch (Exception e) {
			return null;
		}
	}

	private JButton getEjectDeviceButton() {
		if (ejectDeviceButton == null) {
			ejectDeviceButton = new JButton();
			ejectDeviceButton.setName(EJECT_DEVICE_BUTTON_NAME);
			ejectDeviceButton.setBounds(EJECT_BUTTON_BOUNDS);
			ejectDeviceButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ejectDevice();
				}
			});
		}
		return ejectDeviceButton;
	}

	public final void unselectPanel() {
		this.setName(NAME);
	}

	public final void selectDevice() {
		this.setName("externalDeviceSelectedPanel");
		viewEngine.send(Actions.View.SELECT_TRACKCONTAINER, new SelectTrackContainerAction(device.getDeviceRoot(), null));
	}

	private void ejectDevice() {
		log.info("Ejecting device " + device);
		viewEngine.send(Actions.Library.LIBRARY_ROOT_REMOVED, new ValueAction<Root>(device.getDeviceRoot()));
		viewEngine.send(Actions.Devices.DISCONECT, new ValueAction<DeviceBase>(device));
	}

	public DeviceBase getDevice() {
		return device;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
	}

	@Override
	public void destroy(ViewEngine viewEngine) {

	}

}
