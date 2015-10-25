package com.bitzware.exm.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.model.ServerProperties;
import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.Room;


@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
		parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "MasterServerPortType", serviceName = "MasterServer",
		portName = "MasterServerPort", targetNamespace = "http://mserver.bitzware.com/")
public interface IMasterServer {

	@WebMethod(operationName = "sendStationData")
	@WebResult(name = "serverProperties")
	ServerProperties sendStationData(
			@WebParam(name = "stationProperties") StationProperties stationData);
	
	@WebMethod(operationName = "heartbeat")
	@WebResult(name = "serverProperties")
	ServerProperties heartbeat(
			@WebParam(name = "mac") String mac,
			@WebParam(name = "status") ActiveStationStatus status);
	
	@WebMethod(operationName = "getRooms")
	@WebResult(name = "rooms")
	List<Room> getRooms();
	
	@WebMethod(operationName = "registerRfidEvent")
	@WebResult(name = "rfidEventInfo")
	RfidEventInfo registerRfidEvent(
			@WebParam(name = "rfid") String rfid,
			@WebParam(name = "stationMac") String stationMac,
			@WebParam(name = "deviceIndex") final Integer deviceIndex);
	
	@WebMethod(operationName = "registerPirEvent")
	void registerPirEvent(
			@WebParam(name = "stationMac") String stationMac);
	
	@WebMethod(operationName = "registerButtonEvent")
	void registerButtonEvent(
			@WebParam(name = "stationMac") String stationMac,
			@WebParam(name = "index") Integer index,
			@WebParam(name = "down") Boolean down);
	
	@WebMethod(operationName = "registerScoreEvent")
	void registerScoreEvent(
			@WebParam(name = "event") Event event,
			@WebParam(name = "stationMac") String stationMac);
	
	@WebMethod(operationName = "registerDirectorEvent")
	void registerDirectorEvent(
			@WebParam(name = "event") Event event,
			@WebParam(name = "stationMac") String stationMac);
	
	@WebMethod(operationName = "registerClientDisconnectedEvent")
	void registerClientDisconnectedEvent(
			@WebParam(name = "event") Event event,
			@WebParam(name = "stationMac") String stationMac);
	
	@WebMethod(operationName = "registerFloorEvent")
	void registerFloorEvent(
			@WebParam(name = "stationMac") String stationMac,
			@WebParam(name = "index") Integer index,
			@WebParam(name = "down") Boolean down);
	
	@WebMethod(operationName = "synchronize")
	void synchronize(
			@WebParam(name = "synchronize") List<Event> events,
			@WebParam(name = "stationMac") String stationMac);
	
}
