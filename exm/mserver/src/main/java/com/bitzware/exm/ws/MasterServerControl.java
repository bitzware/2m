package com.bitzware.exm.ws;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;

import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.WakeOnLanManager;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;

@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "MasterServerControlPortType", serviceName = "MasterServerControl", portName = "MasterServerControlPort")
public class MasterServerControl {

	@Resource
	WebServiceContext wsContext;

	protected final Logger logger = Logger.getLogger(this.getClass());

	private final MasterServerManager masterServerManager = ServiceFactory
			.getMasterServerManager();
	private final WakeOnLanManager wakeOnLanManager = ServiceFactory
			.getWakeOnLanManager();

	@WebMethod(operationName = "setOpenTime")
	@WebResult(name = "status")
	public Date setOpenTime(Date time) {
		logger.debug("MasterServerControl: setOpenMuseumTime" + time);
		Calendar cal = Calendar.getInstance();
		  cal.setTime(time);
		masterServerManager.setOpenMuseumTime(cal);
		return new Date();
	}
	
	@WebMethod(operationName = "setCloseTime")
	@WebResult(name = "status")
	public Date setCloseTime(Date time) {
		logger.debug("MasterServerControl: setCloseMuseumTime" + time);
		Calendar cal = Calendar.getInstance();
		  cal.setTime(time);
		masterServerManager.setCloseMuseumTime(cal);
		return new Date();
	}
	
	@WebMethod(operationName = "shutdownExhibition")
	@WebResult(name = "status")
	public boolean shutdownExhibition() {
		logger.debug("MasterServerControl: shutting the exhibition down");
		logger.debug("MasterServerControl: localhost:8080/mxcc/allOff");
		masterServerManager.turn("localhost", 8080, "/mxcc/allOff", 200);
		List<Room> rooms = masterServerManager.getRoomsWithStations();
		logger.debug("MasterServerControl: turnOffAllRooms(rooms)");
		masterServerManager.turnOffAllRooms(rooms);
		return true;

	}

	@WebMethod(operationName = "startupExhibition")
	@WebResult(name = "status")
	public boolean startupExhibition() {
		logger.debug("MasterServerControl: starting the exhibition up");
		logger.debug("MasterServerControl: localhost:8080/mxcc/allOn");
		masterServerManager.turn("localhost", 8080, "/mxcc/allOn", 30000);
		List<Station> stations = masterServerManager.getStations();
		for (int i = 0; i < 3; i++) {
			logger.debug("MasterServerControl: sendWakeUpMessage(station.getMacAddress())");
			for (Station station : stations) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wakeOnLanManager.sendWakeUpMessage(station.getMacAddress());
			}
		}
		return true;

	}

}
