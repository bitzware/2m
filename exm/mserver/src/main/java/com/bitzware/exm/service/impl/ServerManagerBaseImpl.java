package com.bitzware.exm.service.impl;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.ServerPropertyDao;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.service.ServerManagerBase;


/**
 * Base class for classes that manage server data.
 * 
 * @author finagle
 *
 */
public class ServerManagerBaseImpl implements ServerManagerBase {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	protected ServerPropertyDao serverPropertyDao;
	
	public ServerPropertyDao getServerPropertyDao() {
		return serverPropertyDao;
	}

	public void setServerPropertyDao(final ServerPropertyDao serverPropertyDao) {
		this.serverPropertyDao = serverPropertyDao;
	}
	
	public String getName() {
		return serverPropertyDao.getStringProperty(ConfigManager.SERVER_NAME);		
	}

	public void saveName(final String name) {
		serverPropertyDao.setStringProperty(ConfigManager.SERVER_NAME, name);
	}
	
	public String getDescription() {
		return serverPropertyDao.getStringProperty(ConfigManager.SERVER_DESCRIPTION);		
	}

	public void saveDescription(final String description) {
		serverPropertyDao.setStringProperty(ConfigManager.SERVER_DESCRIPTION, description);
	}
	
}
