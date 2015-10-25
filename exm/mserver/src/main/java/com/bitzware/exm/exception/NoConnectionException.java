package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

public class NoConnectionException extends MultimediaStationException {

	private static final long serialVersionUID = 1L;

	public NoConnectionException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public NoConnectionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoConnectionException(final String message) {
		super(message);
	}

	public NoConnectionException(final Throwable cause) {
		super(cause);
	}

}
