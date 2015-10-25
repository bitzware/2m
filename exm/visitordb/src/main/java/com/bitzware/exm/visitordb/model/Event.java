package com.bitzware.exm.visitordb.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "event")
@XmlRootElement

@Entity
public class Event {
	
	private Long id;
	private Date timestamp;
	private Visitor visitor;
	private Station station;
	private EventType eventType;
	private Integer version;
	private String data;
	private Integer deviceIndex;
	
	@Transient
	private Boolean standalone;

	public Integer getVersion() { 
		return version; 
	}

	public void setVersion(Integer i) {
		version = i;
	} 

	@XmlAttribute
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@XmlElement(name = "visitor")
	
	@ManyToOne( cascade = {CascadeType.ALL}, fetch = FetchType.LAZY )
	@JoinColumn(name="visitor_id")
	public Visitor getVisitor() {
		return visitor;
	}
	
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}

	@ManyToOne( cascade = {CascadeType.ALL}, fetch = FetchType.LAZY )
	@JoinColumn(name="station_id")
	public Station getStation() {
		return station;
	}
	
	public void setStation(Station station) {
		this.station = station;
	}

	@XmlAttribute
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	@XmlAttribute
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@XmlAttribute
	public Integer getDeviceIndex() {
		return deviceIndex;
	}

	public void setDeviceIndex(Integer deviceIndex) {
		this.deviceIndex = deviceIndex;
	}

	@XmlAttribute
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@XmlAttribute
	
	@Transient
	public Boolean getStandalone() {
		return standalone;
	}

	public void setStandalone(Boolean standalone) {
		this.standalone = standalone;
	}
	
}
