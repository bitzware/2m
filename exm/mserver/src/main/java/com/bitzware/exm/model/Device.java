package com.bitzware.exm.model;

import java.util.Date;

public class Device {
	private String id;
	private String name;
	private String location;
	private long lastModified;
	private long usedUp;
	private boolean state;

	
	
	public long getUsedUp() {
		return usedUp;
	}

	public void setUsedUp(long usedUp) {
		this.usedUp = usedUp;
	}

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

	public Device(String id, String name, long lastModified, String location) {
		super();
		this.id = id;
		this.name = name;
		this.lastModified = lastModified;
		this.location = location;
		if ((new Date()).getTime() - lastModified < 120000)
			state = true;
		else
			state = false;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

}
