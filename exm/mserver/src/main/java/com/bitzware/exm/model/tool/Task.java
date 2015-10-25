package com.bitzware.exm.model.tool;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "event")
@XmlRootElement
public class Task {

	private String name;
	
	private String pid;
	
	private String sessionName;
	
	private String sessionNumber;
	
	private String memoryUsage;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getPid() {
		return pid;
	}

	public void setPid(final String pid) {
		this.pid = pid;
	}

	@XmlAttribute
	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(final String sessionName) {
		this.sessionName = sessionName;
	}

	@XmlAttribute
	public String getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(final String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	@XmlAttribute
	public String getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(final String memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	
}
