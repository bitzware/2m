package com.bitzware.exm.service.report.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.jasperreports.engine.JRException;

import org.springframework.core.io.Resource;

import com.bitzware.exm.exception.ReportException;
import com.bitzware.exm.service.report.ReportGeneratorThread;
import com.bitzware.exm.visitordb.model.EventType;


public class PopularStationsReportGenerator extends ReportGeneratorThread {
	
	private Date from;
	private Date to;
	private EventType eventType;
	
	private boolean chart = false;

	public Date getFrom() {
		return from;
	}

	public void setFrom(final Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(final Date to) {
		this.to = to;
	}

	public boolean getChart() {
		return chart;
	}

	public void setChart(final boolean chart) {
		this.chart = chart;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	@Override
	protected void writeReport(final Resource reportSchemaSource,
			final Resource compiledReportSource)
	throws IOException, JRException, ReportException {	
		
		reportManager.writePopularStationsReport(reportId, from, to, chart, eventType,
				reportProperties, outputFormat, reportSchemaSource, compiledReportSource);
	}
	
}
