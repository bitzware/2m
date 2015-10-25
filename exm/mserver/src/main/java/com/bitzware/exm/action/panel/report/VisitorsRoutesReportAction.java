package com.bitzware.exm.action.panel.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.report.impl.VisitorsRoutesReportGenerator;
import com.bitzware.exm.util.TimeFormatter;
import com.bitzware.exm.visitordb.model.EventType;


/**
 * Action class for 'visitors' routes' report page.
 * 
 * @author finagle
 */
public class VisitorsRoutesReportAction extends ReportAction {

	private static final long serialVersionUID = 1L;

	private MasterServerManager masterServerManager;
	
	// Form values.
	private String date;
	private String eventType;
	
	// Converted values.
	private Date vDate;
	private EventType vEventType;

	public VisitorsRoutesReportAction() {
		super(ReportType.VISITORS_ROUTES, "text.report.visitorsroutes.title");
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
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
			date = new TimeFormatter(this).formatDate(new Date());
		}
		
		return SUCCESS;
	}

	@Override
	protected boolean validateInput() {
		if (!super.validateInput()) {
			return false;
		}
		
		DateFormat dateFormat = new SimpleDateFormat(getText("format.date.java"));
		
		if (date == null) {
			message = getText("text.report.visitorsroutes.error.date");
			messageType = "error";
			return false;
		}
		
		try {
			vDate = dateFormat.parse(date);
		} catch (ParseException e) {
			message = getText("text.report.visitorsroutes.error.date");
			messageType = "error";
			return false;
		}
		
		vEventType = readEventType(eventType);
		
		return true;
	}

	@Override
	protected boolean generateReport(final String reportId) {
		VisitorsRoutesReportGenerator generator =
			ServiceFactory.getVisitorsRoutesReportGenerator();
		
		generator.setReportId(reportId);
		generator.setReportProperties(getReportProperties());
		generator.setOutputFormat(vOutputFormat);
		
		generator.setDate(vDate);
		
		generator.setEventType(vEventType);
		
		generator.start();
		
		return true;
	}

}
