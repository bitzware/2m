package com.bitzware.exm.action.panel.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.report.impl.EventsOnFloorReportGenerator;
import com.bitzware.exm.util.TimeFormatter;
import com.bitzware.exm.visitordb.model.EventType;


/**
 * Action class for 'events on floor' report page.
 * 
 * @author finagle
 */
public class EventsOnFloorReportAction extends ReportAction {

	private static final long serialVersionUID = 1L;

	private MasterServerManager masterServerManager;
	
	private List<String> floors;
	
	// Form values.
	private String from;
	private String to;
	private String floor;
	private Boolean all;
	private String eventType;
	
	// Converted values.
	private Date vFrom;
	private Date vTo;
	private EventType vEventType;

	public EventsOnFloorReportAction() {
		super(ReportType.EVENTS_ON_FLOOR, "text.report.eventsonfloor.title");
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public List<String> getFloors() {
		return floors;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(final String floor) {
		this.floor = floor;
	}

	public Boolean getAll() {
		return all;
	}

	public void setAll(Boolean all) {
		this.all = all;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@Override
	public String execute() throws Exception {
		String result = super.execute();
		if (result != null) {
			return result;
		}
		
		floors = masterServerManager.getFloors();
		
		supportedOutputFormats = createOutputFormatList(new ReportOutputFormat[] {
				ReportOutputFormat.CSV,
				ReportOutputFormat.DOCX,
				ReportOutputFormat.ODS,
				ReportOutputFormat.ODT,
				ReportOutputFormat.RTF,
				ReportOutputFormat.XLS,
				ReportOutputFormat.XLSX
		}
		);
		
		// If report id = null - it means that the user just entered the page, get
		// current date.
		if (getReportId() == null) {
			from = new TimeFormatter(this).formatDate(new Date());
			to = new TimeFormatter(this).formatDate(new Date());
		}
		
		return SUCCESS;
	}

	@Override
	protected boolean validateInput() {
		if (!super.validateInput()) {
			return false;
		}
		
		DateFormat dateFormat = new SimpleDateFormat(getText("format.date.java"));
		
		vFrom = null;
		vTo = null;
		
		if (!StringUtils.isEmpty(from)) {
			try {
				vFrom = dateFormat.parse(from);
			} catch (ParseException e) {
				message = getText("text.report.eventsonfloor.error.date");
				messageType = "error";
				return false;
			}
		}
		
		if (!StringUtils.isEmpty(to)) {
			try {
				vTo = dateFormat.parse(to);
			} catch (ParseException e) {
				message = getText("text.report.eventsonfloor.error.date");
				messageType = "error";
				return false;
			}
		}
		
		if (floor == null && !Boolean.TRUE.equals(all)) {
			message = getText("text.report.eventsonfloor.error.floor");
			messageType = "error";
			return false;
		}
		
		vEventType = readEventType(eventType);
		
		return true;
	}

	@Override
	protected boolean generateReport(final String reportId) {
		EventsOnFloorReportGenerator generator =
			ServiceFactory.getEventsOnFloorReportGenerator();
		
		generator.setReportId(reportId);
		generator.setReportProperties(getReportProperties());
		generator.setOutputFormat(vOutputFormat);
		
		generator.setFrom(vFrom);
		generator.setTo(vTo);
		
		generator.setEventType(vEventType);
		
		if (Boolean.TRUE.equals(all)) {
			generator.setFloor(null);
		} else {
			generator.setFloor(floor);
		}
		
		generator.start();
		
		return true;
	}

}
