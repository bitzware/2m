package com.bitzware.exm.action.panel.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.report.impl.EventsOnStationReportGenerator;
import com.bitzware.exm.util.TimeFormatter;
import com.bitzware.exm.util.TransformCollection;
import com.bitzware.exm.util.Transformer;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Station;


/**
 * Action class for 'events on station' report page.
 * 
 * @author finagle
 */
public class EventsOnStationReportAction extends ReportAction {

	private static final long serialVersionUID = 1L;

	public static class StationInfo {
		private Station station;
		
		public StationInfo(final Station station) {
			this.station = station;
		}

		public Long getId() {
			return station.getId();
		}
		
		public String getDescription() {
			final String openPar = " (";
			final char closePar = ')';
			
			String name = station.getName();
			String roomName = station.getRoom().getName();
			
			StringBuilder sb = new StringBuilder(name.length() + openPar.length()
					+ roomName.length() + 1);
			sb.append(name);
			sb.append(openPar);
			sb.append(roomName);
			sb.append(closePar);
			
			return sb.toString();
		}
	}
	
	private final Transformer<Station, StationInfo> stationTransformer =
		new Transformer<Station, StationInfo>() {

		@Override
		public StationInfo transform(final Station source) {
			return new StationInfo(source);
		}
		
	};
	
	private MasterServerManager masterServerManager;
	
	private Iterable<StationInfo> stations;

	// Form values.
	private String from;
	private String to;
	private String station;
	private Boolean all = Boolean.FALSE;
	private String eventType;
	
	// Converted values.
	private Date vFrom;
	private Date vTo;
	private Long vStation;
	private EventType vEventType;

	public EventsOnStationReportAction() {
		super(ReportType.EVENTS_ON_STATION, "text.report.eventsonstation.title");
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public Iterable<StationInfo> getStations() {
		return stations;
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

	public String getStation() {
		return station;
	}

	public void setStation(final String station) {
		this.station = station;
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
		
		stations = new TransformCollection<Station, StationInfo>(
				masterServerManager.getStationsWithRooms(), stationTransformer);
		
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
		
		vFrom  = null;
		vTo = null;
		
		if (!StringUtils.isEmpty(from)) {
			try {
				vFrom = dateFormat.parse(from);
			} catch (ParseException e) {
				message = getText("text.report.eventsonstation.error.date");
				messageType = "error";
				return false;
			}
		}
		
		if (!StringUtils.isEmpty(to)) {
			try {
				vTo = dateFormat.parse(to);
			} catch (ParseException e) {
				message = getText("text.report.eventsonstation.error.date");
				messageType = "error";
				return false;
			}
		}
		
		if (station == null && !Boolean.TRUE.equals(all)) {
			message = getText("text.report.eventsonstation.error.station");
			messageType = "error";
			return false;
		} else {
			try {
				vStation = Long.valueOf(station);
			} catch (NumberFormatException e) {
				message = getText("text.report.eventsonstation.error.station");
				messageType = "error";
				return false;
			}
		}
		
		vEventType = readEventType(eventType);
		
		return true;
	}

	@Override
	protected boolean generateReport(final String reportId) {
		EventsOnStationReportGenerator generator =
			ServiceFactory.getEventsOnStationReportGenerator();
		
		generator.setReportId(reportId);
		generator.setReportProperties(getReportProperties());
		generator.setOutputFormat(vOutputFormat);
		
		generator.setFrom(vFrom);
		generator.setTo(vTo);
		if (Boolean.TRUE.equals(all)) {
			generator.setStationId(null);
		} else {
			generator.setStationId(vStation);
		}
		
		generator.setEventType(vEventType);
		// FIXME: Remove
		Logger.getLogger(this.getClass()).info("Event type: " + vEventType);
		
		generator.start();
		
		return true;
	}

}
