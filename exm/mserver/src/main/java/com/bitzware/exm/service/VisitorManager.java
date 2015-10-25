package com.bitzware.exm.service;

import com.bitzware.exm.visitordb.model.Visitor;

/**
 * Interface for a class that manager visitor data.
 * 
 * @author finagle
 */
public interface VisitorManager {
	
	/**
	 * Creates a visitor profile on the master server.
	 */
	Long createProfile(Visitor visitor);
	
	/**
	 * Updates visitor profile on the station. Returns true on success, false on failure.
	 */
	boolean updateProfile(Visitor visitor);
	
	/**
	 * Marks the specified RFID as expired.
	 */
	void expireRfid(String rfid);
	
}
