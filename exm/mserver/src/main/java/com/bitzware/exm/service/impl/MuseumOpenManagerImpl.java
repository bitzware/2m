package com.bitzware.exm.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import com.bitzware.exm.exception.JobScheduleException;
import com.bitzware.exm.service.CrestronManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.MuseumOpenManager;
import com.bitzware.exm.service.WakeOnLanManager;
import com.bitzware.exm.service.job.CloseMuseumJob;
import com.bitzware.exm.service.job.OpenMuseumJob;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.visitordb.model.Room;


public class MuseumOpenManagerImpl implements MuseumOpenManager {
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private final String openGroupName = "openMuseum";
	private final String closeGroupName = "closeMuseum";
	
	private final String allName = "all";
	
	private MasterServerManager masterServerManager;
	private WakeOnLanManager wakeOnLanManager;
	private CrestronManager crestronManager;
	private SchedulerFactory schedulerFactory;
	
	private Scheduler scheduler;

	
	
	
	public CrestronManager getCrestronManager() {
		return crestronManager;
	}

	public void setCrestronManager(CrestronManager crestronManager) {
		this.crestronManager = crestronManager;
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public WakeOnLanManager getWakeOnLanManager() {
		return wakeOnLanManager;
	}

	public void setWakeOnLanManager(WakeOnLanManager wakeOnLanManager) {
		this.wakeOnLanManager = wakeOnLanManager;
	}

	public SchedulerFactory getSchedulerFactory() {
		return schedulerFactory;
	}

	public void setSchedulerFactory(SchedulerFactory schedulerFactory) {
		this.schedulerFactory = schedulerFactory;
	}

	public void init() throws SchedulerException {
		logger.debug("init: Before scheduling ");
		scheduler = schedulerFactory.getScheduler();
		logSchedule();
		
		scheduleJobs();
		
		scheduler.start();
		logger.debug("init: after scheduling ");
		logSchedule();
	}
	
	public void destroy() throws SchedulerException {
		scheduler.shutdown();
	}
	
	public void logSchedule() throws SchedulerException {
		
	    //loop all groups
	    for (String groupName : scheduler.getJobGroupNames()) {
	 
		//loop all jobs by groupname
		for (String jobName : scheduler.getJobNames(groupName)) {
	 
	          //get the job's triggers
		  Trigger[] triggers = scheduler.getTriggersOfJob(jobName,groupName);
		  for(Trigger t:triggers ) {
		  Date nextFireTime = t.getNextFireTime();	 		  
		  logger.debug( "[Trigger name] : "+t.getName()+"[JobName] : " + t.getJobName() + " [GroupName] : " + t.getJobGroup() + " - " + nextFireTime);
		  
		  }
		}
	    }
	}
	
	public void update() {
		try {
			synchronized (scheduler) {
				logger.debug("update: Before scheduling ");
				logSchedule();
				
				unscheduleJobs();
				scheduleJobs();
				
				logger.debug("update: After scheduling ");
				logSchedule();
			}
		} catch (SchedulerException e) {
			throw new JobScheduleException("Cannot update open / close jobs.", e);
		}
	}
	
	private void scheduleJobs() throws SchedulerException {
		Calendar openMuseumTime = masterServerManager.getOpenMuseumTime();
		Calendar closeMuseumTime = masterServerManager.getCloseMuseumTime();
		
		scheduleOpenTrigger(null, openMuseumTime);
		scheduleCloseTrigger(null, closeMuseumTime);
		
		List<Room> rooms = masterServerManager.getRooms();
		for (Room room : rooms) {
			if (room.getOpenTime() != null) {
				scheduleOpenTrigger(room.getId(), ExmUtil.toCalendar(room.getOpenTime()));
			}
			if (room.getCloseTime() != null) {
				scheduleCloseTrigger(room.getId(), ExmUtil.toCalendar(room.getCloseTime()));
			}
		}
		
		logSchedule();
	}
	
	private void scheduleOpenTrigger(Long roomId, Calendar time) throws SchedulerException {
		if (time == null) {
			return;
		}
		
		String name;
		if (roomId != null) {
			name = String.valueOf(roomId);
		} else {
			name = allName;
		}
		
		JobDetail jobDetail = new JobDetail(name, openGroupName, OpenMuseumJob.class);
		jobDetail.getJobDataMap().put(
				OpenMuseumJob.MASTER_SERVER_MANAGER_KEY, masterServerManager);
		jobDetail.getJobDataMap().put(
				OpenMuseumJob.WAKE_ON_LAN_MANAGER_KEY, wakeOnLanManager);
		jobDetail.getJobDataMap().put(
				OpenMuseumJob.CRESTRON_MANAGER_KEY, crestronManager);
		
		if (roomId != null) {
			jobDetail.getJobDataMap().put(OpenMuseumJob.ROOM_ID_KEY, roomId);
		}
		
		Trigger trigger = TriggerUtils.makeDailyTrigger(
				time.get(Calendar.HOUR_OF_DAY),
				time.get(Calendar.MINUTE));
		trigger.setGroup(openGroupName);
		trigger.setName(name + "Trigger"+new String(""+Math.random()).substring(0,6));
		
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	private void scheduleCloseTrigger(Long roomId, Calendar time) throws SchedulerException {
		if (time == null) {
			return;
		}
		
		String name;
		if (roomId != null) {
			name = String.valueOf(roomId);
		} else {
			name = allName;
		}
		
		JobDetail jobDetail = new JobDetail(name, closeGroupName, CloseMuseumJob.class);
		jobDetail.getJobDataMap().put(
				CloseMuseumJob.MASTER_SERVER_MANAGER_KEY, masterServerManager);
		jobDetail.getJobDataMap().put(
				CloseMuseumJob.CRESTRON_MANAGER_KEY, crestronManager);
		if (roomId != null) {
			jobDetail.getJobDataMap().put(OpenMuseumJob.ROOM_ID_KEY, roomId);
		}
		
		Trigger trigger = TriggerUtils.makeDailyTrigger(
				time.get(Calendar.HOUR_OF_DAY),
				time.get(Calendar.MINUTE));
		trigger.setGroup(closeGroupName);
		trigger.setName(name + "Trigger"+new String(""+Math.random()).substring(0,6));
		
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	private void unscheduleJobs() throws SchedulerException {
		unscheduleJobs(openGroupName);
		unscheduleJobs(closeGroupName);
	}
	
	private void unscheduleJobs(String groupName) throws SchedulerException {
		String[] triggers = scheduler.getTriggerNames(groupName);
		for (String triggerName : triggers) {
			scheduler.unscheduleJob(triggerName, groupName);
		}
		logSchedule();
	}

}
