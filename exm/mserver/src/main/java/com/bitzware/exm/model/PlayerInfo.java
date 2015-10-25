package com.bitzware.exm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contains information about a presentation player.
 * 
 * @author finagle
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "player")
@XmlRootElement
public class PlayerInfo {

	/**
	 * Player id.
	 */
	private String id;
	/**
	 * Player name.
	 */
	private String name;
	/**
	 * Path of the executable file to run.
	 */
	private String path;
	/**
	 * Parameters passed to the player after the presentation file name.
	 */
	private String parametersAfter;
	
	public PlayerInfo() {
	}

	public PlayerInfo(String id, String name, String path) {
		this.id = id;
		this.name = name;
		this.path = path;
	}

	@XmlAttribute(name = "pid")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "path")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@XmlAttribute(name = "parametersAfter")
	public String getParametersAfter() {
		return parametersAfter;
	}

	public void setParametersAfter(String parametersAfter) {
		this.parametersAfter = parametersAfter;
	}
	
}
