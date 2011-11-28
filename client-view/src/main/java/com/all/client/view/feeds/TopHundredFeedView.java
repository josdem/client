package com.all.client.view.feeds;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.ContainerView;
import com.all.core.model.Views;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.newsfeed.TopHundredFeed;

public class TopHundredFeedView extends FeedView<TopHundredFeed> {

	private static final long serialVersionUID = 5510126592520606880L;

	public TopHundredFeedView(TopHundredFeed feed, Messages messages, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.TOP_HUNDRED, FeedSize.MEDIUM, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages messages) {
		clear();
		internationalizeHeader(messages);
		internationalizeDetails(messages);
		super.internationalize(messages);
	}

	private void internationalizeDetails(Messages messages) {
		getDetailsPanel().appendTopHundredPlaylistLinkLabel(feed.getPlaylistName(), feed.getPlaylistHash(), feed.getCategoryId());
		getDetailsPanel().appendTopHundredCategoryLinkLabel("(" + feed.getCategoryName() + ")", feed.getCategoryId());
	}

	private void internationalizeHeader(Messages messages) {
		ContactInfo owner = feed.getOwner();
		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(messages.getMessage("feed.hundred.header.first"));
		appendTopHundredLinkLabel();
		this.getHeaderPanel().appendText(messages.getMessage("feed.hundred.header.second"));
	}

	public void appendTopHundredLinkLabel() {
		this.getHeaderPanel().add(
				new FeedActionLabel<ValueAction<ContainerView>>("Top 100", SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1,
						Actions.View.setCurrentView, viewEngine, new ValueAction<ContainerView>(new ContainerView(
								Views.HUNDRED))), this.getHeaderPanel().getContraints());
	}

}
