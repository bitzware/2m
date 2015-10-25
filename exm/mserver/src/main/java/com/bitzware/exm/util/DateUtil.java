package com.bitzware.exm.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
	
	private DateUtil() {
	}

	public static void setStartOfDay(final Calendar calendar) {
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
	
	public static Calendar startOfDay(final Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setStartOfDay(calendar);
		
		return calendar;
	}
	
	public static void setEndOfDay(final Calendar calendar) {
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		setStartOfDay(calendar);
	}
	
	public static Calendar endOfDay(final Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setEndOfDay(calendar);
		
		return calendar;
	}
	
	public static Calendar createCalendar(final int year, final int month, final int day) {
		final int defaultHour = 12;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, defaultHour);
		
		return calendar;
	}
}
