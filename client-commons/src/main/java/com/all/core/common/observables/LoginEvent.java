package com.all.core.common.observables;

import com.all.observ.ObserveObject;
import com.all.shared.model.User;

public class LoginEvent extends ObserveObject {

	private static final long serialVersionUID = -5524610761285050272L;
	private final User user;

	public LoginEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
