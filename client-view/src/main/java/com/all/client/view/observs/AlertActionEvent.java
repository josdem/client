package com.all.client.view.observs;

import com.all.observ.ObserveObject;

public class AlertActionEvent extends ObserveObject {

	private static final long serialVersionUID = -5909200846712158334L;
	private final AlertAction alertAction;

	public enum AlertAction {
		ACCEPT, DENY, LATER, DETAILS;
	};

	public AlertActionEvent(AlertAction alertButton) {
		this.alertAction = alertButton;
	}

	public AlertAction getAlertAction() {
		return alertAction;
	}
}
