package com.bitzware.exm.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bitzware.exm.visitordb.model.Event;


/**
 * A list of events.
 * 
 * @author finagle
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "events")
@XmlRootElement
public class Events {

	private ArrayList<Event> events;

	@XmlElement(name = "event")
	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(final ArrayList<Event> events) {
		this.events = events;
	}
	
}
