package com.bitzware.exm.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.bitzware.exm.model.tool.Driver;
import com.bitzware.exm.model.tool.MacAddress;
import com.bitzware.exm.model.tool.SystemProperty;
import com.bitzware.exm.model.tool.Task;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Class that runs the system tools.
 * 
 * @author finagle
 */
public class   SystemToolManager{
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private static class ToolStreamHandler implements ExecuteStreamHandler {
		
		protected boolean finished = false;
		protected String value;
		
		private InputStream inputStream;

		@Override
		public void setProcessErrorStream(final InputStream stream) throws IOException {
		}

		@Override
		public void setProcessInputStream(final OutputStream stream) throws IOException {
		}

		@Override
		public void setProcessOutputStream(final InputStream stream) throws IOException {
			this.inputStream = stream;
		}

		@Override
		public void start() throws IOException {
			value = IOUtils.toString(inputStream);
			
			synchronized (this) {
				finished = true;
				this.notify();
			}
		}

		@Override
		public void stop() {
		}
		
	}
	
	private static class ToolResultHandler implements ExecuteResultHandler {

		@Override
		public void onProcessComplete(final int exitCode) {
		}

		@Override
		public void onProcessFailed(final ExecuteException exception) {
		}
		
	}
	
	public String launchTool(final String command) throws IOException {
		final int waitTime = 10000;
	
		CommandLine commandLine = CommandLine.parse(command);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(waitTime);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWatchdog(watchdog);
		executor.setExitValue(0);
		ToolStreamHandler streamHandler = new ToolStreamHandler();
		executor.setStreamHandler(streamHandler);
		executor.execute(commandLine, new ToolResultHandler());
		
		synchronized (streamHandler) {
			if (!streamHandler.finished) {
				try {
					streamHandler.wait(waitTime);
				} catch (InterruptedException e) {
					logger.warn("Waiting for system tool interrupted!");
				}
			}
			
			return streamHandler.value;
		}
	};
	
	public List<Task> readTaskList() throws IOException {
		final int expectedEntryLength = 5;
		final int nameIndex = 0;
		final int pidIndex = 1;
		final int sessionNameIndex = 2;
		final int sessionNumberIndex = 3;
		final int memoryUsageIndex = 4;
		
		String value = launchTool("tasklist /FO CSV /NH"); // "tasklist.exe","2240","Console","0","4,016 KB"
														   //"wmiprvse.exe","1384","Console","0","5,320 KB"
		if(null==value )
			value = launchTool("ps --no-headers  o \"\\\"%c\\\",\\\"%p\\\",\\\"%y\\\",\\\"%P\\\",\\\"%z\\\"\""); //linux ps --no-headers o comm,pid,tname,ppid,size
		CSVReader reader = new CSVReader(new StringReader(value));
		List<String[]> entries = reader.readAll();
		List<Task> tasks = new ArrayList<Task>(entries.size());
		
		for (String[] entry : entries) {
			if (entry.length < expectedEntryLength) {
				continue;
			}
			
			Task task = new Task();
			task.setName(entry[nameIndex]);
			task.setPid(entry[pidIndex]);
			task.setSessionName(entry[sessionNameIndex]);
			task.setSessionNumber(entry[sessionNumberIndex]);
			task.setMemoryUsage(entry[memoryUsageIndex]);
			
			tasks.add(task);
		}
		
		return tasks;
	}
	
	public List<Driver> readDriverList() throws IOException {
		final int expectedEntryLength = 4;
		final int nameIndex = 0;
		final int displayNameIndex = 1;
		final int typeIndex = 2;
		final int dateIndex = 3;
		
		String value = launchTool("driverquery /FO CSV /NH"); //"VolSnap","VolSnap","Kern","8/4/2004 8:00:14 AM"
		if(value ==null) value = launchTool("lsmod"); 
		CSVReader reader = new CSVReader(new StringReader(value));
		List<String[]> entries = reader.readAll();
		List<Driver> drivers = new ArrayList<Driver>(entries.size());
		
		for (String[] entry : entries) {
			if (entry.length < expectedEntryLength) {
				continue;
			}
			
			Driver driver = new Driver();
			driver.setName(entry[nameIndex]);
			driver.setDisplayName(entry[displayNameIndex]);
			driver.setType(entry[typeIndex]);
			driver.setDate(entry[dateIndex]);
			
			drivers.add(driver);
		}
		
		return drivers;
	}
	
	public List<MacAddress> readMacAddressList() throws IOException {
		final int expectedEntryLength = 2;
		final int addressIndex = 0;
		final int transportNameIndex = 1;
		
		String value = launchTool("getmac /FO CSV /NH"); // "08-00-27-11-A1-A0","\Device\Tcpip_{FA8F3108-717E-47A9-8980-2DB851BB6127}"
		if(value == null) value = launchTool("ifconfig");
		CSVReader reader = new CSVReader(new StringReader(value));
		List<String[]> entries = reader.readAll();
		List<MacAddress> macAddresses = new ArrayList<MacAddress>(entries.size());
		
		for (String[] entry : entries) {
			if (entry.length < expectedEntryLength) {
				continue;
			}
			
			MacAddress macAddress = new MacAddress();
			macAddress.setAddress(entry[addressIndex]);
			macAddress.setTransportName(entry[transportNameIndex]);
			
			macAddresses.add(macAddress);
		}
		
		return macAddresses;
	}
	
	@SuppressWarnings("unchecked")
	public List<SystemProperty> readSystemProperties() throws IOException {
		final int expectedEntryLength = 3;
		final int namesIndex = 0;
		final int valuesIndex = 1;
		
		String value = launchTool("systeminfo /FO CSV");
		if(null == value ) value = launchTool("df -h"); //uname -aip

		CSVReader reader = new CSVReader(new StringReader(value));
		List<String[]> entries = reader.readAll();
		/*if (entries.size() < expectedEntryLength) {
			return Collections.EMPTY_LIST;
		}*/
		
		
		String[] names = entries.get(namesIndex);
		String[] values = entries.get(valuesIndex);
		
		int size = Math.min(names.length, values.length);
		
		List<SystemProperty> properties = new ArrayList<SystemProperty>(size);
		
		for (int i = 0; i < size; i++) {
			SystemProperty property = new SystemProperty();
			property.setName(names[i]);
			property.setValue(values[i]);
			
			properties.add(property);
		}
		
		return properties;
	}
}
