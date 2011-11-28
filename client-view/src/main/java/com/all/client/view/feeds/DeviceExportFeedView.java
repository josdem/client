package com.all.client.view.feeds;

import java.awt.Dimension;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.newsfeed.DeviceExportFeed;

public class DeviceExportFeedView extends FeedView<DeviceExportFeed> {

	private static final long serialVersionUID = -6193483455071762108L;

	private static final String HEADER_KEY = "feed.deviceExport.exported.";


	public DeviceExportFeedView(Messages messages, DeviceExportFeed deviceExportFeed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(deviceExportFeed, FeedIconType.EXPORT_TO_DEVICE, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages messages) {

		ContactInfo owner = feed.getOwner();
		String keyMessage = owner.getGender() == Gender.FEMALE ? Gender.FEMALE.getLabel() : Gender.MALE.getLabel();

		clear();
		
		getHeaderPanel().appendContactInfo(feed.getOwner());
		getHeaderPanel().appendText(messages.getMessage(HEADER_KEY + keyMessage));

		getDetailsPanel().setPreferredSize(new Dimension(getDetailsPanel().getWidth(), 25));
		getDetailsPanel().appendLinkLibraryLabel(
				owner,
				getDetailsPanel().appendMediaAmountMessage(messages, feed.getFolderCount(),
						feed.getPlaylistCount(), feed.getTrackCount()));
		super.internationalize(messages);
	}
}
