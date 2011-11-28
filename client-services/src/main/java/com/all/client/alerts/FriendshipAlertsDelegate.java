package com.all.client.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.client.controller.ContactController;
import com.all.shared.alert.FriendshipRequestAlert;
import com.all.shared.model.ContactRequest;

@Service
public class FriendshipAlertsDelegate {

	@Autowired
	private ContactController contactController;

	public void accept(FriendshipRequestAlert alert) {
		ContactRequest request = alert.getRequest();
		request.accept();
		contactController.createContact(request.getRequester());
		sendRequestResponse(request);
	}

	public void deny(FriendshipRequestAlert alert) {
		sendRequestResponse(alert.getRequest());
	}

	private void sendRequestResponse(ContactRequest request) {
		contactController.respondFriendshipRequest(request);
	}
}
