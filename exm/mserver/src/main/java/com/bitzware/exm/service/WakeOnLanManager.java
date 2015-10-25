package com.bitzware.exm.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;

import com.bitzware.exm.dao.RoomDao;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


/**
 * Class that sends the WOL messages.
 * 
 * @author finagle
 */
public class WakeOnLanManager {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private ConfigManager configManager;
	
	private RoomDao roomDao;
	
	private byte[] broadcastAddress;
	
	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public RoomDao getRoomDao() {
		return roomDao;
	}

	public void setRoomDao(RoomDao roomDao) {
		this.roomDao = roomDao;
	}
	
	public void init() {
		final int addressPartsCount = 4;
		
		String ipAddress = configManager.getStringValue(ConfigManager.WAKE_ON_LAN_ADDRESS);
		if (ipAddress == null) {
			throw new NullArgumentException("No WOL broadcast address specified!");
		}
		String[] ipAddressParts = ipAddress.split("\\.");
		if (ipAddressParts.length != addressPartsCount) {
			throw new IllegalArgumentException("Invalid WOL broadcast address: " + ipAddress);
		}
		
		broadcastAddress = new byte[addressPartsCount];
		for (int i = 0; i < addressPartsCount; i++) {
			Integer iPart = Integer.parseInt(ipAddressParts[i]);
			broadcastAddress[i] = (byte) ((int) iPart);
		}
	}

	/**
	 * Sends the WOL message for the specified MAC address.
	 *  
	 * @return true if the message has been sent successfully, false otherwise.
	 */
	public boolean sendWakeUpMessage(final String mac) {
		if (logger.isDebugEnabled()) {
			logger.debug("Sending WOL message to address: " + mac);
		}
		
		try {
			if (mac == null) {
				return false;
			}
			
			final int macPartsAmount = 6;
			
			String[] parts = mac.split("-");
			
			if (parts == null || parts.length != macPartsAmount) {
				logger.error("Invalid mac address: " + mac);
				return false;
			}
			byte[] address = new byte[macPartsAmount];
			
			final int macPartRadix = 16;
			
			for (int i = 0; i < macPartsAmount; i++) {
				try {
					Integer addressPart = Integer.valueOf(parts[i], macPartRadix);
					address[i] = (byte) ((int) (addressPart));
				} catch (NumberFormatException e) {
					logger.error("Invalid mac part: " + parts[i]);
					return false;
				}
			}
			
			final byte ff = (byte) 0xff;
			InetAddress netAddress = InetAddress.getByAddress(broadcastAddress);
			final int netPort = 9;
			
			final int initPartSize = 6;
			final int addressPartsAmount = 16;
			final int messageSize = initPartSize + macPartsAmount * addressPartsAmount;
			
			byte[] message = new byte[messageSize];
			int currentOffset = 0;
			for (int i = 0; i < initPartSize; i++) {
				message[currentOffset] = ff;
				currentOffset++;
			}
			
			for (int i = 0; i < addressPartsAmount; i++) {
				for (int j = 0; j < macPartsAmount; j++) {
					message[currentOffset] = address[j];
					currentOffset++;
				}
			}
			
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(message, message.length,
					netAddress, netPort);
//			for(int y=0;y<3;y++) // 3 is the number
				socket.send(packet);
			socket.close();
			
			return true;
		} catch (UnknownHostException e) {
			logger.error("Cannot send Wake-On-Lan message.", e);
			return false;
		} catch (SocketException e) {
			logger.error("Cannot send Wake-On-Lan message.", e);
			return false;
		} catch (IOException e) {
			logger.error("Cannot send Wake-On-Lan message.", e);
			return false;
		}
	}
	
	public boolean sendWakeUpMessagesToRoom(Long roomId) {
		Room room = roomDao.getRoomWithStationsById(roomId);
		
	
		if (room == null || room.getStations() == null) {
			return false;
		}
		
		Room room2 = roomDao.getRoomFullById(roomId);
		for (Room subRoom : room2.getChildren()) {
			
			sendWakeUpMessagesToRoom(subRoom.getId()); // recurrency is its own reward
		}
		
		boolean outcome=true;
		for (Station station : room.getStations()) {
			
			outcome&=sendWakeUpMessage(station.getMacAddress());
		}
		
		return outcome;
	}
	
	public boolean sendWakeUpMessagesToRooms(List<Room> roomsWithStations) {
		for (Room room : roomsWithStations) {
			for (Station station : room.getStations()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!sendWakeUpMessage(station.getMacAddress())) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
