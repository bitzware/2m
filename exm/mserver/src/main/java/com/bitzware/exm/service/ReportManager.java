package com.bitzware.exm.service;

import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRException;

import org.springframework.core.io.Resource;

import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportParameters;
import com.bitzware.exm.model.report.ReportProperties;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.visitordb.model.EventType;


/**
 * Interface for a class that creates reports.
 * 
 * @author finagle
 */
public interface ReportManager {

	/**
	 * Returns true if the specified report is ready, false otherwise.
	 */
	boolean isReady(String reportId);
	
	/**
	 * Returns true if the specified report creation failed, false otherwise.
	 */
	boolean failed(String reportId);
	
	/**
	 * Returns data of a report with the specified id.
	 */
	ReportProperties getReportData(String reportId);
	
	/**
	 * Creates an empty report and returns it's id.
	 */
	String createReport(ReportType reportType);
	
	/**
	 * Returns a total number of ready reports.
	 */
	int getReportsAmount();
	
	/**
	 * Returns a subset of reports, starting from 'offset'. Reports are sorted by 
	 * creation timestamp, descending.
	 */
	List<ReportProperties> getReports(int offset, int limit);
	
	/**
	 * Creates 'events on station' report. Returns true on success, false on failure.
	 */
	void writeEventsOnStationReport(String reportId, Long stationId, Date from,
			Date to, EventType eventType, ReportParameters parameters,
			ReportOutputFormat outputFormat, Resource reportSchemaSource,
			Resource compiledReportSource) throws JRException;
	
	/**
	 * Creates 'events in room' report. Returns true on success, false on failure.
	 */
	void writeEventsInRoomReport(String reportId, Long roomId, Date from, Date to,
			EventType eventType, ReportParameters parameters, ReportOutputFormat outputFormat,
			Resource reportSchemaSource, Resource compiledReportSource) throws JRException;
	
	/**
	 * Creates 'events on floor' report. Returns true on success, false on failure.
	 */
	void writeEventsOnFloorReport(String reportId, String floor, Date from, Date to,
			EventType eventType, ReportParameters parameters, ReportOutputFormat outputFormat,
			Resource reportSchemaSource, Resource compiledReportSource) throws JRException;
	
	/**
	 * Creates 'visitor's route' report. Returns true on success, false on failure.
	 */
	void writeVisitorsRoutesReport(String reportId, Date date, EventType eventType,
			ReportParameters parameters, ReportOutputFormat outputFormat,
			Resource reportSchemaSource, Resource compiledReportSource)
	throws JRException;
	
	/**
	 * Creates 'most popular stations' report. Returns true on success, false on failure.
	 */
	void writePopularStationsReport(String reportId, Date from, Date to, boolean chart,
			EventType eventType, ReportParameters parameters, ReportOutputFormat outputFormat,
			Resource reportSchemaSource, Resource compiledReportSource) throws JRException;
	
	/**
	 * Creates 'daily visits' report. Returns true on success, false on failure.
	 */
	void writeDailyVisitsReport(String reportId, Date from, Date to, boolean chart,
			ReportParameters parameters, ReportOutputFormat outputFormat,
			Resource reportSchemaSource, Resource compiledReportSource) throws JRException;
	
}
