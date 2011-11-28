package com.all.client.view.util;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.all.commons.Environment;

public final class MacUtils {
	
	private MacUtils() {
	}
	
	public static boolean isRMCOnMac(MouseEvent e) {
		if (Environment.isMac()) {
			return ((e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) && (e.getButton() == MouseEvent.BUTTON1));
		}
		return false;
	}

}
