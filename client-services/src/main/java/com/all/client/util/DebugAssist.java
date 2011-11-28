package com.all.client.util;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DebugAssist {

	private static final Log LOGGER = LogFactory.getLog(DebugAssist.class);

	public static void showConfirmationDialog(String... message) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StringBuilder builder = new StringBuilder();
		if (message != null && message.length > 0) {
			for (String msg : message) {
				builder.append(msg);
			}
		}
		builder.append("\n\nStack:\n");
		for (int i = 0; i < 15 && i < stackTrace.length; i++) {
			builder.append(stackTrace[i].toString());
			builder.append("\n");
		}

		int dialog = JOptionPane.showConfirmDialog(null, builder.toString());
		if (dialog == JOptionPane.NO_OPTION) {
			throw new IllegalArgumentException();
		}
		if (dialog == JOptionPane.CANCEL_OPTION) {
			try {
				throw new IllegalArgumentException();
			} catch (Exception e) {
				LOGGER.error(e, e);
			}
		}
	}

}
