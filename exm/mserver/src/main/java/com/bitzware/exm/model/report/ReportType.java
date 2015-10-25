package com.bitzware.exm.model.report;

public enum ReportType {

	EVENTS_ON_STATION("eos"),
	EVENTS_IN_ROOM("eir"),
	EVENTS_ON_FLOOR("eof"),
	VISITORS_ROUTES("vr"),
	POPULAR_STATIONS("ps"),
	DAILY_VISITS("dv");
	
	private final String code;

	private ReportType(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
}
