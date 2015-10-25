package com.bitzware.exm.action.panel;

import java.util.List;

import org.apache.log4j.Logger;


import com.bitzware.exm.model.PagerModel;
import com.bitzware.exm.model.PagerSource;
import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportProperties;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.ReportManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action class for the 'display reports' panel.
 * 
 * @author finagle
 */
public class DisplayReportsAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private class ReportsDataSource implements PagerSource<ReportProperties> {

		@Override
		public List<ReportProperties> getData(final int offset, final int limit) {
			return reportManager.getReports(offset, limit);
		}

		@Override
		public int getSize() {
			return reportManager.getReportsAmount();
		}
		
	}
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private ReportManager reportManager;
	
	private PagerModel<ReportProperties> reports;
	
	private Integer pageSize;
	private String page;
	
	public ReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(final ReportManager reportManager) {
		this.reportManager = reportManager;
	}

	public PagerModel<ReportProperties> getReports() {
		return reports;
	}

	public void setReports(final PagerModel<ReportProperties> reports) {
		this.reports = reports;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getPage() {
		return page;
	}

	public void setPage(final String page) {
		this.page = page;
	}

	@Override
	public String execute() throws Exception {
		reports = new PagerModel<ReportProperties>(pageSize, new ReportsDataSource());
		int vPage = 0;
		if (page != null) {
			try {
				vPage = Integer.valueOf(page);
			} catch (NumberFormatException e) {
				logger.warn("Invalid page value: " + page);
			}
		}
		reports.setPage(vPage);
		
		return SUCCESS;
	}
	
	public String getReportTypeText(final ReportType type) {
		return getText("text.report.type." + type.toString());
	}
	
	public String getOutputFormatText(final ReportOutputFormat format) {
		return getText("text.report.outputformat." + format.toString());
	}

}
