package com.all.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.DevicesPanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.DownloadButtonDropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.dnd.TrashDnDListener;
import com.all.commons.Environment;
import com.all.core.actions.Actions;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

@Component
public class LocalFooterPanel extends FooterPanel {

	private static final long serialVersionUID = 1L;

	private static final int ICON_GAP = 3;

	private static final Dimension MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 26);

	private static final Dimension MINIMUM_SIZE = new Dimension(0, 26);

	private static final Insets TRASH_BUTTON_INSETS = new Insets(2, 2, 2, 14);

	private static final String TRASH_BUTTON_NAME = "trashButton";

	private static final String TRASH_BUTTON_LINUX_NAME = "trashButtonLinux";

	private static final String TRASH_PANEL_NAME = "trashPanel";

	private static final String NAME = "previewTreeBackground";

	@Autowired
	private DevicesPanel devicesPanel;
	@Autowired
	private ViewEngine viewEngine;

	private JToggleButton downloadUploadButton;

	private JToggleButton trashButton = null;

	private JPanel trashPanel = null;

	public LocalFooterPanel() {
		
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	@Autowired
	public void setDragAndDrops(MultiLayerDropTargetListener multiLayer, DialogFactory dialogFactory) {
		TrashDnDListener trashDnDListener = new TrashDnDListener(trashButton, viewEngine, dialogFactory);
		multiLayer.addDragListener(trashButton, trashDnDListener);
		multiLayer.addDropListener(trashButton, trashDnDListener);
		DownloadButtonDropListener downloadListener = new DownloadButtonDropListener(downloadUploadButton, viewEngine);
		multiLayer.addDragListener(downloadUploadButton, downloadListener);
		multiLayer.addDropListener(downloadUploadButton, downloadListener);
	}

	@EventMethod(Model.CURRENT_VIEW_ID)
	public void onModelViewCurrentViewChanged(ValueEvent<Views> eventArgs) {
		getTrashButton().setEnabled(eventArgs.getValue() != Views.TRASH);
		getTrashButton().setSelected(eventArgs.getValue() == Views.TRASH);

	}

	@Autowired
	@Override
	public void initialize(ViewEngine viewEngine) {
		this.setName(NAME);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getSeparator(1));
		this.add(getTrashPanel());
		this.add(getSeparator(1));
		setup();
	}

	long dateFSShowed;

	private void setup() {
		getTrashButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Root root = viewEngine.get(Model.USER_ROOT);
				viewEngine.send(Actions.View.SELECT_TRACKCONTAINER, new SelectTrackContainerAction(root, null));
			}
		});
		dateFSShowed = System.currentTimeMillis();
		devicesPanel.getTitleLabel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ((System.currentTimeMillis() - dateFSShowed) > 2000) {
					dateFSShowed = System.currentTimeMillis();
				}
			}
		});
		getTrashButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.TRASH)));
			}
		});

	}

	@Override
	public void internationalize(Messages messages) {
//		String devicesLabel = messages.getMessage("externalDevices.label");
		String trashLabel = messages.getMessage("trash.label");
//		getDevicesPanel().setTitle(devicesLabel);
		getTrashButton().setText(trashLabel);
		getTrashButton().setToolTipText(messages.getMessage("tooltip.recycle"));
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	private JPanel getSeparator(int height) {
		JPanel separator = new JPanel();
		separator.setPreferredSize(new Dimension(0, height));
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		separator.setMinimumSize(new Dimension(0, height));
		separator.setName(NAME);
		return separator;
	}


	private JPanel getTrashPanel() {
		if (trashPanel == null) {
			trashPanel = new JPanel();
			trashPanel.setLayout(new BoxLayout(trashPanel, BoxLayout.X_AXIS));
			trashPanel.setPreferredSize(MINIMUM_SIZE);
			trashPanel.setMinimumSize(MINIMUM_SIZE);
			trashPanel.setMaximumSize(MAXIMUM_SIZE);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1.0;
			trashPanel.add(getTrashButton());
			trashPanel.setName(TRASH_PANEL_NAME);
		}
		return trashPanel;
	}

	private JToggleButton getTrashButton() {
		if (trashButton == null) {
			trashButton = new JToggleButton();
			trashButton.setBackground(Color.DARK_GRAY);
			trashButton.setPreferredSize(MINIMUM_SIZE);
			trashButton.setMaximumSize(MAXIMUM_SIZE);
			trashButton.setMinimumSize(MINIMUM_SIZE);
			trashButton.setHorizontalAlignment(SwingConstants.LEFT);
			trashButton.setMargin(TRASH_BUTTON_INSETS);
			trashButton.setIconTextGap(ICON_GAP);
			if (!Environment.isLinux()) {
				trashButton.setName(TRASH_BUTTON_NAME);
			} else {
				trashButton.setName(TRASH_BUTTON_LINUX_NAME);
			}
		}
		return trashButton;
	}

}
