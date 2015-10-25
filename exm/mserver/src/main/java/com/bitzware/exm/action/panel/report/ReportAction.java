package com.bitzware.exm.action.panel.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportParameters;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.ReportManager;
import com.bitzware.exm.util.EnumComparator;
import com.bitzware.exm.util.TextSourceCache;
import com.bitzware.exm.util.TimeFormatter;
import com.bitzware.exm.visitordb.model.EventType;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Base class for report action classes.
 * 
 * @author finagle
 */
/**
 * @author finagle
 *
 */
public abstract class ReportAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	public static class OutputFormatData {
		private Integer index;
		private String description;
		
		public Integer getIndex() {
			return index;
		}
		
		public void setIndex(final Integer index) {
			this.index = index;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}
		
	}
	
	public static class LanguageData {
		private String shortName;
		private String description;
		
		public String getShortName() {
			return shortName;
		}
		
		public void setShortName(final String shortName) {
			this.shortName = shortName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}
		
	}
	
	public static class EventTypeData {
		private String key;
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	private static final String ALL_EVENTS = "all";
	private static final List<EventTypeData> EVENT_TYPES;
	
	static {
		EventType[] eventTypesArr = EventType.values();
		Arrays.sort(eventTypesArr, new EnumComparator<EventType>());
		
		List<EventTypeData> eventTypesList = new ArrayList<EventTypeData>(
				eventTypesArr.length + 1);
		
		EventTypeData allTypesEntry = new EventTypeData();
		allTypesEntry.setKey(ALL_EVENTS);
		allTypesEntry.setValue(StringUtils.EMPTY);
		eventTypesList.add(allTypesEntry);
		
		for (EventType type : eventTypesArr) {
			EventTypeData entry = new EventTypeData();
			entry.setKey(String.valueOf(type.ordinal()));
			entry.setValue(type.name());
			
			eventTypesList.add(entry);
		}
		
		EVENT_TYPES = Collections.unmodifiableList(eventTypesList);
	}

	private final ReportType reportType;
	private final String titleKey;
	
	private int checkReportInterval;
	
	private ReportManager reportManager;
	
	private TextSourceCache textSourceCache;
	
	private List<LanguageData> languages;
	private List<String> languageCodes;
	
	protected List<OutputFormatData> supportedOutputFormats;
	
	protected String message;
	protected String messageType;
	
	private String reportId;
	
	private String action;
	private String language;
	private String outputformat;
	
	protected ReportOutputFormat vOutputFormat;
	
	protected final String generateAction = "generate";
	
	protected final String generateResult = "GENERATE";

	public ReportAction(final ReportType reportType, final String titleKey) {
		this.reportType = reportType;
		this.titleKey = titleKey;
	}
	
	public List<EventTypeData> getEventTypes() {
		return ReportAction.EVENT_TYPES;
	}

	public int getCheckReportInterval() {
		return checkReportInterval;
	}

	public void setCheckReportInterval(final int checkReportInterval) {
		this.checkReportInterval = checkReportInterval;
	}

	public ReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(final ReportManager reportManager) {
		this.reportManager = reportManager;
	}

	public TextSourceCache getTextSourceCache() {
		return textSourceCache;
	}

	public void setTextSourceCache(TextSourceCache textSourceCache) {
		this.textSourceCache = textSourceCache;
	}

	public ReportParameters getReportProperties() {
		ReportParameters properties = new ReportParameters();
		properties.setTextSource(textSourceCache.getTextSource(language));
		properties.setTimeFormatter(new TimeFormatter(properties.getTextSource()));
		
		return properties;
	}

	public String getTitle() {
		return getText(titleKey);
	}
	
	public void setLanguageCodes(final List<String> languageCodes) {
		this.languageCodes = languageCodes;
	}
	
	public String getDefaultLanguage() {
		return getText("text.report.language.default");
	}

	/**
	 * Returns a list of languages (short name and localized description).
	 */
	public List<LanguageData> getLanguages() {
		languages = new ArrayList<LanguageData>(languageCodes.size());
		
		for (String languageCode : languageCodes) {
			LanguageData languageEntry = new LanguageData();
			languageEntry.shortName = languageCode;
			languageEntry.description = getText("text.report.language." + languageCode);
			
			languages.add(languageEntry);
		}
		
		return languages;
	}
	
	public List<OutputFormatData> getSupportedOutputFormats() {
		return supportedOutputFormats;
	}

	public void setSupportedOutputFormats(
			final List<OutputFormatData> supportedOutputFormats) {
		this.supportedOutputFormats = supportedOutputFormats;
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

	public String getReportId() {
		return reportId;
	}

	public void setReportId(final String reportId) {
		this.reportId = reportId;
	}

	public String getAction() {
		return action;
	}
	
	public void setAction(final String action) {
		this.action = action;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public String getOutputformat() {
		return outputformat;
	}

	public void setOutputformat(final String outputformat) {
		this.outputformat = outputformat;
	}

	@Override
	public String execute() throws Exception {
		if (language == null) {
			language = getDefaultLanguage();
		}
		
		if (action != null) {
			if (generateAction.equals(action)) {
				if (validateInput()) {
					String newReportId = reportManager.createReport(reportType);
					if (!generateReport(newReportId)) {
						message = getText("text.report.error");
						messageType = "error";
					}
					
					this.reportId = newReportId;
				}
			}
		}
		
		return null;
	}

	/**
	 * Adds a supported format entry to the specified collection.
	 */
	protected void addSupportedOutputFormat(final Collection<OutputFormatData> collection,
			final ReportOutputFormat outputFormat) {
		OutputFormatData entry = new OutputFormatData();
		entry.index = outputFormat.ordinal();
		entry.description = getText("text.report.outputformat." + outputFormat.toString());
		
		collection.add(entry);
	}

	/**
	 * Creates a list of supported formats (index and description displayed on the page)
	 * from the specified array.
	 */
	protected List<OutputFormatData> createOutputFormatList(
			final ReportOutputFormat[] outputFormats) {
		
		List<OutputFormatData> result = new ArrayList<OutputFormatData>(outputFormats.length);
		
		for (ReportOutputFormat outputFormat : outputFormats) {
			addSupportedOutputFormat(result, outputFormat);
		}
		
		return result;
	}
	
	protected boolean validateInput() {
		if (language == null) {
			message = getText("text.report.error.language");
			messageType = "error";
			return false;
		}
		if (outputformat == null) {
			message = getText("text.report.error.outputformat");
			messageType = "error";
			return false;
		} else {
			try {
				Integer iOutputFormat = Integer.valueOf(outputformat);
				
				if (iOutputFormat < 0
						|| iOutputFormat >= ReportOutputFormat.values().length) {
					message = getText("text.report.error.outputformat");
					messageType = "error";
					return false;
				}
				
				vOutputFormat = ReportOutputFormat.values()[iOutputFormat];
			} catch (NumberFormatException e) {
				message = getText("text.report.error.outputformat");
				messageType = "error";
				return false;
			}
		}
		
		return true;
	}
	
	protected EventType readEventType(String value) {
		if (value != null && !ReportAction.ALL_EVENTS.equals(value)) {
			try {
				int iEventType = Integer.valueOf(value);
				EventType[] eventTypes = EventType.values();
				if (iEventType >= 0 && iEventType < eventTypes.length) {
					return eventTypes[iEventType];
				}
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}
	
	protected abstract boolean generateReport(String reportId);

}
