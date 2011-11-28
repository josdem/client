package com.all.client.view.format;

import java.text.NumberFormat;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class TimeFormater implements Internationalizable{
	private static final int UNIT_CONVERSION_TIME = 60;
	private static NumberFormat timeFormatter = NumberFormat.getInstance();
	private static String SEC_LABEL;
	private static String MINUTES_LABEL;
	private static String HOURS_LABEL;
	
	static{
		timeFormatter.setMaximumFractionDigits(1);
	}
	
	public String getFormat(long sec) {
		if (sec < UNIT_CONVERSION_TIME ){
			return timeFormatter.format(sec) + SEC_LABEL;
		}
		double minutes = sec / 60.0;
		if (minutes < UNIT_CONVERSION_TIME ) {
			return timeFormatter.format(minutes) + MINUTES_LABEL;
		}
		double hours = minutes / 60.0;
	    return timeFormatter.format(hours) + HOURS_LABEL;
	}

	@Override
	public void internationalize(Messages messages) {
		SEC_LABEL = " " + messages.getMessage("secs");
		MINUTES_LABEL = " " +  messages.getMessage("minutes");
		HOURS_LABEL = " " + messages.getMessage("hours");
	}

	@Override
	public void removeMessages(Messages messages) {
		
	}

	@Override
	public void setMessages(Messages messages) {
		
	}
}
