package com.bitzware.exm.dao;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import com.bitzware.exm.model.report.ReportProperties;
import com.bitzware.exm.model.report.ReportType;


/**
 * Interface for a class that manages stored reports.
 * 
 * @author finagle
 */
public interface ReportDao {
	
	/**
	 * Creates an empty report and returns it's id.
	 */
	String createReport(ReportType type);

	/**
	 * Opens a report with the specified id for writing. This method also saves specified
	 * report properties. After the data is written, the calling object should close the
	 * stream and call the 'closeReport' method. 
	 */
	OutputStream openReportForWrite(String id, ReportProperties reportProperties);
	
	/**
	 * Opens a report with the specified id for reading. Returns report properties and
	 * report binary input stream.
	 */
	ReportProperties openReportForRead(String id);
	
	/**
	 * Returns true if and only if a report with the specified id exists and can be
	 * read.
	 */
	boolean isReady(String id);
	
	/**
	 * Returns true if and only if creation of the specified report failed.
	 */
	boolean failed(String id);
	
	/**
	 * Closes the specified report. If 'success' is set to true, the report will be marked
	 * as 'ready', if set to false, it will be marked as 'failed'.
	 */
	void closeReport(String id, Date timestamp, boolean success);
	
	/**
	 * Returns a total number of ready reports.
	 */
	int getReportsAmount();
	
	/**
	 * Returns a subset of reports, starting from 'offset'. Reports are sorted by 
	 * creation timestamp, descending.
	 */
	List<ReportProperties> getReports(int offset, int limit);
	
}
