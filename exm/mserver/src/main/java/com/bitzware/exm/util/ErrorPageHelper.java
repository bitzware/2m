package com.bitzware.exm.util;

import java.util.Locale;

import org.apache.log4j.Logger;

public class ErrorPageHelper {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	private TextSource textSource;
	
	public ErrorPageHelper() {
	}
	
	public void setLocale(Locale locale) {
		textSource = new TextSource("/com.bitzware.exm/util/error", locale);		
	}

	public String getText(String key) {
		return textSource.getText(key);
	}
	
	public void logException(Throwable t) {
		logger.error("Exception during request.", t);
	}
	
}
