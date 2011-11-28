package com.all.client.view.format;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.all.i18n.DefaultMessages;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class TimeFeedFormatter implements Internationalizable {

	private static final String EMPTY_STRING = "";
	private static final int UNIT_CONVERSION_TIME = 60;
	private static final int DAY_UNIT_CONVERSION_TIME = 60 * 60 * 24;
	private static final int WEEK_UNIT_CONVERSION_TIME = 60 * 60 * 24 * 7;
	private static NumberFormat timeFormatter = NumberFormat.getInstance();
	private static String SEC_LABEL;
	private static String ABOUT_A_MINUTE_LABEL;
	private static String MINUTES_LABEL;
	private static String HOURS_LABEL;
	private static String FEW_HOURS_LABEL;
	private static String DAY_OF_THIS_WEEK_LABEL;
	private static String YESTERDAY_LABEL;
	private Messages messages;
	private Calendar calendar;
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

	static {
		timeFormatter.setMaximumFractionDigits(1);
	}

	public String getTimeFormatString(Date feedDate, Date serverDate) {
		Date local;
		if (serverDate == null) {
			local = GregorianCalendar.getInstance(TIME_ZONE).getTime();
		} else {
			local = serverDate;
		}
		long diff = (local.getTime() - feedDate.getTime()) / 1000;
		if (diff < DAY_UNIT_CONVERSION_TIME) {
			return getFormat(diff);
		}
		if (diff < getYesterdayTime(feedDate, local)) {
			return YESTERDAY_LABEL;
		} else if (diff < WEEK_UNIT_CONVERSION_TIME) {
			return getDaysWithinAWeek(feedDate);
		} else {
			return getDateAfterAWeek(feedDate);
		}
	}

	private String getDateAfterAWeek(Date feedDate) {
		SimpleDateFormat simpleDateFormat = null;
		calendar = Calendar.getInstance();
		calendar.setTime(feedDate);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		switch (dayOfMonth) {
		case 1:
			simpleDateFormat = messages.getFormatter("format.date.feed.1");
			break;
		case 2:
			simpleDateFormat = messages.getFormatter("format.date.feed.2");
			break;
		case 3:
			simpleDateFormat = messages.getFormatter("format.date.feed.3");
			break;
		default:
			simpleDateFormat = messages.getFormatter("format.date.feed");
			break;
		}
		return simpleDateFormat.format(feedDate);
	}

	public String getFormat(long sec) {
		if (sec < UNIT_CONVERSION_TIME) {
			return timeFormatter.format(sec) + SEC_LABEL;
		}
		if (sec < UNIT_CONVERSION_TIME * 2) {
			return ABOUT_A_MINUTE_LABEL;
		}
		long minutes = sec / 60;
		if (minutes < UNIT_CONVERSION_TIME) {
			return timeFormatter.format(minutes) + MINUTES_LABEL;
		}
		long hours = minutes / 60;
		if (hours < 2) {
			return HOURS_LABEL;
		} else if (hours < 24) {
			return timeFormatter.format(hours) + FEW_HOURS_LABEL;
		}
		return EMPTY_STRING;
	}

	private String getDaysWithinAWeek(Date feedDate) {
		SimpleDateFormat simpleDateFormat = messages.getFormatter("format.date.weekDay");
		if (((DefaultMessages) messages).getLocale().equals(Locale.US)) {
			return DAY_OF_THIS_WEEK_LABEL + " " + simpleDateFormat.format(feedDate);
		}
		return simpleDateFormat.format(feedDate) + " " + DAY_OF_THIS_WEEK_LABEL;
	}

	@Override
	public void internationalize(Messages messages) {
		this.messages = messages;
		SEC_LABEL = " " + messages.getMessage("feed.timer.secs");
		ABOUT_A_MINUTE_LABEL = " " + messages.getMessage("feed.timer.aboutMinute");
		MINUTES_LABEL = " " + messages.getMessage("feed.timer.minutes");
		HOURS_LABEL = " " + messages.getMessage("feed.timer.hours");
		FEW_HOURS_LABEL = " " + messages.getMessage("feed.timer.fewHours");
		YESTERDAY_LABEL = " " + messages.getMessage("feed.timer.yesterday");
		DAY_OF_THIS_WEEK_LABEL = " " + messages.getMessage("feed.timer.thisWeek");
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	private long getYesterdayTime(Date feedDate, Date local) {
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minutes = Calendar.getInstance().get(Calendar.MINUTE);
		int seconds = Calendar.getInstance().get(Calendar.SECOND);
		Date yesterdayDate = new Date();
		long newTime = (yesterdayDate.getTime()) - (DAY_UNIT_CONVERSION_TIME * 1000) - (hours * 60 * 60 * 1000)
				- (minutes * 60 * 1000) - (seconds * 1000);
		yesterdayDate.setTime(newTime);
		return (local.getTime() - yesterdayDate.getTime()) / 1000;
	}
}
