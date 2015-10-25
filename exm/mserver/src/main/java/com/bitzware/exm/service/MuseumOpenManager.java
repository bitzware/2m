package com.bitzware.exm.service;

public interface MuseumOpenManager {

	/**
	 * Restarts open / close museum jobs, reading new data from the configuration.
	 */
	void update();
	
}
