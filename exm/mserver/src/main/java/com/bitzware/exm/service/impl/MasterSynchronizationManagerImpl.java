package com.bitzware.exm.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.EventDao;
import com.bitzware.exm.dao.StationDao;
import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.MasterSynchronizationManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.util.EventUtil;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Station;
import com.bitzware.exm.visitordb.model.Visitor;


public class MasterSynchronizationManagerImpl implements MasterSynchronizationManager {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
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

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void saveData(final List<Event> events, final String stationMac) {
		Station station = stationDao.getStationByMacAddress(stationMac);
		
		if (station == null) {
			station = new Station();
			station.setMacAddress(stationMac);
			
			stationDao.saveStation(station);
		}
		
		Integer rfidTimeout = masterServerManager.getRfidTimeout();
		Integer rfidTimeOfEntry = masterServerManager.getRfidTimeOfEntry();
		
		Set<String> updated = new HashSet<String>();
		
		for (Event event : events) {
			event.setId(null);
			event.setStation(station);
			
			// Check if there is a visitor attached to this event.
			Visitor remoteVisitor = event.getVisitor();
			
			if (event.getEventType() == EventType.RFID_INVALID) {
				if (remoteVisitor != null && remoteVisitor.getRfid() != null) {
					updateVisitor(remoteVisitor, updated);

					// Find a local copy of this visitor and check if there was a timeout.
					Visitor localVisitor = visitorDao.getVisitorByRfid(remoteVisitor.getRfid());

					if (localVisitor != null) {
						event.setVisitor(localVisitor);

						EventType type = EventType.RFID_EXPIRED;

						// Check if all required properties are not null.
						if (EventUtil.checkIfValid(localVisitor, rfidTimeout, rfidTimeOfEntry)) {
							// Check if there was a timeout.
							if (!EventUtil.rfidExpired(localVisitor.getEntryTime(),
									localVisitor.getRfidValidFrom(), event.getTimestamp(),
									rfidTimeout, rfidTimeOfEntry, localVisitor.getExpired())) {
								
								if (localVisitor.getEntryTime() == null) {
									localVisitor.setEntryTime(event.getTimestamp());
									visitorDao.update(localVisitor);
								}
								
								type = EventType.RFID_ACCEPTED;
							}
						}

						event.setEventType(type);
					} else {
						event.setVisitor(null);
						event.setEventType(EventType.RFID_INVALID);
					}
				} else if (remoteVisitor != null) {
					event.setVisitor(null);
				}
			} else {
				if (remoteVisitor != null && remoteVisitor.getRfid() != null) {
					Visitor localVisitor = visitorDao.getVisitorByRfid(remoteVisitor.getRfid());
					
					event.setVisitor(localVisitor);
					
					if (event.getEventType() == EventType.PROFILE_CHANGED) {
						updateVisitor(remoteVisitor, updated);
					}
				}
			}
			
			eventDao.saveEvent(event);
		}
	}
	
	/**
	 * Updates the specified visitor profile. Returns the local updated visitor reference.
	 * If the 'updated' set is not null and it contains the RFID of the visitor, it will
	 * be NOT updated. Visitor's RFID is added to the set.
	 */
	private Visitor updateVisitor(final Visitor remoteVisitor, final Set<String> updated) {
		if (remoteVisitor == null || remoteVisitor.getRfid() == null) {
			return null;
		}
		
		if (updated != null && updated.contains(remoteVisitor.getRfid())) {
			return null;
		}
		
		Visitor localVisitor = visitorDao.getVisitorByRfid(remoteVisitor.getRfid());
		
		if (localVisitor == null) {
			return null;
		}
		
		Date localLastUpdate = localVisitor.getLastUpdate();
		Date remoteLastUpdate = remoteVisitor.getLastUpdate();
		if (localLastUpdate == null || remoteLastUpdate == null
				|| (localLastUpdate.getTime() < remoteLastUpdate.getTime())) {
			
			ExmUtil.updateVisitorProfile(localVisitor, remoteVisitor);

			visitorDao.update(localVisitor);
		}
		
		if (updated != null) {
			updated.add(remoteVisitor.getRfid());
		}
		
		return localVisitor;
	}
	
}
