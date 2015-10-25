package com.bitzware.exm.service;

import com.bitzware.exm.model.Events;
import com.bitzware.exm.visitordb.model.Event;

/**
 * Interface that manages events.
 * 
 * @author finagle
 */
public interface EventManager {

	/**
	 * Registers new event, which will be dispatched to the client application.
	 */
	void registerEvent(Event event);

	/**
	 * Returns all registered events and removes them from the list.
	 */
	Events retrieveEvents();
	
	/**
	 * Blocks the current thread until the specified time in milliseconds elapses
	 * or a new event comes.
	 */
	void waitForEvent(long maxWait);
	
	/**
	 * Clears the event queue.
	 */
	void restart();

}
