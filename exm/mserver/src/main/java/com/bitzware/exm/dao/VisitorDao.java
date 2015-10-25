package com.bitzware.exm.dao;

import java.util.Date;

import com.bitzware.exm.visitordb.model.Visitor;


public interface VisitorDao {

	/**
	 * Returns a visitor with the specified id.
	 */
	Visitor getVisitorById(final Long id);
	
	/**
	 * Gets a visitor by the specified RFID tag. Returns null if the visitor is not
	 * found.
	 */
	Visitor getVisitorByRfid(final String rfid);
	
	/**
	 * Returns a visitor with the specified rfid or creates one if it does not exist.
	 */
	Visitor getOrCreateVisitorWithRfid(final String rfid);
	
	/**
	 * Deletes all visitors.
	 */
	void deleteVisitors();
	
	/**
	 * Updates the specified visitor profile on the station.
	 */
	boolean updateStationProfile(final Visitor visitor);
	
	/**
	 * Updates the specified visitor.
	 */
	void update(final Visitor visitor);

	/**
	 * Creates a visitor.
	 */
	Long createVisitor(Visitor visitor);
	
	/**
	 * Marks the specified RFID as expired.
	 */
	void expireRfid(String rfid);
	
	/**
	 * FOR TESTING PURPOSE ONLY.
	 */
	void setRfidTimestamp(final String rfid, final Date timestamp);
	
}
