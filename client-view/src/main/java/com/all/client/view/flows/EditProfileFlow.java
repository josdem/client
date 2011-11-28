package com.all.client.view.flows;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.actions.UpdateProfileAction;
import com.all.core.common.bean.UpdateUserCommand;

public class EditProfileFlow {

	private final ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	public EditProfileFlow(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	public void execute(boolean fromMainFrame) {
		UpdateUserCommand userCommand = dialogFactory.showEditProfiledialog(fromMainFrame);
		UpdateProfileAction action = new UpdateProfileAction(userCommand.toUser(), userCommand.getAvatar());
		viewEngine.send(Actions.UserProfile.UPDATE_PROFILE, action);
	}

}
