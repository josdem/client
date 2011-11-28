package com.all.core.common.view.util;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SetIconImageAll {

	private static final Log LOG = LogFactory.getLog(SetIconImageAll.class);
	private static SetIconImageAll instance;
	private static final String IMAGE = "all.icon.taskbar.win";

	public void setIconImageAll(JFrame frame)	{

		try {
			ImageIcon image = (ImageIcon)UIManager.getDefaults().getIcon(IMAGE);
			frame.setIconImage(image.getImage());
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	public static SetIconImageAll getInstance()	{

		if(instance==null)	{
			instance = new SetIconImageAll();
		}
		return instance;
	}
}