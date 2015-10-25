package com.bitzware.exm.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextSource {
	
	private ResourceBundle bundle;
	private Locale locale;
	
	public TextSource(final String bundlePath) {
		init(bundlePath, Locale.getDefault());
	}
	
	public TextSource(final String bundlePath, final Locale locale) {
		init(bundlePath, locale);
	}
	
	public String getText(final String key) {
		return bundle.getString(key);
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	private void init(final String bundlePath, final Locale newLocale) {
		this.bundle = ResourceBundle.getBundle(bundlePath, newLocale);
		this.locale = newLocale;
	}

}
