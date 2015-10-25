package com.bitzware.exm.service.job;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bitzware.exm.model.Device;
import com.bitzware.exm.service.CrestronManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.visitordb.model.Room;


public class CloseMuseumJob implements Job {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	public static final String MASTER_SERVER_MANAGER_KEY = "masterServerManager";
	public static final String ROOM_ID_KEY = "roomId";
	public static final String CRESTRON_MANAGER_KEY = "crestronManager";


	public  void runShutdownScript() throws Exception {
		String[] commandArray = {
		  //  "/bin/bash",
		  //  "-c",
		    "/home/keeper/crestron/wylacz.sh"
		};
		Process proc = Runtime.getRuntime().exec(commandArray);
		BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		proc.waitFor();

		while (in.ready()) {
		    logger.debug(in.readLine());
		}
		}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing job: " + context.getJobDetail().getName());
		}
		
		JobDataMap dataMap = context.getMergedJobDataMap();
		MasterServerManager masterServerManager =
			(MasterServerManager) dataMap.get(MASTER_SERVER_MANAGER_KEY);
		CrestronManager crestronManager =
				(CrestronManager) dataMap.get(CRESTRON_MANAGER_KEY);
		
		
		
		Long roomId = (Long) dataMap.get(ROOM_ID_KEY);
		
		if (roomId == null) {
			logger.debug("doing the check against the db");
			
		//	if(Math.abs(new Date().getTime() - masterServerManager.getOpenMuseumTime().getTimeInMillis()) > 5*60*1000) return;// 	
			
			logger.debug("localhost:8080/mxcc/allOff");
			masterServerManager.turn("localhost",8080,"/mxcc/allOff",200);
			
			List<Room> rooms = masterServerManager.getRoomsWithStations();
			masterServerManager.turnOffAllRooms(rooms);
		} else {
			try {
				turnOffRoomDevices(roomId,crestronManager,masterServerManager);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e);
			}
			masterServerManager.turnOffRoom(roomId);
		}
		
//		logger.debug("running shutdown script");
//		try {
//			runShutdownScript();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			logger.debug(e);
//		}
//		logger.debug("shutdown script finished");
//		
//		
	}
	
	private int turnOffRoomDevices(long roomIdentifier,CrestronManager crestronManager,MasterServerManager masterServerManager) throws InterruptedException {
		List<Device> devs = crestronManager.getRooms2Devices().get(""+roomIdentifier);
		int devNo = 0;
		if(devs !=null)
			for(Device dev:devs) {
				logger.debug("localhost:"+8080+"/mxcc/off/"+dev.getLocation());
				Thread.sleep(300);
				masterServerManager.turn("localhost",8080,"/mxcc/off/"+dev.getLocation(),1000);
				devNo++;
			}
		else 
			logger.debug("no devices for room "+roomIdentifier);
		return devNo;
	}

	
}
