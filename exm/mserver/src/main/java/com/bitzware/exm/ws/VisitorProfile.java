package com.bitzware.exm.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.VisitorManager;
import com.bitzware.exm.visitordb.model.Visitor;


@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
		parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "VisitorProfilePortType", serviceName = "VisitorProfile",
		portName = "VisitorProfilePort")
public class VisitorProfile {

	private final VisitorManager visitorManager = ServiceFactory.getVisitorManager(); 
	
	@WebMethod(operationName = "updateVisitorProfile")
	@WebResult(name = "result")
	public Boolean updateVisitorProfile(
			@WebParam(name = "visitor") final Visitor visitor) {
		
		return visitorManager.updateProfile(visitor);
	}
	
}
