package com.bitzware.exm.model;

/**
 * Station status.
 * 
 * @author finagle
 */
public enum StationStatus {

	/**
	 * Station not connected.
	 */
	INACTIVE("text.station.status.inactive", false),
	/**
	 * Station connected, but not playing.
	 */
	IDLE("text.station.status.idle", true),
	/**
	 * Station connected and playing.
	 */
	PLAYING("text.station.status.playing", true),
	/**
	 * Station is connected, but the current station is unknown. It should
	 * not happen, but it was added for debugging purpose.
	 */
	UNKNOWN("text.station.status.unknown", true);
	
	private final String key;
	private final boolean active;

	private StationStatus(final String key, final boolean active) {
		this.key = key;
		this.active = active;
	}

	public String getKey() {
		return key;
	}

	public boolean isActive() {
		return active;
	}
	
}
