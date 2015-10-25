package com.bitzware.exm.exception;

import com.bitzware.exm.ws.ErrorCode;

public class JobScheduleException extends MultimediaStationException {

	private static final long serialVersionUID = 1L;

	public JobScheduleException(ErrorCode errorCode) {
		super(errorCode);
	}

	public JobScheduleException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobScheduleException(String message) {
		super(message);
	}

	public JobScheduleException(Throwable cause) {
		super(cause);
	}

}
