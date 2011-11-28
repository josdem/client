package com.all.core.common;

import java.util.ArrayList;
import java.util.List;

import com.all.core.common.services.ApplicationConfig;
import com.all.core.common.services.ApplicationDao;
import com.all.core.common.services.NetworkDetectionService;
import com.all.core.common.services.UltrapeerProxy;
import com.all.core.common.services.UltrapeerSource;
import com.all.core.common.spring.SpringInitializeBeanPostProcessor;
import com.all.i18n.DefaultMessages;
import com.all.testing.SpringTestSuite;

public class TestConfiguration extends SpringTestSuite {
	@Override
	public String[] configurations() {
		return new String[] { "/core/common/CommonAppContext.xml" };
	}

	@Override
	protected List<Object> additionalBeans() {
		ArrayList<Object> beans = new ArrayList<Object>();
		beans.add(new DefaultMessages());
		return beans;
	}

	@Override
	public void initBeans() throws Exception {
		testBean(ApplicationConfig.class);
		testBean(NetworkDetectionService.class, "clientSettings", "messEngine", "controlEngine");
		testBean(ApplicationDao.class);
		testBean(UltrapeerProxy.class, "messEngine", "peerNetworkingService", "controlEngine", "ultrapeerSource");
		testBean(UltrapeerSource.class, "loginDao", "clientSettings", "messEngine");
		testBean(com.all.networking.PeerNetworkingService.class);
		testBean(com.all.messengine.impl.DefaultMessEngine.class);
		testBean(SpringInitializeBeanPostProcessor.class);
	}

	@Override
	protected void verify() throws Exception {
		appContext.getBean(SpringInitializeBeanPostProcessor.class).awaitInitialization();
	}

}
