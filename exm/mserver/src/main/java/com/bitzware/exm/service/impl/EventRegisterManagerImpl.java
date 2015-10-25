package com.bitzware.exm.service.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.EventDao;
import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.exception.NoConnectionException;
import com.bitzware.exm.listener.ConnectionListener;
import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.service.EventManager;
import com.bitzware.exm.service.EventRegisterManager;
import com.bitzware.exm.service.ScoreManager;
import com.bitzware.exm.service.ServerManager;
import com.bitzware.exm.service.SynchronizationManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Visitor;


/**
 * Class that handles event registration on the station.
 * 
 * @author finagle
 */
public class EventRegisterManagerImpl implements ConnectionListener, EventRegisterManager {

	protected Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * If the user selects the same score 2 times within this time period, the second
	 * read will be ignored.
	 */
	private final int scoreTimeout = 10000;
	
	private EventDao eventDao;
	private VisitorDao visitorDao;
	
	private EventManager eventManager;
	private ScoreManager scoreManager;
	private ServerManager serverManager;
	private SynchronizationManager synchronizationManager;
	
	private AtomicBoolean needsSynchronization = new AtomicBoolean(false);
	private AtomicBoolean performingSynchronization = new AtomicBoolean(false);
	
	private Long lastScoreNumber;
	private long lastScoreTime = 0L;
	private final Object lastScoreMutex = new Object();

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

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public ScoreManager getScoreManager() {
		return scoreManager;
	}

	public void setScoreManager(ScoreManager scoreManager) {
		this.scoreManager = scoreManager;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public void setServerManager(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}
	
	public SynchronizationManager getSynchronizationManager() {
		return synchronizationManager;
	}

	public void setSynchronizationManager(final SynchronizationManager synchronizationManager) {
		this.synchronizationManager = synchronizationManager;
	}

	public void init() {
		serverManager.registerConnectionListener(this);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerRfidEvent(final int deviceNumber, final String rfid) {
		// Try to register event on the master server.
		RfidEventInfo rfidEventInfo = serverManager.registerRfidEvent(rfid, deviceNumber);
		
		Event event = new Event();
		event.setDeviceIndex(deviceNumber);
		
		if (rfidEventInfo != null) {
			// If successful - dispatch event.
			if (rfidEventInfo.isInvalid()) {
				event.setEventType(EventType.RFID_INVALID);
			} else if (rfidEventInfo.isTimeout()) {
				event.setEventType(EventType.RFID_EXPIRED);
			} else {
				event.setEventType(EventType.RFID_ACCEPTED);
			}
			
			event.setData(rfid);
			event.setTimestamp(new Date());
			event.setVisitor(rfidEventInfo.getVisitor());
			event.setStandalone(false);
		} else {
			// If failed - save event in the local database and dispatch.
			Visitor visitor = visitorDao.getOrCreateVisitorWithRfid(rfid);
			
			event.setData(rfid);
			event.setEventType(EventType.RFID_INVALID);
			event.setVisitor(visitor);
			event.setTimestamp(new Date());
			event.setStandalone(true);
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
		
		eventManager.registerEvent(event);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerButtonEvent(final int index, final boolean down) {
		Event event = new Event();
		if (down) {
			event.setEventType(EventType.BUTTON_DOWN);
		} else {
			event.setEventType(EventType.BUTTON_UP);
		}
		event.setTimestamp(new Date());
		event.setData(String.valueOf(index));
		
		// Try to register event on the master server.
		if (serverManager.registerButtonEvent(index, down)) {
			event.setStandalone(false);
		} else {
			// If failed - save event in the local database and dispatch.
			event.setStandalone(true);
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
		
		eventManager.registerEvent(event);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerPirEvent() {
		Event event = new Event();
		event.setEventType(EventType.PIR_CHANGED);
		event.setTimestamp(new Date());
		
		// Try to register event on the master server.
		if (serverManager.registerPirEvent()) {
			event.setStandalone(false);
		} else {
			// If failed - save event in the local database and dispatch.
			event.setStandalone(true);
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
		
		eventManager.registerEvent(event);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerScoreEvent(final String rfid) {
		Long scoreNumber = scoreManager.getScoreNumber(rfid);
		if (scoreNumber == null) {
			return;
		}
		
		synchronized (lastScoreMutex) {
			long currentTime = new Date().getTime();
			if (scoreNumber.equals(lastScoreNumber)) {
				if (currentTime - lastScoreTime < scoreTimeout) {
					lastScoreTime = currentTime;
					// If the user selects the same score 2 times within a
					// timeout period, ignore the second selection.
					return;
				}
			}
			
			lastScoreNumber = scoreNumber;
			lastScoreTime = currentTime;
		}
		
		Event event = new Event();
		event.setEventType(EventType.SCORE_SELECTED);
		event.setTimestamp(new Date());
		event.setData(String.valueOf(scoreNumber));
		
		eventManager.registerEvent(event);
		
		// Try to register event on the master server.
		if (!serverManager.registerScoreEvent(event)) {
			// If failed - save event in the local database and dispatch.
			event.setStandalone(true);
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
		
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerDirectorEvent(final Event event) {
		event.setTimestamp(new Date());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Registering Director event: " + event.getEventType());
		}
		
		if (!serverManager.registerDirectorEvent(event)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Saving event in local database...");
			}
			// If failed - save event in the local database and dispatch.
			event.setStandalone(true);
			
			if (event.getVisitor() != null && event.getVisitor().getRfid() != null) {
				Visitor remoteVisitor = event.getVisitor();
				Visitor localVisitor =
					visitorDao.getOrCreateVisitorWithRfid(remoteVisitor.getRfid());
				
				event.setVisitor(localVisitor);
				
				if (event.getEventType() == EventType.PROFILE_CHANGED) {
					ExmUtil.updateVisitorProfile(localVisitor, remoteVisitor);
					visitorDao.update(localVisitor);
				}
			} else {
				event.setVisitor(null);
			}
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
	}

	@Override
	public void registerClientDisconnectedEvent() {
		Event event = new Event();
		event.setEventType(EventType.CLIENT_DISCONNECTED);
		event.setTimestamp(new Date());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Registering 'client disconnected' event: " + event.getEventType());
		}
		
		if (!serverManager.registerClientDisconnectedEvent(event)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Saving event in local database...");
			}
			// If failed - save event in the local database and dispatch.
			event.setStandalone(true);
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
	}

	@Override
	public void registerFloorEvent(int index, boolean down) {
		Event event = new Event();
		if (down) {
			event.setEventType(EventType.FLOOR_PRESSED);
		} else {
			event.setEventType(EventType.FLOOR_RELEASED);
		}
		event.setTimestamp(new Date());
		event.setDeviceIndex(index);
		
		// Try to register event on the master server.
		if (serverManager.registerFloorEvent(index, down)) {
			event.setStandalone(false);
		} else {
			// If failed - save event in the local database and dispatch.
			event.setStandalone(true);
			
			eventDao.saveEvent(event);
			
			needsSynchronization.set(true);
			
			// Check if the connection has been restored.
			if (serverManager.isConnected()) {
				synchronize();
			}
		}
		
		eventManager.registerEvent(event);
	}

	@Override
	public void registerDummyRfidEvent() {
		Visitor visitor = new Visitor();
		visitor.setAge("29");
		visitor.setEntryTime(new Date());
		visitor.setId(23L);
		visitor.setLanguage("pl");
		visitor.setLevel("3");
		visitor.setName("Jan Kowalski");
		visitor.setRfid("43fe6a880b");
		visitor.setRfidTimestamp(new Date());
		visitor.setRfidValidFrom(new Date());
		visitor.setVersion(0);
		visitor.setZoom("1");
		
		Event event = new Event();
		event.setData(visitor.getRfid());
		event.setDeviceIndex(0);
		event.setEventType(EventType.RFID_ACCEPTED);
		event.setId(5124L);
		event.setStandalone(false);
		event.setTimestamp(new Date());
		event.setVersion(0);
		event.setVisitor(visitor);
		
		eventManager.registerEvent(event);
	}

	@Override
	public void onSuccessfulConnection() {
		if (needsSynchronization.get()) {
			synchronize();
		}
	}
	
	private void synchronize() {
		if (performingSynchronization.compareAndSet(false, true)) {
			try {
				synchronizationManager.sendData();
			} catch (NoConnectionException e) {
				logger.warn("Synchronization failed.", e);
				return;
			} catch (CannotAcquireLockException e) {
				logger.warn("Synchronization failed.", e);
				return;
			} finally {
				performingSynchronization.set(false);
			}
			
			needsSynchronization.set(false);
		}
	}
	
}
