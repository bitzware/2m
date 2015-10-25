package com.bitzware.exm.util;

public final class ReportUtil {

	private ReportUtil() {
	}
	
	public static String getMonthName(final Integer month, final TextSource textSource) {
		return textSource.getText("text.month." + (month - 1));
	}
	
	public static String getMonthShortName(final Integer month, final TextSource textSource) {
		return textSource.getText("text.month.short." + (month - 1));
	}
	
}
