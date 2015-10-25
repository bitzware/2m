package com.bitzware.exm.action.panel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.hibernate.Hibernate;

import com.bitzware.exm.model.Device;
import com.bitzware.exm.model.TimeInterval;
import com.bitzware.exm.service.CrestronManager;
import com.bitzware.exm.service.MasterDuplicateManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.MuseumOpenManager;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


/**
 * Struts action for the 'Rooms' panel.
 * 
 * @author finagle
 */
public class RoomsAction extends StationsInfoActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 1L;

	private static class StationNameComparator implements Comparator<Station> {

		@Override
		public int compare(final Station o1, final Station o2) {
			if (o1.getName() == null) {
				if (o2.getName() == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (o2.getName() == null) {
				return 1;
			}

			return o1.getName().compareTo(o2.getName());
		}

	}
	
	private static class RoomFormData {
		private String openTime;
		private String closeTime;
		private String errorMessage;
		
		public String getOpenTime() {
			return openTime;
		}
		
		public void setOpenTime(String openTime) {
			this.openTime = openTime;
		}

		public String getCloseTime() {
			return closeTime;
		}

		public void setCloseTime(String closeTime) {
			this.closeTime = closeTime;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		
	}
	
	private HttpServletRequest request;

	private MasterDuplicateManager masterDuplicateManager;
	private MasterServerManager masterServerManager;
	private MuseumOpenManager museumOpenManager;
	private CrestronManager crestronManager;

	private final String turnOnRoomAction = "turnOnRoom";
	private final String turnOnAllAction = "turnOnAll";
	private final String turnOffAction = "turnOff";
	private final String turnOffRoomAction = "turnOffRoom";
	private final String turnOffAllAction = "turnOffAll";
	private final String duplicateRoomAction = "duplicateRoom";
	private final String saveAction = "save";
	private final String deleteAction = "delete";
	
	private Map<Long, RoomFormData> roomFormData;

	private String openMuseumTime;
	private String closeMuseumTime;
	private List<Room> roomsWithStations;
	private List<Room> allRoomsWithStations;

	private String roomId;
	private String stationId;
	private String stationIpAddress;
		
	private DateFormat timeFormat;

	@Override
	public void setServletRequest(HttpServletRequest servletRequest) {
		this.request = servletRequest;
	}

	public MasterDuplicateManager getMasterDuplicateManager() {
		return masterDuplicateManager;
	}

	public void setMasterDuplicateManager(
			MasterDuplicateManager masterDuplicateManager) {
		this.masterDuplicateManager = masterDuplicateManager;
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public MuseumOpenManager getMuseumOpenManager() {
		return museumOpenManager;
	}

	public void setMuseumOpenManager(MuseumOpenManager museumOpenManager) {
		this.museumOpenManager = museumOpenManager;
	}

	public String getOpenMuseumTime() {
		return openMuseumTime;
	}

	public void setOpenMuseumTime(String openMuseumTime) {
		this.openMuseumTime = openMuseumTime;
	}

	public String getCloseMuseumTime() {
		return closeMuseumTime;
	}

	public void setCloseMuseumTime(String closeMuseumTime) {
		this.closeMuseumTime = closeMuseumTime;
	}

	public List<Room> getRoomsWithStations() {
		return roomsWithStations;
	}

	
	public void setRoomsWithStations(final List<Room> roomsWithStations) {
		this.roomsWithStations = roomsWithStations;
	}

	
	
	public List<Room> getAllRoomsWithStations() {
		return allRoomsWithStations;
	}

	public void setAllRoomsWithStations(List<Room> allRoomsWithStations) {
		this.allRoomsWithStations = allRoomsWithStations;
	}

	public CrestronManager getCrestronManager() {
		return crestronManager;
	}

	public void setCrestronManager(CrestronManager crestronManager) {
		this.crestronManager = crestronManager;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getStationIpAddress() {
		return stationIpAddress;
	}

	public void setStationIpAddress(String stationIpAddress) {
		this.stationIpAddress = stationIpAddress;
	}

	@SuppressWarnings("unchecked")
	public Iterator<Room> getSubRoomIterator(final Room room) {
		return room.getChildren()!=null && room.getChildren().size() > 0?room.getChildren().iterator():null;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Room> getSubRooms(final Room room) {
		Room r = masterServerManager.getRoom(room.getId());
		Hibernate.initialize(r.getChildren());
		return r.getChildren();
	}
	
	
	@SuppressWarnings("unchecked")
	public Iterator<Station> getStationIterator(final Room room) {
		Station[] stations = new Station[room.getStations().size()];
		int i = 0;
		for (Station station : room.getStations()) {
			stations[i] = station;
			i++;
		}

		Arrays.sort(stations, new StationNameComparator());

		return new ArrayIterator(stations);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Iterator<Device> getDeviceIterator(final Room room) {
		List<Device> devs = crestronManager.getRooms2Devices().get(""+room.getId());
		return devs.iterator();
		}
	
	

	@Override
	public String execute() throws Exception {
		prepare();

		roomsWithStations = masterServerManager.getToplevelRoomsWithStations();
		allRoomsWithStations = masterServerManager.getRoomsWithStations();
		crestronManager.getChambers();
		
		timeFormat = new SimpleDateFormat(getText("format.time.java"));

		if (action != null) {
			if (turnOnRoomAction.equals(action)) {
				// Turning on all stations in the room.
				try {
					if (roomId != null) {
						Long lRoomId = Long.valueOf(roomId);
						logger.debug("turning  on devices for room "+lRoomId);
						turnOnRoomDevices(lRoomId,request);
						Room roo = masterServerManager.getRoom(lRoomId);
						Set<Room> rooms = masterServerManager.getRoomFull(lRoomId).getChildren();
						
						if(rooms != null)
						for(Room r:rooms) { //max 2 levels of nesting
							logger.debug("turning  on devices for subroom "+r.getId());
							if(turnOnRoomDevices(r.getId(),request)>0) 
								try {
									Thread.sleep(2000); // 2 seconds between requests
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

						}
						else 
							logger.debug("no subrooms for  "+roo);
						
						
						try {
							Thread.sleep(120000); // 2 minutes
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for(int i=0;i<3;i++) // times 3!
						if (wakeOnLanManager.sendWakeUpMessagesToRoom(lRoomId)) {
							message = getText("text.room.turnon.success");
							messageType = "success";
						} else {
							message = getText("text.room.turnon.failure");
							messageType = "error";
						}
					} else {
						message = getText("text.room.turnon.failure");
						messageType = "error";
					}
				} catch (NumberFormatException e) {
					message = getText("text.room.turnon.failure");
					messageType = "error";
				}
			} else if (turnOnAllAction.equals(action)) {
				turn(request.getServerName(),request.getServerPort(),"/mxcc/allOn",30000);
				try {
					Thread.sleep(120000); // 120 seconds = 2 minutes!
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Turning on all stations.
				for(int i=0;i<3;i++) // times 3!
				if (wakeOnLanManager.sendWakeUpMessagesToRooms(roomsWithStations)) {
					message = getText("text.room.turnon.all.success");
					messageType = "success";
				} else {
					message = getText("text.room.turnon.all.failure");
					messageType = "error";
				}
			} else if (turnOffAction.equals(action)) {
				// Turning off a station.
				if (stationIpAddress != null && masterServerManager.turnOff(stationIpAddress)) {
					message = getText("text.station.turnoff.success");
					messageType = "success";	
				} else {
					message = getText("text.station.turnoff.failure");
					messageType = "error";
				}
			} else if (turnOffRoomAction.equals(action)) {
				// Turning off all stations in the room.
				try {
					if (roomId != null) {
						Long lRoomId = Long.valueOf(roomId);
						logger.debug("turning  off devices for room "+lRoomId);
						turnOffRoomDevices(lRoomId,request);
						Room roo = masterServerManager.getRoom(lRoomId);
						Set<Room> rooms = masterServerManager.getRoomFull(lRoomId).getChildren();
				
						for(Room r: rooms) {
							logger.debug("turning  off devices for subroom "+r.getId());
							turnOffRoomDevices(r.getId(),request);
						}
					
						if (masterServerManager.turnOffRoom(lRoomId)) {
							message = getText("text.room.turnoff.success");
							messageType = "success";
						} else {
							message = getText("text.room.turnoff.failure");
							messageType = "error";
						}
					} else {
						message = getText("text.room.turnoff.failure");
						messageType = "error";
					}
				} catch (NumberFormatException e) {
					message = getText("text.room.turnoff.failure");
					messageType = "error";
				}
			} else if (turnOffAllAction.equals(action)) {
				logger.debug(request.getServerName()+":"+request.getServerPort()+"/mxcc/allOff");
				turn(request.getServerName(),request.getServerPort(),"/mxcc/allOff",200);
				// Turning off all stations.
				if (masterServerManager.turnOffAllRooms(allRoomsWithStations)) {
					message = getText("text.room.turnoff.all.success");
					messageType = "success";
				} else {
					message = getText("text.room.turnoff.all.failure");
					messageType = "error";
				}
			} else if (duplicateRoomAction.equals(action)) {
				// Duplicate data in the room.
				try {
					if (stationId != null) {
						Long lStationId = Long.valueOf(stationId);
						
						String presentationId = masterDuplicateManager.downloadPresentation(
								lStationId, new Date()); 
						if (presentationId != null) {
							masterDuplicateManager.sendDownloadRequestsToRoom(lStationId,
									presentationId);
							
							message = getText("text.rooms.duplicateRoom.success");
							messageType = "success";
						} else {
							message = getText("text.rooms.duplicateRoom.failure");
							messageType = "error";
						}
					} else {
						message = getText("text.rooms.duplicateRoom.failure");
						messageType = "error";
					}
				} catch (NumberFormatException e) {
					message = getText("text.rooms.duplicateRoom.failure");
					messageType = "error";
				}
			}  else if (deleteAction.equals(action)) {
				if (getStationmac() != null && masterServerManager.deleteStation(getStationmac())) {
					message = getText("text.station.turnoff.success");
					messageType = "success";	
				} else {
					message = getText("text.station.turnoff.failure");
					messageType = "error";
				}
			}	else if (saveAction.equals(action)) {
				Calendar cOpenMuseumTime = null;
				Calendar cCloseMuseumTime = null;
				
				readRoomDataParameters();
				
				try {
					Map<Long, TimeInterval> roomIntervals = convertRoomData();
					
					if (roomIntervals != null) {
						if (!StringUtils.isEmpty(openMuseumTime)) {
							cOpenMuseumTime = Calendar.getInstance();
							cOpenMuseumTime.setTime(timeFormat.parse(openMuseumTime));
						}
						if (!StringUtils.isEmpty(closeMuseumTime)) {
							cCloseMuseumTime = Calendar.getInstance();
							cCloseMuseumTime.setTime(timeFormat.parse(closeMuseumTime));
						}

						masterServerManager.setOpenMuseumTime(cOpenMuseumTime);
						masterServerManager.setCloseMuseumTime(cCloseMuseumTime);
						masterServerManager.updateRoomOpenTimes(roomIntervals);

						museumOpenManager.update();

						message = getText("text.rooms.save.success");
						messageType = "success";
					} else {
						message = getText("text.rooms.save.error.format");
						messageType = "error";
					}
				} catch (ParseException e) {
					message = getText("text.rooms.save.error.museum");
					messageType = "error";
				}
				
			}
		}
		
		readMuseumTime();

		return SUCCESS;
	}

	private void turnOffRoomDevices(long roomIdentifier,HttpServletRequest request) throws InterruptedException {
		List<Device> devs = crestronManager.getRooms2Devices().get(""+roomIdentifier);
		if(devs !=null)
			for(Device dev:devs) {
				logger.debug(request.getServerName()+":"+request.getServerPort()+"/mxcc/off/"+dev.getLocation());
				Thread.sleep(300);
				turn(request.getServerName(),request.getServerPort(),"/mxcc/off/"+dev.getLocation(),2000);
			}
		else 
			logger.debug("no devices for room "+roomIdentifier);
	}

	private int turnOnRoomDevices(long roomIdentifier,HttpServletRequest request) throws InterruptedException {
		List<Device> devs = crestronManager.getRooms2Devices().get(""+roomIdentifier);
		int devNo=0;
		if(devs !=null)
			for(Device dev:devs) {
				logger.debug(request.getServerName()+":"+request.getServerPort()+"/mxcc/on/"+dev.getLocation());
				Thread.sleep(300);
				turn(request.getServerName(),request.getServerPort(),"/mxcc/on/"+dev.getLocation(),2000);
				devNo ++;
			}
		else 
			logger.debug("no devices for room "+roomIdentifier);
		return devNo;
		
	}
	
	private boolean turn(String url,Integer port, String path,Integer timeout) {
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
	
	
	public String formatTime(Date date) {
		if (date != null) {
			return timeFormat.format(date);
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public Date parseDate(String value) throws ParseException {
		if (!StringUtils.isEmpty(value)) {
			return timeFormat.parse(value);
		} else {
			return null;
		}
	}
	
	public String getRoomOpenTime(Room room) {
		if (roomFormData != null) {
			RoomFormData entry = roomFormData.get(room.getId());
			if (entry != null) {
				return entry.getOpenTime();
			}
		}
		
		return formatTime(room.getOpenTime());
	}
	
	public String getRoomCloseTime(Room room) {
		if (roomFormData != null) {
			RoomFormData entry = roomFormData.get(room.getId());
			if (entry != null) {
				return entry.getCloseTime();
			}
		}
		
		return formatTime(room.getCloseTime());
	}
	
	public String escapeId(Long value) {
		return new String(Hex.encodeHex(String.valueOf(value).getBytes()));
	}
	
	public String getErrorMessage(Long room) {
		if (roomFormData != null) {
			RoomFormData entry = roomFormData.get(room);
			
			if (entry != null) {
				return entry.getErrorMessage();
			}
		}
		
		return StringUtils.EMPTY;
	}
	
	private void readMuseumTime() {
		Calendar cOpenMuseumTime = masterServerManager.getOpenMuseumTime();
		if (cOpenMuseumTime != null) {
			openMuseumTime = timeFormat.format(cOpenMuseumTime.getTime());
		} else {
			openMuseumTime = StringUtils.EMPTY;
		}
		
		Calendar cCloseMuseumTime = masterServerManager.getCloseMuseumTime();
		if (cCloseMuseumTime != null) {
			closeMuseumTime = timeFormat.format(cCloseMuseumTime.getTime());
		} else {
			closeMuseumTime = StringUtils.EMPTY;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readRoomDataParameters() throws ParseException {
		final String roomOpeningPrefix = "ro_";
		final String roomClosingPrefix = "rc_";
		
		roomFormData = new HashMap<Long, RoomFormData>();
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			final String key = entry.getKey();
			
			if (key.startsWith(roomOpeningPrefix)) {
				Long roomIdentifier = extractRoomId(roomOpeningPrefix.length(), key);
				if (roomIdentifier != null) {
					RoomFormData roomDataEntry = getOrCreateRoomEntry(roomIdentifier, roomFormData);
					roomDataEntry.setOpenTime(entry.getValue()[0]);
				}
			} else if (key.startsWith(roomClosingPrefix)) {
				Long roomIdentifier = extractRoomId(roomClosingPrefix.length(), key);
				if (roomIdentifier != null) {
					RoomFormData roomDataEntry = getOrCreateRoomEntry(roomIdentifier, roomFormData);
					roomDataEntry.setCloseTime(entry.getValue()[0]);
				}
			}
		}
	}
	
	private Map<Long, TimeInterval> convertRoomData() {
		Map<Long, TimeInterval> result = new HashMap<Long, TimeInterval>(roomFormData.size());
		
		boolean error = false;
		
		for (Map.Entry<Long, RoomFormData> entry : roomFormData.entrySet()) {
			Long roomIdentifier = entry.getKey();
			RoomFormData roomEntry = entry.getValue();
			roomEntry.setErrorMessage(null);
			
			try {
				TimeInterval interval = new TimeInterval();
				interval.setStart(parseDate(roomEntry.getOpenTime()));
				interval.setEnd(parseDate(roomEntry.getCloseTime()));
				
				result.put(roomIdentifier, interval);
			} catch (ParseException e) {
				error = true;
				roomEntry.setErrorMessage(getText("text.rooms.save.error.format.detail"));
			}
		}
		
		if (!error) {
			return result;
		} else {
			return null;
		}
	}
	
	private Long extractRoomId(int begin, String key) {
		if (key.length() <= begin) {
			return null;
		}
		String sVal = key.substring(begin);
		return unescapeId(sVal);
	}
	
	private RoomFormData getOrCreateRoomEntry(Long key, Map<Long, RoomFormData> map) {
		RoomFormData result = map.get(key);
		if (result == null) {
			result = new RoomFormData();
			map.put(key, result);
		}
		
		return result;
	}
	
	private Long unescapeId(String value) {
		try {
			return Long.valueOf(new String(Hex.decodeHex(value.toCharArray())));
		} catch (NumberFormatException e) {
			return null;
		} catch (DecoderException e) {
			return null;
		}
	}
}
