package com.bitzware.exm.dao.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bitzware.exm.dao.ReportDao;
import com.bitzware.exm.dao.ServerPropertyDao;
import com.bitzware.exm.exception.ReportException;
import com.bitzware.exm.exception.UnexpectedDataException;
import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportProperties;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.util.ExmUtil;
import com.bitzware.exm.util.IdGenerator;


/**
 * Implementation of 'ReportDao' that stores report on the hard drive. Each report has
 * unique id.
 * 
 * @author finagle
 */
public class ReportDaoImpl implements ReportDao {

	private static final class SuffixFilter implements FilenameFilter {
		private final String suffix;

		private SuffixFilter(final String suffix) {
			this.suffix = suffix;
		}

		
		public boolean accept(final File dir, final String name) {
			return name.endsWith(suffix);
		}
	}
	
	private static class ReportFile implements Comparable<ReportFile> {
		private String id;
		private Long timestamp;
		
		public ReportFile(final String id, final Long timestamp) {
			this.id = id;
			this.timestamp = timestamp;
		}

		
		public int compareTo(final ReportFile o) {
			return -timestamp.compareTo(o.timestamp);
		}
		
	}
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private final String reportExt = ".rep";
	private final String readyExt = ".ready";
	private final String failedExt = ".failed";
	private final String timestampExt = ".time";
	
	private final FilenameFilter timestampFilter = new SuffixFilter(timestampExt); 
	
	private IdGenerator idGenerator;
	private ServerPropertyDao serverPropertyDao;
	
//	private Map<ReportType, String> reportDirs;
	private File rootDir;
	
	public ServerPropertyDao getServerPropertyDao() {
		return serverPropertyDao;
	}

	public void setServerPropertyDao(ServerPropertyDao serverPropertyDao) {
		this.serverPropertyDao = serverPropertyDao;
		if (serverPropertyDao != null) {
			rootDir = new File(
					serverPropertyDao.getStringProperty(ConfigManager.MASTER_REPORT_DIR)
			);
		}
	}

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(final IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	
	public String createReport(final ReportType type) {
		try {
			// Create directory.
			rootDir.mkdirs();

			File file;
			String id;
			do {
				// Generate random report id.
				id = idGenerator.generate();

				file = propertiesFile(id);
				// Check if the id is already in use.
			} while (!file.createNewFile());
			
			return id;
		} catch (IOException e) {
			throw new ReportException("Cannot create report file.", e);
		}
	}

	
	public boolean isReady(final String id) {
		return readyFile(id).exists();
	}

	
	public boolean failed(final String id) {
		return failedFile(id).exists();
	}

	
	public ReportProperties openReportForRead(final String id) {
		BufferedReader propertiesReader = null;
		try {
			if (isReady(id)) {
				ReportProperties result = new ReportProperties();
				File propertiesFile = propertiesFile(id);
				propertiesReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8"));
				
				readPropertiesFrom(result, propertiesReader);
				result.setReportInput(new FileInputStream(reportDataFile(id)));
				
				return result;
			} else {
				logger.debug("Trying to open report which is not ready.");
				return null;
			}
		} catch (FileNotFoundException e) {
			logger.debug("Cannot open report.", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			logger.debug("Cannot open report.", e);
			return null;
		} catch (NumberFormatException e) {
			logger.debug("Cannot open report.", e);
			return null;
		} catch (UnexpectedDataException e) {
			logger.debug("Cannot open report.", e);
			return null;
		} catch (IOException e) {
			logger.debug("Cannot open report.", e);
			return null;
		} finally {
			IOUtils.closeQuietly(propertiesReader);
		}
	}

	
	public OutputStream openReportForWrite(final String id,
			final ReportProperties reportProperties) {
		BufferedWriter propertiesWriter = null;
		try {
			File propertiesFile = propertiesFile(id);
			// Save report properties.
			propertiesWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(propertiesFile), "UTF-8"));
			writePropertiesTo(reportProperties, propertiesWriter);
			
			File reportFile = reportDataFile(id);
			// Open output stream for the report data.
			FileOutputStream output = new FileOutputStream(reportFile);
			return output;
		} catch (FileNotFoundException e) {
			throw new ReportException("Cannot write report to disk.", e);
		} catch (UnsupportedEncodingException e) {
			throw new ReportException("Cannot write report to disk.", e);
		} catch (IOException e) {
			throw new ReportException("Cannot write report to disk.", e);
		} finally {
			IOUtils.closeQuietly(propertiesWriter);
		}
	}
	
	
	public void closeReport(final String id, final Date timestamp, final boolean success) {
		try {
			if (success) {
				readyFile(id).createNewFile();
				
				File timestampFile = timestampFile(id, timestamp);
				timestampFile.createNewFile();
			} else {
				failedFile(id).createNewFile();
			}
			
			
		} catch (IOException e) {
			throw new ReportException("Cannot create report file.");
		}
	}
	
	
	public int getReportsAmount() {
		return rootDir.list(timestampFilter).length;
	}

	@SuppressWarnings("unchecked")
	
	public List<ReportProperties> getReports(final int offset, final int limit) {
		String[] nameList = rootDir.list(timestampFilter);
		
		if (offset >= nameList.length) {
			return Collections.EMPTY_LIST; 
		}
		
		List<ReportFile> fileList = new ArrayList<ReportFile>(nameList.length);
		
		for (String name : nameList) {
			ReportFile reportFile = convertReportFileName(name);
			
			if (reportFile != null) {
				fileList.add(reportFile);
			}
		}
		
		if (offset >= fileList.size()) {
			return Collections.EMPTY_LIST; 
		}
		
		Collections.sort(fileList);
		
		List<ReportProperties> result = new ArrayList<ReportProperties>(limit);
		int index = 0;
		while (offset + index < fileList.size() && result.size() < limit) {
			
			ReportFile file = fileList.get(offset + index);
			BufferedReader propertiesReader = null;
			
			try {
				ReportProperties properties = new ReportProperties();
				propertiesReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(propertiesFile(file.id)), "UTF-8"));
				readPropertiesFrom(properties, propertiesReader);
				properties.setId(file.id);
				
				result.add(properties);
			} catch (NumberFormatException e) {
				logger.debug("Cannot read report properties.", e);
			} catch (UnexpectedDataException e) {
				logger.debug("Cannot read report properties.", e);
			} catch (UnsupportedEncodingException e) {
				logger.debug("Cannot read report properties.", e);
			} catch (FileNotFoundException e) {
				logger.debug("Cannot read report properties.", e);
			} catch (IOException e) {
				logger.debug("Cannot read report properties.", e);
			} finally {
				IOUtils.closeQuietly(propertiesReader);
			}
			
			index++;
		}
		
		return result;
	}

	/**
	 * Returns properties file for the specified report. This file stores report properties.
	 * It is created first and it is used to check if the id is available.
	 */
	private File propertiesFile(final String reportId) {
		return new File(rootDir, reportId);
	}
	
	/**
	 * Returns data file for the specified report. This file contains report binary data.
	 */
	private File reportDataFile(final String reportId) {
		return new File(rootDir, reportId + reportExt);
	}

	/**
	 * Returns 'ready' file for the specified report. This file is empty. If it exists,
	 * it means that the report is ready and it may be downloaded by the user.
	 */
	private File readyFile(final String reportId) {
		return new File(rootDir, reportId + readyExt);
	}
	
	/**
	 * Returns 'failed' file for the specified report. This file is empty. If it exists,
	 * it means that the report creation failed.
	 */
	private File failedFile(final String reportId) {
		return new File(rootDir, reportId + failedExt);
	}
	
	/**
	 * Returns 'timestamp' file for the specified report. This file is empty. It's name
	 * contains report timestamp to help with sorting when report list is read.
	 */
	private File timestampFile(final String reportId, final Date timestamp) {
		return new File(rootDir, ExmUtil.concat(reportId, ".",
				String.valueOf(timestamp.getTime()), timestampExt));
	}
	
	public void writePropertiesTo(final ReportProperties data, final BufferedWriter writer)
	throws IOException {
		writer.write(String.valueOf(data.getType().ordinal()));
		writer.write('\n');
		writer.write(String.valueOf(data.getFormat().ordinal()));
		writer.write('\n');
		writer.write(String.valueOf(data.getTimestamp().getTime()));
		writer.write('\n');
		writer.write(data.getIdString());
		writer.write('\n');
		writer.write(data.getDescription());
		writer.write('\n');
	}
	
	public void readPropertiesFrom(final ReportProperties data, final BufferedReader reader)
	throws IOException, NumberFormatException, UnexpectedDataException {
		String sType = reader.readLine();
		String sFormat = reader.readLine();
		String sTimestamp = reader.readLine();
		data.setIdString(reader.readLine());
		data.setDescription(reader.readLine());
		
		if (sType == null || sFormat == null || sTimestamp == null || data.getIdString() == null) {
			throw new IOException("Unexpected end of file.");
		}
		
		if (data.getDescription() == null) {
			data.setDescription(StringUtils.EMPTY);
		}
		
		Integer iType = Integer.valueOf(sType);
		if (iType < 0 || iType >= ReportType.values().length) {
			throw new UnexpectedDataException("Invalid report type.");
		}
		data.setType(ReportType.values()[iType]);
		
		Integer iFormat = Integer.valueOf(sFormat);
		if (iFormat < 0 || iFormat >= ReportOutputFormat.values().length) {
			throw new UnexpectedDataException("Invalid report output format.");
		}
		data.setFormat(ReportOutputFormat.values()[iFormat]);
		
		data.setTimestamp(new Date(Long.valueOf(sTimestamp)));
	}
	
	private ReportFile convertReportFileName(final String name) {
		int dot1Index = name.indexOf('.');
		if (dot1Index < 0 || dot1Index >= name.length() - 1) {
			return null;
		}
		int dot2Index = name.indexOf('.', dot1Index + 1);
		if (dot2Index < 0 || dot2Index >= name.length() - 1) {
			return null;
		}
		
		try {
			String id = name.substring(0, dot1Index);
			String sTimestamp = name.substring(dot1Index + 1, dot2Index);
			Long timestamp = Long.valueOf(sTimestamp);
			
			ReportFile reportFile = new ReportFile(id, timestamp);
			
			return reportFile;
		} catch (NumberFormatException e) {
			return null;
		}
	}

//	private String getDir(String rootDir, ReportType type) {
//		String typeName = type.toString();
//		StringBuilder sb = new StringBuilder(rootDir.length() + 1 + typeName.length());
//		sb.append(rootDir).append("/").append(typeName);
//		
//		return sb.toString();
//	}
}
