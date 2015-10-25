package com.bitzware.exm.model.tool;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "macAddress")
@XmlRootElement
public class MacAddress {

	private String address;
	
	private String transportName;

	@XmlAttribute
	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	@XmlAttribute
	public String getTransportName() {
		return transportName;
	}

	public void setTransportName(final String transportName) {
		this.transportName = transportName;
	}
	
}
