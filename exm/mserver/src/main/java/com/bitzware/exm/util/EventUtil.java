package com.bitzware.exm.util;

import java.util.Date;

import org.apache.log4j.Logger;

import com.bitzware.exm.visitordb.model.Visitor;


public final class EventUtil {

	private static final Logger LOGGER = Logger.getLogger(EventUtil.class);

	private EventUtil() {
	}

	/**
	 * Checks if all fields required for RFID timeout validation are not null.
	 */
	public static boolean checkIfValid(final Visitor visitor, final Integer rfidTimeout,
			final Integer rfidTimeOfEntry) {
		if (rfidTimeout == null) {
			LOGGER.warn("RFID timeout not set!");
			return false;
		}

		if (rfidTimeOfEntry == null) {
			LOGGER.warn("RFID time of entry not set!");
			return false;
		}

		Date validFrom = visitor.getRfidValidFrom();
		if (validFrom == null) {
			LOGGER.warn("Visitor's RFID 'valid from' field not set!");
			return false;
		}

		return true;
	}

	/**
	 * Checks if the RFID has expired.
	 * 
	 * @param validFrom RFID 'valid from' time.
	 * @param entryTime entry time of the visitor.
	 * @param currentTime current time.
	 * @param rfidTimeout RFID timeout property (in minutes).
	 * @param rfidTimeOfEntry RFID time of entry property (in minutes).
	 */
	public static boolean rfidExpired(final Date validFrom, final Date entryTime,
			final Date currentTime, final int rfidTimeout, final int rfidTimeOfEntry,
			final Boolean expired) {
		if (currentTime == null || Boolean.TRUE.equals(expired)) {
			return true;
		}

		if (entryTime == null) {
			// This is the first read of this RFID - check if the visitor is not
			// too late.
			return (currentTime.getTime() > validFrom.getTime()
					+ minutesToMilliseconds(rfidTimeOfEntry));
		} else {
			return (currentTime.getTime() > entryTime.getTime()
					+ minutesToMilliseconds(rfidTimeout));
		}
	}

	private static final int SECONDS_IN_MINUTE = 60;
	private static final int MILLIS_IN_SECOND = 1000;

	/**
	 * Converts minutes to milliseconds.
	 */
	private static int minutesToMilliseconds(final int val) {
		return val * SECONDS_IN_MINUTE * MILLIS_IN_SECOND;
	}

}
