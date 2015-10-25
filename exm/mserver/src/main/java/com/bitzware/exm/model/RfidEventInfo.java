package com.bitzware.exm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bitzware.exm.visitordb.model.Visitor;


/**
 * Data returned by the master server on a RFID event.
 * 
 * @author finagle
*/
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "rfidEventInfo")
@XmlRootElement
public class RfidEventInfo {

	private Visitor visitor;
	private boolean timeout;
	private boolean invalid;
	
	@XmlElement(name = "visitor")
	public Visitor getVisitor() {
		return visitor;
	}
	
	public void setVisitor(final Visitor visitor) {
		this.visitor = visitor;
	}
	
	@XmlAttribute
	public boolean isTimeout() {
		return timeout;
	}
	
	public void setTimeout(final boolean timeout) {
		this.timeout = timeout;
	}

	@XmlAttribute
	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(final boolean invalid) {
		this.invalid = invalid;
	}
	
}

