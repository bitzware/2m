package com.bitzware.exm.dao;

public interface ServerPropertyDao {

	/**
	 * Gets a string property with the specified name from the database. If it does
	 * not exist, it will be read from the configuration file.
	 */
	String getStringProperty(final String name);
	
	/**
	 * Gets a long property with the specified name from the database. If it does
	 * not exist, it will be read from the configuration file.
	 */
	Long getLongProperty(final String name);
	
	/**
	 * Gets an integer property with the specified name from the database. If it does
	 * not exist, it will be read from the configuration file.
	 */
	Integer getIntegerProperty(final String name);
	
	/**
	 * Saves a string property with the specified name and value in the database.
	 */
	void setStringProperty(final String name, final String value);
	
	/**
	 * Saves a long property with the specified name and value in the database.
	 */
	void setLongProperty(final String name, final Long value);
	
	/**
	 * Saves an integer property with the specified name and value in the database.
	 */
	void setIntegerProperty(final String name, final Integer value);
	
}
