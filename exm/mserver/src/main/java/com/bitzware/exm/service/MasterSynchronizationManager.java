package com.bitzware.exm.service;

import java.util.List;

import com.bitzware.exm.visitordb.model.Event;


/**
 * Class that handles the synchronization process on the 'master server' side.
 * 
 * @author finagle
 */
public interface MasterSynchronizationManager {

	/**
	 * Saves the synchronization data in the database.
	 */
	void saveData(List<Event> events, String stationMac);
	
}
