package com.all.client.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DateUtil {
	
	private DateUtil() {
		
	}
	
	public static int getAge(Date birthDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(birthDate);
		Calendar now = new GregorianCalendar();
		int res = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
		if ((cal.get(Calendar.MONTH) > now.get(Calendar.MONTH))
				|| (cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) && cal.get(Calendar.DAY_OF_MONTH) > now
						.get(Calendar.DAY_OF_MONTH))) {
			res--;
		}
		return res;
	}

}
