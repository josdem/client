package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.newsfeed.MediaImportFeed;

public class MediaImportFeedView extends FeedView<MediaImportFeed> {

	private static final long serialVersionUID = 5510126592520606880L;

	public MediaImportFeedView(MediaImportFeed feed, Messages messages, ViewEngine viewEngine,
			DialogFactory dialogFactory) {
		super(feed, feed.isFromItunes() ? FeedIconType.MEDIA_IMPORT_ITUNES : FeedIconType.MEDIA_IMPORT,
				FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages messages) {
		clear();
		internationalizeHeader(messages);
		internationalizeDetails(messages);
		super.internationalize(messages);
	}

	private void internationalizeDetails(Messages messages) {
		getDetailsPanel().appendLinkLibraryLabel(
				feed.getOwner(),
				getDetailsPanel().appendMediaAmountMessage(messages, feed.getTotalFolders(), feed.getTotalPlaylists(),
						feed.getTotalTracks()));
	}

	private void internationalizeHeader(Messages messages) {
		ContactInfo owner = feed.getOwner();
		this.getHeaderPanel().appendContactInfo(owner);
		String header = feed.isFromItunes() ? "feed.import.itunes.header" : "feed.import.header";
		String gender = Gender.FEMALE == owner.getGender() ? "feed.import.female" : "feed.import.male";
		this.getHeaderPanel().appendText(messages.getMessage(header, messages.getMessage(gender)));
	}

}
