package com.all.client.view.flows;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.actions.CloseType;

public class CloseFlow {
	private final ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	public CloseFlow(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	public void logout() {
		close(CloseType.LOGOUT);

	}

	private void close(final CloseType type) {
		viewEngine.request(Actions.Downloads.IS_UPLOADING, new ResponseCallback<Boolean>() {
			@Override
			public void onResponse(Boolean uploading) {
				if (!uploading || dialogFactory.showCloseApplicationDialog()) {
					viewEngine.send(Actions.View.HIDE_DRAWER);
					viewEngine.send(Actions.Player.STOP);
					viewEngine.sendValueAction(Actions.Application.APP_CLOSE, type);
				}
			}
		});
	}

	public void close() {
		close(CloseType.EXIT);
	}

}
