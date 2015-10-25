package com.bitzware.exm.util;

import java.io.OutputStream;

public class ByteBufferOutputStream extends OutputStream {

	private final byte[] buffer;
	private int index = 0;

	public ByteBufferOutputStream(byte[] buffer) {
		this.buffer = buffer;
	}

	@Override
	public void write(int b) {
		buffer[index] = (byte) b;
		index++;
	}
	
	public int getLength() {
		return index;
	}
	
}
