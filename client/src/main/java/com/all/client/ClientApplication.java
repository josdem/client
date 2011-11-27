package com.all.client;

import java.awt.Window;

import com.all.app.DefaultApplication;
import com.all.client.devices.DevicesController;
import com.all.core.common.LookAndFeelAppListener;
import com.all.shared.model.User;

public class ClientApplication extends DefaultApplication<ClientResult> {
	private ClientModule clientModule;

	public ClientApplication(User user, Window loaderWindow) {
		super();

		clientModule = new ClientModule(user, loaderWindow);

		this.setMainModule(clientModule);

//		JarLockerAppListener jarLocker = new JarLockerAppListener();
		LookAndFeelAppListener lnfListener = new LookAndFeelAppListener();
		JnaCheckPhase jnaListener = new JnaCheckPhase();
		DevicesController devicesController = new DevicesController();

		clientModule.addBean(devicesController);

//		this.addAppListener(jarLocker);
		this.addAppListener(lnfListener);
		this.addAppListener(jnaListener);
		this.addAppListener(devicesController);

		ClientResultProcessor resultProcessor = new ClientResultProcessor();
		this.setResultProcessor(resultProcessor);
	}
	
	public void enableDebug(){
		clientModule.enableDebug();
	}
}
