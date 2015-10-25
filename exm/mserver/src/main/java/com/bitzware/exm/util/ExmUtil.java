package com.bitzware.exm.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bitzware.exm.visitordb.model.Visitor;


public final class ExmUtil {

	private ExmUtil() {
	}
	
	public static String concat(final String s1, final String s2, final String s3) {
		StringBuilder sb = new StringBuilder(s1.length() + s2.length() + s3.length());
		sb.append(s1).append(s2).append(s3);
		
		return sb.toString();
	}
	
	public static String concat(final String s1, final String s2, final String s3,
			final String s4) {
		StringBuilder sb = new StringBuilder(s1.length() + s2.length() + s3.length() + s4.length());
		sb.append(s1).append(s2).append(s3).append(s4);
		
		return sb.toString();
	}
	
	public static void updateVisitorProfile(final Visitor localVisitor,
			final Visitor remoteVisitor) {
		if (localVisitor == null || remoteVisitor == null) {
			return;
		}
		
		if (!StringUtils.isEmpty(remoteVisitor.getLanguage())) {
			localVisitor.setLanguage(remoteVisitor.getLanguage());
		}
		if (!StringUtils.isEmpty(remoteVisitor.getLevel())) {
			localVisitor.setLevel(remoteVisitor.getLevel());
		}
		if (!StringUtils.isEmpty(remoteVisitor.getZoom())) {
			localVisitor.setZoom(remoteVisitor.getZoom());
		}
	}
	
	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return calendar;
	}
	
}
