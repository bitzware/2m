package com.bitzware.exm.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;

import com.bitzware.exm.dao.ServerPropertyDao;
import com.bitzware.exm.dao.StationDao;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.service.MasterDuplicateManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.visitordb.model.Station;


public class MasterDuplicateManagerImpl implements MasterDuplicateManager {

	private class PresentationFileFilter implements IOFileFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(presentationExt);
		}

		public boolean accept(File file) {
			return file.isFile() && file.getName().endsWith(presentationExt);
		}
		
	}
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private final String separator = ".";
	private final String presentationExt = ".pr";
	
	private ConfigManager configManager;
	private ServerPropertyDao serverPropertyDao;
	private StationDao stationDao;
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public ServerPropertyDao getServerPropertyDao() {
		return serverPropertyDao;
	}

	public void setServerPropertyDao(ServerPropertyDao serverPropertyDao) {
		this.serverPropertyDao = serverPropertyDao;
	}

	public StationDao getStationDao() {
		return stationDao;
	}

	public void setStationDao(StationDao stationDao) {
		this.stationDao = stationDao;
	}

	@SuppressWarnings("unchecked")
	public void init() {
		// Delete all presentations remaining from the previous session.
		File presentationDir = getPresentationDir();
		
		Collection<File> listFiles = FileUtils.listFiles(
				presentationDir,
				new PresentationFileFilter(),
				FalseFileFilter.INSTANCE);
		
		for (File file : listFiles) {
			FileUtils.deleteQuietly(file);
		}
	}
	
	public String downloadPresentation(Long stationId, Date timestamp) {
		logger.debug("Downloading presentation...");
		
		final Station station = stationDao.getStationById(stationId);
		
		if (station == null || station.getIpAddress() == null) {
			logger.error("Invalid station.");
			return null;
		}
		
		File presentationDir = getPresentationDir();
		
		String sStationId = String.valueOf(stationId);
		String sTimestamp = String.valueOf(timestamp.getTime());
		
		final String outputFileName = ExmUtil.concat(
				sStationId, separator, sTimestamp, presentationExt
		);
		File outputFile = new File(presentationDir, outputFileName);
		
		final Integer port = serverPropertyDao.getIntegerProperty(
				ConfigManager.MASTER_DUPLICATION_PORT);
		final String path = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_PRESENTATION_PATH);
		final String login = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_LOGIN);
		final String password = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_PASSWORD);
		final Integer timeout = serverPropertyDao.getIntegerProperty(
				ConfigManager.MASTER_DUPLICATION_PRESENTATION_TIMEOUT);
		
		InputStream input = null;
		OutputStream output = null;
		try {
			final URL url = new URL("http", station.getIpAddress(), port, path);
			
			final String loginPassword = ExmUtil.concat(login, ":", password);
			final String loginPasswordEnc = new String(
					Base64.encodeBase64(loginPassword.getBytes()));
			
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + loginPasswordEnc);
			connection.setConnectTimeout(timeout);
//			connection.setReadTimeout(timeout);
			
			presentationDir.mkdirs();

			input = connection.getInputStream();
			output = new FileOutputStream(outputFile);
			
			IOUtils.copy(input, output);
			
			output.close();
			input.close();
		} catch (MalformedURLException e) {
			logger.error("Cannot connect to the station.", e);
		} catch (IOException e) {
			logger.error("Cannot connect to the station.", e);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		return outputFileName;
	}
	
	public InputStream getPresentation(String presentationId) {
		try {
			File presentationDir = getPresentationDir();
			File presentationFile = new File(presentationDir, presentationId);
			
			if (!presentationFile.exists()) {
				return null;
			} else {
				return new FileInputStream(presentationFile);
			}
		} catch (FileNotFoundException e) {
			logger.debug("Presentation not found.");
			return null;
		}
	}

	public void sendDownloadRequestsToRoom(Long sourceStationId, String presentationId) {
		final Integer port = serverPropertyDao.getIntegerProperty(
				ConfigManager.MASTER_DUPLICATION_PORT);
		final String presentationPath = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_PRESENTATION_REQUEST_PATH);
		final String propertiesPath = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_PROPERTIES_REQUEST_PATH);
		final String login = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_LOGIN);
		final String password = serverPropertyDao.getStringProperty(
				ConfigManager.MASTER_DUPLICATION_PASSWORD);
		final Integer presentationTimeout = serverPropertyDao.getIntegerProperty(
				ConfigManager.MASTER_DUPLICATION_PRESENTATION_REQUEST_TIMEOUT);
		final Integer propertiesTimeout = serverPropertyDao.getIntegerProperty(
				ConfigManager.MASTER_DUPLICATION_PROPERTIES_REQUEST_TIMEOUT);
		
		Set<Station> stations = stationDao.getStationsFromRoom(sourceStationId);
		
		URLConnection connection = null;
		
		InputStream input = null;
		
		Station sourceStation = stationDao.getStationById(sourceStationId);
		
		if (sourceStation == null) {
			logger.error("Invalid station: " + sourceStationId);
			return;
		}
		
		for (Station station : stations) {
			if (sourceStationId.equals(station.getId())) {
				// If it is a source station - skip.
				continue;
			}
			
			try {
				String fullPath = ExmUtil.concat(
						presentationPath,
						"?presentationId=",
						URLEncoder.encode(presentationId, "UTF-8"));
				URL url = new URL("http", station.getIpAddress(), port, fullPath);
				
				final String loginPassword = ExmUtil.concat(login, ":", password);
				final String loginPasswordEnc =
					new String(Base64.encodeBase64(loginPassword.getBytes()));
				
				if (logger.isDebugEnabled()) {
					logger.debug("Connecting to station: " + station.getIpAddress());
				}
				
				connection = url.openConnection();
				connection.setRequestProperty("Authorization", "Basic " + loginPasswordEnc);
				connection.setConnectTimeout(presentationTimeout);
				connection.setReadTimeout(presentationTimeout);
				input = connection.getInputStream();
				input.close();
				input = null;
				
				String sourceAddress = ExmUtil.concat(
						sourceStation.getIpAddress(), ":", String.valueOf(port));
				fullPath = ExmUtil.concat(
						propertiesPath,
						"?sourceAddress=",
						URLEncoder.encode(sourceAddress, "UTF-8"));
				url = new URL("http", station.getIpAddress(), port, fullPath);
				
				if (logger.isDebugEnabled()) {
					logger.debug("Connecting to station: " + station.getIpAddress());
				}
				
				connection = url.openConnection();
				connection.setRequestProperty("Authorization", "Basic " + loginPasswordEnc);
				connection.setConnectTimeout(propertiesTimeout);
				connection.setReadTimeout(propertiesTimeout);
				input = connection.getInputStream();
				input.close();
				input = null;				
			} catch (MalformedURLException e) {
				logger.warn("Cannot connect to the station: " + station.getIpAddress(), e);
			} catch (IOException e) {
				logger.warn("Cannot connect to the station: " + station.getIpAddress(), e);
			} finally {
				IOUtils.closeQuietly(input);
			}
		}
	}

	private File getPresentationDir() {
		final String presentationDirName = configManager.getStringValue(
				ConfigManager.MASTER_PRESENTATION_DIR);
		return new File(presentationDirName);
	}
	
}
