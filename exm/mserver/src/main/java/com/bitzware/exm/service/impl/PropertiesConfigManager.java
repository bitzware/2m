package com.bitzware.exm.service.impl;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.core.io.Resource;

import com.bitzware.exm.service.ConfigManager;


/**
 * Configuration manager that reads data from the '*.properties' file.
 * 
 * @author finagle
 */
@SuppressWarnings("unchecked")
public class PropertiesConfigManager extends PropertiesConfiguration implements ConfigManager {

	public PropertiesConfigManager(final Resource configResource)
	throws IOException, ConfigurationException {
		
		super(configResource.getFile());
	}


	public String getStringValue(final String key) {
		try {
			return getString(key);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public String[] getStringArrayValue(String key) {
		try {
			return getStringArray(key);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public Integer getIntegerValue(final String key) {
		try {
			int value = getInt(key);
			return value;
		} catch (ConversionException e) {
			return null;
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	
	public Long getLongValue(final String key) {
		try {
			long value = getLong(key);
			return value;
		} catch (ConversionException e) {
			return null;
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
}
