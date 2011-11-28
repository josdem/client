package com.all.client.view.alerts;

import static com.all.client.view.alerts.AlertView.IconType.MUSIC;

import org.apache.commons.lang.StringUtils;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dialog.ReceiveContentDialog.ReceiveContentResult;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.alert.Alert;
import com.all.shared.alert.MusicContentAlert;

public class MusicContentAlertView extends AlertView<MusicContentAlert> {

	private static final long serialVersionUID = -8505490993682182539L;
	private final MusicContentAlert musicContentAlert;
	private DialogFactory dialogFactory;
	private final ViewEngine viewEngine;

	public MusicContentAlertView(Messages messages, MusicContentAlert musicContentAlert, DialogFactory dialogFactory,
			ViewEngine viewEngine) {
		super(musicContentAlert, MUSIC, messages);
		this.musicContentAlert = musicContentAlert;
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
	}

	@Override
	ButtonBar getButtonBar() {
		return ButtonBar.MUSIC_CONTENT;
	}

	@Override
	String getHeader() {
		return messages.getMessage("musicContentAlert.header", musicContentAlert.getSender().getNickName());
	}

	@Override
	String getDescriptionMessage() {
		String personalizedMessage = musicContentAlert.getPersonalizedMessage();
		if (StringUtils.isEmpty(personalizedMessage)) {
			return messages.getMessage("musicContentAlert.defaultMessage");
		}
		return personalizedMessage;
	}

	@Override
	String getFooter() {
		return messages.getMessage("musicContentAlert.question", musicContentAlert.getModel().trackCount() + "");
	}

	@Override
	void executeAccept() {
		viewEngine.send(Actions.Alerts.ALERT_ACTION_ACCEPT, new ValueAction<Alert>(getAlert()));
	}

	@Override
	void executeDeny() {
		viewEngine.send(Actions.Alerts.ALERT_ACTION_DELETE, new ValueAction<Alert>(getAlert()));
	}

	@Override
	void executeDetails() {
		Observer<ObservValue<ReceiveContentResult>> closeListener = new Observer<ObservValue<ReceiveContentResult>>() {
			@Override
			public void observe(ObservValue<ReceiveContentResult> e) {
				ReceiveContentResult result = e.getValue();
				if (result == ReceiveContentResult.ACCEPT_ALL) {
					viewEngine.send(Actions.Alerts.ALERT_ACTION_ACCEPT, new ValueAction<Alert>(getAlert()));
				} else if (result != ReceiveContentResult.CANCEL) {
					viewEngine.send(Actions.Alerts.ALERT_ACTION_DELETE, new ValueAction<Alert>(getAlert()));
				}
			}
		};
		dialogFactory.showReceiveContentDialog(musicContentAlert.getModel(), messages, closeListener);
	}

}
