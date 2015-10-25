package com.bitzware.exm.action.panel;

import java.util.Date;

import org.apache.log4j.Logger;


import com.bitzware.exm.model.StationStatus;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.service.WakeOnLanManager;
import com.bitzware.exm.visitordb.model.Station;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Base abstract class that contains common functionality used in station lists on the
 * master server.
 * 
 * @author finagle
 */
public abstract class StationsInfoActionSupport extends ActionSupport {

	private static final long serialVersionUID = 1L;

	protected Logger logger = Logger.getLogger(this.getClass());
	
	private ConfigManager configManager;
	protected WakeOnLanManager wakeOnLanManager;
	
	/**
	 * Amount of time (in seconds). If a station doesn't send any heartbeat message in
	 * this time, it is considered inactive.
	 */
	private Integer maxHeartbeat;
	private long currentTime;
	
	protected String action;
	private String stationmac;
	
	private final String turnOnAction = "turnOn";
	
	protected String message;
	protected String messageType;
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public void setConfigManager(final ConfigManager configManager) {
		this.configManager = configManager;
	}

	public WakeOnLanManager getWakeOnLanManager() {
		return wakeOnLanManager;
	}

	public void setWakeOnLanManager(final WakeOnLanManager wakeOnLanManager) {
		this.wakeOnLanManager = wakeOnLanManager;
	}

	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public String getStationmac() {
		return stationmac;
	}

	public void setStationmac(final String stationmac) {
		this.stationmac = stationmac;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(final String messageType) {
		this.messageType = messageType;
	}

	/**
	 * This method should be called before displaying the data on the page. It 
	 * initializes some internal variables used to establish the station status. 
	 */
	public void prepare() {
		maxHeartbeat = configManager.getIntegerValue(ConfigManager.MASTER_HEARTBEAT_MAX);
		if (maxHeartbeat == null) {
			logger.warn("No maximum heartbeat wait time specified!");
		}
		
		currentTime = new Date().getTime();
		
		if (action != null) {
			if (turnOnAction.equals(action)) {
				if (wakeOnLanManager.sendWakeUpMessage(stationmac)) {
					messageType = "success";
					message = getText("text.station.turnon.success");
				} else {
					messageType = "error";
					message = getText("text.station.turnon.failure");
				}
			}
		}
	}
	
	/**
	 * Returns the status of the specified station.
	 */
	public StationStatus getStationStatus(final Station station) {
		final int millisInSecond = 1000;

		if (station.getLastHeartbeat() == null) {
			return StationStatus.INACTIVE;
		}
		
		if (maxHeartbeat != null) {
			long lastHeartbeatDelta = currentTime - station.getLastHeartbeat().getTime();
			
			if (lastHeartbeatDelta > maxHeartbeat * millisInSecond) {
				return StationStatus.INACTIVE;
			}
		}
		
		if (station.getStatus() == null) {
			return StationStatus.UNKNOWN;
		}
		
		switch (station.getStatus()) {
		case IDLE: return StationStatus.IDLE;
		case PLAYING: return StationStatus.PLAYING;
		default: return StationStatus.UNKNOWN;
		}
	}
	
}
