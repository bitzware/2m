package com.bitzware.exm.dao;

import java.util.List;

import com.bitzware.exm.visitordb.model.Event;


/**
 * DAO class for events.
 * 
 * @author finagle
 */
public interface EventDao {

	/**
	 * Saves the specified event.
	 */
	void saveEvent(Event event);
	
	/**
	 * Retrieves all events with visitors.
	 */
	List<Event> getEventsWithVisitors();
	
	/**
	 * Deletes all events.
	 */
	void deleteEvents();
	
}
