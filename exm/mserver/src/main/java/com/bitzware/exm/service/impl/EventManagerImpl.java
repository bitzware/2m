package com.bitzware.exm.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.bitzware.exm.model.Events;
import com.bitzware.exm.service.EventManager;
import com.bitzware.exm.visitordb.model.Event;


/**
 * Class that manages events. All methods are thread safe.
 * 
 * @author finagle
 */
public class EventManagerImpl implements EventManager {

	protected Logger logger = Logger.getLogger(this.getClass());
	
	private final int maxSize = 128;
	
	private LinkedList<Event> eventList = new LinkedList<Event>();

	/**
	 * Registers new event, which will be dispatched to the client application.
	 */
	public synchronized void registerEvent(final Event event) {
		if (eventList.size() >= maxSize) {
			eventList.removeFirst();
		}
		
		eventList.addLast(event);
		notifyAll();
	}

	/**
	 * Returns all registered events and removes them from the list.
	 */
	public synchronized Events retrieveEvents() {
		ArrayList<Event> currentEventList = new ArrayList<Event>(eventList);
		eventList.clear();

		Events events = new Events();
		events.setEvents(currentEventList);

		return events;
	}

	/**
	 * Blocks the current thread until the specified time in milliseconds elapses
	 * or a new event comes.
	 */
	public synchronized void waitForEvent(final long maxWait) {
		try {
			if (eventList.isEmpty()) {
				wait(maxWait);
			}
		} catch (InterruptedException e) {
			logger.warn("Waiting for event interrupted!");
		}
	}
	
	public synchronized void restart() {
		eventList.clear();
	}

}
