package com.bitzware.exm.model.report;

import java.io.InputStream;
import java.util.Date;

/**
 * Report properties.
 * 
 * @author finagle
 */
public class ReportProperties {

	private String id;
	
	private ReportOutputFormat format;
	private ReportType type;
	private Date timestamp;
	private String idString;
	private String description;
	
	private InputStream reportInput;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ReportOutputFormat getFormat() {
		return format;
	}

	public void setFormat(final ReportOutputFormat format) {
		this.format = format;
	}

	public ReportType getType() {
		return type;
	}

	public void setType(final ReportType type) {
		this.type = type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getIdString() {
		return idString;
	}

	public void setIdString(final String idString) {
		this.idString = idString;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public InputStream getReportInput() {
		return reportInput;
	}

	public void setReportInput(final InputStream reportInput) {
		this.reportInput = reportInput;
	}
	
}
