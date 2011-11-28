package com.all.client.view.alerts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Messages;
import com.all.shared.alert.Alert;
import com.all.shared.alert.AllNotificationAlert;
import com.all.shared.alert.FriendshipRequestAlert;
import com.all.shared.alert.FriendshipResponseAlert;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.alert.MusicContentAlert;

@Component
public class AlertViewFactory {

	@Autowired
	private Messages messages;
	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private ViewEngine viewEngine;

	private final Log log = LogFactory.getLog(this.getClass());

	public AlertView<?> getAlertView(Alert alert) {
		try {
			AlertView<?> alertView = createAlertView(alert);
			alertView.initialize();
			return alertView;
		} catch (Exception e) {
			log.error(e, e);
			return null;
		}
	}

	private AlertView<?> createAlertView(Alert alert) {
		String type = alert.getType();
		if (FriendshipRequestAlert.TYPE.equals(type)) {
			return new FriendshipRequestAlertView(messages, (FriendshipRequestAlert) alert, viewEngine);
		}
		if (FriendshipResponseAlert.TYPE.equals(type)) {
			return new FriendshipResponseAlertView(messages, (FriendshipResponseAlert) alert, viewEngine);
		}
		if (MusicContentAlert.TYPE.equals(type)) {
			return new MusicContentAlertView(messages, (MusicContentAlert) alert, dialogFactory, viewEngine);
		}
		if (McRequestAlert.TYPE.equals(type)) {
			return new McRequestAlertView(messages, (McRequestAlert) alert, viewEngine);
		}
		if (AllNotificationAlert.TYPE.equals(type)) {
			return new AllNotificationAlertView((AllNotificationAlert) alert, messages, viewEngine);
		}
		throw new IllegalArgumentException("Cannot find an appropiate view for alerts of type " + alert.getType()
				+ ".  The alert won't be displayed.");
	}

}
