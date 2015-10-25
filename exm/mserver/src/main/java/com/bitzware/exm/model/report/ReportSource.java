package com.bitzware.exm.model.report;

import org.springframework.core.io.Resource;

/**
 * Class that contains report schemas.
 * 
 * @author finagle
 */
public class ReportSource {

	/**
	 * Normal report source code.
	 */
	private final Resource normal;
	/**
	 * Plain report source code.
	 */
	private final Resource plain;
	/**
	 * Spreadsheet report source code.
	 */
	private final Resource spreadsheet;
	
	/**
	 * Compiled normal report.
	 */
	private final Resource compiledNormal;
	/**
	 * Compiled plain report.
	 */
	private final Resource compiledPlain;
	/**
	 * Compiled spreadsheet report.
	 */
	private final Resource compiledSpreadsheet;
	
	public ReportSource(final Resource normal, final Resource plain,
			final Resource spreadsheet, final Resource compiledNormal,
			final Resource compiledPlain, final Resource compiledSpreadsheet) {
		this.normal = normal;
		this.plain = plain;
		this.spreadsheet = spreadsheet;
		this.compiledNormal = compiledNormal;
		this.compiledPlain = compiledPlain;
		this.compiledSpreadsheet = compiledSpreadsheet;
	}

	public Resource getNormal() {
		return normal;
	}

	public Resource getPlain() {
		return plain;
	}

	public Resource getSpreadsheet() {
		return spreadsheet;
	}

	public Resource getCompiledNormal() {
		return compiledNormal;
	}

	public Resource getCompiledPlain() {
		return compiledPlain;
	}

	public Resource getCompiledSpreadsheet() {
		return compiledSpreadsheet;
	}
	
}
