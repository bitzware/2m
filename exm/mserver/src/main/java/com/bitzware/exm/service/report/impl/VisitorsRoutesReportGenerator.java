package com.bitzware.exm.service.report.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.jasperreports.engine.JRException;

import org.springframework.core.io.Resource;

import com.bitzware.exm.exception.ReportException;
import com.bitzware.exm.service.report.ReportGeneratorThread;
import com.bitzware.exm.visitordb.model.EventType;


public class VisitorsRoutesReportGenerator extends ReportGeneratorThread {

	private Date date;
	private EventType eventType;
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(final Date date) {
		this.date = date;
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
			
		reportManager.writeVisitorsRoutesReport(reportId, date, eventType, reportProperties,
				outputFormat, reportSchemaSource, compiledReportSource);
	}
	
}
