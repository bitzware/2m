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
import com.bitzware.exm.service.report.impl.EventsInRoomReportGenerator;
import com.bitzware.exm.util.TimeFormatter;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Room;


/**
 * Action class for 'events in room' report page.
 * 
 * @author finagle
 */
public class EventsInRoomReportAction extends ReportAction {

	private static final long serialVersionUID = 1L;

	private MasterServerManager masterServerManager;
	
	private List<Room> rooms;
	
	// Form values.
	private String from;
	private String to;
	private String room;
	private Boolean all;
	private String eventType;
	
	// Converted values.
	private Date vFrom;
	private Date vTo;
	private Long vRoom;
	private EventType vEventType;

	public EventsInRoomReportAction() {
		super(ReportType.EVENTS_IN_ROOM, "text.report.eventsinroom.title");
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public List<Room> getRooms() {
		return rooms;
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

	public String getRoom() {
		return room;
	}

	public void setRoom(final String room) {
		this.room = room;
	}

	@Override
	public String execute() throws Exception {
		String result = super.execute();
		if (result != null) {
			return result;
		}
		
		rooms = masterServerManager.getRooms();
		
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
				message = getText("text.report.eventsinroom.error.date");
				messageType = "error";
				return false;
			}
		}
		
		if (!StringUtils.isEmpty(to)) {
			try {
				vTo = dateFormat.parse(to);
			} catch (ParseException e) {
				message = getText("text.report.eventsinroom.error.date");
				messageType = "error";
				return false;
			}
		}
		
		if (room == null && !Boolean.TRUE.equals(all)) {
			message = getText("text.report.eventsinroom.error.room");
			messageType = "error";
			return false;
		} else {
			try {
				vRoom = Long.valueOf(room);
			} catch (NumberFormatException e) {
				message = getText("text.report.eventsinroom.error.room");
				messageType = "error";
				return false;
			}
		}
		
		vEventType = readEventType(eventType);
		
		return true;
	}

	@Override
	protected boolean generateReport(final String reportId) {
		EventsInRoomReportGenerator generator =
			ServiceFactory.getEventsInRoomReportGenerator();
		
		generator.setReportId(reportId);
		generator.setReportProperties(getReportProperties());
		generator.setOutputFormat(vOutputFormat);
		
		generator.setFrom(vFrom);
		generator.setTo(vTo);
		
		generator.setEventType(vEventType);
		
		if (Boolean.TRUE.equals(all)) {
			generator.setRoomId(null);
		} else {
			generator.setRoomId(vRoom);
		}
		
		generator.start();
		
		return true;
	}

}
