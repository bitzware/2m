package com.bitzware.exm.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that returns data from the specified address. It is useful when there is a
 * need to disable authentication for one specified resource and it is not possible to
 * do it in the web.xml file.
 * 
 * @author finagle
 */
public class RedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String redirectResource;
	
	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		
		redirectResource = config.getInitParameter("resource");
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher(redirectResource);
		requestDispatcher.forward(req, resp);
	}

}
