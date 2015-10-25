package com.bitzware.exm.visitordb.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;


@Entity
public class Station {
	private Long id;
	private String name;
	private String description;
	private String ipAddress;
	private String macAddress;
	private Date registeredOn;
	private Date lastHeartbeat;
	private Integer version;
	private Room room;
	private ActiveStationStatus status;

	public Integer getVersion() { 
		return version; 
	}

	public void setVersion(Integer i) {
		version = i;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(length = 50)
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Column(unique = true, length = 50)
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Date getRegisteredOn() {
		return registeredOn;
	}

	public void setRegisteredOn(Date registeredOn) {
		this.registeredOn = registeredOn;
	}

	public Date getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void setLastHeartbeat(Date lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public ActiveStationStatus getStatus() {
		return status;
	}

	public void setStatus(ActiveStationStatus status) {
		this.status = status;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

}
