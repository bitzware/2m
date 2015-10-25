package com.bitzware.exm.service;

import java.io.InputStream;
import java.util.Date;

/**
 * Interface for a class that manages duplication on the master server.
 * 
 * @author finagle
 */
public interface MasterDuplicateManager {

	/**
	 * Downloads the presentation from the specified station and saves it on
	 * the disk. Returns the presentation id.
	 */
	String downloadPresentation(Long stationId, Date timestamp);

	/**
	 * Opens the presentation with the specified id and returns it's input stream.
	 */
	InputStream getPresentation(String presentationId);

	/**
	 * Sents a request to download updated presentation and properties to all other stations
	 * in the same room.
	 */
	void sendDownloadRequestsToRoom(Long sourceStationId, String presentationId);
	
}
