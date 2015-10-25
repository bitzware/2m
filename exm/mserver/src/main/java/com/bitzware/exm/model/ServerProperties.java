package com.bitzware.exm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Server properties that are sent to the stations.
 * 
 * @author finagle
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "serverProperties")
@XmlRootElement
public class ServerProperties {
	
	private Integer rfidTimeout;
	
	public ServerProperties() {
	}
	
	public ServerProperties(final ServerProperties source) {
		this.rfidTimeout = source.rfidTimeout;
	}

	@XmlAttribute
	public Integer getRfidTimeout() {
		return rfidTimeout;
	}

	public void setRfidTimeout(final Integer rfidTimeout) {
		this.rfidTimeout = rfidTimeout;
	}
	
}
