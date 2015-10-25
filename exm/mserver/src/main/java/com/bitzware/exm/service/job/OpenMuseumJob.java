package com.bitzware.exm.service.job;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bitzware.exm.model.Device;
import com.bitzware.exm.service.CrestronManager;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.WakeOnLanManager;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


public class OpenMuseumJob implements Job {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	public static final String MASTER_SERVER_MANAGER_KEY = "masterServerManager";
	public static final String ROOM_ID_KEY = "roomId";
	public static final String WAKE_ON_LAN_MANAGER_KEY = "wakeOnLanManager";
	public static final String CRESTRON_MANAGER_KEY = "crestronManager";
	
	
		public  void runStartupScript() throws Exception {
		String[] commandArray = {
		  //  "/bin/bash",
		  //  "-c",
		    "/home/keeper/crestron/wlacz.sh"
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
		WakeOnLanManager wakeOnLanManager =
			(WakeOnLanManager) dataMap.get(WAKE_ON_LAN_MANAGER_KEY);
		CrestronManager crestronManager =
				(CrestronManager) dataMap.get(CRESTRON_MANAGER_KEY);
		
		
		Long roomId = (Long) dataMap.get(ROOM_ID_KEY);
		//first the crestron devices...
		if(roomId == null) { // turn on all 
			logger.debug("doing the check against the db");
			
			//if(Math.abs(new Date().getTime() - masterServerManager.getOpenMuseumTime().getTimeInMillis()) > 5*60*1000) return;// 	
			
		logger.debug("localhost:8080/mxcc/allOn");
		masterServerManager.turn("localhost",8080,"/mxcc/allOn",30000);
		try {
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		
		}
		else { //only the specific room
			//masterServerManager.getRoomFull(1).getOpenTime()
			try {
				turnOnRoomDevices(roomId,crestronManager,masterServerManager);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Set<Room> rooms = masterServerManager.getRoomFull(roomId).getChildren();
			
			if(rooms != null)
			for(Room r:rooms) { //max 2 levels of nesting
				logger.debug("turning  on devices for subroom "+r.getId());
			
					try {
						turnOnRoomDevices(r.getId(),crestronManager,masterServerManager);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
		}
			 
			
			
		try {
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		}
		
		
		//then the stations...
		if (roomId == null) {
			List<Station> stations = masterServerManager.getStations();
			for(int i=0;i<3;i++)
			for (Station station : stations) {
			
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				wakeOnLanManager.sendWakeUpMessage(station.getMacAddress());
			}
		} else {
			for(int i=0;i<3;i++)
			wakeOnLanManager.sendWakeUpMessagesToRoom(roomId);
		}
		
		
//		logger.debug("running startup script"); 
//		
//		try {
//			runStartupScript();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			logger.debug(e);
//		}
//		logger.debug("startup script finished" );
	}
		private int turnOnRoomDevices(long roomIdentifier,CrestronManager crestronManager,MasterServerManager masterServerManager) throws InterruptedException {
			List<Device> devs = crestronManager.getRooms2Devices().get(""+roomIdentifier);
			int devNo = 0;
			if(devs !=null)
				for(Device dev:devs) {
					logger.debug("localhost:"+8080+"/mxcc/on/"+dev.getLocation());
					Thread.sleep(300);
					masterServerManager.turn("localhost",8080,"/mxcc/on/"+dev.getLocation(),2000);
					devNo++;
				}
			else 
				logger.debug("no devices for room "+roomIdentifier);
			return devNo;
		}

}
