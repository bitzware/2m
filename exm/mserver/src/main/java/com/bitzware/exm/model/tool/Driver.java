package com.bitzware.exm.model.tool;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "driver")
@XmlRootElement
public class Driver {

	private String name;
	
	private String displayName;
	
	private String type;
	
	private String date;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}
		
}
