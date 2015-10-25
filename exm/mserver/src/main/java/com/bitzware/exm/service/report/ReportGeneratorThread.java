package com.bitzware.exm.service.report;

import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.bitzware.exm.exception.ReportException;
import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportParameters;
import com.bitzware.exm.model.report.ReportSource;
import com.bitzware.exm.service.ReportManager;
import com.bitzware.exm.visitordb.model.EventType;


/**
 * Base class for threads that generate reports.
 * 
 * @author finagle
 */
public abstract class ReportGeneratorThread extends Thread {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	protected ReportManager reportManager;
	
	protected ReportSource reportSource;
	protected Resource styleTemplateSource;
	protected Resource logoImageSource;
	
	protected String reportId;
	protected ReportParameters reportProperties;
	protected ReportOutputFormat outputFormat;
	
	public ReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(final ReportManager reportManager) {
		this.reportManager = reportManager;
	}

	public ReportSource getReportSource() {
		return reportSource;
	}

	public void setReportSource(ReportSource reportSource) {
		this.reportSource = reportSource;
	}

	public Resource getStyleTemplateSource() {
		return styleTemplateSource;
	}

	public void setStyleTemplateSource(final Resource styleTemplateSource) {
		this.styleTemplateSource = styleTemplateSource;
	}

	public Resource getLogoImageSource() {
		return logoImageSource;
	}

	public void setLogoImageSource(final Resource logoImageSource) {
		this.logoImageSource = logoImageSource;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(final String reportId) {
		this.reportId = reportId;
	}

	public ReportParameters getReportProperties() {
		return reportProperties;
	}

	public void setReportProperties(final ReportParameters reportProperties) {
		this.reportProperties = reportProperties;
	}

	public ReportOutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(final ReportOutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	@Override
	public final void run() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Generating report.");
			}

			reportProperties.setStyleTemplateInput(styleTemplateSource.getInputStream());
			if (outputFormat.getType().isDisplayLogo()) {
				reportProperties.setLogoImageInput(logoImageSource.getInputStream());
			}
			
			writeReport(
					getReportSource(outputFormat, reportSource),
					getCompiledReport(outputFormat, reportSource)
			);
			
		} catch (Exception e) {
			logger.error("Report generation failed.", e);
		} finally {
			IOUtils.closeQuietly(reportProperties.getStyleTemplateInput());
			IOUtils.closeQuietly(reportProperties.getLogoImageInput());
		}
	}

	protected abstract void writeReport(Resource reportSchemaSource, Resource compiledReportSource)
	throws IOException, JRException, ReportException;

	/**
	 * Returns an input stream to the specified report schema.
	 */
	private Resource getReportSource(final ReportOutputFormat reqOutputFormat,
			final ReportSource reqSource)
	throws IOException {
		
		switch (reqOutputFormat.getType()) {
		case NORMAL:
			return reqSource.getNormal();
		case SPREADSHEET:
			return reqSource.getSpreadsheet();
		case PLAIN:
			return reqSource.getPlain();
		default:
			return null;
		}
	}
	
	/**
	 * Returns an input stream to the specified compiled report schema.
	 */
	private Resource getCompiledReport(final ReportOutputFormat reqOutputFormat,
			final ReportSource reqSource)
	throws IOException {
		
		switch (reqOutputFormat.getType()) {
		case NORMAL:
			return reqSource.getCompiledNormal();
		case SPREADSHEET:
			return reqSource.getCompiledSpreadsheet();
		case PLAIN:
			return reqSource.getCompiledPlain();
		default:
			return null;
		}
	}
}
