package com.bitzware.exm.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bitzware.exm.service.MasterDuplicateManager;


public class MasterGetPresentationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected Logger logger = Logger.getLogger(this.getClass());

	private MasterDuplicateManager masterDuplicateManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ApplicationContext applicationContext =
			WebApplicationContextUtils.getRequiredWebApplicationContext(
					getServletContext());

		this.masterDuplicateManager =
			(MasterDuplicateManager) applicationContext.getBean("masterDuplicateManager");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		resp.setContentType("application/octet-stream");

		String presentationId = req.getParameter("presentationId");
		
		if (presentationId == null) {
			resp.sendError(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		
		InputStream presentationInput = null;
		
		try {
			presentationInput = masterDuplicateManager.getPresentation(presentationId);
			
			if (presentationInput == null) {
				resp.sendError(HttpStatus.SC_NOT_FOUND);
				return;
			}
			
			IOUtils.copy(presentationInput, resp.getOutputStream());
			presentationInput.close();
		} finally {
			IOUtils.closeQuietly(presentationInput);
		}
	}

}
