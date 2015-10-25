package com.bitzware.exm.visitordb.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "room")
@XmlRootElement

@Entity
public class Room {

	private Long id;
	private Room parent;
	private String name;
	private String floor;
	private String description;
	private Set<Station> stations;
//	private String[] devices;
	private Integer version;
	private Date openTime;
	private Date closeTime;
	private Set<Room> children;

	@XmlAttribute
	public Integer getVersion() { 
		return version; 
	}

	public void setVersion(Integer i) {
		version = i;
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
	public Set<Station> getStations() {
		return stations;
	}

	public void setStations(Set<Station> stations) {
		this.stations = stations;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	public Room getParent() {
		return parent;
	}

	public void setParent(Room room) {
		this.parent = room;
	}

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	public Set<Room> getChildren() {
		return children;
	}
	

	public void setChildren(Set<Room> children) {
		this.children = children;
	}

	@XmlAttribute
	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	@XmlAttribute
	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	@XmlAttribute
	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", parent=" + (parent!=null?parent.name:"null") + ", name=" + name
				+ ", floor=" + floor + ", description=" + description
				+ ", version=" + version
				+ ", openTime=" + openTime + ", closeTime=" + closeTime
				+ ", children=" + (children!=null?children.size():null) + "]";
	}

//	public String[] getDevices() {
//		return devices;
//	}
//
//	public void setDevices(String[] devices) {
//		this.devices = devices;
//	}


	
	
}
