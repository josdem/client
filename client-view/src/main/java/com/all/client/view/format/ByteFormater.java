package com.all.client.view.format;

import java.text.NumberFormat;

public class ByteFormater {
	private static final int UNIT_CONVERSION_BYTES = 1024;
	private static NumberFormat byteFormatter = NumberFormat.getInstance();
	static final String BYTES_LABEL = " B";
	static final String KILO_BYTES_LABEL = " KB";
	static final String MEGA_BYTES_LABEL = " MB";
	static final String GIGA_BYTES_LABEL = " GB";
	
	static{
		byteFormatter.setMaximumFractionDigits(1);	
	}
	
	public String getFormat(long bytes) {
		if (bytes < UNIT_CONVERSION_BYTES) {
			return byteFormatter.format(bytes) + BYTES_LABEL;
		}
		double kilobytes = bytes / 1024.0;
		if (kilobytes < UNIT_CONVERSION_BYTES ){
			return byteFormatter.format(kilobytes) + KILO_BYTES_LABEL;
		}
		double megabytes = kilobytes / 1024.0;
		if (megabytes < UNIT_CONVERSION_BYTES){
			return byteFormatter.format(megabytes) + MEGA_BYTES_LABEL;
		}
		return byteFormatter.format(megabytes/1024.0) + GIGA_BYTES_LABEL;
	}
}
