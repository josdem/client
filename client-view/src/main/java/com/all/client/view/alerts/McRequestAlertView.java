package com.all.client.view.alerts;

import static com.all.client.view.alerts.AlertView.IconType.MC_REQUEST;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;
import com.all.shared.alert.McRequestAlert;

public class McRequestAlertView extends AlertView<McRequestAlert> {

	private static final long serialVersionUID = 1L;
	private final McRequestAlert mcRequestAlert;
	private ViewEngine viewEngine;

	public McRequestAlertView(Messages messages, McRequestAlert mcRequestAlert, ViewEngine viewEngine) {
		super(mcRequestAlert, MC_REQUEST, messages);
		this.mcRequestAlert = mcRequestAlert;
		this.viewEngine = viewEngine;
	}

	@Override
	ButtonBar getButtonBar() {
		return ButtonBar.STANDARD;
	}

	@Override
	String getHeader() {
		return messages.getMessage("mcRequestAlert.header", mcRequestAlert.getSender().getNickName());
	}

	@Override
	String getDescriptionMessage() {
		return messages.getMessage("mcRequestAlert.defaultMessage", mcRequestAlert.getSender().getNickName());
	}

	@Override
	String getFooter() {
		return messages.getMessage("mcRequestAlert.question", mcRequestAlert.getModel().trackCount() + "");
	}

	@Override
	void executeAccept() {
		viewEngine.sendValueAction(Actions.Alerts.ALERT_ACTION_ACCEPT, getAlert());
		// alertController.accept(getAlert());
	}

	@Override
	void executeDeny() {

		// alertController.delete(getAlert());
	}

	@Override
	void executeDetails() {
		// Do nothing
	}

}
