package com.all.client.services.reporting;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.messengine.MessageMethod;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.usage.UserActions;

@Service
public class ReportingMessageInterceptor {

	public static final String TRACK_ID = "trackId";

	@Autowired
	private ClientReporter reporter;

	@MessageMethod(UserActions.USER_ACTION_MESSAGE_TYPE)
	public void onMessage(AllMessage<Integer> message) {
		reporter.logUserAction(message.getBody());
		if (StringUtils.isNotEmpty(message.getProperty(TRACK_ID))) {
			reporter.logDownloadAction(message.getBody(), message.getProperty(TRACK_ID));
		}
	}

}
