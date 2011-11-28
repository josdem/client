package com.all.client.view.util;

import java.util.Arrays;

import junit.framework.AssertionFailedError;

public class PixelNotEqualException extends AssertionFailedError {
	private static final long serialVersionUID = 1L;

	private final int x1;
	private final int y1;
	private final int[] pixel1;
	private final int x2;
	private final int y2;
	private final int[] pixel2;

	public PixelNotEqualException(int x1, int y1, int[] pixel1, int x2, int y2, int[] pixel2) {
		this.x1 = x1;
		this.y1 = y1;
		this.pixel1 = pixel1;
		this.x2 = x2;
		this.y2 = y2;
		this.pixel2 = pixel2;
	}

	@Override
	public String toString() {
		return "Pixel " + x1 + "," + y1 + ": " + Arrays.toString(pixel1) + " and Pixel " + x2 + "," + y2 + ": "
				+ Arrays.toString(pixel2) + " are not equal";
	}

}
