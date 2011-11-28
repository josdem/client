package com.all.client.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.ContactRoot;
import com.all.client.model.DeviceBase;
import com.all.client.model.DeviceRoot;
import com.all.client.view.components.DevicesPanel;
import com.all.client.view.components.ExternalDevicePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.Root;
import com.all.shared.model.Root.ContainerType;

@Service
public class LibraryPanelFactory {

	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private Messages messages;
	@Autowired
	private MultiLayerDropTargetListener multiLayer;
	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private DevicesPanel devicesPanel;

	public LibraryPanel createLibraryPanel(Root root) {
		LibraryPanel libraryPanel = null;
		final MediaPanel mediaPanel = new MediaPanel();
		if (root.getType() == ContainerType.REMOTE || root.getType() == ContainerType.CONTACT) {
			PreviewPanel previewPanel = new ContactPreviewPanel(root, viewEngine);

			ContentTitlePanel contentTitlePanel = new ContactContentTitlePanel(root, viewEngine);

			ContentPanel contentPanel = new ContentPanel(contentTitlePanel, previewPanel);
			final TitleHeaderPanel titleHeaderPanel = new ContactTitleHeaderPanel(root, viewEngine);

			HeaderPanel headerPanel = new HeaderPanel(mediaPanel, titleHeaderPanel);
			
			FooterPanel footerPanel = new ContactFooterPanel();

			CollapsedLibraryPanel collapsedLibraryPanel = new ContactCollapsedLibraryPanel(root, messages);

			libraryPanel = new ContactLibraryPanel(contentPanel, headerPanel, footerPanel, collapsedLibraryPanel, root,
					multiLayer);
			libraryPanel.onClose().add(new Observer<ObservValue<Root>>() {
				@Override
				public void observe(ObservValue<Root> eventArgs) {
					Root contactValue = eventArgs.getValue();
					if (contactValue instanceof ContactRoot) {
						String email = ((ContactRoot) contactValue).getOwner().getEmail();
						viewEngine.sendValueAction(Actions.Library.CANCEL_SYNC_DOWNLOAD, email);
					}

				}
			});
		} else if (root.getType() == ContainerType.DEVICE) {
			DevicePreviewPanel previewPanel = new DevicePreviewPanel(root, multiLayer, dialogFactory, viewEngine);

			ContentTitlePanel titlePanel = new DeviceContentTitlePanel(root, multiLayer, viewEngine, previewPanel,
					dialogFactory);
			ContentPanel contentPanel = new ContentPanel(titlePanel, previewPanel);

			final TitleHeaderPanel titleHeaderPanel = new DeviceTitleHeaderPanel(root);

			final MediaPanel mediaPanel2 = new MediaPanel(false, messages, devicesPanel, viewEngine);
			
			HeaderPanel headerPanel = new HeaderPanel(mediaPanel2, titleHeaderPanel);
			
			FooterPanel footerPanel = new DevicesFooterPanel((DeviceRoot) root, messages);

			CollapsedLibraryPanel collapsedLibraryPanel = new ContactCollapsedLibraryPanel(root, messages);

			libraryPanel = new ContactLibraryPanel(contentPanel, headerPanel, footerPanel, collapsedLibraryPanel, root,
					multiLayer);
			libraryPanel.onClose().add(new Observer<ObservValue<Root>>() {
				@Override
				public void observe(ObservValue<Root> eventArgs) {
					viewEngine.request(Actions.Devices.GET_DEVICES, null, new ResponseCallback<List<DeviceBase>>() {
						@Override
						public void onResponse(List<DeviceBase> devices) {
							for (DeviceBase deviceBase : devices) {
								if (deviceBase.getDeviceRoot().equals(viewEngine.get(Model.SELECTED_ROOT))) {
									ExternalDevicePanel externalDevicePanel = devicesPanel.searchDevicePanel(deviceBase);
									externalDevicePanel.unselectPanel();
									break;
								}
							}
						}
					});
				}
			});

		}
		if (libraryPanel != null) {
			libraryPanel.setMessages(messages);
			libraryPanel.initialize(viewEngine);
		}
		return libraryPanel;
	}
}
