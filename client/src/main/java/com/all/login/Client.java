package com.all.login;

import java.awt.Window;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.ClientApplication;
import com.all.client.ClientResult;
import com.all.shared.model.User;

public class Client implements Callable<Boolean> {
	private final static Log log = LogFactory.getLog(Client.class);

	@Override
	public Boolean call() throws Exception {
		ClientResult result;
		do {
			result = ClientResult.exit;
			LoginApplication loginApplication = new LoginApplication();
			User user = loginApplication.execute();
			if (user != null) {
				Window loaderWindow = loginApplication.getLoaderWindow();
				ClientApplication clientApplication = new ClientApplication(user, loaderWindow);
				result = clientApplication.execute();
				log.info("App finished with status: " + result);
			}
		} while (result == ClientResult.logout);
		return result == ClientResult.restart ? true : false;
	}

	public static void main(String[] args) throws Exception {
		new Client().call();
		System.exit(0);
	}

}
