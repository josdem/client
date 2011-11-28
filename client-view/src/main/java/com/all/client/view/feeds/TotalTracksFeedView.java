package com.all.client.view.feeds;

import java.text.DecimalFormat;

import javax.swing.Icon;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.ShowProfileFlow;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.model.User;
import com.all.shared.newsfeed.TotalTracksFeed;

public class TotalTracksFeedView extends FeedView<TotalTracksFeed> {

	private static final long serialVersionUID = -2249182934976229631L;
	private final DialogFactory dialgFactory;

	protected TotalTracksFeedView(Messages messages, TotalTracksFeed feed, ViewEngine viewEngine,
			DialogFactory dialogFactory) {
		super(feed, FeedIconType.TOTAL_TRACKS, FeedSize.SMALL, messages, viewEngine, dialogFactory);
		dialgFactory = dialogFactory;
	}

	@Override
	public void internationalize(Messages messages) {
		clear();
		getHeaderPanel().appendContactInfo(feed.getOwner());
		getHeaderPanel().appendText(messages.getMessage("feed.totaltracks.header"));
		appendTracks();
		String gender = Gender.FEMALE == feed.getOwner().getGender() ? "feed.import.female" : "feed.import.male";
		getHeaderPanel().appendText(messages.getMessage("feed.totaltracks.headerTracks", messages.getMessage(gender)));
		super.internationalize(messages);
	}

	private void appendTracks() {
		User currentUser = viewEngine.get(Model.CURRENT_USER);
		ContactInfo currentContactInfo = new ContactInfo(currentUser);
		DecimalFormat formatter = new DecimalFormat("###,###,###");
		String trackCount = formatter.format(feed.getTrackCount());
		if (currentContactInfo.equals(feed.getOwner())) {
			appendOwnLinkLibraryLabel(trackCount + "", SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1);
		} else {
			appendOthersLinkLibraryLabel(feed.getOwner(), trackCount + "", SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1);
		}

	}

	private void appendOwnLinkLibraryLabel(String message, String style) {
		getHeaderPanel().add(
				new FeedActionLabel<ValueAction<ContainerView>>(message, style, Actions.View.setCurrentView, viewEngine,
						new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC))), getHeaderPanel().getContraints());
	}

	private void appendOthersLinkLibraryLabel(final ContactInfo contact, String message, String style) {
		appendOthersLibraryLinkLabel(contact, message, style, null);
	}

	private void appendOthersLibraryLinkLabel(final ContactInfo contact, String message, String style, Icon icon) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					new ShowProfileFlow(viewEngine, dialgFactory).execute(contact, getHeaderPanel());
				} catch (Exception e) {
				}
			}
		};

		getHeaderPanel().add(new FeedRunnableLabel(message, style, runnable, icon));
	}

}
