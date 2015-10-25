package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

public class UnexpectedDataException extends MultimediaStationException {

	private static final long serialVersionUID = 1L;

	public UnexpectedDataException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public UnexpectedDataException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnexpectedDataException(final String message) {
		super(message);
	}

	public UnexpectedDataException(final Throwable cause) {
		super(cause);
	}

}
