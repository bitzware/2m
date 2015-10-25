package com.bitzware.exm.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NumberUtils;
import org.apache.log4j.Logger;

import com.bitzware.exm.model.Chamber;
import com.bitzware.exm.model.Device;

public class CrestronManagerBean implements CrestronManager {

	private String rootFolder = "/home/keeper/meximus";

	private static final Logger log = Logger
			.getLogger(CrestronManagerBean.class);
	
	private ConfigManager configManager;
	
	
	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	private Map<String, List<Device>> roomz2Devs = new HashMap<String, List<Device>>();

	@Override
	public List<Chamber> getChambers() {
		File fp = new File(rootFolder);
		roomz2Devs = new HashMap<String, List<Device>>();
		List<Chamber> roomz = new ArrayList<Chamber>();
		if (fp.exists() && fp.isDirectory()) {
			for (File f : fp.listFiles()) {
				if (f.isDirectory()) {
					Chamber r = new Chamber(f.getAbsolutePath(), f.getName(),
							null);
					r.setDevices(new ArrayList<Device>());
					for (File f1 : f.listFiles()) {
						Device device = new Device(f.getName()+"/"+f1.getName(),
								f1.getName(), f1.lastModified(),f1.getParentFile().getName()+"/"+f1.getName());
						r.getDevices().add(device);
						File froom;
						if ((froom = new File(f1.getPath() + "/" + "room"))
								.exists()) {

							try {
								FileReader fr;
								fr = new FileReader(froom);
								BufferedReader br = new BufferedReader(fr);
								String roomNo = br.readLine();
								br.close();
								fr.close();
								List<Device> devices = roomz2Devs.get(roomNo);
								if (devices == null) {
									devices = new ArrayList<Device>();
									roomz2Devs.put(roomNo, devices);
								}
								devices.add(device);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
//							long utilization=0;
//							for(String f2 :f1.list()) 
//								if(NumberUtils.isNumber(f2) && utilization < Long.parseLong(f2)) 
//									utilization = Long.parseLong(f2);
//							
//							device.setUsedUp(utilization);
						}
					}
					roomz.add(r);
				}
			}
		}

		return roomz;
	}

	@Override
	public Map<String, List<Device>> getRooms2Devices() {
		return roomz2Devs;
	}

	@Override
	public void assignDevice2Room(String device, String room) {
		String path[] = device.split("\\|");
		File fp = new File(rootFolder + "/" + path[0] + "/" + path[1] + "/"
				+ "room");
		try {
			fp.createNewFile();
			FileWriter fw = new FileWriter(fp);
			fw.write(room);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<Device> getUtilizedDevices() {
		List<Chamber> chambers = getChambers();
		List<Device> devices = new ArrayList<Device>();
		for(Chamber chamber:chambers)
			for(Device device : chamber.getDevices())
				if(device.getUsedUp()>0)
					devices.add(device);
		return devices;
	}

}