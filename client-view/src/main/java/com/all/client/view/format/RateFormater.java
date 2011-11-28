package com.all.client.view.format;

import java.text.NumberFormat;

public class RateFormater {
	public static final String BYTES_LABEL = " B/s";
	public static final String KILO_BYTES_LABEL = " KB/s";
	public static final String MEGA_BYTES_LABEL = " MB/s";
	private static final int MB = 1024;
	private static NumberFormat numberFormat = NumberFormat.getInstance();

	static {
		numberFormat.setMaximumFractionDigits(1);
	}

	public String getFormat(long bps) {
		if(bps < 1024) {
			return numberFormat.format(bps) + BYTES_LABEL;
		}

		double kbps = bps / 1024.0;
		if (kbps >= MB) {
			return numberFormat.format(kbps / 1024.0) + MEGA_BYTES_LABEL;
		}
		
		return numberFormat.format(kbps) + KILO_BYTES_LABEL;
	}
	
	public String getFormatNoDigits(long bps) {
		return getFormat(bps);
	}
}
