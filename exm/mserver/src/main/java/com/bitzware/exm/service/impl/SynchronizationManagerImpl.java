package com.bitzware.exm.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.EventDao;
import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.exception.NoConnectionException;
import com.bitzware.exm.service.ServerManager;
import com.bitzware.exm.service.SynchronizationManager;
import com.bitzware.exm.visitordb.model.Event;


public class SynchronizationManagerImpl implements SynchronizationManager {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private EventDao eventDao;
	private VisitorDao visitorDao;
	
	private ServerManager serverManager;

	public EventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(final EventDao eventDao) {
		this.eventDao = eventDao;
	}

	public VisitorDao getVisitorDao() {
		return visitorDao;
	}

	public void setVisitorDao(final VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public void setServerManager(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void sendData() {
		logger.debug("Synchronization...");
		
		List<Event> events = eventDao.getEventsWithVisitors();
		
		if (!serverManager.sendSynchronizationData(events)) {
			throw new NoConnectionException("Cannot connect to the synchronization service!");
		}
		
		eventDao.deleteEvents();
		visitorDao.deleteVisitors();
	}
	
}
