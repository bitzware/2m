package com.bitzware.exm.service;

import java.util.Iterator;

/**
 * Interface for a class that reads configuration.
 * 
 * @author finagle
 */
public interface ConfigManager {

	// Configuration keys.
	
	/**
	 * Address of the master server (ip:port).
	 */
	String MASTER_SERVER_ADDRESS = "server.master.address";
	
	/**
	 * User name for the master server.
	 */
	String MASTER_SERVER_USERNAME = "server.master.username";
	
	/**
	 * User password for the master server.
	 */
	String MASTER_SERVER_PASSWORD = "server.master.password";
	
	/**
	 * MAC address of this station. If it is set, it will overwrite the MAC address
	 * retrieved from the system.
	 */
	String SERVER_MAC = "server.mac";

	/**
	 * Directory that contains the presentation.
	 */
	String PRESENTATION_DIR = "presentation.dir";
	
	/**
	 * Path to the presentation file (relative, starting from the PRESENTATION_DIR).
	 */
	String PRESENTATION_FILE = "presentation.file";
	
	/**
	 * Default presentation player id.
	 */
	String PRESENTATION_PLAYER = "presentation.player";

	/**
	 * Directory on the master server that contains presentations downloaded from
	 * the stations.
	 */
	String MASTER_PRESENTATION_DIR = "master.presentation.dir";
	
	/**
	 * Directory on the master server that contains reports.
	 */
	String MASTER_REPORT_DIR = "master.report.dir";
	
	/**
	 * Name of the station.
	 */
	String SERVER_NAME = "server.name";
	
	/**
	 * Description of the station.
	 */
	String SERVER_DESCRIPTION = "server.description";
	
	/**
	 * Station's room id.
	 */
	String SERVER_ROOM = "server.room";
	
	/**
	 * RFID timeout in minutes.
	 */
	String RFID_TIMEOUT = "rfid.timeout";
	
	/**
	 * RFID time of entry in minutes.
	 */
	String RFID_TIMEOFENTRY = "rfid.timeofentry";
	
	/**
	 * Heartbeat interval in seconds.
	 */
	String SERVER_HEARTBEAT = "server.heartbeat";
	
	/**
	 * Heartbeat timeout (if the station does not connect heartbeat in this time,
	 * it is considered inactive by the master server) in seconds.
	 */
	String MASTER_HEARTBEAT_MAX = "master.heartbeat.max";
	
	/**
	 * Station port for the 'shutdown' call.
	 */
	String STATION_SHUTDOWN_PORT = "server.shutdown.port";
	
	/**
	 * Path for the 'shutdown' call.
	 */
	String STATION_SHUTDOWN_PATH = "server.shutdown.path";
	
	/**
	 * 'Shutdown' call timeout in milliseconds.
	 */
	String STATION_SHUTDOWN_TIMEOUT = "server.shutdown.timeout";
	
	/**
	 * 'Shutdown' call user name.
	 */
	String STATION_SHUTDOWN_LOGIN = "server.shutdown.login";
	
	/**
	 * 'Shutdown' call password.
	 */
	String STATION_SHUTDOWN_PASSWORD = "server.shutdown.password";
	
	/**
	 * Station port for the 'download presentation' call from the master server.
	 */
	String MASTER_DUPLICATION_PORT = "master.duplication.port";
	
	/**
	 * Path for the 'download presentation' call from the master server.
	 */
	String MASTER_DUPLICATION_PRESENTATION_PATH = "master.duplication.presentation.path";
	
	/**
	 * Path for the 'request presentation update' call from the master server.
	 */
	String MASTER_DUPLICATION_PRESENTATION_REQUEST_PATH =
		"master.duplication.presentation.request.path";
	
	/**
	 * Path for the 'request properties update' call from the master server.
	 */
	String MASTER_DUPLICATION_PROPERTIES_REQUEST_PATH =
		"master.duplication.properties.request.path";
	
	/**
	 * Timeout for the 'download presentation' call from the master server
	 * (in milliseconds).
	 */
	String MASTER_DUPLICATION_PRESENTATION_TIMEOUT = "master.duplication.presentation.timeout";
	
	/**
	 * Timeout for the 'request presentation update' call from the master server
	 * (in milliseconds).
	 */
	String MASTER_DUPLICATION_PRESENTATION_REQUEST_TIMEOUT =
		"master.duplication.presentation.request.timeout";
	
	/**
	 * Timeout for the 'request properties update' call from the master server
	 * (in milliseconds).
	 */
	String MASTER_DUPLICATION_PROPERTIES_REQUEST_TIMEOUT =
		"master.duplication.properties.request.timeout";
	
	/**
	 * User name for the 'download presentation' call from the master server.
	 */
	String MASTER_DUPLICATION_LOGIN = "master.duplication.login";
	
	/**
	 * Password for the 'download presentation' call from the master server.
	 */
	String MASTER_DUPLICATION_PASSWORD = "master.duplication.password";
	
	/**
	 * Complete HTTP URL for the 'download presentation' call from a station to
	 * the master server.
	 */
	String DOWNLOAD_PRESENTATION_ADDRESS = "presentation.download.address";
	
	/**
	 * Timeout for the 'download presentation' call from a station (in milliseconds).
	 */
	String DOWNLOAD_PRESENTATION_TIMEOUT = "presentation.download.timeout";
	
	/**
	 * User name for the 'download presentation' call from a station.
	 */
	String DOWNLOAD_PRESENTATION_LOGIN = "presentation.download.login";
	
	/**
	 * User password for the 'download presentation' call from a station.
	 */
	String DOWNLOAD_PRESENTATION_PASSWORD = "presentation.download.password";
	
	/**
	 * Path for the 'download properties' call.
	 */
	String DOWNLOAD_PROPERTIES_PATH = "properties.download.path";
	
	/**
	 * User name for the 'download properties' call.
	 */
	String DOWNLOAD_PROPERTIES_LOGIN = "properties.download.login";
	
	/**
	 * User password for the 'download properties' call.
	 */
	String DOWNLOAD_PROPERTIES_PASSWORD = "properties.download.password";
	
	/**
	 * An interval for the 'download presentation and properties' job (in seconds).
	 * This job checks if there is a new presentation / properties download request
	 * and processes it.
	 */
	String DOWNLOAD_PRESENTATION_INTERVAL = "presentation.download.interval";
	
	/**
	 * Time of the opening of the museum (HH:mm).
	 */
	String OPEN_MUSEUM_TIME = "museum.open.time";
	
	/**
	 * Time of the closing of the museum (HH:mm).
	 */
	String CLOSE_MUSEUM_TIME = "museum.close.time";
	
	/**
	 * Wake on lan broadcast address.
	 */
	String WAKE_ON_LAN_ADDRESS = "wol.address";
	
	/**
	 * Returns the configuration value for the specified key.
	 */
	String getStringValue(String key);
	
	String[] getStringArrayValue(String key);
	
	Integer getIntegerValue(String key);
	
	Long getLongValue(String key);
	
	Iterator<String> getKeys();
	
}
