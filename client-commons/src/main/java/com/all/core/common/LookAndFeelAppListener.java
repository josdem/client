package com.all.core.common;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.app.AppListener;

public class LookAndFeelAppListener implements AppListener {
	public static final String HIPECOTECH_LNF = "com.all.plaf.hipecotech.HipecotechLookAndFeel";

	private static final Log log = LogFactory.getLog(LookAndFeelAppListener.class);

	private final String lookAndFeel;

	public LookAndFeelAppListener() {
		this(HIPECOTECH_LNF);
	}

	public LookAndFeelAppListener(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	@Override
	public void initialize() {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e) {
			log.error(e, e);
		} catch (InstantiationException e) {
			log.error(e, e);
		} catch (IllegalAccessException e) {
			log.error(e, e);
		} catch (UnsupportedLookAndFeelException e) {
			log.error(e, e);
		}
	}

	@Override
	public void destroy() {
	}

}
