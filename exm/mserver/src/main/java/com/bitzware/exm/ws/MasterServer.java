package com.bitzware.exm.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;

import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.model.ServerProperties;
import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.service.MasterEventRegisterManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.MasterSynchronizationManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.Room;


/**
 * Master server web service. Used for communication with stations.
 * 
 * @author finagle
 */
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
		parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "MasterServerPortType", serviceName = "MasterServer",
		portName = "MasterServerPort", targetNamespace = "http://mserver.bitzware.com/")
public class MasterServer implements IMasterServer {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private MasterEventRegisterManager masterEventRegisterManager =
		ServiceFactory.getMasterEventRegisterManager();
	private MasterServerManager masterServerManager = ServiceFactory.getMasterServerManager();
	private MasterSynchronizationManager masterSynchronizationManager =
		ServiceFactory.getMasterSynchronizationManager();
	
	/**
	 * Saves the station data. This method should be called on registration and
	 * every time the station changes it's data (name, description etc.).
	 */
	@WebMethod(operationName = "sendStationData")
	@WebResult(name = "serverProperties")
	public ServerProperties sendStationData(
			@WebParam(name = "stationProperties") final StationProperties stationData) {
		logger.debug("sendStationData");
		
		masterServerManager.updateStation(stationData);
		return masterServerManager.getServerProperties();
	}
	
	/**
	 * Heartbeat method.
	 */
	@WebMethod(operationName = "heartbeat")
	@WebResult(name = "serverProperties")
	public ServerProperties heartbeat(
			@WebParam(name = "mac") final String mac,
			@WebParam(name = "status") final ActiveStationStatus status) {
//		logger.debug("heartbeat");
		
		if (masterServerManager.heartbeat(mac, status)) {
			return masterServerManager.getServerProperties();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a list of rooms.
	 */
	@WebMethod(operationName = "getRooms")
	@WebResult(name = "rooms")
	public List<Room> getRooms() {
		logger.debug("getRooms");
		
		return masterServerManager.getRooms();
	}
	
	/**
	 * Registers a RFID event.
	 */
	@WebMethod(operationName = "registerRfidEvent")
	@WebResult(name = "rfidEventInfo")
	public RfidEventInfo registerRfidEvent(
			@WebParam(name = "rfid") final String rfid,
			@WebParam(name = "stationMac") final String stationMac,
			@WebParam(name = "deviceIndex") final Integer deviceIndex) {
		return masterEventRegisterManager.registerRfidEvent(rfid, stationMac,
				deviceIndex);
	}
	
	/**
	 * Registers a Button event.
	 */
	@WebMethod(operationName = "registerButtonEvent")
	public void registerButtonEvent(
			@WebParam(name = "stationMac") final String stationMac,
			@WebParam(name = "index") final Integer index,
			@WebParam(name = "down") final Boolean down) {
		masterEventRegisterManager.registerButtonEvent(stationMac, index, down);
	}

	/**
	 * Registers a PIR event.
	 */
	@WebMethod(operationName = "registerPirEvent")
	public void registerPirEvent(
			@WebParam(name = "stationMac") final String stationMac) {
		masterEventRegisterManager.registerPirEvent(stationMac);
	}
	
	/**
	 * Registers a Director event.
	 */
	@WebMethod(operationName = "registerScoreEvent")
	public void registerScoreEvent(
			@WebParam(name = "event") final Event event,
			@WebParam(name = "stationMac") final String stationMac) {
		masterEventRegisterManager.registerScoreEvent(event, stationMac);
	}
	
	/**
	 * Registers a Director event.
	 */
	@WebMethod(operationName = "registerDirectorEvent")
	public void registerDirectorEvent(
			@WebParam(name = "event") final Event event,
			@WebParam(name = "stationMac") final String stationMac) {
		masterEventRegisterManager.registerDirectorEvent(event, stationMac);
	}
	
	/**
	 * Registers a 'client disconnected' event.
	 */
	@WebMethod(operationName = "registerClientDisconnectedEvent")
	public void registerClientDisconnectedEvent(
			@WebParam(name = "event") Event event,
			@WebParam(name = "stationMac") String stationMac) {
		masterEventRegisterManager.registerClientDisconnectedEvent(event, stationMac);
	}
	
	/**
	 * Registers a 'floor' event.
	 */
	@WebMethod(operationName = "registerFloorEvent")
	public void registerFloorEvent(
			@WebParam(name = "stationMac") String stationMac,
			@WebParam(name = "index") Integer index,
			@WebParam(name = "down") Boolean down) {
		masterEventRegisterManager.registerFloorEvent(stationMac, index, down);
	}
	
	/**
	 * Perform events synchronization.
	 */
	@WebMethod(operationName = "synchronize")
	public void synchronize(
			@WebParam(name = "synchronize") final List<Event> events,
			@WebParam(name = "stationMac") final String stationMac) {
		masterSynchronizationManager.saveData(events, stationMac);
	}
}
