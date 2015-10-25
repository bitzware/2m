package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

/**
 * Generic server exception.
 * 
 * @author finagle
 */
public class MultimediaStationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MultimediaStationException(final ErrorCode errorCode) {
		super();
	}

	public MultimediaStationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MultimediaStationException(final String message) {
		super(message);
	}

	public MultimediaStationException(final Throwable cause) {
		super(cause);
	}

}
