package com.all.login;

import java.awt.Window;

import com.all.client.ClientApplication;
import com.all.core.common.util.UserUtils;
import com.all.shared.model.User;

public class DirectClient {
	public static void main(String[] args) {
		User user = UserUtils.loadUser("/defaultUser.dbg");
		if (user == null) {
			user = UserUtils.defaultUser();
		}
		Window loaderWindow = new SimpleLoaderWindow();
		ClientApplication clientApplication = new ClientApplication(user, loaderWindow);
		clientApplication.enableDebug();
		clientApplication.execute();
		System.exit(0);
	}

}
