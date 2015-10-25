package com.bitzware.exm.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This implementation of OutputStream is used to write report data. After closing,
 * it creates an empty 'ready' file to signalize that the report is ready and may be
 * downloaded by the user.
 * 
 * @author finagle
 */
public class ReportOutputStream extends OutputStream {

	private final OutputStream reportOutput;
	private final File readyFile;
	private final File failFile;
	
	public ReportOutputStream(final OutputStream reportOutput, final File readyFile,
			final File failFile) {
		this.reportOutput = reportOutput;
		this.readyFile = readyFile;
		this.failFile = failFile;
	}
	
	public void closeReport(final boolean success) throws IOException {
		reportOutput.close();
		if (success) {
			readyFile.createNewFile();
		} else {
			failFile.createNewFile();
		}
	}

	@Override
	public void close() throws IOException {
		closeReport(false);
	}

	@Override
	public void flush() throws IOException {
		reportOutput.flush();
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		reportOutput.write(b, off, len);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		reportOutput.write(b);
	}

	@Override
	public void write(final int b) throws IOException {
		reportOutput.write(b);
	}
	
}
