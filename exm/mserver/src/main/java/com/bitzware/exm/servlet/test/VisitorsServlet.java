package com.bitzware.exm.servlet.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRStyledText;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.bitzware.exm.dao.TestDao;
import com.bitzware.exm.service.ServiceFactory;
import com.bitzware.exm.visitordb.model.Visitor;


public class VisitorsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream reportSchemaInput = null;
		
		try {
			Resource reportSchema =
				new ClassPathResource("/com.bitzware.exm/report/test/visitorsReport.jrxml");
			reportSchemaInput = reportSchema.getInputStream();
			
			JasperReport report = JasperCompileManager.compileReport(reportSchemaInput);
			IOUtils.closeQuietly(reportSchemaInput);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(JRStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT, "true");
			
			TestDao testDao = ServiceFactory.getTestDao();
			
			String sFrom = req.getParameter("from");
			String sTo = req.getParameter("to");
			
			Long from;
			Long to;
			
			if (sFrom != null) {
				from = Long.valueOf(sFrom);
			} else {
				from = Long.MIN_VALUE;
			}
			
			if (sTo != null) {
				to = Long.valueOf(sTo);
			} else {
				to = Long.MAX_VALUE;
			}
			
			List<Visitor> visitors = testDao.getVisitors(from, to);
			
			JasperPrint print =
				JasperFillManager.fillReport(report, parameters,
						new JRBeanCollectionDataSource(visitors));
			
			resp.setContentType("text/html");
			JRExporter exporter = new JRHtmlExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, resp.getOutputStream());
			exporter.exportReport();
		} catch (JRException e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(reportSchemaInput);
		}
	}

}
