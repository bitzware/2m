package com.bitzware.exm.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.bitzware.exm.model.ServerProperties;
import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.model.TimeInterval;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


/**
 * An interface for a class that manages master server data.
 * 
 * @author finagle
 */
public interface MasterServerManager extends ServerManagerBase {
	
	/**
	 * A class that represents this server's configurable properties.
	 * 
	 * @author finagle
	 */
	static class Properties {
		private String name;
		private String description;
		private Integer rfidTimeout;
		private Integer rfidTimeOfEntry;
		
		public String getName() {
			return name;
		}
		
		public void setName(final String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

		public Integer getRfidTimeout() {
			return rfidTimeout;
		}

		public void setRfidTimeout(final Integer rfidTimeout) {
			this.rfidTimeout = rfidTimeout;
		}

		public Integer getRfidTimeOfEntry() {
			return rfidTimeOfEntry;
		}

		public void setRfidTimeOfEntry(Integer rfidTimeOfEntry) {
			this.rfidTimeOfEntry = rfidTimeOfEntry;
		}
		
	}
	
	/**
	 * Returns all properties of this server (name, description, etd.).
	 */
	Properties getProperties();
	
	/**
	 * Saves properties of this server.
	 */
	void saveProperties(Properties properties);
	
	/**
	 * Update data of the specified station.
	 */
	void updateStation(StationProperties stationData);

	/**
	 * Method called on heartbeat from the connected station.
	 */
	boolean heartbeat(String mac, ActiveStationStatus status);

	/**
	 * Returns station properties.
	 */
	ServerProperties getServerProperties();

	/**
	 * Returns a list of unconfigured stations.
	 */
	List<Station> getUnconfiguredStations();
	
	/**
	 * Returns a list of all stations.
	 */
	List<Station> getStations();
	
	/**
	 * Returns a list of all stations with rooms.
	 */
	List<Station> getStationsWithRooms();
	
	/**
	 * Returns a list of rooms.
	 */
	List<Room> getRooms();
	
	Room getRoom(long id);

	/**
	 * Returns a list of rooms with stations.
	 */
	List<Room> getRoomsWithStations();
	
	/**
	 * Returns a list of rooms with stations.
	 */
	List<Room> getToplevelRoomsWithStations();
	
	
	/**
	 * Returns a list of available floors.
	 */
	List<String> getFloors();

	/**
	 * Saves room configuration. Rooms with not-null id will be updated, rooms
	 * with null id will be created. This method never deletes rooms from
	 * the database.
	 */
	void saveRoomsConfig(Collection<Room> rooms);

	
	public boolean assignRoom2Room(long x,long y);
	/**
	 * Turns off the specified station (via a HTTP calls to the admin panel).
	 */
	boolean turnOff(String stationIp);
		
	public boolean deleteStation(String stationIp);
	
	/**
	 * Turns off all stations in the specified room (via a HTTP calls to
	 * the admin panel).
	 */
	boolean turnOffRoom(Long roomId);
	
	/**
	 * Turns off all stations in the specified rooms (via a HTTP calls to
	 * the admin panel).
	 */
	boolean turnOffAllRooms(List<Room> roomsWithStations);
	
	/**
	 * Returns the time of the opening of the museum.
	 */
	Calendar getOpenMuseumTime();
	
	/**
	 * Sets the time of the museum opening.
	 */
	void setOpenMuseumTime(Calendar time);
	
	/**
	 * Returns the time of the closing of the museum.
	 */
	Calendar getCloseMuseumTime();
	
	/**
	 * Sets the time of the museum closing.
	 */
	void setCloseMuseumTime(Calendar time);
	
	/**
	 * Updates room open / close times in the database. The specified map should
	 * contain room ids as keys and time intervals as values.
	 */
	void updateRoomOpenTimes(Map<Long, TimeInterval> roomIntervals);
	
	/**
	 * Returns the rfid timeout from the database or config file.
	 * @return
	 */
	Integer getRfidTimeout();
	
	/**
	 * Returns the rfid time of entry from the database or config file.
	 * @return
	 */
	Integer getRfidTimeOfEntry();
	
	/**
	 * Saves the rfid timeout to the database.
	 */
	void saveRfidTimeout(Integer newTimeout);
	
	/**
	 * Saves the rfid time of entry to the database.
	 */
	void saveRfidTimeOfEntry(Integer newTimeOfEntry);
	
	/**
	 * Saves server properties.
	 */
	void saveProperties(String name, String description, Integer timeout, Integer timeOfEntry);

	boolean turn(String url, Integer port, String path, Integer timeout);

	Room getRoomFull(long id);
	
}
