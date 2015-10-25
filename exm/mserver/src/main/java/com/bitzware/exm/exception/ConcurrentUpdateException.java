package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

public class ConcurrentUpdateException extends MultimediaStationException {

	private static final long serialVersionUID = 1L;

	public ConcurrentUpdateException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public ConcurrentUpdateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConcurrentUpdateException(final String message) {
		super(message);
	}

	public ConcurrentUpdateException(final Throwable cause) {
		super(cause);
	}
	
}
