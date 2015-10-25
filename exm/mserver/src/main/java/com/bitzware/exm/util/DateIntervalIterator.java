package com.bitzware.exm.util;

import java.util.Calendar;
import java.util.Iterator;

/**
 * An iterator that wraps a Hibernate query result iterator and adds missing days in
 * case they were not in the database. For example, if the query result is:
 * YEAR MONTH DAY VALUE
 * 2009    09   4    15
 * 2009    09   5    15
 * 2009    09   7    15
 * 2009    09   8    15
 * 2009    09  12    15
 * 
 * this iterator will return rows:
 * 
 * YEAR MONTH DAY VALUE
 * 2009    09   4    15
 * 2009    09   5    15
 * 2009    09   6     0
 * 2009    09   7    15
 * 2009    09   8    15
 * 2009    09   9     0
 * 2009    09  10     0
 * 2009    09  11     0
 * 2009    09  12    15 
 * 
 * @author finagle
 */
public class DateIntervalIterator implements Iterator<Object[]> {

	private final Iterator<Object[]> wrappedIterator;
	private final Object[] defaultValues;
	private final int yearIndex;
	private final int monthIndex;
	private final int dayIndex;

	private Object[] nextValue = null;

	private Calendar calendar = null;

	public DateIntervalIterator(final Iterator<Object[]> wrappedIterator,
			final Object[] defaultValues, final int yearIndex, final int monthIndex,
			final int dayIndex) {
		this.wrappedIterator = wrappedIterator;
		this.defaultValues = defaultValues;
		this.yearIndex = yearIndex;
		this.monthIndex = monthIndex;
		this.dayIndex = dayIndex;
	}

	@Override
	public boolean hasNext() {
		if (nextValue != null) {
			return true;
		}
		return wrappedIterator.hasNext();
	}

	@Override
	public Object[] next() {
		// This is a first row - set the start date.
		if (calendar == null) {
			Object[] value = wrappedIterator.next();

			calendar = DateUtil.createCalendar(
					(Integer) value[yearIndex],
					((Integer) value[monthIndex]) - 1,
					(Integer) value[dayIndex]);

			return value;
		} else {
			// Next day
			calendar.add(Calendar.DAY_OF_MONTH, 1);

			// Expected date in the next row.
			int expectedYear = calendar.get(Calendar.YEAR);
			int expectedMonth = calendar.get(Calendar.MONTH) + 1;
			int expectedDay = calendar.get(Calendar.DAY_OF_MONTH);

			// Get the next row.
			Object[] value;
			if (nextValue != null) {
				value = nextValue;
			} else {
				value = wrappedIterator.next();

				// Check if the next row is valid - it cannot contain date before
				// the expected date. It would mean that the data retrieved from the
				// database was not sorted correctly.
				if (isBefore((Integer) value[yearIndex], (Integer) value[monthIndex],
						(Integer) value[dayIndex], expectedYear, expectedMonth, expectedDay)) {
					throw new IllegalStateException("Invalid data result.");
				}

			}

			// If the day is correct in the next row - return it, else - create a new
			// default row and return it.
			if (value[yearIndex].equals(expectedYear) && value[monthIndex].equals(expectedMonth)
					&& value[dayIndex].equals(expectedDay)) {
				nextValue = null;
				return value;
			} else {
				nextValue = value;

				Object[] emptyRow = new Object[defaultValues.length];
				for (int i = 0; i < emptyRow.length; i++) {
					emptyRow[i] = defaultValues[i];
				}
				emptyRow[yearIndex] = expectedYear;
				emptyRow[monthIndex] = expectedMonth;
				emptyRow[dayIndex] = expectedDay;

				return emptyRow;
			}
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private boolean isBefore(final int y1, final int m1, final int d1, final int y2,
			final int m2, final int d2) {
		if (y1 < y2) {
			return true;
		}
		if (y1 == y2 && m1 < m2) {
			return true;
		}
		if (y1 == y2 && m1 == m2 && d1 < d2) {
			return true;
		}
		return false;
	}

}
