package com.bitzware.exm.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;

import com.bitzware.exm.service.EventRegisterManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.visitordb.model.Event;


@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
		parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "EventDispatcherPortType", serviceName = "EventDispatcher",
		portName = "EventDispatcherPort")
public class EventDispatcher {

	private EventRegisterManager eventRegisterManager = ServiceFactory.getEventRegisterManager();
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@WebMethod(operationName = "dispatchEvent")
	public void dispatchEvent(
			@WebParam(name = "event") final Event event) {
		logger.debug("dispatchEvent");
		
		eventRegisterManager.registerDirectorEvent(event);
	}
	
}
