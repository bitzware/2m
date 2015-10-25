package com.bitzware.exm.dao;

import java.util.Collection;
import java.util.List;

import com.bitzware.exm.visitordb.model.Room;


public interface RoomDao {

	/**
	 * Returns a room with the specified id or null if it does not exist.
	 */
	Room getRoomById(Long id);
	
	/**
	 * Returns a room with stations with the specified id or null if it
	 * does not exist.
	 */
	Room getRoomWithStationsById(Long id);
	
	/**
	 * Get room list.
	 */
	List<Room> getRooms();

	/**
	 * Get list of rooms with stations.
	 */
	List<Room> getRoomsWithStations();
	
	/**
	 * Get list of rooms with stations.
	 */
	List<Room> getToplevelRoomsWithStations();
		
	
	/**
	 * Updates the specified room.
	 */
	void updateRoom(Room room);
	
	/**
	 * Returns a list of available floors.
	 */
	List<String> getFloors();
		
	/**
	 * Save rooms configuration. This method does not change attached stations.
	 */
	void saveRoomsConfig(final Collection<Room> rooms);

	Room getRoomFullById(Long id);
	
	
}
