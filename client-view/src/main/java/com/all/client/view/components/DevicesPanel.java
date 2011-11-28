package com.all.client.view.components;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.PopupMenu;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.DeviceBase;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

@org.springframework.stereotype.Component
public final class DevicesPanel extends JPanel implements Internationalizable {

	private static final String EXTERNAL_DEVICES_LABEL = "externalDevices.label";

	private static final Dimension TITLE_CONTAINER_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 25);

	private static final String NOT_IMPLEMENTED = "NOT IMPLEMENTED";

	private static final int MAX_VISIBLE_DEVICES = 6;

	private static final long serialVersionUID = 1L;

	private static final Dimension ARROW_BUTTON_DEFAULT_SIZE = new Dimension(8, 8);

	private static final Dimension BOTTOM_SEPARATOR_MAXIMUM_SIZE = new Dimension(2147483647, 0);

	private static final Dimension BOTTOM_SEPARATOR_PREFERRED_SIZE = new Dimension(1, 0);

	private static final Dimension MINIMUM_SIZE = new Dimension(0, 0);

	private static final Dimension TITLE_CONTAINER_DEFAULT_SIZE = new Dimension(1, 25);

	private static final Dimension TITLE_CONTAINER_MINIMUM_SIZE = new Dimension(0, 25);

	private static final Insets ARROW_INSETS = new Insets(2, 6, 2, 0);

	private static final Insets TITLE_INSETS = new Insets(1, 6, 1, 0);

	private static final String COLLAPSABLE_PANEL_SEPARATOR_STYLE = "collapsablePanelSeparator";

	private static final String COLLAPSABLE_PANEL_COLLAPSED_BUTTON_STYLE = "collapsablePanelCollapsedButton";

	private static final String COLLAPSABLE_PANEL_EXPANDED_BUTTON_STYLE = "collapsablePanelExpandedButton";

	private static final String COLLAPSABLE_PANEL_FLAT_STYLE = "collapsablePanelFlat";

	private int devicesLoaded;

	private boolean expanded = false;

	private JButton arrowButton = null;

	private JLabel titleLabel = null;

	private JPanel bottomSeparator = null;

	private JPanel titleContainer = null;

	private JPanel contentContainer;

	@Autowired
	private ViewEngine viewEngine;

	private Log log = LogFactory.getLog(this.getClass());

	public DevicesPanel() {
		super();
		initialize();
		setup();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setName(COLLAPSABLE_PANEL_FLAT_STYLE);
		this.setIcon("ExternalDevices.icon", 3);

		GridBagConstraints title = new GridBagConstraints();
		title.gridx = 0;
		title.gridy = 0;
		title.weightx = 1;
		title.weighty = 0;
		title.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints content = new GridBagConstraints();
		content.gridx = 0;
		content.gridy = 1;
		content.weightx = 1;
		content.weighty = 0;
		content.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints bottom = new GridBagConstraints();
		bottom.gridx = 0;
		bottom.gridy = 2;
		bottom.weightx = 0;
		bottom.weighty = 1;
		bottom.fill = GridBagConstraints.HORIZONTAL;

		super.add(getTitleContainer(), title);
		super.add(getBottomSeparator(), content);
		super.add(content(), bottom);

		devicesLoaded = 0;
	}

	private void setup() {
		getTitleContainer().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				click();
			}
		});
		getArrowButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				click();
			}
		});
		expandOrCollapse();
	}

	@EventMethod(Events.Library.LIBRARY_ROOT_ADDED_ID)
	public void onLibraryAdded(Root root) {
		unselectPanels(root);
	}

	@EventMethod(Events.Devices.ADD_DEVICE_ID)
	protected void addDevice(ValueEvent<DeviceBase> device) {
		if (devicesLoaded >= MAX_VISIBLE_DEVICES) {
			log.info("THE MAXIMUM AMOUNT OF DEVICES IS " + MAX_VISIBLE_DEVICES);
			// TODO: implement the action needed when trying to add another
			// device
			return;
		}
		ExternalDevicePanel panel = new ExternalDevicePanel(device.getValue(), viewEngine);
		panel.initialize(viewEngine);
		content().add(panel);
		devicesLoaded++;
		expand();
		recalculate();
	}

	protected void recalculate() {
		if (content().isVisible()) {
			content().invalidate();
			Window win = SwingUtilities.getWindowAncestor(content());
			if (win == null) {
				this.validate();
			} else {
				win.validate();
			}
		}
	}

	@EventMethod(Events.Devices.REMOVE_DEVICE_ID)
	protected void removeDevice(ValueEvent<DeviceBase> device) {
		ExternalDevicePanel panelToRemove = searchDevicePanel(device.getValue());
		if (panelToRemove != null) {
			content().remove(panelToRemove);
			devicesLoaded--;
			recalculate();
		}
	}

	public ExternalDevicePanel searchDevicePanel(DeviceBase device) {
		for (Component component : contentContainer.getComponents()) {
			if (component instanceof ExternalDevicePanel) {
				ExternalDevicePanel panel = (ExternalDevicePanel) component;
				if (panel.getDevice().equals(device)) {
					return panel;
				}
			}
		}
		return null;
	}

	protected void unselectPanels(Root root) {
		for (Component component : content().getComponents()) {
			if (component instanceof ExternalDevicePanel) {
				ExternalDevicePanel devicePanel = (ExternalDevicePanel) component;
				if (!devicePanel.getDevice().getDeviceRoot().equals(root)) {
					devicePanel.unselectPanel();
				}
			}
		}
	}

	public JPanel content() {
		if (contentContainer == null) {
			contentContainer = new JPanel();
			contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
		}
		return contentContainer;
	}

	private JPanel getTitleContainer() {
		if (titleContainer == null) {
			titleContainer = new JPanel();
			titleContainer.setLayout(new GridBagLayout());
			titleContainer.setMaximumSize(TITLE_CONTAINER_MAXIMUM_SIZE);
			titleContainer.setMinimumSize(TITLE_CONTAINER_MINIMUM_SIZE);
			titleContainer.setPreferredSize(TITLE_CONTAINER_DEFAULT_SIZE);
			titleContainer.setSize(TITLE_CONTAINER_DEFAULT_SIZE);
			titleContainer.setName(COLLAPSABLE_PANEL_FLAT_STYLE);
			titleContainer.add(getTitleLabel(), getTitleContraints());
			titleContainer.add(getArrowButton(), getArrowConstraints());
		}
		return titleContainer;
	}

	private GridBagConstraints getTitleContraints() {
		GridBagConstraints titleConstraints = new GridBagConstraints();
		titleConstraints.gridx = 1;
		titleConstraints.gridy = 0;
		titleConstraints.weightx = 1.0;
		titleConstraints.fill = GridBagConstraints.HORIZONTAL;
		titleConstraints.insets = TITLE_INSETS;
		return titleConstraints;
	}

	private GridBagConstraints getArrowConstraints() {
		GridBagConstraints arrowConstraints = new GridBagConstraints();
		arrowConstraints.gridx = 0;
		arrowConstraints.gridy = 0;
		arrowConstraints.fill = GridBagConstraints.NONE;
		arrowConstraints.insets = ARROW_INSETS;
		return arrowConstraints;
	}

	public JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			// titleLabel.setText(EMPT Y_TITLE);
			titleLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			titleLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			titleLabel.setName(SynthFonts.BOLD_FONT11_GRAY64_64_64);
		}
		log.debug(Thread.currentThread().getName() + " - getTitleLabel() Called, value is: '" + titleLabel.getText() + "'.");
		return titleLabel;
	}

	public void setIcon(String icon, int gap) {
		Icon iconImage = UIManager.getDefaults().getIcon(icon);
		getTitleLabel().setIconTextGap(gap);
		getTitleLabel().setIcon(iconImage);
	}

	private JButton getArrowButton() {
		if (arrowButton == null) {
			arrowButton = new JButton();
			arrowButton.setMaximumSize(ARROW_BUTTON_DEFAULT_SIZE);
			arrowButton.setPreferredSize(ARROW_BUTTON_DEFAULT_SIZE);
			arrowButton.setMinimumSize(ARROW_BUTTON_DEFAULT_SIZE);
		}
		return arrowButton;
	}

	public void setTitle(String string) {
		getTitleLabel().setText(string);
		log.debug(Thread.currentThread().getName() + " - setTitle(): '" + string + "'");
	}

	private JPanel getBottomSeparator() {
		if (bottomSeparator == null) {
			bottomSeparator = new JPanel();
			bottomSeparator.setPreferredSize(BOTTOM_SEPARATOR_PREFERRED_SIZE);
			bottomSeparator.setMaximumSize(BOTTOM_SEPARATOR_MAXIMUM_SIZE);
			bottomSeparator.setMinimumSize(MINIMUM_SIZE);
			bottomSeparator.setName(COLLAPSABLE_PANEL_SEPARATOR_STYLE);
		}
		return bottomSeparator;
	}

	private void click() {
		if (expanded) {
			collapse();
		} else {
			expand();
		}
	}

	public void collapse() {
		this.expanded = false;
		expandOrCollapse();
	}

	public void expand() {
		this.expanded = true;
		expandOrCollapse();
	}

	private void expandOrCollapse() {
		content().setVisible(expanded);
		arrowButton.setName(expanded ? COLLAPSABLE_PANEL_EXPANDED_BUTTON_STYLE
				: COLLAPSABLE_PANEL_COLLAPSED_BUTTON_STYLE);
	}

	public int getDevicesLoaded() {
		return devicesLoaded;
	}

	@Override
	public Component add(Component comp) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public Component add(Component comp, int index) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public void add(Component comp, Object constraints) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public synchronized void add(PopupMenu popup) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public Component add(String name, Component comp) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public void remove(Component comp) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public void remove(int index) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public synchronized void remove(MenuComponent popup) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public void removeAll() {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

	@Override
	public void internationalize(Messages messages) {
		setTitle(messages.getMessage(EXTERNAL_DEVICES_LABEL));
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
