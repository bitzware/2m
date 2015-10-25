package com.bitzware.exm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.opensymphony.xwork2.TextProvider;

/**
 * Class for formatting time, used in reports and on web pages.
 * 
 * @author finagle
 */
public class TimeFormatter {

	private final DateFormat timeFormat;
	private final DateFormat dateFormat;
	private final DateFormat timestampFormat;

	public TimeFormatter(final String timeFormat, final String dateFormat,
			final String timestampFormat) {
		if (timeFormat != null) {
			this.timeFormat = new SimpleDateFormat(timeFormat);
		} else {
			this.timeFormat = null;
		}
		
		if (dateFormat != null) {
			this.dateFormat = new SimpleDateFormat(dateFormat);
		} else {
			this.dateFormat = null;
		}
		
		if (timestampFormat != null) {
			this.timestampFormat = new SimpleDateFormat(timestampFormat);
		} else {
			this.timestampFormat = null;
		}
	}

	public TimeFormatter(final TextSource textSource) {
		this.timeFormat = new SimpleDateFormat(textSource.getText("format.time"));
		this.dateFormat = new SimpleDateFormat(textSource.getText("format.date"));
		this.timestampFormat = new SimpleDateFormat(textSource.getText("format.timestamp"));
	}

	public TimeFormatter(final TextProvider textProvider) {
		timeFormat = null;
		dateFormat = new SimpleDateFormat(textProvider.getText("format.date.java"));
		timestampFormat = null;
	}

	public String formatTime(final Date date) {
		if (date != null) {
			return timeFormat.format(date);
		} else {
			return StringUtils.EMPTY;
		}
	}

	public String formatDate(final Date date) {
		if (date != null) {
			return dateFormat.format(date);
		} else {
			return StringUtils.EMPTY;
		}
	}

	public String formatTimestamp(final Date date) {
		if (date != null) {
			return timestampFormat.format(date);
		} else {
			return StringUtils.EMPTY;
		}
	}
}
