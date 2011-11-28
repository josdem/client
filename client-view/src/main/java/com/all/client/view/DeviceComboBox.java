package com.all.client.view;

import java.awt.Dimension;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.DeviceBase;
import com.all.core.actions.Actions;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.Model;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;
import com.all.shared.model.Root;
import com.all.shared.model.Root.ContainerType;

public final class DeviceComboBox extends JPanel implements Internationalizable {

	private static final int LIBRARY_LABEL_X_INSET = 4;

	private static final int LIBRARY_LABEL_HEIGHT_INSET = 2;

	private static final int LIBRARY_LABEL_WIDTH_INSET = 20;

	private static final int LIBRARY_LABEL_Y_COORDINATE = 0;

	private static final long serialVersionUID = 1L;

	private static final int MENU_HEIGHT = 20;

	private static final int MENU_POSITION_X = 0;

	private static final int MENU_POSITION_Y = 16;

	private static final int MENU_WIDTH = 150;

	private static final Dimension MENU_MAXIMUM_SIZE = new Dimension(150, Integer.MAX_VALUE);

	private static final Dimension MENU_MINIMUM_SIZE = new Dimension(150, 0);

	private static final String DROP_DOWN_PANEL_NAME = "libraryDropDown";

	private static final String MENU_NO_RECENT_LIBRARIES = "No recent libraries.";

	private JLabel libraryLabel = null;

	private final ViewEngine viewEngine;

	private JPopupMenu menu;

	private final Observable<ObserveObject> onSelectDeviceEvent = new Observable<ObserveObject>();

	public DeviceComboBox(ViewEngine viewEngine, Messages messages) {
		this.viewEngine = viewEngine;
		initialize();
		internationalize(messages);
	}

	private void initialize() {
		this.setLayout(null);
		this.setName(DROP_DOWN_PANEL_NAME);
		this.add(getLibraryLabel());
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showMenu();
			}
		});
	}

	private JLabel getLibraryLabel() {
		if (libraryLabel == null) {
			libraryLabel = new JLabel();
			libraryLabel.setOpaque(false);
			libraryLabel.setVerticalAlignment(Label.CENTER);
			libraryLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return libraryLabel;
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		libraryLabel.setBounds(calculateBounds());
	}

	private Rectangle calculateBounds() {
		int x = this.getX() + LIBRARY_LABEL_X_INSET;
		int width = this.getWidth() - LIBRARY_LABEL_WIDTH_INSET;
		int height = this.getHeight() - LIBRARY_LABEL_HEIGHT_INSET;
		return new Rectangle(x, LIBRARY_LABEL_Y_COORDINATE, width, height);
	}

	private void showMenu() {
		createShowMenu();
		menu.removeAll();
		addDevicesToMenu();
		menu.show(this, MENU_POSITION_X, MENU_POSITION_Y);
	}

	private void addDevicesToMenu() {
		viewEngine.request(Actions.Devices.GET_DEVICES, null, new ResponseCallback<List<DeviceBase>>() {
			@Override
			public void onResponse(List<DeviceBase> devices) {
				int i = 0;
				for (DeviceBase device : devices) {
					Root currentRoot = viewEngine.get(Model.SELECTED_ROOT);
					boolean isDevice = ContainerType.DEVICE.equals(currentRoot.getType());
					boolean isCurrentSelectedDevice = isDevice && (device == null ? false : device.getDeviceRoot().equals(currentRoot));
					if (!isCurrentSelectedDevice) {
						JMenuItem menuItem = new JMenuItem(device.getDeviceRoot().getName());
						menuItem.addActionListener(new MenuItemListener(device.getDeviceRoot()));
						menu.add(menuItem);
						i++;
					}
					menu.setPreferredSize(new Dimension(getWidth(), i * MENU_HEIGHT));
					menu.setSize(new Dimension(MENU_WIDTH, i * MENU_HEIGHT));
				}
				if (i == 0) {
					menu.add(MENU_NO_RECENT_LIBRARIES);
					i = 1;
				}
			}
		});
	}

	private void createShowMenu() {
		if (menu == null) {
			menu = new JPopupMenu();
			menu.setMinimumSize(MENU_MINIMUM_SIZE);
			menu.setMaximumSize(MENU_MAXIMUM_SIZE);
		}
	}

	private final class MenuItemListener implements ActionListener {
		private final Root library;

		public MenuItemListener(Root library) {
			this.library = library;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			viewEngine.send(Actions.View.SELECT_TRACKCONTAINER, new SelectTrackContainerAction(library, library));
			onSelectDeviceEvent.fire(ObserveObject.EMPTY);
		}
	}

	public void setText(String text) {
		getLibraryLabel().setText(text);
	}

	@Override
	public void internationalize(Messages messages) {
		setText(messages.getMessage("devices.combobox.title"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	ObserverCollection<ObserveObject> onSelectDevice() {
		return onSelectDeviceEvent;
	}
}
