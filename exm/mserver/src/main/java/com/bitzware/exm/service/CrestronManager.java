package com.bitzware.exm.service;

import java.util.List;
import java.util.Map;

import com.bitzware.exm.model.Chamber;
import com.bitzware.exm.model.Device;

public interface CrestronManager 
{
	
	public List<Chamber> getChambers();
	public void assignDevice2Room(String device, String room);
	public Map<String,List<Device>> getRooms2Devices();
	public List<Device> getUtilizedDevices();
}
