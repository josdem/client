package com.all.client.util;

import java.awt.Component;
import java.awt.Container;

public class SwingHeirarchyPrint {
	public static void print(Component c) {
		print(c, "");
	}

	private static void print(Component c, String string) {
		if (c instanceof Container) {
			for (Component com : ((Container) c).getComponents()) {
				print(com, string + "-");
			}
		}
	}
}
