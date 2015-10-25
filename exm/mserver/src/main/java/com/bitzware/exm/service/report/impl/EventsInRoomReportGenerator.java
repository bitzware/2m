package com.bitzware.exm.service.report.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.jasperreports.engine.JRException;

import org.springframework.core.io.Resource;

import com.bitzware.exm.exception.ReportException;
import com.bitzware.exm.service.report.ReportGeneratorThread;
import com.bitzware.exm.visitordb.model.EventType;


public class EventsInRoomReportGenerator extends ReportGeneratorThread {

	private Date from;
	private Date to;
	private Long roomId;
	private EventType eventType;	
	
	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(final Long roomId) {
		this.roomId = roomId;
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
		
		reportManager.writeEventsInRoomReport(reportId, roomId, from, to, eventType,
				reportProperties, outputFormat, reportSchemaSource, compiledReportSource);
	}
	
}
