package com.all.core.common.observables;

import com.all.core.common.bean.RegisterUserCommand;
import com.all.observ.ObserveObject;

public class SignUpEvent extends ObserveObject {

	private static final long serialVersionUID = 1L;

	private final int aboutUsIndex;

	private final RegisterUserCommand user;

	public SignUpEvent(RegisterUserCommand user, int aboutUsIndex) {
		this.user = user;
		this.aboutUsIndex = aboutUsIndex;
	}

	public int getAboutUsIndex() {
		return aboutUsIndex;
	}

	public RegisterUserCommand getUser() {
		return user;
	}
}
