package com.bitzware.exm.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.bitzware.exm.service.MasterEventRegisterManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.TestManager;
import com.bitzware.exm.visitordb.model.Visitor;


@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
		parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "MasterTestPortType", serviceName = "MasterTest", portName = "MasterTestPort")
public class MasterTest {

	private MasterEventRegisterManager masterEventRegisterManager =
		ServiceFactory.getMasterEventRegisterManager();
	private TestManager testManager = ServiceFactory.getTestManager();
	
	@WebMethod(operationName = "setRfidTimestamp")
	public void setRfidTimestamp(
			@WebParam(name = "rfid") final String rfid,
			@WebParam(name = "timestamp") final Date timestamp) {
		
		masterEventRegisterManager.setRfidTimestamp(rfid, timestamp);
	}
	
	@WebMethod(operationName = "resetRfidTimestamp")
	public void resetRfidTimestamp(
			@WebParam(name = "rfid") final String rfid) {
		
		masterEventRegisterManager.setRfidTimestamp(rfid, new Date());
	}
	
	@WebMethod(operationName = "createVisitor")
	public void createVisitor(
			@WebParam(name = "visitor") final Visitor visitor) {
		
		testManager.createVisitor(visitor);
	}
	
}
