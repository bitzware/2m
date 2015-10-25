package com.bitzware.exm.action.panel;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import com.bitzware.exm.model.report.ReportProperties;
import com.bitzware.exm.service.ReportManager;
import com.bitzware.exm.util.ExmUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * A Struts action that returns a report data file.
 * 
 * @author finagle
 */
public class GetReportAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	protected Logger logger = Logger.getLogger(this.getClass());
	
	private ReportManager reportManager;
	
	private String reportId;
	
	private String contentDisposition;
	private InputStream reportStream;
	
	private String message;
	private String messageType;

	public ReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(final ReportManager reportManager) {
		this.reportManager = reportManager;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(final String reportId) {
		this.reportId = reportId;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(final String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public InputStream getReportStream() {
		return reportStream;
	}

	public void setReportStream(final InputStream reportStream) {
		this.reportStream = reportStream;
	}

	public String getMessage() {
		return message;
	}

	public String getMessageType() {
		return messageType;
	}

	@Override
	public String execute() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting report: " + reportId);
		}
		
		if (reportId != null) {
			ReportProperties reportData = reportManager.getReportData(reportId);
			
			if (reportData != null) {
				String fileName = generateFileName(reportData);
				contentDisposition = ExmUtil.concat("attachment;filename=\"", fileName, "\";");
				reportStream = reportData.getReportInput();
				
				return SUCCESS;
			} 
		}
		
		message = getText("text.getreport.error.notfound");
		messageType = "error";
		
		return ERROR;
	}
	
	private String generateFileName(final ReportProperties reportData) {
		String reportCode = reportData.getType().getCode();
		String date = String.valueOf(reportData.getTimestamp().getTime());
		String extension = reportData.getFormat().getExtension();
		
		StringBuilder sb = new StringBuilder(reportCode.length() + 1 + date.length() + 1
				+ reportData.getIdString().length() + 1 + extension.length());
		sb.append(reportCode).append('.').append(date).append('.');
		if (!StringUtils.isEmpty(reportData.getIdString())) {
			sb.append(reportData.getIdString()).append('.');
		}
		sb.append(extension);
		
		return sb.toString();
	}
	
}
