package com.bitzware.exm.service;

import java.util.List;

import com.bitzware.exm.listener.ConnectionListener;
import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.Room;


/**
 * An interface for a class that manages station data and connection functions.
 * 
 * @author finagle
 */
public interface ServerManager extends ServerManagerBase {

	/**
	 * A class that represents this station's configurable properties.
	 * 
	 * @author finagle
	 */
	static class Properties {
		private String name;
		private String description;
		private Long room;
		
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

		public Long getRoom() {
			return room;
		}

		public void setRoom(final Long room) {
			this.room = room;
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
	 * Returns station's room.
	 */
	Long getRoom();
	
	/**
	 * Saves station's name.
	 */
	void saveRoom(Long room);
	
	/**
	 * Returns true if this station is connected to the master server, false otherwise.
	 */
	boolean isConnected();
	
	/**
	 * This method tries to connect to the master server (if not connected already) and
	 * sends this station's name and other properties.
	 */
	void register();
	
	/**
	 * Sends the heartbeat message.
	 * 
	 * @return true if the master server knows this station, false otherwise.
	 */
	boolean heartbeat();
	
	/**
	 * Sends this station's data to the master server.
	 * @return
	 */
	boolean updataData();
	
	/**
	 * Returns a list of rooms from the master server.
	 * @return
	 */
	List<Room> getRooms();
	
	/**
	 * Registers a RFID event on the master server.
	 */
	RfidEventInfo registerRfidEvent(String rfid, Integer deviceIndex);
	
	/**
	 * Registers a Button event on the master server.
	 */
	boolean registerButtonEvent(int index, boolean down);
	
	/**
	 * Registers a PIR event on the master server.
	 */
	boolean registerPirEvent();
	
	/**
	 * Registers a 'score selected' event on the master server.
	 */
	boolean registerScoreEvent(Event event);
	
	/**
	 * Registers a Director event on the master server.
	 */
	boolean registerDirectorEvent(Event event);
	
	/**
	 * Registers a 'client disconnected' event on the master server.
	 */
	boolean registerClientDisconnectedEvent(Event event);
	
	/**
	 * Registers a 'floor' event on the master server.
	 */
	boolean registerFloorEvent(int index, boolean down);
	
	/**
	 * Sends synchronization data to the master server.
	 */
	boolean sendSynchronizationData(List<Event> events);

	/**
	 * Adds the connection listener.
	 */
	void registerConnectionListener(ConnectionListener listener);

}
