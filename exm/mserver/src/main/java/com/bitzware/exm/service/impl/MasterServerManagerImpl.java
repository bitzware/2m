package com.bitzware.exm.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.RoomDao;
import com.bitzware.exm.dao.StationDao;
import com.bitzware.exm.model.ServerProperties;
import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.model.TimeInterval;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


/**
 * Class that manages master server data.
 * 
 * @author finagle
 */
public class MasterServerManagerImpl extends ServerManagerBaseImpl implements MasterServerManager {

	private final DateFormat propertiesTimeFormat = new SimpleDateFormat("HH:mm");
	
	private RoomDao roomDao;
	private StationDao stationDao;

	public RoomDao getRoomDao() {
		return roomDao;
	}

	public void setRoomDao(final RoomDao roomDao) {
		this.roomDao = roomDao;
	}

	public StationDao getStationDao() {
		return stationDao;
	}

	public void setStationDao(final StationDao stationDao) {
		this.stationDao = stationDao;
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Properties getProperties() {
		Properties result = new Properties();
		result.setName(getName());
		result.setDescription(getDescription());
		result.setRfidTimeout(getRfidTimeout());

		return result;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveProperties(final Properties properties) {
		if (properties != null) {
			saveName(properties.getName());
			saveDescription(properties.getDescription());
			saveRfidTimeout(properties.getRfidTimeout());
		}
	}

	/**
	 * Update data of the specified station.
	 */
	public void updateStation(final StationProperties stationData) {
		stationDao.updateStation(stationData);
	}

	/**
	 * Method called on heartbeat from the connected station.
	 */
	public boolean heartbeat(final String mac, final ActiveStationStatus status) {
		return stationDao.heartbeat(mac, status);
	}

	/**
	 * Returns station properties.
	 */
	public ServerProperties getServerProperties() {
		ServerProperties serverProperties = new ServerProperties();
		serverProperties.setRfidTimeout(getRfidTimeout());

		return serverProperties;
	}

	/**
	 * Returns a list of unconfigured stations.
	 */
	public List<Station> getUnconfiguredStations() {
		return stationDao.getUnconfiguredStations();
	}

	@Override
	public List<Station> getStations() {
		return stationDao.getStations();
	}

	@Override
	public List<Station> getStationsWithRooms() {
		return stationDao.getStationsWithRooms();
	}

	/**
	 * Get a list of rooms.
	 */
	public List<Room> getRooms() {
		return roomDao.getRooms();
	}

	public Room getRoom(long id) {
		return roomDao.getRoomById(id);
	}
	
	@Override
	public Room getRoomFull(long id) {
		return roomDao.getRoomFullById(id);
	}
	 
	
	/**
	 * Returns a list of rooms with stations.
	 */
	public List<Room> getRoomsWithStations() {
		return roomDao.getRoomsWithStations();
	}

	
	
	/**
	 * Returns a list of rooms with stations.
	 */
	public List<Room> getToplevelRoomsWithStations() {
		return roomDao.getToplevelRoomsWithStations();
	}
	
	public List<String> getFloors() {
		return roomDao.getFloors();
	}

	/**
	 * Saves room configuration. Rooms with not-null id will be updated, rooms
	 * with null id will be created. This method never deletes rooms from
	 * the database.
	 */
	public void saveRoomsConfig(final Collection<Room> rooms) {
		roomDao.saveRoomsConfig(rooms);
	}
	
	public boolean assignRoom2Room(long assignMe, long assignTo) {
		System.out.println("assignemt of "+assignMe + " to"+assignTo);
		Room child = roomDao.getRoomById(assignMe);
		Room parent = roomDao.getRoomById(assignTo);
		if(parent.getParent()!=null) {
			return false;
		}
		if(child.getChildren()!=null && child.getChildren().size()>0) {
			return false;
		}
		// we allow 2 levels only
		child.setParent(parent);
		parent.getChildren().add(child);
		roomDao.updateRoom(child);
		return true;
	}
	
	public boolean deleteStation(String stationIp) {
	 return stationDao.deleteStation(stationIp);
	}
	
	@Override
	public boolean turnOff(String stationIp) {
		final String addressSuffix = getTurnOffAddressSuffix();
		final Integer port = getTurnOffPort();
		final Integer timeout = getTurnOffTimeout();
		final String login = getTurnOffLogin();
		final String password = getTurnOffPassword();

		return turnOff(stationIp, port, addressSuffix, timeout, login, password);
	}

	@Override
	public boolean turnOffRoom(Long roomId) {
		final String addressSuffix = getTurnOffAddressSuffix();
		final Integer port = getTurnOffPort();
		final Integer timeout = getTurnOffTimeout();
		final String login = getTurnOffLogin();
		final String password = getTurnOffPassword();

		Room room = roomDao.getRoomWithStationsById(roomId);
		
		
			
		if (room == null || room.getStations() == null) {
			logger.warn("Invalid room id: " + roomId);
			return false;
		}
		
		Room room2 = roomDao.getRoomFullById(roomId);
		
		
		for (Room subRoom : room2.getChildren()) {
			turnOffRoom(subRoom.getId()); // recurrency is its own reward
		}

		for (Station station : room.getStations()) {
			if (station.getIpAddress() != null) {
				if (!turnOff(station.getIpAddress(), port, addressSuffix, timeout, login,
						password)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Cannot shutdown station: " + station.getIpAddress());
					}
				}
			} else {
				logger.warn("Station is without an IP address.");
			}
		}
		
		
		return true;
	}

	@Override
	public boolean turnOffAllRooms(List<Room> roomsWithStations) {
		final String addressSuffix = getTurnOffAddressSuffix();
		final Integer port = getTurnOffPort();
		final Integer timeout = getTurnOffTimeout();
		final String login = getTurnOffLogin();
		final String password = getTurnOffPassword();

		for (Room room : roomsWithStations) {
			for (Station station : room.getStations()) {
				if (station.getIpAddress() != null) {
					if (!turnOff(station.getIpAddress(), port, addressSuffix, timeout, login,
							password)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Cannot shutdown station: " + station.getIpAddress());
						}
					}
				} else {
					logger.warn("Station is without an IP address.");
				}
			}
		}

		return true;
	}
	
	@Override
	public Calendar getOpenMuseumTime() {
		String openMuseumTime = serverPropertyDao.getStringProperty(
				ConfigManager.OPEN_MUSEUM_TIME);
		return parseProperty(openMuseumTime);
	}
	
	@Override
	public void setOpenMuseumTime(Calendar time) {
		serverPropertyDao.setStringProperty(ConfigManager.OPEN_MUSEUM_TIME,
				formatProperty(time));
	}

	@Override
	public Calendar getCloseMuseumTime() {
		String openMuseumTime = serverPropertyDao.getStringProperty(
				ConfigManager.CLOSE_MUSEUM_TIME);
		return parseProperty(openMuseumTime);
	}
	
	@Override
	public void setCloseMuseumTime(Calendar time) {
		serverPropertyDao.setStringProperty(ConfigManager.CLOSE_MUSEUM_TIME,
				formatProperty(time));
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateRoomOpenTimes(Map<Long, TimeInterval> roomIntervals) {
		List<Room> rooms = roomDao.getRooms();
		Map<Long, Room> roomsMap = new HashMap<Long, Room>(rooms.size());
		
		for (Room room : rooms) {
			roomsMap.put(room.getId(), room);
		}
		
		for (Map.Entry<Long, TimeInterval> entry : roomIntervals.entrySet()) {
			Long roomId = entry.getKey();
			TimeInterval interval = entry.getValue();
			
			Room room = roomsMap.get(roomId);
			if (room != null) {
				room.setOpenTime(interval.getStart());
				room.setCloseTime(interval.getEnd());
				
				roomDao.updateRoom(room);
			}
		}
	}

	/**
	 * Returns the rfid timeout from the database or config file.
	 */
	public Integer getRfidTimeout() {
		return serverPropertyDao.getIntegerProperty(ConfigManager.RFID_TIMEOUT);
	}
	
	/**
	 * Saves the rfid timeout to the database and local cache.
	 */
	public void saveRfidTimeout(final Integer newTimeout) {
		serverPropertyDao.setIntegerProperty(ConfigManager.RFID_TIMEOUT, newTimeout);
	}

	@Override
	public Integer getRfidTimeOfEntry() {
		return serverPropertyDao.getIntegerProperty(ConfigManager.RFID_TIMEOFENTRY);
	}

	@Override
	public void saveRfidTimeOfEntry(Integer newTimeOfEntry) {
		serverPropertyDao.setIntegerProperty(ConfigManager.RFID_TIMEOFENTRY, newTimeOfEntry);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveProperties(final String name, final String description, final Integer timeout,
			Integer timeOfEntry) {
		saveName(name);
		saveDescription(description);
		saveRfidTimeout(timeout);
		saveRfidTimeOfEntry(timeOfEntry);
	}
	
	private Integer getTurnOffPort() {
		return serverPropertyDao.getIntegerProperty(ConfigManager.STATION_SHUTDOWN_PORT);
	}

	private String getTurnOffAddressSuffix() {
		final Integer port = serverPropertyDao.getIntegerProperty(
				ConfigManager.STATION_SHUTDOWN_PORT);
		final String path = serverPropertyDao.getStringProperty(
				ConfigManager.STATION_SHUTDOWN_PATH);

		return ExmUtil.concat(":", String.valueOf(port), path);
	}

	private Integer getTurnOffTimeout() {
		return serverPropertyDao.getIntegerProperty(ConfigManager.STATION_SHUTDOWN_TIMEOUT);
	}
	
	private String getTurnOffLogin() {
		return serverPropertyDao.getStringProperty(ConfigManager.STATION_SHUTDOWN_LOGIN);
	}
	
	private String getTurnOffPassword() {
		return serverPropertyDao.getStringProperty(ConfigManager.STATION_SHUTDOWN_PASSWORD);
	}


	private boolean turnOff(String stationIp, Integer port, String addressSuffix,
			Integer timeout, String login, String password) {
		final int responseOk = 200;

		try {
			// FIXME: Remove debug logs.
			final String address = ExmUtil.concat("http://", stationIp, addressSuffix);

			logger.debug("Creating HTTP client.");
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter("http.socket.timeout", timeout);
			if (login != null) {
				logger.debug("Using basic authentication.");
				
				Credentials credentials = new UsernamePasswordCredentials(login, password);
				AuthScope scope = new AuthScope(stationIp, port);
				BasicCredentialsProvider provider = new BasicCredentialsProvider();
				provider.setCredentials(scope, credentials);
				httpClient.setCredentialsProvider(provider);
			}
			
			logger.debug("Creating HTTP GET request.");
			HttpGet httpGet = new HttpGet(address);
			logger.debug("Executing HTTP request.");
			HttpResponse response = httpClient.execute(httpGet);
			logger.debug("Request executed.");

			if (response.getStatusLine().getStatusCode() == responseOk) {
				return true;
			} else {
				logger.error(
						"Response: " + response.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			logger.error("Error while shutting the station down.", e);
		} catch (IOException e) {
			logger.error("Error while shutting the station down.", e);
		}

		return false;
	}

	private Calendar parseProperty(String s) {
		try {
			if (s != null) {
				Calendar result = Calendar.getInstance();
				result.setTime(propertiesTimeFormat.parse(s));
				
				return result;
			} else {
				return null;
			}
		} catch (ParseException e) {
			logger.error("Invalid time format: " + s);
			return null;
		}
	}
	
	private String formatProperty(Calendar c) {
		if (c == null) {
			return null;
		} else {
			return propertiesTimeFormat.format(c.getTime());
		}
	}
	
	@Override
	public boolean turn(String url,Integer port, String path,Integer timeout) {
		final int responseOk = 200;

		try {
			// FIXME: Remove debug logs.
			//final String address = "http://"+url;

			logger.debug("Creating HTTP client.");
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter("http.socket.timeout", timeout);
			java.net.URI address=null;
			try {
				address = new java.net.URI("http", null, url, port,path,"","");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				logger.debug(e);
			} 

			
			logger.debug("Creating HTTP GET request.");
			HttpGet httpGet = new HttpGet(address);
			logger.debug("Executing HTTP request.");
			HttpResponse response = httpClient.execute(httpGet);
			logger.debug("Request executed.");

			if (response.getStatusLine().getStatusCode() == responseOk) {
				return true;
			} else {
				logger.error(
						"Response: " + response.getStatusLine().getReasonPhrase());
			}
		}  catch (IOException e) {
			logger.error("Error while accessing url.", e);
		}

		return false;
	}

}
