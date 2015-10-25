package com.bitzware.exm.service.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;



import org.apache.log4j.Logger;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;




import com.bitzware.exm.listener.ConnectionListener;
import com.bitzware.exm.model.RfidEventInfo;
import com.bitzware.exm.model.ServerProperties;
import com.bitzware.exm.model.StationProperties;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.service.ProcessManager;
import com.bitzware.exm.service.ServerManager;
import com.bitzware.exm.service.SystemToolManager;
import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.ws.IMasterServer;

/**
 * Class that manages station data and connection functions.
 * 
 * @author finagle
 */
public class ServerManagerImpl extends ServerManagerBaseImpl implements ServerManager {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Guards the 'connection', 'masterServerService' and 'serviceFactory' objects.
	 */
	private final Object connectionMutex = new Object();
	
	private ConfigManager configManager;
	private ProcessManager processManager;
	private SystemToolManager systemToolManager;
	
	private boolean connected = false;
	private IMasterServer masterServerService = null;
	private final JaxWsPortProxyFactoryBean serviceFactory = new JaxWsPortProxyFactoryBean();
	
	private String mac;
	private final Object macMutex = new Object();
	
	private List<ConnectionListener> connectionListeners = new LinkedList<ConnectionListener>();
	
	public ServerManagerImpl() throws UnknownHostException, SocketException {
		// Default data for the web service client factory.
		serviceFactory.setServiceInterface(IMasterServer.class);
		serviceFactory.setNamespaceUri("http://mserver.bitzware.com/");
		serviceFactory.setServiceName("MasterServer");
		serviceFactory.setPortName("MasterServerPort");

		synchronized (macMutex) {
			readMacAddress();
		}
	}

	
	
	public SystemToolManager getSystemToolManager() {
		return systemToolManager;
	}



	public void setSystemToolManager(SystemToolManager systemToolManager) {
		this.systemToolManager = systemToolManager;
	}



	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(final ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	public ProcessManager getProcessManager() {
		return processManager;
	}

	public void setProcessManager(ProcessManager processManager) {
		this.processManager = processManager;
	}

	public void init() {
		String overwriteMac = configManager.getStringValue(ConfigManager.SERVER_MAC);
		if (overwriteMac != null) {
			logger.warn("Overwriting MAC address: " + overwriteMac);
			
			synchronized (macMutex) {
				mac = overwriteMac;
			}
		}
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Properties getProperties() {
		Properties result = new Properties();
		result.setName(getName());
		result.setDescription(getDescription());
		result.setRoom(getRoom());
		
		return result;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveProperties(final Properties properties) {
		if (properties != null) {
			saveName(properties.getName());
			saveDescription(properties.getDescription());
			saveRoom(properties.getRoom());
		}
	}
	
	public Long getRoom() {
		return serverPropertyDao.getLongProperty(ConfigManager.SERVER_ROOM);
	}

	public void saveRoom(final Long room) {
		serverPropertyDao.setLongProperty(ConfigManager.SERVER_ROOM, room);
	}

	public boolean isConnected() {
		synchronized (connectionMutex) {
			return connected;
		}
	}

	/**
	 * This method tries to connect to the master server (if not connected already) and
	 * sends this station's name and other properties.
	 */
	public void register() {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering...");
		}
		
		// Get this station's data.
		StationProperties stationData = new StationProperties();
		fillData(stationData);
		
		
		// Create local copy of the web service client (so that it may be called
		// outside of the synchronization block).
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (ensureIsConnected()) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Calling sendStationData web method.");
			}
		
			try {
				// Call the web service.
				ServerProperties masterProperties =
					masterServerServiceCopy.sendStationData(stationData);
				updateServerProperties(masterProperties);
				
				dispatchSuccessfulConnectionEvent();
			} catch (Exception e) {
				logger.error("Error while registering.", e);
				
				closeConnection();
			}
		}
	}
	
	/**
	 * Sends the heartbeat message.
	 * 
	 * @return true if the master server knows this station, false otherwise.
	 */
	public boolean heartbeat() {
		if (logger.isDebugEnabled()) {
			logger.debug("Sending heartbeat...");
		}
		
		String macAddress = getMac();

		IMasterServer masterServerServiceCopy = null;

		synchronized (connectionMutex) {
			if (connected) {
				masterServerServiceCopy = masterServerService;
			} else {
				return false;
			}
		}

		if (masterServerServiceCopy != null) {
			try {
				ServerProperties masterProperties =
					masterServerServiceCopy.heartbeat(macAddress, getActiveStatus());
				updateServerProperties(masterProperties);
				
				if (masterProperties != null) {
					return true;
				}
				
				dispatchSuccessfulConnectionEvent();
			} catch (Exception e) {
				logger.error("Error while calling heartbeat.", e);

				closeConnection();
			}
		}
		return false;
	}
	
	/**
	 * Sends this station's data to the master server.
	 * 
	 * @return true on success, false on failure
	 */
	public boolean updataData() {
		if (logger.isDebugEnabled()) {
			logger.debug("Updating station data...");
		}
		
		StationProperties stationData = new StationProperties();
		fillData(stationData);
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				ServerProperties masterProperties =
					masterServerServiceCopy.sendStationData(stationData);
				updateServerProperties(masterProperties);
				
				dispatchSuccessfulConnectionEvent();
				
				return true;
			} catch (Exception e) {
				logger.error("Error while updating station data.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}
	
	public List<Room> getRooms() {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting rooms...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				List<Room> rooms = masterServerServiceCopy.getRooms();
				
				return rooms;
			} catch (Exception e) {
				logger.error("Error while retrieving rooms.", e);
				
				closeConnection();
			}
		}
		
		return null;
	}
	
	public RfidEventInfo registerRfidEvent(final String rfid, final Integer deviceIndex) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering RFID event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				RfidEventInfo result = masterServerServiceCopy.registerRfidEvent(rfid, getMac(),
						deviceIndex);
				
				return result;
			} catch (Exception e) {
				logger.error("Error while registering RFID event.", e);
				
				closeConnection();
			}
		}
		
		return null;
	}
	
	public boolean registerButtonEvent(final int index, final boolean down) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering Button event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.registerButtonEvent(getMac(), index, down);
				
				return true;
			} catch (Exception e) {
				logger.error("Error while registering Button event.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}
	
	public boolean registerPirEvent() {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering PIR event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.registerPirEvent(getMac());
				
				return true;
			} catch (Exception e) {
				logger.error("Error while registering PIR event.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}
	
	@Override
	public boolean registerScoreEvent(final Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering 'score selected' event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.registerScoreEvent(event, getMac());
				
				return true;
			} catch (Exception e) {
				logger.error("Error while registering 'score selected' event.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}
	
	@Override
	public boolean registerDirectorEvent(final Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering Director event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.registerDirectorEvent(event, getMac());
				
				return true;
			} catch (Exception e) {
				logger.error("Error while registering Director event.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}
	
	@Override
	public boolean registerClientDisconnectedEvent(final Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering 'client disconnected' event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.registerClientDisconnectedEvent(event, getMac());
				
				return true;
			} catch (Exception e) {
				logger.error("Error while registering 'client disconnected' event.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}

	@Override
	public boolean registerFloorEvent(int index, boolean down) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering 'floor' event...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.registerFloorEvent(getMac(), index, down);
				
				return true;
			} catch (Exception e) {
				logger.error("Error while registering 'floor' event.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}

	public boolean sendSynchronizationData(final List<Event> events) {
		if (logger.isDebugEnabled()) {
			logger.debug("Sending synchronization data...");
		}
		
		IMasterServer masterServerServiceCopy = null;
		
		synchronized (connectionMutex) {
			if (connected) {				
				masterServerServiceCopy = masterServerService;
			}
		}
		
		if (masterServerServiceCopy != null) {
			try {
				masterServerServiceCopy.synchronize(events, getMac());
				
				return true;
			} catch (Exception e) {
				logger.error("Error while sending synchronization data.", e);
				
				closeConnection();
			}
		}
		
		return false;
	}
	
	public void registerConnectionListener(final ConnectionListener listener) {
		connectionListeners.add(listener);
	}
	
	
	public ActiveStationStatus getActiveStatus() {
		if (processManager.isRunning()) {
			return ActiveStationStatus.PLAYING;
		} else {
			return ActiveStationStatus.IDLE;
		}
	}
	
	/**
	 * Reads the MAC address of this server and updates the 'mac' field. If it
	 * is not possible to obtain the address, the field is set to null.
	 */
	private void readMacAddress() {
		try {
			// Getting MAC address
			InetAddress i = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(i);
			if (ni != null) {
				byte[] macAddress = ni.getHardwareAddress();
				
				if (macAddress != null) {
					final int macPartLength = 3;
					if(macAddress.length * macPartLength - 1 >0 ) {
					StringBuilder macString =
						new StringBuilder(macAddress.length * macPartLength - 1);
					for (int k = 0; k < macAddress.length; k++) {
						if (k > 0) {
							macString.append('-');
						}
						macString.append(String.format("%02X", macAddress[k]));
					}
					
					mac = macString.toString();
					}
				}
			}
		} catch (UnknownHostException e) {
			mac = null;
			logger.error("Cannot read MAC address.", e);
		} catch (SocketException e) {
			mac = null;
			logger.error("Cannot read MAC address.", e);
		}
		
		if (isEmpty(mac)) {
			logger.warn("MAC address is null!");
			try {
				logger.error("trying system tool manager");
				mac = systemToolManager.readMacAddressList().get(0).getAddress();
			} catch (IOException e) {
				logger.error("...but failed");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("mac address is:"+mac);
		}
	}

	private void closeConnection() {
		synchronized (connectionMutex) {
			connected = false;
			masterServerService = null;
		}
	}

	/**
	 * If the station is not connected to the master server, this method starts the
	 * connection.
	 * 
	 * @return true on success or if the server is already connected, false otherwise.
	 */
	private boolean ensureIsConnected() {		
		if (connected) {
			return true;
		}
		
		String masterServerAddress = null;
		
		try {
			masterServerAddress = configManager.getStringValue(ConfigManager.MASTER_SERVER_ADDRESS);
			if (masterServerAddress == null) {
				logger.error("No master server address in the configuration!");
				return false;
			}

			StringBuilder wsdlLocation = new StringBuilder();
			wsdlLocation.append("http://").append(masterServerAddress)
				.append("/mserver/masterServerWsdl");
			
			String username = configManager.getStringValue(ConfigManager.MASTER_SERVER_USERNAME);
			String password = configManager.getStringValue(ConfigManager.MASTER_SERVER_PASSWORD);
			
			serviceFactory.setWsdlDocumentUrl(new URL(wsdlLocation.toString()));
			
			if (!isEmpty(username)) {
				serviceFactory.setUsername(username);
			}
			if (!isEmpty(password)) {
				serviceFactory.setPassword(password);
			}

			try {
				serviceFactory.afterPropertiesSet();
				
				masterServerService = (IMasterServer) serviceFactory.getObject();
				if (masterServerService != null) {
					connected = true;

					return true;
				} else {
					logger.error("Cannot connect to the heartbeat web service.");
				}
			} catch (Exception e) {
				logger.error("Cannot connect to the heartbeat web service.");

				if (logger.isDebugEnabled()) {
					logger.debug(e);
				}
			}
		} catch (MalformedURLException e) {
			logger.error("Invalid master server address: " + masterServerAddress);
		}
		
		return false;
	}
	
	/**
	 * Update properties retrieved from the master server.
	 */
	private void updateServerProperties(final ServerProperties masterProperties) {
		// Currently unused, may be needed in future versions.
		
//		if (masterProperties != null) {
//			saveRfidTimeout(masterProperties.getRfidTimeout());
//		}
	}
	
	private void fillData(final StationProperties dest) {
		dest.setDescription(getDescription());
		dest.setName(getName());
		dest.setRoom(getRoom());
		dest.setStatus(getActiveStatus());
		dest.setStationVersion(getStationVersion());
		
		try {
			dest.setIp(getIp());
		} catch (UnknownHostException e) {
			logger.error("Cannot obtain local IP address!");
		}
		dest.setMac(getMac());
	}
	
	private String getStationVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getMac() {
		synchronized (macMutex) {
			if (mac == null) {
				readMacAddress();
			}
			
			return mac;
		}
	}
	
	private boolean isEmpty(String str) {
		if(null==str) return true;
		return str.trim().length()==0;
	}
	
	private String getIp() throws UnknownHostException {
		InetAddress i = InetAddress.getLocalHost();
		return i.getHostAddress();
	}
	
	private void dispatchSuccessfulConnectionEvent() {
		for (ConnectionListener listener : connectionListeners) {
			listener.onSuccessfulConnection();
		}
	}
	
}
