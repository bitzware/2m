package com.bitzware.exm.dao;

import java.util.List;

import com.bitzware.exm.visitordb.model.config.Device;
import com.bitzware.exm.visitordb.model.config.Interaction;


/**
 * Dao interface for configuration.
 * 
 * @author finagle
 */
public interface ConfigDao {

	/**
	 * Gets device list.
	 */
	List<Device> getDevices();
	
	/**
	 * Saves device list. All existing devices will be deleted before this operation.
	 */
	void saveDevices(final List<Device> devices);

	/**
	 * Gets interaction list.
	 */
	List<Interaction> getInteractions();
	
	/**
	 * Saves interaction list. All existing interactions will be deleted before this operation.
	 */
	void saveInteractions(final List<Interaction> interactions);
	
}
