package com.all.client.view.flows;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.AddContactDialog.AddContactAction;
import com.all.client.view.dialog.AddContactDialog.AddContactResult;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.shared.model.ContactInfo;

public class AddContactFlow {

	private final DialogFactory dialogFactory;
	private final ViewEngine viewEngine;

	public AddContactFlow(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	public void executeAdd() {
		executeAdd(null);
	}

	public void executeAdd(String keyword) {
		AddContactResult result = dialogFactory.showAddContactDialog(keyword);
		AddContactAction action = result.getAction();
		if (AddContactAction.CANCEL == action) {
			return;
		}
		if (AddContactAction.ADD_AS_FRIEND == action) {
			executeRequest(result.getContact(), true);
		}
		if (AddContactAction.SEND_EMAIL == action) {
			dialogFactory.showSendInvitationDialog(result.getEmail());
		}
	}

	public void executeRequest(ContactInfo contact, final boolean fromContactFrame) {
		viewEngine.sendValueAction(Actions.Social.REQUEST_FRIENDSHIP, contact);
		;
	}
}
