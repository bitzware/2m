package com.bitzware.exm.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class LoggingFilter implements Filter {
	
	private static class MockInputStream extends ServletInputStream {
		
		private final InputStream innerInputStream;

		public MockInputStream(InputStream innerInputStream) {
			this.innerInputStream = innerInputStream;
		}

		public int available() throws IOException {
			return innerInputStream.available();
		}

		public void close() throws IOException {
			innerInputStream.close();
		}

		public boolean equals(Object obj) {
			return innerInputStream.equals(obj);
		}

		public int hashCode() {
			return innerInputStream.hashCode();
		}

		public void mark(int readlimit) {
			innerInputStream.mark(readlimit);
		}

		public boolean markSupported() {
			return innerInputStream.markSupported();
		}

		public int read() throws IOException {
			return innerInputStream.read();
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return innerInputStream.read(b, off, len);
		}

		public int read(byte[] b) throws IOException {
			return innerInputStream.read(b);
		}

		public void reset() throws IOException {
			innerInputStream.reset();
		}

		public long skip(long n) throws IOException {
			return innerInputStream.skip(n);
		}

		public String toString() {
			return innerInputStream.toString();
		}
		
	}
	
	private static class MockOutputStream extends ServletOutputStream {
		
		private final ByteArrayOutputStream innerOutput = new ByteArrayOutputStream();

		public void close() throws IOException {
			innerOutput.close();
		}

		public boolean equals(Object obj) {
			return innerOutput.equals(obj);
		}

		public void flush() throws IOException {
			innerOutput.flush();
		}

		public int hashCode() {
			return innerOutput.hashCode();
		}

		public void reset() {
			innerOutput.reset();
		}

		public int size() {
			return innerOutput.size();
		}

		public byte[] toByteArray() {
			return innerOutput.toByteArray();
		}

		public String toString() {
			return innerOutput.toString();
		}

		public String toString(int hibyte) {
			return innerOutput.toString(hibyte);
		}

		public String toString(String charsetName)
				throws UnsupportedEncodingException {
			return innerOutput.toString(charsetName);
		}

		public void write(byte[] b, int off, int len) {
			innerOutput.write(b, off, len);
		}

		public void write(byte[] arg0) throws IOException {
			innerOutput.write(arg0);
		}

		public void write(int b) {
			innerOutput.write(b);
		}

		public void writeTo(OutputStream out) throws IOException {
			innerOutput.writeTo(out);
		}
		
		public byte[] getData() {
			return innerOutput.toByteArray();
		}
		
	}
	
	private static class MockRequest extends HttpServletRequestWrapper {

		private final byte[] data;
		private final String encoding;
		
		public MockRequest(HttpServletRequest request, byte[] data, String encoding) {
			super(request);
			this.data = data;
			this.encoding = encoding;
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			return new MockInputStream(new ByteArrayInputStream(data));
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
		}
		
	}
	
	private static class MockResponse extends HttpServletResponseWrapper {

		private final MockOutputStream mockOutputStream = new MockOutputStream();
		private PrintWriter writer = null;
		
		public MockResponse(HttpServletResponse response) {
			super(response);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return mockOutputStream;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			writer = new PrintWriter(new OutputStreamWriter(mockOutputStream, getCharacterEncoding()));
			
			return writer;
		}
		
		public byte[] getData() {
			if (writer != null) {
				writer.flush();
			}
			
			return mockOutputStream.getData();
		}
	}

	private final static Logger logger = Logger.getLogger(LoggingFilter.class);
	private final static AtomicLong reqIdGen = new AtomicLong();
	
	
	public void destroy() {
	}

	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest) req;
		String queryString = httpReq.getQueryString();
		if (queryString != null && (queryString.endsWith("?wsdl") || queryString.endsWith("?xsd=1"))) {
			chain.doFilter(req, resp);
			return;
		}
		
		long reqId = reqIdGen.getAndIncrement();
		
		String encoding = req.getCharacterEncoding();
		if (encoding == null) {
			encoding = "US-ASCII";
		}
		
		InputStream input = req.getInputStream();
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		IOUtils.copy(input, outputBuffer);
		
		byte[] requestBytes = outputBuffer.toByteArray();
		
		if (logger.isDebugEnabled()) {
			String requestString = new String(requestBytes, encoding);
			StringBuilder sb = new StringBuilder(50 + encoding.length() + requestString.length());
			sb.append(reqId).append(" - request with encoding: ").append(encoding).append(": ").append(requestString);
			logger.debug(sb.toString());
		}
		
		MockRequest mockRequest = new MockRequest((HttpServletRequest) req, requestBytes, encoding);
		MockResponse mockResponse = new MockResponse((HttpServletResponse) resp);
		
		chain.doFilter(mockRequest, mockResponse);
		
		byte[] responseBytes = mockResponse.getData();
		
		if (logger.isDebugEnabled()) {
			String responseString = new String(responseBytes, mockResponse.getCharacterEncoding());
			StringBuilder sb = new StringBuilder(30 + responseString.length());
			sb.append(reqId).append(" - response: ").append(responseString);
			logger.debug(sb.toString());
		}
		
		IOUtils.copy(new ByteArrayInputStream(responseBytes), resp.getOutputStream());
	}

	
	public void init(FilterConfig config) throws ServletException {
	}

}
