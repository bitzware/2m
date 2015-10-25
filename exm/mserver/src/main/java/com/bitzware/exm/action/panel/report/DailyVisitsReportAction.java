package com.bitzware.exm.action.panel.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.service.report.impl.DailyVisitsReportGenerator;


/**
 * Action class for 'daily visits' report page.
 * 
 * @author finagle
 */
public class DailyVisitsReportAction extends ReportAction {

	private static final long serialVersionUID = 1L;

	private MasterServerManager masterServerManager;
	
	// Form values.
	private String from;
	private String to;
	private Boolean chart;
	
	// Converted values.
	private Date vFrom;
	private Date vTo;

	public DailyVisitsReportAction() {
		super(ReportType.DAILY_VISITS, "text.report.dailyvisits.title");
	}

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(final String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(final String to) {
		this.to = to;
	}

	public Boolean getChart() {
		return chart;
	}

	public void setChart(final Boolean chart) {
		this.chart = chart;
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
			from = StringUtils.EMPTY;
			to = StringUtils.EMPTY;
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
		try {
			if (!StringUtils.isEmpty(from)) {
				vFrom = dateFormat.parse(from);
			}
		} catch (ParseException e) {
			message = getText("text.report.dailyvisits.error.from");
			messageType = "error";
			return false;
		}
		
		vTo = null;
		try {
			if (!StringUtils.isEmpty(to)) {
				vTo = dateFormat.parse(to);
			}
		} catch (ParseException e) {
			message = getText("text.report.dailyvisits.error.to");
			messageType = "error";
			return false;
		}

		return true;
	}

	@Override
	protected boolean generateReport(final String reportId) {
		DailyVisitsReportGenerator generator =
			ServiceFactory.getDailyVisitsReportGenerator();
		
		generator.setReportId(reportId);
		generator.setReportProperties(getReportProperties());
		generator.setOutputFormat(vOutputFormat);
		
		generator.setFrom(vFrom);
		generator.setTo(vTo);
		generator.setChart(Boolean.TRUE.equals(chart));
		
		generator.start();
		
		return true;
	}

}
