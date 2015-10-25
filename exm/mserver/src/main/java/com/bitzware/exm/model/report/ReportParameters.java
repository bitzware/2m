package com.bitzware.exm.model.report;

import java.io.InputStream;

import com.bitzware.exm.util.TextSource;
import com.bitzware.exm.util.TimeFormatter;


/**
 * This class contains some parameters used for generating reports.
 * 
 * @author finagle
 */

public class ReportParameters {
	
	/**
	 * Provider of the localized text.
	 */
	private TextSource textSource;
	
	/**
	 * Object used to format date and time.
	 */
	private TimeFormatter timeFormatter;
	
	/**
	 * Style template.
	 */
	private InputStream styleTemplateInput;
	
	/**
	 * Logo image.
	 */
	private InputStream logoImageInput;

	public TextSource getTextSource() {
		return textSource;
	}

	public void setTextSource(final TextSource textSource) {
		this.textSource = textSource;
	}

	public TimeFormatter getTimeFormatter() {
		return timeFormatter;
	}

	public void setTimeFormatter(final TimeFormatter timeFormatter) {
		this.timeFormatter = timeFormatter;
	}

	public InputStream getStyleTemplateInput() {
		return styleTemplateInput;
	}

	public void setStyleTemplateInput(final InputStream styleTemplateInput) {
		this.styleTemplateInput = styleTemplateInput;
	}

	public InputStream getLogoImageInput() {
		return logoImageInput;
	}

	public void setLogoImageInput(final InputStream logoImageInput) {
		this.logoImageInput = logoImageInput;
	}
	
	
}
