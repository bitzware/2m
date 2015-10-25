package com.bitzware.exm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import com.bitzware.exm.dao.TestDao;

import com.bitzware.exm.service.report.impl.DailyVisitsReportGenerator;
import com.bitzware.exm.service.report.impl.EventsInRoomReportGenerator;
import com.bitzware.exm.service.report.impl.EventsOnFloorReportGenerator;
import com.bitzware.exm.service.report.impl.EventsOnStationReportGenerator;
import com.bitzware.exm.service.report.impl.PopularStationsReportGenerator;
import com.bitzware.exm.service.report.impl.VisitorsRoutesReportGenerator;


/**
 * Utility class that retrieves beans from the Spring context.
 * 
 * @author finagle
 */
public final class ServiceFactory {
	
	private static Logger logger = Logger.getLogger(ServiceFactory.class);

	private static BeanFactory beanFactory;
	
	private ServiceFactory() {
	}
	
	public static void setBeanFactory(final BeanFactory beanFactory) {
		ServiceFactory.beanFactory = beanFactory;
	}
	
//	public static ActionManager getActionManager() {
//		return (ActionManager) beanFactory.getBean("actionManager");
//	}
//	
//	public static ConfigManager getConfigManager() {
//		return (ConfigManager) beanFactory.getBean("configManager");
//	}
//	
//	public static DeviceManager getDeviceManager(boolean required) {
//		try {
//			return (DeviceManager) beanFactory.getBean("deviceManager");
//		} catch (BeansException e) {
//			if (required) {
//				throw e;
//			} else {
//				return null;
//			}
//		}
//	}
	
	public static EventRegisterManager getEventRegisterManager() {
		return (EventRegisterManager) beanFactory.getBean("eventRegisterManager");
	}
	
	public static EventManager getEventManager() {
		return (EventManager) beanFactory.getBean("eventManager");
	}
	
	public static MasterEventRegisterManager getMasterEventRegisterManager() {
		return (MasterEventRegisterManager) beanFactory.getBean("masterEventRegisterManager");
	}
	
	public static MasterSynchronizationManager getMasterSynchronizationManager() {
		return (MasterSynchronizationManager) beanFactory.getBean("masterSynchronizationManager");
	}
	
//	public static ProcessManager getProcessManager() {
//		return (ProcessManager) beanFactory.getBean("processManager");
//	}
//	
	public static MasterServerManager getMasterServerManager() {
		return (MasterServerManager) beanFactory.getBean("masterServerManager");
	}
	
//	public static PlayerManager getPlayerManager() {
//		return (PlayerManager) beanFactory.getBean("playerManager");
//	}
//	
//	public static PresentationManager getPresentationManager() {
//		return (PresentationManager) beanFactory.getBean("presentationManager");
//	}
//	
//	public static PresentationFilesManager getPresentationFilesManager() {
//		return (PresentationFilesManager) beanFactory.getBean("presentationFilesManager");
//	}
	
	public static VisitorManager getVisitorManager() {
		return (VisitorManager) beanFactory.getBean("visitorManager");
	}
	
	public static TestManager getTestManager() {
		return (TestManager) beanFactory.getBean("testManager");
	}
	
	public static EventsOnStationReportGenerator getEventsOnStationReportGenerator() {
		return (EventsOnStationReportGenerator) beanFactory.getBean(
				"eventsOnStationReportGenerator");
	}
	
	public static EventsInRoomReportGenerator getEventsInRoomReportGenerator() {
		return (EventsInRoomReportGenerator) beanFactory.getBean("eventsInRoomReportGenerator");
	}
	
	public static EventsOnFloorReportGenerator getEventsOnFloorReportGenerator() {
		return (EventsOnFloorReportGenerator) beanFactory.getBean("eventsOnFloorReportGenerator");
	}
	
	public static VisitorsRoutesReportGenerator getVisitorsRoutesReportGenerator() {
		return (VisitorsRoutesReportGenerator) beanFactory.getBean("visitorsRoutesReportGenerator");
	}
	
	public static PopularStationsReportGenerator getPopularStationsReportGenerator() {
		return (PopularStationsReportGenerator) beanFactory.getBean(
				"popularStationsReportGenerator");
	}
	
	public static DailyVisitsReportGenerator getDailyVisitsReportGenerator() {
		return (DailyVisitsReportGenerator) beanFactory.getBean("dailyVisitsReportGenerator");
	}
	
	public static WakeOnLanManager getWakeOnLanManager() {
		return (WakeOnLanManager) beanFactory.getBean("wakeOnLanManager");
	}
	
	public static TestDao getTestDao() {
		return (TestDao) beanFactory.getBean("testDao");
	}
	
}
