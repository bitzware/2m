package com.bitzware.exm.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bitzware.exm.model.Events;
import com.bitzware.exm.service.EventManager;
import com.bitzware.exm.service.EventRegisterManager;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Visitor;


/**
 * A servlet that sends events to the client application.
 * 
 * @author finagle
 */
public class EventServlet extends HttpServlet {

	private static final long serialVersionUID = 0L;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private final int defaultHeartbeat = 1000;
	
	private long heartbeatTime;
	private EventManager eventManager;
	private EventRegisterManager eventRegisterManager;

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		
		heartbeatTime = 0;
		
		String sHeartbeat = config.getInitParameter("heartbeat");
		if (sHeartbeat != null) {
			try {
				heartbeatTime = Long.valueOf(sHeartbeat);
			} catch (NumberFormatException e) {
				logger.error("Incorrect heartbeat value.");
			}
		}
		
		if (heartbeatTime <= 0) {
			logger.warn("Setting heartbeat default value.");
			heartbeatTime = defaultHeartbeat;
		}
		
		ApplicationContext applicationContext =
			WebApplicationContextUtils.getRequiredWebApplicationContext(
					getServletContext());
		eventManager =
			(EventManager) applicationContext.getBean("eventManager");
		eventRegisterManager =
			(EventRegisterManager) applicationContext.getBean("eventRegisterManager");
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	throws ServletException, IOException {
		final int messageLengthSize = 10;

		try {
			logger.debug("Receiving event request.");
			
			// Clear events list.
			eventManager.restart();

//			resp.setContentType("text/xml");
//			resp.setStatus(200);

			ServletOutputStream output = resp.getOutputStream();

			Marshaller marshaller = null;
			
			try {
				JAXBContext jaxbContext =
					JAXBContext.newInstance(Events.class, Event.class, EventType.class,
							Visitor.class);
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			} catch (JAXBException e) {
				logger.error("Error while creating JAXB context.", e);
				return;
			}
			
			while (true) {
				logger.debug("Sending events.");

				try {
					Events events = eventManager.retrieveEvents();
					
					if (events.getEvents().isEmpty()) {
						output.print(StringUtils.rightPad("0", messageLengthSize));
						output.flush();
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("Sending events:");
							
							for (Event event : events.getEvents()) {
								logger.debug(event.getEventType());
							}
						}
						
						ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
						marshaller.marshal(events, byteStream);

						byte[] bytes = byteStream.toByteArray();
						String lengthString = String.valueOf(bytes.length);
						output.print(StringUtils.rightPad(lengthString, messageLengthSize));

						output.write(bytes);

						output.flush();
					}
				} catch (JAXBException e) {
					logger.error("Error while marshalling events message.", e);
				}

				eventManager.waitForEvent(heartbeatTime);
			}
		} finally {
			logger.debug("Closing connection.");
			
			eventRegisterManager.registerClientDisconnectedEvent();
		}

	}

}
