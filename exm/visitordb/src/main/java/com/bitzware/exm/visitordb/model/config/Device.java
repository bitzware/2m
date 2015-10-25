package com.bitzware.exm.visitordb.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Device {

	private Long id;
	
	private DeviceType deviceType;
	private Integer number;
	private String port;
	private RfidConversion rfidConversion;
	private RfidType rfidType;
	private Integer index;
	private String initialState;
	private AdamModel adamModel;
	private Integer adamAddress;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public RfidConversion getRfidConversion() {
		return rfidConversion;
	}

	public void setRfidConversion(RfidConversion rfidConversion) {
		this.rfidConversion = rfidConversion;
	}

	public RfidType getRfidType() {
		return rfidType;
	}

	public void setRfidType(RfidType rfidType) {
		this.rfidType = rfidType;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getInitialState() {
		return initialState;
	}

	public void setInitialState(String initialState) {
		this.initialState = initialState;
	}

	public AdamModel getAdamModel() {
		return adamModel;
	}

	public void setAdamModel(AdamModel adamModel) {
		this.adamModel = adamModel;
	}

	public Integer getAdamAddress() {
		return adamAddress;
	}

	public void setAdamAddress(Integer adamAddress) {
		this.adamAddress = adamAddress;
	}
	
}
