package com.bitzware.exm.service.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.EventDao;
import com.bitzware.exm.dao.StationDao;
import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.service.MasterEventRegisterManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.util.EventUtil;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Station;
import com.bitzware.exm.visitordb.model.Visitor;


/**
 * Class that handles event registration on the master server.
 * 
 * @author finagle
 *
 */
public class MasterEventRegisterManagerImpl implements MasterEventRegisterManager {

	protected Logger logger = Logger.getLogger(this.getClass());
	
	private final String rfidInvalidStr = "invalid";
	private final String rfidExpiredStr = "expired";
	private final String rfidValidStr = "valid";
	
	private EventDao eventDao;
	private StationDao stationDao;
	private VisitorDao visitorDao;
	
	private MasterServerManager masterServerManager;

	public EventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(final EventDao eventDao) {
		this.eventDao = eventDao;
	}

	public StationDao getStationDao() {
		return stationDao;
	}

	public void setStationDao(final StationDao stationDao) {
		this.stationDao = stationDao;
	}

	public VisitorDao getVisitorDao() {
		return visitorDao;
	}

	public void setVisitorDao(final VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}
	
	public String checkRfid(String rfid) {
		Visitor visitor = visitorDao.getVisitorByRfid(rfid);
		
		if (visitor == null) {
			return rfidInvalidStr;
		}
		Date currentTime = new Date();
		// Get RFID timeout and time of entry.
		Integer rfidTimeout = masterServerManager.getRfidTimeout();
		Integer rfidTimeOfEntry = masterServerManager.getRfidTimeOfEntry();
		
		if (EventUtil.checkIfValid(visitor, rfidTimeout, rfidTimeOfEntry)) {
			if (!EventUtil.rfidExpired(visitor.getRfidValidFrom(), visitor.getEntryTime(),
					currentTime, rfidTimeout, rfidTimeOfEntry, visitor.getExpired())) {
				return rfidValidStr;
			} else {
				return rfidExpiredStr;
			}
		} else {
			return rfidInvalidStr;
		}
	}

	/**
	 * Registers a RFID event in the database.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public RfidEventInfo registerRfidEvent(final String rfid, final String stationMac,
			final Integer deviceIndex) {
		
		RfidEventInfo result = new RfidEventInfo();
		
		Station station = getOrCreateStation(stationMac);
		
		Date currentDate = new Date();
		
		Event event = new Event();
		event.setStation(station);
		event.setTimestamp(currentDate);
		event.setData(rfid);
		event.setDeviceIndex(deviceIndex);
		
		Visitor visitor = visitorDao.getVisitorByRfid(rfid);
		
		if (logger.isDebugEnabled()) {
			if (visitor != null) {
				logger.debug("Visitor for event: " + visitor.getId());
			} else {
				logger.debug("Visitor for event: null");
			}
		}
		
		if (visitor != null) {
			result.setVisitor(visitor);
			event.setVisitor(visitor);
			
			// Get RFID timeout and time of entry.
			Integer rfidTimeout = masterServerManager.getRfidTimeout();
			Integer rfidTimeOfEntry = masterServerManager.getRfidTimeOfEntry();
			
			// Check if all necessary fields are not null and if the visitor didn't
			// come too late.
			if (EventUtil.checkIfValid(visitor, rfidTimeout, rfidTimeOfEntry)) {
				if (!EventUtil.rfidExpired(visitor.getRfidValidFrom(), visitor.getEntryTime(),
						currentDate, rfidTimeout, rfidTimeOfEntry, visitor.getExpired())) {
					result.setTimeout(false);
					result.setInvalid(false);
					event.setEventType(EventType.RFID_ACCEPTED);
					
					if (visitor.getEntryTime() == null) {
						visitor.setEntryTime(currentDate);
						visitorDao.update(visitor);
					}
				} else {
					result.setTimeout(true);
					result.setInvalid(false);
					event.setEventType(EventType.RFID_EXPIRED);
				}
			} else {
				result.setInvalid(true);
				event.setEventType(EventType.RFID_INVALID);
			}
		} else {
			result.setInvalid(true);
			event.setEventType(EventType.RFID_INVALID);
		}
		
		eventDao.saveEvent(event);
		
		return result;
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerButtonEvent(final String stationMac, final Integer index,
			final Boolean down) {
		Station station = getOrCreateStation(stationMac);
		
		Event event = new Event();
		
		if (down) {
			event.setEventType(EventType.BUTTON_DOWN);
		} else {
			event.setEventType(EventType.BUTTON_UP);
		}
		event.setStation(station);
		event.setTimestamp(new Date());
		event.setData(String.valueOf(index));
		
		eventDao.saveEvent(event);
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerPirEvent(final String stationMac) {
		Station station = getOrCreateStation(stationMac);
		
		Event event = new Event();
		event.setEventType(EventType.PIR_CHANGED);
		event.setStation(station);
		event.setTimestamp(new Date());
		
		eventDao.saveEvent(event);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerScoreEvent(final Event event, final String stationMac) {
		Station station = getOrCreateStation(stationMac);
		
		event.setId(null);
		event.setStation(station);
		event.setTimestamp(new Date());
		
		eventDao.saveEvent(event);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerDirectorEvent(final Event event, final String stationMac) {
		Visitor localVisitor = null;
		if (event.getVisitor() != null && event.getVisitor().getRfid() != null) {
			localVisitor = visitorDao.getVisitorByRfid(event.getVisitor().getRfid());
			
			if (event.getEventType() == EventType.PROFILE_CHANGED && localVisitor != null) {
				ExmUtil.updateVisitorProfile(localVisitor, event.getVisitor());
				visitorDao.update(localVisitor);
			} 
		}
		
		Station station = getOrCreateStation(stationMac);
		
		event.setId(null);
		event.setTimestamp(new Date());
		event.setVisitor(localVisitor);
		event.setStation(station);
		
		eventDao.saveEvent(event);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void registerClientDisconnectedEvent(Event event, String stationMac) {
		Station station = getOrCreateStation(stationMac);
		
		event.setId(null);
		event.setStation(station);
		event.setTimestamp(new Date());
		
		eventDao.saveEvent(event);
	}

	@Override
	public void registerFloorEvent(String stationMac, Integer index,
			Boolean down) {
		Station station = getOrCreateStation(stationMac);
		
		Event event = new Event();
		
		if (down) {
			event.setEventType(EventType.FLOOR_PRESSED);
		} else {
			event.setEventType(EventType.FLOOR_RELEASED);
		}
		event.setStation(station);
		event.setTimestamp(new Date());
		event.setDeviceIndex(index);
		
		eventDao.saveEvent(event);
	}

	public void setRfidTimestamp(final String rfid, final Date date) {
		visitorDao.setRfidTimestamp(rfid, date);
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	private Station getOrCreateStation(final String stationMac) {
		Station station = stationDao.getStationByMacAddress(stationMac);
		
		if (station == null) {
			station = new Station();
			station.setMacAddress(stationMac);
			
			stationDao.saveStation(station);
		}
		
		return station;
	}
	
}
