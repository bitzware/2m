package com.bitzware.exm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bitzware.exm.visitordb.model.ActiveStationStatus;


/**
 * Station data. Used by the web services to exchange information about stations.
 * 
 * @author finagle
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "stationProperties")
@XmlRootElement
public class StationProperties {

	private String ip;
	private String stationVersion;
	private String mac;
	private String name;
	private String description;
	private Long room;
	private ActiveStationStatus status;
	
	@XmlAttribute
	public String getDescription() {
		return description;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}

	@XmlAttribute
	public String getIp() {
		return ip;
	}

	public void setIp(final String ip) {
		this.ip = ip;
	}

	@XmlAttribute
	public String getMac() {
		return mac;
	}

	public void setMac(final String mac) {
		this.mac = mac;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlAttribute
	public Long getRoom() {
		return room;
	}

	public void setRoom(final Long room) {
		this.room = room;
	}

	@XmlAttribute
	public ActiveStationStatus getStatus() {
		return status;
	}

	public void setStatus(final ActiveStationStatus status) {
		this.status = status;
	}

	@XmlAttribute
	public String getStationVersion() {
		return stationVersion;
	}

	public void setStationVersion(String stationVersion) {
		this.stationVersion = stationVersion;
	}
	
	
}
