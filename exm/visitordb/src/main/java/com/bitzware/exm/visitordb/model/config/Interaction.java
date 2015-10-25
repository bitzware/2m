package com.bitzware.exm.visitordb.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Interaction {

	private Long id;
	
	private DeviceType sourceType;
	private Integer sourceNumber;
	private DeviceType targetType;
	private Integer targetNumber;
	
	private ActionDefinition action;
	private String actionParams;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public DeviceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(DeviceType sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getSourceNumber() {
		return sourceNumber;
	}

	public void setSourceNumber(Integer sourceNumber) {
		this.sourceNumber = sourceNumber;
	}

	public DeviceType getTargetType() {
		return targetType;
	}

	public void setTargetType(DeviceType targetType) {
		this.targetType = targetType;
	}

	public Integer getTargetNumber() {
		return targetNumber;
	}

	public void setTargetNumber(Integer targetNumber) {
		this.targetNumber = targetNumber;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public ActionDefinition getAction() {
		return action;
	}

	public void setAction(ActionDefinition action) {
		this.action = action;
	}

	public String getActionParams() {
		return actionParams;
	}

	public void setActionParams(String actionParams) {
		this.actionParams = actionParams;
	}
	
}
