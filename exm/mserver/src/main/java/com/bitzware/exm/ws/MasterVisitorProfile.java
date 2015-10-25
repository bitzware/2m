package com.bitzware.exm.ws;

import java.text.DateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import com.bitzware.exm.service.MasterEventRegisterManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.VisitorManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.visitordb.model.Visitor;


@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
	parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebService(name = "MasterVisitorProfilePortType", serviceName = "MasterVisitorProfile",
	portName = "MasterVisitorProfilePort")
public class MasterVisitorProfile {


	  @Resource
	  WebServiceContext wsContext; 

	  
	protected final Logger logger = Logger.getLogger(this.getClass());

	private final MasterEventRegisterManager masterEventRegisterManager =
		ServiceFactory.getMasterEventRegisterManager();

	private final VisitorManager visitorManager = ServiceFactory.getVisitorManager(); 

	@WebMethod(operationName = "createVisitorProfile")
	@WebResult(name = "id")
	public Long createVisitorProfile(
			@WebParam(name = "visitor") final Visitor visitor) {
		if (visitor == null) {
			logger.warn("Trying to add a null visitor!");
			return null;
		}
		
		if (logger.isDebugEnabled()) {
			if (visitor != null) {
				final DateFormat format = DateFormat.getDateTimeInstance();

				final StringBuilder debugMessage = new StringBuilder(100);

				debugMessage.append("Creating visitor: ").append(visitor.getRfid())
				.append(", valid from: ").append(format.format(visitor.getRfidValidFrom()));

				if (visitor.getEntryTime() != null) {
					debugMessage.append(", entry time: ").append(format.format(visitor.getEntryTime()));
				}

				logger.debug(debugMessage.toString());
			}
		}

		Long id = visitorManager.createProfile(visitor);

		if (logger.isDebugEnabled()) {
			if (visitor != null && id != null) {
				logger.debug(ExmUtil.concat("Visitor: ", visitor.getRfid(), " created."));
			} else {
				logger.debug("Visitor's RFID already in the database, skipping.");
			}
		}

		return id;
	}

	@WebMethod(operationName = "checkRfid")
	@WebResult(name = "result")
	public String checkRfid(@WebParam(name = "rfid") final String rfid) {
		final String result = masterEventRegisterManager.checkRfid(rfid); 

		if (logger.isDebugEnabled()) {
			final DateFormat format = DateFormat.getDateTimeInstance();

			final StringBuilder debugMessage = new StringBuilder(100);



		    MessageContext mc = wsContext.getMessageContext();
		    HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST); 
//		    System.out.println("Client IP = " + req.getRemoteAddr())
//		    @Context private javax.servlet.http.HttpServletRequest hsr;
		    
			debugMessage.append("Checking RFID: ").append(rfid)
				.append(" at time: ").append(format.format(new Date()))
				.append(", from host: ").append(req.getRemoteAddr())
				.append(", result: ").append(result);

			logger.debug(debugMessage.toString());
		}

		return result;
	}

	@WebMethod(operationName = "expireRfid")
	public void expireRfid(@WebParam(name = "rfid") final String rfid) {
		visitorManager.expireRfid(rfid);
	}

}
