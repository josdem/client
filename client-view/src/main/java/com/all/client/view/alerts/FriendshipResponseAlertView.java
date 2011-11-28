package com.all.client.view.alerts;

import static com.all.client.view.alerts.AlertView.IconType.CONTACT;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;
import com.all.shared.alert.Alert;
import com.all.shared.alert.FriendshipResponseAlert;

public class FriendshipResponseAlertView extends AlertView<FriendshipResponseAlert> {

	private static final long serialVersionUID = -8525377274508931937L;
	private final FriendshipResponseAlert friendshipResponseAlert;
	// Log log = LogFactory.getLog(this.getClass());
	private final ViewEngine viewEngine;

	public FriendshipResponseAlertView(Messages messages, FriendshipResponseAlert friendshipResponseAlert,
			ViewEngine viewEngine) {
		super(friendshipResponseAlert, CONTACT, messages);
		this.friendshipResponseAlert = friendshipResponseAlert;
		this.viewEngine = viewEngine;
	}

	@Override
	ButtonBar getButtonBar() {
		return ButtonBar.SINGLE;
	}

	@Override
	String getDescriptionMessage() {
		return messages
				.getMessage("friendshipResponseAlert.description", friendshipResponseAlert.getSender().getNickName());
	}

	@Override
	String getFooter() {
		return messages.getMessage("friendshipResponseAlert.footer");
	}

	@Override
	String getHeader() {
		return messages.getMessage("friendshipResponseAlert.header", friendshipResponseAlert.getSender().getNickName());
	}

	@Override
	void executeAccept() {
		viewEngine.send(Actions.Alerts.ALERT_ACTION_DELETE, new ValueAction<Alert>(getAlert()));
	}

	@Override
	void executeDeny() {
	}

	@Override
	void executeDetails() {
	}

}
