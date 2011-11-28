package com.all.client.view.actions;

import java.awt.Component;

import com.all.action.ActionObject;
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

public class ContactFeedAction extends ActionObject{
	
	private ViewEngine viewEngine;
	
	private DialogFactory dialogFactory;

	public ContactFeedAction(){
		
	}
	
	public void getAction(final ContactInfo contact, final Component component, Boolean accessible) {
		if (accessible) {
			if(!contact.getEmail().equals(viewEngine.get(Model.CURRENT_USER).getEmail())){
				viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(contact
						.getEmail()));
			} else {
				viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(contact, new ComposeView(Views.PROFILE, SubViews.ALL)));
			}
		} else {
			dialogFactory.showAddAsAFriendProfileDialog(contact, component);
		}
	}
	
	
}
