package com.bitzware.exm.action.panel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


import com.bitzware.exm.service.ReportManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action that checks if the specified report is ready.
 * 
 * @author finagle
 */
public class IsReportReadyAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private static final byte[] GENERATING = "GENERATING".getBytes();
	private static final byte[] READY = "READY".getBytes();
	private static final byte[] FAILED = "FAILED".getBytes();
	private static final byte[] ERROR = "ERROR".getBytes();
	
	private ReportManager reportManager;
	
	private String reportId;
	
	/**
	 * Data returned by the request.
	 */
	private InputStream value;
	
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

	public InputStream getValue() {
		return value;
	}

	public void setValue(final InputStream value) {
		this.value = value;
	}

	@Override
	public String execute() throws Exception {
		if (reportId != null && reportManager.isReady(reportId)) {
			value = new ByteArrayInputStream(READY);
		} else if (reportId != null && reportManager.failed(reportId)) {
			value = new ByteArrayInputStream(FAILED);
		} else if (reportId != null) {
			value = new ByteArrayInputStream(GENERATING);
		} else {
			value = new ByteArrayInputStream(ERROR);
		}
		
		return SUCCESS;
	}
	
}
