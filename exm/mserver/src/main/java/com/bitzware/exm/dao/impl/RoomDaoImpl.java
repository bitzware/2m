package com.bitzware.exm.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.RoomDao;
import com.bitzware.exm.visitordb.model.Room;


public class RoomDaoImpl extends HibernateDaoSupport implements RoomDao {

	protected Logger logger = Logger.getLogger(this.getClass());
	
		
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Room getRoomById(final Long id) {
		return (Room) getHibernateTemplate().get(Room.class, id);
	}

	
	public Room getRoomWithStationsById(final Long id) {
		return (Room) getHibernateTemplate().execute(new HibernateCallback() {
			
			
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				
				return session.createQuery(
						"from Room r left join fetch r.stations "
						+ "where r.id=:id")
						.setParameter("id", id)
						.uniqueResult();
			}
		});
	}
	
	
	public Room getRoomFullById(final Long id) {
		return (Room) getHibernateTemplate().execute(new HibernateCallback() {
			
			
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				
				return session.createQuery(
						"from Room r left join fetch r.children "
						+ "where r.id=:id")
						.setParameter("id", id)
						.uniqueResult();
			}
		});
	}

	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Room> getRooms() {
		return (List<Room>) getHibernateTemplate().execute(new HibernateCallback() {

			
			public List<Room> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				List<Room> rooms = session.createQuery("from Room order by name").list();
				
				for (Room room : rooms) {
					session.evict(room);
					
					room.setStations(null);
				}
				
				return rooms;
			}
			
		});
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Room> getRoomsWithStations() {
		return getHibernateTemplate().find(
				"select distinct r "
				+ "from Room r left join fetch r.stations s "
				+ "order by r.floor, r.name");
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Room> getToplevelRoomsWithStations() {
		return getHibernateTemplate().find(
				"select distinct r "
				+ "from Room r left join fetch r.stations s left join fetch r.children ch left join fetch ch.stations "
				+ "where r.parent is null "
				+ "order by r.floor, r.name");
	}

	
	
	public void updateRoom(Room room) {
		getHibernateTemplate().update(room);
	}

	@SuppressWarnings("unchecked")
	
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<String> getFloors() {
		return getHibernateTemplate().find("select distinct floor from Room order by floor");
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveRoomsConfig(final Collection<Room> rooms) {
		getHibernateTemplate().execute(new HibernateCallback() {

			@SuppressWarnings("unchecked")
			
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				List<Room> oldRooms = session.createQuery("from Room").list();
				Map<Long, Room> roomsMap = new HashMap<Long, Room>(oldRooms.size());
				
				for (Room oldRoom : oldRooms) {
					roomsMap.put(oldRoom.getId(), oldRoom);
				}
				
				for (Room room : rooms) {
					if (room.getId() == null) {
						session.save(room);
					} else {
						Room oldRoom = roomsMap.get(room.getId());
						
						if (oldRoom == null) {
							logger.error("Room with id: " + room.getId() + " does not exist!");
							continue;
						}

						oldRoom.setName(room.getName());
						oldRoom.setDescription(room.getDescription());
						oldRoom.setFloor(room.getFloor());

						session.update(oldRoom);
					}
				}
				
				return null;
			}
			
		});
	}

	
}
