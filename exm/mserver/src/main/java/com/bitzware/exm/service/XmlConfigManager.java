package com.bitzware.exm.service;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.springframework.core.io.Resource;

public class XmlConfigManager extends XMLConfiguration {

	private static final long serialVersionUID = 1L;

	public XmlConfigManager(Resource resource) throws ConfigurationException,
	IOException {
		
		super(resource.getFile());
	}
	
}
