package com.bitzware.exm.visitordb.model;

public enum EventType {
	RFID_ACCEPTED,
	RFID_INVALID,
	RFID_EXPIRED,
	PIR_CHANGED,
	BUTTON_DOWN,
	BUTTON_UP,
	SCORE_SELECTED,
	PROFILE_CHANGED,
	PAGE_CHANGED,
	CUSTOM_DIRECTOR_EVENT,
	CLIENT_DISCONNECTED,
	FLOOR_PRESSED,
	FLOOR_RELEASED
}

//import javax.persistence.*;
//
//
//@Entity
//public class EventType {
//
//	private Long id;
//	private String name;
//	private String description;
//	private Integer version;
//
//	public Integer getVersion() { 
//		return version; 
//	}
//
//	public void setVersion(Integer i) {
//		version = i;
//	} 
//	
//	public String getName() {
//		return name;
//	}
//
//	@Id
//	@GeneratedValue(strategy=GenerationType.AUTO)
//	@Column(name="id")
//	public Long getId() {
//		return id;
//	}
//	
//	public void setId(Long id) {
//		this.id = id;
//	}
//	
//	public void setName(String name) {
//		this.name = name;
//	}
//	
//	public String getDescription() {
//		return description;
//	}
//	
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//}
