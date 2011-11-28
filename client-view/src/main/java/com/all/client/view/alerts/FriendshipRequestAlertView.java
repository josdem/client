package com.all.client.view.alerts;

import static com.all.client.view.alerts.AlertView.IconType.CONTACT;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;
import com.all.shared.alert.Alert;
import com.all.shared.alert.FriendshipRequestAlert;

public class FriendshipRequestAlertView extends AlertView<FriendshipRequestAlert> {
	private static final long serialVersionUID = -8006202108445540754L;
	private final FriendshipRequestAlert friendshipRequestAlert;
	private ViewEngine viewEngine;

	public FriendshipRequestAlertView(Messages messages, FriendshipRequestAlert friendshipRequestAlert,
			ViewEngine viewEngine) {
		super(friendshipRequestAlert, CONTACT, messages);
		this.friendshipRequestAlert = friendshipRequestAlert;
		this.viewEngine = viewEngine;
	}

	@Override
	ButtonBar getButtonBar() {
		return ButtonBar.STANDARD;
	}

	@Override
	String getDescriptionMessage() {
		return messages.getMessage("friendshipRequestAlert.description");
	}

	@Override
	String getFooter() {
		return messages.getMessage("friendshipRequestAlert.questionMessage");
	}

	@Override
	String getHeader() {
		return messages.getMessage("friendshipRequestAlert.header", friendshipRequestAlert.getSender().getNickName());
	}

	@Override
	void executeAccept() {
		viewEngine.send(Actions.Alerts.ALERT_ACTION_ACCEPT, new ValueAction<Alert>(friendshipRequestAlert));
	}

	@Override
	void executeDeny() {
		viewEngine.send(Actions.Alerts.ALERT_ACTION_DELETE, new ValueAction<Alert>(friendshipRequestAlert));
	}

	@Override
	void executeDetails() {
	}

}
