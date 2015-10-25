package com.bitzware.exm.model.report;

import java.util.Date;

import com.bitzware.exm.util.TimeFormatter;


/**
 * Used for charts.
 * 
 * @author finagle
 */
public class CategoryDate implements Comparable<CategoryDate> {

	private final TimeFormatter timeFormatter;
	private final Date date;

	public CategoryDate(final TimeFormatter timeFormatter, final Date date) {
		this.timeFormatter = timeFormatter;
		this.date = date;
	}


	public int compareTo(final CategoryDate o) {
		return date.compareTo(o.date);
	}

	
	public String toString() {
		return timeFormatter.formatDate(date);
	}
	
}
