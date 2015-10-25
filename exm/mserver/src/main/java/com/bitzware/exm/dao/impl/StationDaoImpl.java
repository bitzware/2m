package com.bitzware.exm.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.StationDao;
import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


public class StationDaoImpl extends HibernateDaoSupport implements StationDao {

	private Logger logger = Logger.getLogger(this.getClass());

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveStation(final Station station) {
		getHibernateTemplate().save(station);
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateStation(final StationProperties stationData) {
		if (StringUtils.isEmpty(stationData.getMac())) {
			logger.warn("Station sent an empty MAC address!");
			return;
		}
		
		getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				if (logger.isDebugEnabled()) {
					logger.debug("Room: " + stationData.getRoom());
					logger.debug("Status: " + stationData.getStatus());
				}
				
				// Get station from the database
				Station station = (Station) session
					.createQuery("from Station s left join fetch s.room where s.macAddress=:mac")
					.setParameter("mac", stationData.getMac())
					.uniqueResult();
				
				// If not exists - it means that the station is registering now, create it in
				// the database
				if (station == null) {
					station = new Station();
					station.setMacAddress(stationData.getMac());
				}
				
				Date currentTime = new Date();
	
				station.setIpAddress(getNonEmptyString(stationData.getIp()));
				station.setName(getNonEmptyString(stationData.getName()));
				station.setDescription(getNonEmptyString(stationData.getDescription()));
				station.setStatus(stationData.getStatus());
				station.setLastHeartbeat(currentTime);
				
				if (station.getRegisteredOn() == null) {
					station.setRegisteredOn(currentTime);
				}
				
				if (station.getId() == null) {
					// If it is a new station, assign the correct room.
					session.save(station);
					
					assignStationRoom(station, stationData, session);
				} else {
					// If it is a registered station, check if the room has been changed.
					Room room = station.getRoom();
					
					// If the room has been changed, update it in the database.
					if (isDifferentRoom(room, stationData.getRoom())) {
						if (room != null) {
							room.getStations().remove(station);
							station.setRoom(null);
						}
						
						assignStationRoom(station, stationData, session);
						
						if (room != null) {
							session.update(room);
						}
					}
					session.update(station);
				}
				
				return null;
			}
			
		});
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean deleteStation(final String mac) {
		if (StringUtils.isEmpty(mac)) {
			logger.warn("An empty MAC address!");
			return false;
		}
		
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Boolean doInHibernate(final Session session)
					throws HibernateException, SQLException {
				List<?> stations = session
					.createQuery("from Station where macAddress=:mac")
					.setParameter("mac", mac)
					.list();
				
				if (stations.isEmpty()) {
					return Boolean.FALSE;
				} else {
					Station station = (Station) stations.get(0);
					session.delete(station);
					return Boolean.TRUE;
				}
			}	
		});
	}
	

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean heartbeat(final String mac, final ActiveStationStatus status) {
		if (StringUtils.isEmpty(mac)) {
			logger.warn("Station sent an empty MAC address!");
			return false;
		}
		
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Boolean doInHibernate(final Session session)
					throws HibernateException, SQLException {
				List<?> stations = session
					.createQuery("from Station where macAddress=:mac")
					.setParameter("mac", mac)
					.list();
				
				if (stations.size() > 1) {
					logger.error("Multiple stations with the same MAC in the database!");
				}
				
				if (stations.isEmpty()) {
					return Boolean.FALSE;
				} else {
					Station station = (Station) stations.get(0);
					station.setLastHeartbeat(new Date());
					station.setStatus(status);
					session.update(station);
					
					return Boolean.TRUE;
				}
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Station> getUnconfiguredStations() {
		final String query =
			"from Station "
			+ "where name is null or name='' or room is null "
			+ "order by ipAddress";
		
		return getHibernateTemplate().find(query);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Station> getStations() {
		return (List<Station>) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public List<Station> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				return session.createQuery("from Station").list();
			}
			
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Station> getStationsWithRooms() {
		return (List<Station>) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public List<Station> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
				return session.createQuery(
						"from Station s left join fetch s.room where s.name is not null order by s.name").list();
			}
			
		});
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Station getStationById(final Long id) {
		return (Station) getHibernateTemplate().get(Station.class, id);
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Station getStationWithRoomById(final Long id) {
		return (Station) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Station doInHibernate(final Session session)
					throws HibernateException, SQLException {
				return (Station) session.createQuery(
						"from Station s left join fetch s.room where s.id=:id")
						.setParameter("id", id)
						.uniqueResult();
			}
			
		});
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Station getStationByMacAddress(final String mac) {
		return (Station) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				return session
					.createQuery("from Station where macAddress=:macAddress")
					.setParameter("macAddress", mac)
					.uniqueResult();
				
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	public Set<Station> getStationsFromRoom(final Long stationId) {
		return (Set<Station>) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public Set<Station> doInHibernate(Session session) throws HibernateException,
					SQLException {
				Station station = (Station) session.createQuery(
						"from Station s left join fetch s.room where s.id=:id")
						.setParameter("id", stationId)
						.uniqueResult();
				
				if (station == null || station.getRoom() == null) {
					return Collections.EMPTY_SET;
				}
				
				Room room = (Room) session.createQuery(
						"from Room r left join fetch r.stations "
						+ "where r.id=:id")
						.setParameter("id", station.getRoom().getId())
						.uniqueResult();
				
				return room.getStations();
			}
		});
	}

	/**
	 * Returns the specified string or null if it is empty.
	 */
	private String getNonEmptyString(final String s) {
		if (s == null || s.equals("")) {
			return null;
		} else {
			return s;
		}
	}
	
	private void assignStationRoom(final Station station, final StationProperties stationData,
			final Session session) {
		if (stationData.getRoom() != null) {
			Room room = (Room) session.get(Room.class, stationData.getRoom());
			
			if (room != null) {
				room.getStations().add(station);
				station.setRoom(room);
			
				session.update(room);
			} else {
				logger.warn("Invalid room: " + stationData.getRoom());
			}
		}
	}
	
	private boolean isDifferentRoom(final Room room, final Long roomId) {
		if (room == null || room.getId() == null) {
			return roomId != null;
		} else {
			return !room.getId().equals(roomId);
		}
	}
}
