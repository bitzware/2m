package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

public class ReportException extends MultimediaStationException {

	private static final long serialVersionUID = 1L;

	public ReportException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public ReportException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ReportException(final String message) {
		super(message);
	}

	public ReportException(final Throwable cause) {
		super(cause);
	}

}
