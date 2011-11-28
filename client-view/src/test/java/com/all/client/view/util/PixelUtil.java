package com.all.client.view.util;

public class PixelUtil {
	public static long encodeColor(int[] pixel) {
		return (long) pixel[0] + (pixel[1] * 256L) + (pixel[2] * 65536L);
	}

	public static int[] decodeColor(long col) {
		return new int[] { (int) (col % 256), (int) ((col / 256) % 256), (int) (col / 65536) };
	}

	public static boolean colorEqual(int[] rgb, int[] pixel, int tolerance) {
		if (tolerance == 0) {
			return rgb[0] == pixel[0] && rgb[1] == pixel[1] && rgb[2] == pixel[2];
		}
		for (int i = 0; i < 3; i++) {
			int low = rgb[i] - tolerance;
			int high = rgb[i] + tolerance;
			int value = pixel[i];
			if (!(value >= low && value <= high)) {
				return false;
			}
		}
		return true;
	}

}
