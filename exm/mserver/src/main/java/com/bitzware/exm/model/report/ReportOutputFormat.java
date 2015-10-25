package com.bitzware.exm.model.report;


/**
 * Report output formats.
 * 
 * @author finagle
 */
public enum ReportOutputFormat {
	CSV(ReportOutputType.PLAIN, "csv"),
	DOCX(ReportOutputType.NORMAL, "docx"),
	ODS(ReportOutputType.SPREADSHEET, "ods"),
	ODT(ReportOutputType.NORMAL, "odt"),
	PDF(ReportOutputType.NORMAL, "pdf"),
	RTF(ReportOutputType.NORMAL, "rtf"),
	XLS(ReportOutputType.SPREADSHEET, "xls"),
	XLSX(ReportOutputType.SPREADSHEET, "xlsx"),
	XML(ReportOutputType.PLAIN, "xml");
	
	private final ReportOutputType type;
	private final String extension;

	private ReportOutputFormat(final ReportOutputType type, final String extension) {
		this.type = type;
		this.extension = extension;
	}

	public ReportOutputType getType() {
		return type;
	}

	public String getExtension() {
		return extension;
	}
	
}
