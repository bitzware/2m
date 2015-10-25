package com.bitzware.exm.util;

import com.bitzware.exm.model.report.NamedColumnsResult;

/**
 * This class wraps Hibernate query result for JasperReports. It is designed to be used
 * with date intervals - it creates a special iterator that returns days and months
 * missing in the query result.
 * 
 * @author finagle
 */
public class DateIntervalDataSource extends NamedColumnsDataSource {

	/**
	 * Constructor.
	 * 
	 * @param result data rows.
	 * @param defaultValues default row (returned if there is no row in the database,
	 * date columns are filled automatically).
	 * @param yearIndex year column index.
	 * @param monthIndex month column index.
	 * @param dayIndex day column index.
	 */
	public DateIntervalDataSource(final NamedColumnsResult result, final Object[] defaultValues,
			final int yearIndex, final int monthIndex, final int dayIndex) {
		super(result, new DateIntervalIterator(result.getData().iterator(), defaultValues,
				yearIndex, monthIndex, dayIndex));
	}
}
