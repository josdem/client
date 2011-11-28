package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.newsfeed.QuoteFeed;

public class QuoteFeedView extends FeedView<QuoteFeed> {
	private static final long serialVersionUID = -8006202108445540754L;

	public QuoteFeedView(Messages messages, QuoteFeed feed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.QUOTE, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner = feed.getOwner();
		String gender = Gender.FEMALE == owner.getGender() ? "feed.import.female" : "feed.import.male";
		
		StringBuilder sbHeader = new StringBuilder();
		sbHeader.append(messages.getMessage("feed.changed"));
		sbHeader.append(messages.getMessage(gender));
		sbHeader.append(messages.getMessage("feed.quote"));
		
		StringBuilder sbDetails = new StringBuilder();
		sbDetails.append("\"");
		sbDetails.append(feed.getQuote());
		sbDetails.append("\"");
		
		clear();
		
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(sbHeader.toString());
		this.getDetailsPanel().appendText(sbDetails.toString());
		super.internationalize(messages);
	}

}