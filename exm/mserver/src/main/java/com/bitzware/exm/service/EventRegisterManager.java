package com.bitzware.exm.service;

import com.bitzware.exm.visitordb.model.Event;

/**
 * Interface that handles event registration on the station.
 * 
 * @author finagle
 */
public interface EventRegisterManager {

	void registerRfidEvent(int deviceNumber, String rfid);
	
	void registerButtonEvent(int index, boolean down);
	
	void registerPirEvent();
	
	void registerScoreEvent(String rfid);
	
	void registerDirectorEvent(Event event);
	
	void registerClientDisconnectedEvent();
	
	void registerFloorEvent(int index, boolean down);

	void registerDummyRfidEvent();
	
}
