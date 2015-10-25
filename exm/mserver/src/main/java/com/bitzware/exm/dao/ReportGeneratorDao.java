package com.bitzware.exm.dao;

import java.util.Date;
import java.util.List;

import com.bitzware.exm.model.report.NamedColumnsResult;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


public interface ReportGeneratorDao {
	
	/**
	 * Retrieves data for 'events on station' report.
	 */
	List<Event> getEventsOnStationReportData(Station station, Date from, Date to,
			EventType eventType);
	
	/**
	 * Retrieves data for 'events in room' report.
	 */
	List<Event> getEventsInRoomReportData(Room room, Date from, Date to, EventType eventType);
	
	/**
	 * Retrieves data for 'events on floor' report.
	 */
	List<Event> getEventsOnFloorReportData(String floor, Date from, Date to, EventType eventType);
	
	/**
	 * Retrieves data for Creates 'visitors' routes' report.
	 */
	List<Event> getVisitorsRoutesReportData(Date date, EventType eventType);
	
	/**
	 * Retrieves data for 'most popular stations' report.
	 */
	NamedColumnsResult getPopularStationsReportData(Date from, Date to, EventType eventType);
	
	/**
	 * Retrieves data for 'daily visits' report.
	 */
	NamedColumnsResult getDailyVisitsReportData(Date from, Date to);
	
}
