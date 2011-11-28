package com.all.client.util;

public final class PercentageUtil {
	
	private PercentageUtil() {
		
	}

	public static int convertFloat(float d) {
		return (int) (d * 100);
		
	}

	public static float convertPercentage(int i) {
		return (float)i/100;
	}

}
