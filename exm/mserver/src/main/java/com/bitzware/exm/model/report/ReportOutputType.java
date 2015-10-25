package com.bitzware.exm.model.report;

/**
 * Types of the report output formats. Different types may have different report layouts.
 * 
 * @author finagle
 */
public enum ReportOutputType {
	/**
	 * Normal document.
	 */
	NORMAL(true),
	/**
	 * Plain document, without headers and footers.
	 */
	PLAIN(false),
	/**
	 * Spreadsheet document.
	 */
	SPREADSHEET(false);
	
	private final boolean displayLogo;

	private ReportOutputType(final boolean displayLogo) {
		this.displayLogo = displayLogo;
	}

	public boolean isDisplayLogo() {
		return displayLogo;
	}
	
}
