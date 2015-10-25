package com.bitzware.exm.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bitzware.exm.service.ServiceFactory;


/**
 * Listener, used to assign the Spring web application context to the application
 * bean factory. This factory is used by the web services to retrieve beans from
 * the Spring context.
 * 
 * @author finagle
 */
public class ServiceFactoryListener implements ServletContextListener {

	
	public void contextDestroyed(final ServletContextEvent e) {
		ServiceFactory.setBeanFactory(null);
	}

	
	public void contextInitialized(final ServletContextEvent e) {
		ServiceFactory.setBeanFactory(
				WebApplicationContextUtils.getRequiredWebApplicationContext(
						e.getServletContext()));
	}

}
