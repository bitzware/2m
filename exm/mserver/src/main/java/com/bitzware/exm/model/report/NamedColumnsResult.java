package com.bitzware.exm.model.report;

import java.util.List;

/**
 * Result of a Hibernate query that returns an array of objects as a row.
 * 
 * @author finagle
 */
public class NamedColumnsResult {

	/**
	 * Rows.
	 */
	private final List<Object[]> data;
	/**
	 * Column names.
	 */
	private final String[] columnNames;

	public NamedColumnsResult(final List<Object[]> data, final String[] columnNames) {
		this.data = data;
		this.columnNames = columnNames;
	}

	public List<Object[]> getData() {
		return data;
	}

	public String[] getColumnNames() {
		return columnNames;
	}
	
}
