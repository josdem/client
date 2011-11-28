package com.all.client.view.flows;

import java.awt.Component;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.actions.ComposeView;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.model.Model;
import com.all.core.model.SubViews;
import com.all.core.model.Views;
import com.all.shared.model.ContactInfo;

public class ShowContactFeedInfoFlow {

	private final ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	public ShowContactFeedInfoFlow(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	public void execute(final ContactInfo contact, final Component component) {
		viewEngine.request(Actions.Social.IS_CONTACT_ACCESSIBLE, contact, new ResponseCallback<Boolean>() {
			@Override
			public void onResponse(Boolean accessible) {
				if (accessible) {
					if(!contact.getEmail().equals(viewEngine.get(Model.CURRENT_USER).getEmail())){
						viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(contact.getEmail()));
					} else {
						viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(contact, new ComposeView(Views.PROFILE, SubViews.ALL)));
					}
				} else {
					dialogFactory.showAddAsAFriendProfileDialog(contact, component);
				}
			}

		});
	}

}
