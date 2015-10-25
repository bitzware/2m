package com.bitzware.exm.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextSourceCache {
	
	private final Map<String, TextSource> textSources = new HashMap<String, TextSource>();
	private final Object textSourcesMutex = new Object();
	
	private final String resourcePath;

	public TextSourceCache(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public TextSource getTextSource(String language) {
		synchronized (textSourcesMutex) {
			TextSource result = textSources.get(language);
			
			if (result != null) {
				return result;
			} else {
				result = new TextSource(resourcePath, new Locale(language));
				textSources.put(language, result);
				
				return result;
			}
		}
	}

}
