package com.all.client.view.util;

import java.util.Arrays;

public class Pixel {
	private final int x;
	private final int y;
	private final int[] argb;
	private final int hashCode;
	private final String toString;

	public Pixel(int x, int y, int[] rgba) {
		super();
		if (rgba.length != 4) {
			throw new IllegalArgumentException("Not an RGB Alpha array");
		}
		this.x = x;
		this.y = y;
		this.argb = rgba;
		this.hashCode = (x + y) | Arrays.hashCode(argb);
		this.toString = "{" + x + "," + y + ":" + Arrays.toString(argb) + "}";

	}

	public Pixel(int x, int y, int red, int green, int blue, int alpha) {
		this(x, y, new int[] { red, green, blue, alpha });
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int[] getArgb() {
		return argb.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pixel) {
			Pixel other = (Pixel) obj;
			return x == other.x && y == other.y && Arrays.equals(argb, other.argb);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return toString;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
