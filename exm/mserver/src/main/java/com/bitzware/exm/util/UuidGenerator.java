package com.bitzware.exm.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

import com.bitzware.exm.exception.InitializationException;


public class UuidGenerator implements IdGenerator {

	private final int idLengthInBytes = 16;
	private SecureRandom random;
	
	public UuidGenerator() {
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new InitializationException(
					"Cannot create random number generator for session identifiers.", e);
		}
	}

	@Override
	public String generate() {
		byte[] bytes = new byte[idLengthInBytes];
		random.nextBytes(bytes);
		String id = new String(Hex.encodeHex(bytes));
		return id;
	}

}
