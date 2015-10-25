package com.bitzware.exm.visitordb.model.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Device type.
 * 
 * @author finagle
 */
public enum DeviceType {
	AUDIO_CONTROL("audio_control", false, true),
	BUTTON("button", true, false),
	DMX("dmx", false, true),
	GENERIC("generic", false, false),
	LED("led", false, true),
	PIR("pir", true, false),
	PW392("pw392", false, true),
	RFID("rfid", true, false),
	FLOOR("floor", true, false),
	RCV_SOCKET("rcv_socket",true,false);
	
	private static List<String> inputDevices;
	private static List<String> outputDevices;
	
	private static List<String> nameList;
	
	static {
		inputDevices = new ArrayList<String>(DeviceType.values().length);
		outputDevices =  new ArrayList<String>(DeviceType.values().length);
		
		DeviceType[] types = DeviceType.values();
		
		nameList = new ArrayList<String>(types.length);
		
		for (DeviceType type : types) {
			if (type.isInput()) {
				inputDevices.add(type.getName());
			}
			if (type.isOutput()) {
				outputDevices.add(type.getName());
			}
			nameList.add(type.getName());
		}
		
		inputDevices = Collections.unmodifiableList(inputDevices);
		outputDevices = Collections.unmodifiableList(outputDevices);
		nameList = Collections.unmodifiableList(nameList);
	}
	
	private final String name;
	
	private final Boolean input;
	private final Boolean output;

	private DeviceType(String name, Boolean input, Boolean output) {
		this.name = name;
		this.input = input;
		this.output = output;
	}

	public String getName() {
		return name;
	}
	
	public Boolean isInput() {
		return input;
	}

	public Boolean isOutput() {
		return output;
	}

	public static DeviceType getDeviceType(String name) {
		for (DeviceType deviceType : DeviceType.values()) {
			if (deviceType.name.equals(name)) {
				return deviceType;
			}
		}
		
		return null;
	}

	public static List<String> getInputDevices() {
		return inputDevices;
	}
	
	public static List<String> getOutputDevices() {
		return outputDevices;
	}
	
	public static List<String> getNameList() {
		return nameList;
	}
	
}
