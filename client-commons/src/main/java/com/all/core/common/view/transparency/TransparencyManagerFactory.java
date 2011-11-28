package com.all.core.common.view.transparency;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class TransparencyManagerFactory {
	private static final Log LOG = LogFactory.getLog(TransparencyManagerFactory.class);
	private static TransparencyManager manager;

	public static TransparencyManager getManager() {
		if (manager != null) {
			return manager;
		}
		try {
			manager = new Jdk6u10TransparencyManager();
			return manager;
		} catch (Throwable t) {
			LOG.error("Unexpected exception during TransparencyManager instantiation.", t);
		}
		manager = new NullTransparencyManager();
		return manager;
	}

}
