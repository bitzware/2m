package com.bitzware.exm.action.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.visitordb.model.Room;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Struts action for the 'Rooms configuration' panel.
 * 
 * @author finagle
 */
public class RoomsConfigAction extends ActionSupport implements
		ServletRequestAware {

	private static final long serialVersionUID = 1L;

	protected final Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Room properties.
	 * 
	 * @author finagle
	 */
	public static class RoomFormData {
		private String id;

		private String name;
		private String description;
		private String floor;
		private String parent;

		public String getId() {
			return id;
		}

		public void setId(final String id) {
			this.id = id;
		}

		
		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

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

		public String getFloor() {
			return floor;
		}

		public void setFloor(final String floor) {
			this.floor = floor;
		}

	}

	private HttpServletRequest servletRequest;

	private MasterServerManager masterServerManager;

	/**
	 * This data will be displayed on the page.
	 */
	private List<RoomFormData> roomsFormData;

	private String action;

	private Long assignMe;
	private Long toRoom;

	private final String saveAction = "save";
	private final String assignAction = "assignRooms";

	private String message;
	private String messageType;

	public Long getAssignMe() {
		return assignMe;
	}

	public void setAssignMe(Long assignMe) {
		this.assignMe = assignMe;
	}

	public Long getToRoom() {
		return toRoom;
	}

	public void setToRoom(Long toRoom) {
		this.toRoom = toRoom;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setServletRequest(final HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(
			final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public List<RoomFormData> getRoomsFormData() {
		return roomsFormData;
	}

	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(final String messageType) {
		this.messageType = messageType;
	}

	@Override
	public String execute() throws Exception {
		if (action == null) {
			// Read rooms and stations from the database.
			List<Room> rooms = masterServerManager.getRooms();

			// Convert it to the format displayed on the page.
			createFormData(rooms);

		} else if (assignAction.equals(action)) {
			// Read rooms and stations from the database.
			
			if ((assignMe == null || toRoom == null) || (assignMe.equals(toRoom))) {
				message = getText("Przypisanie niepoprawne");
				messageType = "erron";
			} else {
				if (masterServerManager.assignRoom2Room(assignMe.longValue(),
						toRoom.longValue())) {
					message = getText("text.roomsconfig.success");
					messageType = "success";
				}else {
					message = getText("Przypisanie niepoprawne");
					messageType = "erron";
				}
			}
			List<Room> rooms = masterServerManager.getRooms();

			// Convert it to the format displayed on the page.
			createFormData(rooms);
		} else if (saveAction.equals(action)) {
			// Saving data entered by the user.

			// Read request parameters to the 'roomsFormData' list.
			readRequestParams();

			// Convert the 'roomsFormData' list to the model data.
			List<Room> rooms = convertRequestParams();

			// Save the data.
			masterServerManager.saveRoomsConfig(rooms);

			// Convert the saved data to the 'roomsFormData' list, which will be
			// displayed on the page (it is needed, because database may assign
			// new identifiers to the created rooms).
			createFormData(rooms);

			message = getText("text.roomsconfig.success");
			messageType = "success";
		}

		return SUCCESS;
	}

	/**
	 * Convert a model list of rooms to the 'roomsFormData' list. The former
	 * list is used by the database, tha latter is used to display data on the
	 * page.
	 */
	private void createFormData(final List<Room> rooms) {
		roomsFormData = new ArrayList<RoomFormData>(rooms.size());
		for (Room room : rooms) {
			RoomFormData roomInfo = new RoomFormData();

			if (room.getId() != null) {
				roomInfo.id = String.valueOf(room.getId());
			}
			roomInfo.name = room.getName();
			roomInfo.description = room.getDescription();
			roomInfo.floor = room.getFloor();
			roomInfo.parent = room.getParent()!=null?room.getParent().getName():null;

			roomsFormData.add(roomInfo);
		}
	}

	/**
	 * Reads request parameters.
	 */
	@SuppressWarnings("unchecked")
	private void readRequestParams() {
		Map<String, String[]> paramMap = servletRequest.getParameterMap();

		Map<Integer, RoomFormData> roomsSavedData = new HashMap<Integer, RoomFormData>();

		for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
			final String paramName = param.getKey();
			final String paramValue = param.getValue()[0];

			// Check parameter type. Parameter names are in the following
			// format:
			// i_{id} - room's id (empty if this room was added)
			// n_{id} - room's name
			// d_{id} - room's description
			// f_{id} - room's floor
			// {id} is the room index used in the form.
			// If the room's id is null it means that the room was added by the
			// user.
			if (paramName.startsWith("i_")) {
				// Room's id
				Integer index = Integer.valueOf(paramName.substring(2));
				getSavedRoomsConfigEntry(roomsSavedData, index).id = paramValue;
			} else if (paramName.startsWith("n_")) {
				// Room's name
				Integer index = Integer.valueOf(paramName.substring(2));
				getSavedRoomsConfigEntry(roomsSavedData, index).name = paramValue;
			} else if (paramName.startsWith("d_")) {
				// Room's description
				Integer index = Integer.valueOf(paramName.substring(2));
				getSavedRoomsConfigEntry(roomsSavedData, index).description = paramValue;
			} else if (paramName.startsWith("f_")) {
				// Room's floor
				Integer index = Integer.valueOf(paramName.substring(2));
				getSavedRoomsConfigEntry(roomsSavedData, index).floor = paramValue;
			}
		}

		roomsFormData = new ArrayList<RoomFormData>(roomsSavedData.size());

		for (RoomFormData roomFormInfo : roomsSavedData.values()) {
			roomsFormData.add(roomFormInfo);
		}
	}

	/**
	 * Converts the 'roomsFormData' list to the model list used by the database.
	 */
	private List<Room> convertRequestParams() {
		List<Room> result = new ArrayList<Room>(roomsFormData.size());

		for (RoomFormData roomData : roomsFormData) {
			Room room = new Room();

			if (roomData.id != null) {
				try {
					Long roomId = Long.valueOf(roomData.id);
					room.setId(roomId);
				} catch (NumberFormatException e) {
					logger.warn("Invalid room id: " + roomData.id);
				}
			}
			room.setName(roomData.name);
			room.setDescription(roomData.description);
			room.setFloor(roomData.floor);

			result.add(room);
		}

		return result;
	}

	/**
	 * Gets (or creates if non-existent) room information for the specified room
	 * index from the 'savedRoomsConfig' map.
	 */
	private RoomFormData getSavedRoomsConfigEntry(
			final Map<Integer, RoomFormData> roomsSavedData, final Integer index) {
		RoomFormData roomFormInfo = roomsSavedData.get(index);
		if (roomFormInfo == null) {
			roomFormInfo = new RoomFormData();
			roomsSavedData.put(index, roomFormInfo);
		}

		return roomFormInfo;
	}
}
