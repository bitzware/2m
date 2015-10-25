package com.bitzware.exm.service;


public interface ServerManagerBase {

	/**
	 * Returns station's name.
	 */
	String getName();
	
	/**
	 * Saves station's name.
	 */
	void saveName(String name);
	
	/**
	 * Returns station's description.
	 */
	String getDescription();

	/**
	 * Saves station's description.
	 */
	void saveDescription(String description);
	
}
