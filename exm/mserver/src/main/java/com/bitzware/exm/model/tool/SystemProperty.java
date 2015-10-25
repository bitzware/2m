package com.bitzware.exm.model.tool;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "systemProperty")
@XmlRootElement
public class SystemProperty {

	private String name;
	
	private String value;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
	
}
