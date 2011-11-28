package com.all.client.view.feeds;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.newsfeed.ContactsFeed;

public class ContactsFeedView extends FeedView<ContactsFeed> {
	private static final long serialVersionUID = 2235074156356178479L;


	public ContactsFeedView(Messages messages, ContactsFeed feed, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.CONTACTS, FeedSize.SMALL, messages, viewEngine, dialogFactory);
		initialize(FeedSize.SMALL);
	}
	
	
	@Override
	public void internationalize(Messages messages) {
		ContactInfo owner = feed.getOwner();
		StringBuilder contacts = new StringBuilder().append(feed.getTotalContacts());
		
		clear();
		
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(messages.getMessage("feed.contacts"));
		this.getHeaderPanel().appendText(contacts.toString());
		this.getHeaderPanel().appendText(messages.getMessage("feed.contacts.friends"));
		super.internationalize(messages);
	}
	
}
