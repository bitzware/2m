package com.bitzware.exm.model;

import java.util.List;

public class Chamber {

	private String id;
	private String name;
	private String location;
	private List<Device> devices;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Chamber(String id, String name, String location) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
	}
	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	
}
