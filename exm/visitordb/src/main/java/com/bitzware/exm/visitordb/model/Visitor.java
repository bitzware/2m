package com.bitzware.exm.visitordb.model;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "visitor")
@XmlRootElement

@Entity
public class Visitor {

	private Long id;
	private String name;
	private String age;
	private String language;
	private String level;
	private String zoom;
	private String rfid;
	private Date rfidTimestamp;
	private Date rfidValidFrom;
	private Date entryTime;
	private Integer version;
	private Date lastUpdate;
	private Boolean expired;

	@Column(nullable = false)
	@Version
	public Integer getVersion() { 
		return version; 
	}

	public void setVersion(Integer i) {
		version = i;
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	@Column(length = 20)
	public String getAge() {
		return age;
	}
	
	public void setAge(String age) {
		this.age = age;
	}
	
	@XmlAttribute
	@Column(length = 20)
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@XmlAttribute
	@Column(length = 30)
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	@XmlAttribute
	@Column(length = 30)
	public String getZoom() {
		return zoom;
	}
	
	public void setZoom(String zoom) {
		this.zoom = zoom;
	}

	@XmlAttribute
	@Column(length = 50)
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	@XmlAttribute
	public Date getRfidTimestamp() {
		return rfidTimestamp;
	}

	public void setRfidTimestamp(Date rfidTimestamp) {
		this.rfidTimestamp = rfidTimestamp;
	}

	@XmlAttribute
	public Date getRfidValidFrom() {
		return rfidValidFrom;
	}

	public void setRfidValidFrom(Date rfidValidFrom) {
		this.rfidValidFrom = rfidValidFrom;
	}

	@XmlAttribute
	public Date getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
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
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@XmlTransient
	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

}
