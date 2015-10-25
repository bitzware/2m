package com.bitzware.exm.service;

import java.util.Date;

import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.visitordb.model.Event;


/**
 * Interface for a class that handles event registration on the master server.
 * 
 * @author finagle
 *
 */
public interface MasterEventRegisterManager {
	
	String checkRfid(String rfid);
	
	/**
	 * Registers a RFID event in the database.
	 */
	RfidEventInfo registerRfidEvent(String rfid, String stationMac, Integer deviceIndex);
	
	/**
	 * Registers a Button event.
	 */
	void registerButtonEvent(String stationMac, Integer index, Boolean down);
	
	/**
	 * Registers a PIR event.
	 */
	void registerPirEvent(String stationMac);
	
	/**
	 * Registers a 'score selected' event.
	 */
	void registerScoreEvent(Event event, String stationMac);
	
	/**
	 * Registers a Director event.
	 */
	void registerDirectorEvent(Event event, String stationMac);
	
	/**
	 * Registers a 'client disconnected' event.
	 */
	void registerClientDisconnectedEvent(Event event, String stationMac);
	
	/**
	 * Registers a 'floor' event.
	 */
	void registerFloorEvent(String stationMac, Integer index, Boolean down);
	
	/**
	 * FOR TESTING PURPOSE ONLY.
	 */
	void setRfidTimestamp(String rfid, Date date);
	
}
