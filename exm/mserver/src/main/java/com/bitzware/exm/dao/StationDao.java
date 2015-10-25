package com.bitzware.exm.dao;

import java.util.List;
import java.util.Set;

import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Station;


public interface StationDao {

	/**
	 * Saves the specified station in the database.
	 */
	void saveStation(final Station station);
	
	/**
	 * Updates a station with the specified data. The 'lastHeartbeat' property will
	 * be set to current time.
	 */
	void updateStation(final StationProperties stationData);

	/**
	 * This method updated the station status. It should be called on heartbeat from
	 * this station.
	 * @return true if the station exists in the database, false otherwise.
	 */
	boolean heartbeat(final String mac, final ActiveStationStatus status);
	
	/**
	 * Gets the unconfigured station list.
	 */
	List<Station> getUnconfiguredStations();

	/**
	 * Returns all stations.
	 */
	List<Station> getStations();
	
	/**
	 * Returns all stations with rooms.
	 */
	List<Station> getStationsWithRooms();
	
	/**
	 * Returns a station with the specified id or null if it does not exist.
	 */
	Station getStationById(final Long id);
	
	/**
	 * Returns a station with the specified it and it's room.
	 */
	Station getStationWithRoomById(final Long id);
	
	/**
	 * Returns a station with the given mac address.
	 */
	Station getStationByMacAddress(final String mac);

	/**
	 * Returns all stations from the room, which contains the specified station.
	 * @param stationId
	 */
	Set<Station> getStationsFromRoom(final Long stationId);
	
	boolean deleteStation(final String mac);
}
