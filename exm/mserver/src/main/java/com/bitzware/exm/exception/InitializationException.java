package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

public class InitializationException extends MultimediaStationException {

	private static final long serialVersionUID = 1L;

	public InitializationException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public InitializationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InitializationException(final String message) {
		super(message);
	}

	public InitializationException(final Throwable cause) {
		super(cause);
	}

}
