package com.bitzware.exm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeTableXYDataset;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.ReportDao;
import com.bitzware.exm.dao.ReportGeneratorDao;
import com.bitzware.exm.dao.RoomDao;
import com.bitzware.exm.dao.StationDao;
import com.bitzware.exm.exception.ReportException;
import com.bitzware.exm.model.report.NamedColumnsResult;
import com.bitzware.exm.model.report.ReportOutputFormat;
import com.bitzware.exm.model.report.ReportParameters;
import com.bitzware.exm.model.report.ReportProperties;
import com.bitzware.exm.model.report.ReportType;
import com.bitzware.exm.service.ReportManager;
import com.bitzware.exm.util.ChartUtil;
import com.bitzware.exm.util.DateIntervalDataSource;
import com.bitzware.exm.util.DateIntervalIterator;
import com.bitzware.exm.util.NamedColumnsDataSource;
import com.bitzware.exm.util.TextSource;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


public class ReportManagerImpl implements ReportManager {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private final String styleTemplateInputParam = "styleTemplateInput";
	private final String logoImageInputParam = "logoImageInput";
	private final String timeFormatParam = "timeFormat";
	private final String textSourceParam = "textSource";

	private ReportDao reportDao;
	private ReportGeneratorDao reportGeneratorDao;
	private RoomDao roomDao;
	private StationDao stationDao;
	
	private final int defaultPopularStationsChartMaxColumns = 10;
	
	private int popularStationsChartMaxColumns = defaultPopularStationsChartMaxColumns;

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(final ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public ReportGeneratorDao getReportGeneratorDao() {
		return reportGeneratorDao;
	}

	public void setReportGeneratorDao(final ReportGeneratorDao reportGeneratorDao) {
		this.reportGeneratorDao = reportGeneratorDao;
	}

	public RoomDao getRoomDao() {
		return roomDao;
	}

	public void setRoomDao(final RoomDao roomDao) {
		this.roomDao = roomDao;
	}

	public StationDao getStationDao() {
		return stationDao;
	}

	public void setStationDao(final StationDao stationDao) {
		this.stationDao = stationDao;
	}

	public int getPopularStationsChartMaxColumns() {
		return popularStationsChartMaxColumns;
	}

	public void setPopularStationsChartMaxColumns(final int popularStationsChartMaxColumns) {
		this.popularStationsChartMaxColumns = popularStationsChartMaxColumns;
	}

	
	public boolean isReady(final String reportId) {
		return reportDao.isReady(reportId);
	}
	
	
	public boolean failed(final String reportId) {
		return reportDao.failed(reportId);
	}

	
	public ReportProperties getReportData(final String reportId) {
		return reportDao.openReportForRead(reportId);
	}

	
	public String createReport(final ReportType reportType) {
		return reportDao.createReport(reportType);
	}
	
	
	public int getReportsAmount() {
		return reportDao.getReportsAmount();
	}

	
	public List<ReportProperties> getReports(final int offset, final int limit) {
		return reportDao.getReports(offset, limit);
	}

	
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public void writeEventsOnStationReport(final String reportId, final Long stationId,
			final Date from, final Date to, final EventType eventType,
			final ReportParameters parameters, final ReportOutputFormat outputFormat,
			final Resource reportSchemaSource, final Resource compiledReportSource)
	throws JRException {
		
		JasperReport report = readReport(reportSchemaSource, compiledReportSource);

		OutputStream reportOutput = null;
		boolean success = false;

		Date reportTimestamp = new Date();
		try {
			Station station = null;
			if (stationId != null) {
				station = stationDao.getStationWithRoomById(stationId);
			}

			List<Event> events = reportGeneratorDao.getEventsOnStationReportData(station, from, to,
					eventType);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(events);
			
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			putParameters(parametersMap, parameters);
			parametersMap.put("from", from);
			parametersMap.put("to", to);
			parametersMap.put("eventType", getEventTypeLabel(eventType,
					parameters.getTextSource()));
			
			JasperPrint print = JasperFillManager.fillReport(report, parametersMap, dataSource);

			ReportProperties reportProperties = new ReportProperties();
			reportProperties.setFormat(outputFormat);
			reportProperties.setType(ReportType.EVENTS_ON_STATION);
			reportProperties.setTimestamp(reportTimestamp);
			
			if (stationId != null) {
				reportProperties.setIdString(String.valueOf(stationId));
			} else {
				reportProperties.setIdString(StringUtils.EMPTY);
			}
			
			StringBuilder description = new StringBuilder();
			if (station != null) {
				description.append(station.getName()).append(',');
			}
			if (from != null) {
				description.append(parameters.getTimeFormatter().formatDate(from));
			}
			if (from != null || to != null) {
				description.append(" - ");
			}
			if (to != null) {
				description.append(parameters.getTimeFormatter().formatDate(to));
			}
			reportProperties.setDescription(description.toString());

			reportOutput = reportDao.openReportForWrite(reportId, reportProperties);
			exportReport(print, reportOutput, outputFormat);
			
			success = true;
		} finally {
			IOUtils.closeQuietly(reportOutput);
			
			reportDao.closeReport(reportId, reportTimestamp, success);
		}
	}
	
	
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public void writeEventsInRoomReport(final String reportId, final Long roomId,
			final Date from, final Date to, final EventType eventType,
			final ReportParameters parameters, final ReportOutputFormat outputFormat,
			final Resource reportSchemaSource, final Resource compiledReportSource)
	throws JRException {
		
		JasperReport report = readReport(reportSchemaSource, compiledReportSource);
		
		OutputStream reportOutput = null;
		boolean success = false;
		
		Date reportTimestamp = new Date();
		try {
			Room room = null;
			if (roomId != null) {
				room = roomDao.getRoomById(roomId);
			}
			
			List<Event> events = reportGeneratorDao.getEventsInRoomReportData(room, from, to,
					eventType);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(events);
			
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			putParameters(parametersMap, parameters);
			parametersMap.put("from", from);
			parametersMap.put("to", to);
			parametersMap.put("eventType", getEventTypeLabel(eventType,
					parameters.getTextSource()));
			
			JasperPrint print = JasperFillManager.fillReport(report, parametersMap, dataSource);
			
			ReportProperties reportProperties = new ReportProperties();
			reportProperties.setFormat(outputFormat);
			reportProperties.setType(ReportType.EVENTS_IN_ROOM);
			reportProperties.setTimestamp(reportTimestamp);
			
			if (roomId != null) {
				reportProperties.setIdString(String.valueOf(roomId));
			} else {
				reportProperties.setIdString(StringUtils.EMPTY);
			}
			
			StringBuilder description = new StringBuilder();
			if (room != null) {
				description.append(room.getName()).append(',');
			}
			if (from != null) {
				description.append(parameters.getTimeFormatter().formatDate(from));
			}
			if (from != null || to != null) {
				description.append(" - ");
			}
			if (to != null) {
				description.append(parameters.getTimeFormatter().formatDate(to));
			}
			reportProperties.setDescription(description.toString());
			
			reportOutput = reportDao.openReportForWrite(reportId, reportProperties);
			exportReport(print, reportOutput, outputFormat);
			
			success = true;
		} finally {
			IOUtils.closeQuietly(reportOutput);
			
			reportDao.closeReport(reportId, reportTimestamp, success);
		}
	}

	
	public void writeEventsOnFloorReport(final String reportId, final String floor,
			final Date from, Date to, final EventType eventType, final ReportParameters parameters,
			final ReportOutputFormat outputFormat, final Resource reportSchemaSource,
			final Resource compiledReportSource)
	throws JRException {
		
		JasperReport report = readReport(reportSchemaSource, compiledReportSource);
		
		OutputStream reportOutput = null;
		boolean success = false;
		
		Date reportTimestamp = new Date();
		try {
			List<Event> events = reportGeneratorDao.getEventsOnFloorReportData(floor, from, to,
					eventType);

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(events);
			
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			putParameters(parametersMap, parameters);
			parametersMap.put("from", from);
			parametersMap.put("to", to);
			parametersMap.put("eventType", getEventTypeLabel(eventType,
					parameters.getTextSource()));
			
			JasperPrint print = JasperFillManager.fillReport(report, parametersMap, dataSource);
			
			ReportProperties reportProperties = new ReportProperties();
			reportProperties.setFormat(outputFormat);
			reportProperties.setType(ReportType.EVENTS_ON_FLOOR);
			reportProperties.setTimestamp(reportTimestamp);

			if (floor != null) {
				reportProperties.setIdString(floor);
			} else {
				reportProperties.setIdString(StringUtils.EMPTY);
			}
			
			StringBuilder description = new StringBuilder();
			if (floor != null) {
				description.append(floor).append(',');
			}
			if (from != null) {
				description.append(parameters.getTimeFormatter().formatDate(from));
			}
			if (from != null || to != null) {
				description.append(" - ");
			}
			if (to != null) {
				description.append(parameters.getTimeFormatter().formatDate(to));
			}
			reportProperties.setDescription(description.toString());
			
			reportOutput = reportDao.openReportForWrite(reportId, reportProperties);
			exportReport(print, reportOutput, outputFormat);
			
			success = true;
		} finally {
			IOUtils.closeQuietly(reportOutput);
			
			reportDao.closeReport(reportId, reportTimestamp, success);
		}
	}
	
	
	public void writeVisitorsRoutesReport(final String reportId, final Date date,
			final EventType eventType, final ReportParameters parameters,
			final ReportOutputFormat outputFormat, final Resource reportSchemaSource,
			final Resource compiledReportSource)
	throws JRException {
		
		JasperReport report = readReport(reportSchemaSource, compiledReportSource);
		
		OutputStream reportOutput = null;
		boolean success = false;
		
		Date reportTimestamp = new Date();
		try {
			List<Event> events = reportGeneratorDao.getVisitorsRoutesReportData(date, eventType);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(events);
			
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			putParameters(parametersMap, parameters);
			parametersMap.put("date", date);
			parametersMap.put("eventType", getEventTypeLabel(eventType,
					parameters.getTextSource()));
			
			JasperPrint print = JasperFillManager.fillReport(report, parametersMap, dataSource);
			
			ReportProperties reportProperties = new ReportProperties();
			reportProperties.setFormat(outputFormat);
			reportProperties.setType(ReportType.VISITORS_ROUTES);
			reportProperties.setTimestamp(reportTimestamp);
			reportProperties.setIdString(String.valueOf(date.getTime()));
			reportProperties.setDescription(parameters.getTimeFormatter().formatDate(date));
			
			reportOutput = reportDao.openReportForWrite(reportId, reportProperties);
			exportReport(print, reportOutput, outputFormat);
			
			success = true;
		} finally {
			IOUtils.closeQuietly(reportOutput);
			
			reportDao.closeReport(reportId, reportTimestamp, success);
		}
	}

	
	public void writePopularStationsReport(final String reportId, final Date from,
			final Date to, final boolean chart, final EventType eventType,
			final ReportParameters parameters, final ReportOutputFormat outputFormat,
			final Resource reportSchemaSource, final Resource compiledReportSource)
	throws JRException {
		
		JasperReport report = readReport(reportSchemaSource, compiledReportSource);
		
		OutputStream reportOutput = null;
		boolean success = false;
		
		Date reportTimestamp = new Date();
		try {
			NamedColumnsResult data = reportGeneratorDao.getPopularStationsReportData(from, to,
					eventType);
			
			NamedColumnsDataSource dataSource = new NamedColumnsDataSource(data);
			
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			putParameters(parametersMap, parameters);
			parametersMap.put("from", from);
			parametersMap.put("to", to);
			parametersMap.put("eventType", getEventTypeLabel(eventType,
					parameters.getTextSource()));
//			if (chart) {
//				JFreeChart chartObj = generatePopularStationsChart(dataSource,
//				parameters.textSource);
//				parametersMap.put("chart", new JCommonDrawableRenderer(chartObj));
//			}
			
			JasperPrint print = JasperFillManager.fillReport(report, parametersMap, dataSource);
			
			ReportProperties reportProperties = new ReportProperties();
			reportProperties.setFormat(outputFormat);
			reportProperties.setType(ReportType.POPULAR_STATIONS);
			reportProperties.setTimestamp(reportTimestamp);
			reportProperties.setIdString(StringUtils.EMPTY);
			
			StringBuilder description = new StringBuilder();
			if (from != null) {
				description.append(parameters.getTimeFormatter().formatDate(from));
			}
			description.append('-');
			if (to != null) {
				description.append(parameters.getTimeFormatter().formatDate(to));
			}
			
			reportProperties.setDescription(description.toString());
			
			reportOutput = reportDao.openReportForWrite(reportId, reportProperties);
			exportReport(print, reportOutput, outputFormat);
			
			success = true;
		} finally {
			IOUtils.closeQuietly(reportOutput);
			
			reportDao.closeReport(reportId, reportTimestamp, success);
		}		
	}
	
	
	public void writeDailyVisitsReport(final String reportId, final Date from,
			final Date to, final boolean chart, final ReportParameters parameters,
			final ReportOutputFormat outputFormat, final Resource reportSchemaSource,
			final Resource compiledReportSource)
	throws JRException {
		
		JasperReport report = readReport(reportSchemaSource, compiledReportSource);
		
		OutputStream reportOutput = null;
		boolean success = false;
		
		Date reportTimestamp = new Date();
		try {
			NamedColumnsResult data = reportGeneratorDao.getDailyVisitsReportData(from, to);
			
			final int yearIndex = 0;
			final int monthIndex = 1;
			final int dayIndex = 2;
			final int visitsAmountIndex = 3;
			final Object[] defaultRow = new Object[] {null, null, null, 0L};
			
			NamedColumnsDataSource dataSource = new DateIntervalDataSource(data, defaultRow,
					yearIndex, monthIndex, dayIndex);
			
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			putParameters(parametersMap, parameters);
			parametersMap.put("from", from);
			parametersMap.put("to", to);
			if (chart) {
				JFreeChart chartObj = generateDailyVisitsChart(
						new DateIntervalIterator(data.getData().iterator(),
								defaultRow, yearIndex, monthIndex, dayIndex),
						parameters.getTextSource(),
						yearIndex,
						monthIndex,
						dayIndex,
						visitsAmountIndex);
				parametersMap.put("chart", new JCommonDrawableRenderer(chartObj));
			}
			
			JasperPrint print = JasperFillManager.fillReport(report, parametersMap, dataSource);
			
			ReportProperties reportProperties = new ReportProperties();
			reportProperties.setFormat(outputFormat);
			reportProperties.setType(ReportType.DAILY_VISITS);
			reportProperties.setTimestamp(reportTimestamp);
			reportProperties.setIdString(StringUtils.EMPTY);
			
			StringBuilder description = new StringBuilder();
			if (from != null) {
				description.append(parameters.getTimeFormatter().formatDate(from));
			}
			description.append('-');
			if (to != null) {
				description.append(parameters.getTimeFormatter().formatDate(to));
			}
			
			reportProperties.setDescription(description.toString());
			
			reportOutput = reportDao.openReportForWrite(reportId, reportProperties);
			exportReport(print, reportOutput, outputFormat);
			
			success = true;
		} finally {
			IOUtils.closeQuietly(reportOutput);
			
			reportDao.closeReport(reportId, reportTimestamp, success);
		}		
	}
	
	private JFreeChart generateDailyVisitsChart(final Iterator<Object[]> iterator,
			final TextSource textSource, final int yearIndex, final int monthIndex,
			final int dayIndex, final int visitsAmountIndex) {
		
//		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		
//		TimeFormatter timeFormatter = new TimeFormatter(
//				null,
//				textSource.getText("format.date.chart"),
//				null);
//		
//		int currentMonth = -1;
//		int currentYear = -1;
//		long visitsSum = 0;
//		
//		while (iterator.hasNext()) {
//			Object[] rowObj = iterator.next();
//			
//			int year = (Integer) rowObj[yearIndex];
//			int month = (Integer) rowObj[monthIndex];
//			long visitsAmount = (Long) rowObj[visitsAmountIndex];
//			
//			if (currentMonth == -1) {
//				currentMonth = month;
//				currentYear = year;
//				visitsSum = visitsAmount;
//			} else if (currentMonth != month || currentYear != year) {
//				String monthName = ReportUtil.getMonthShortName(currentMonth, textSource);
//				StringBuilder categoryName = new StringBuilder(8);
//				categoryName.append(monthName).append('-').append(String.valueOf(currentYear));
//				
//				CategoryIndexedName monthCategory = new CategoryIndexedName(
//						currentMonth, categoryName.toString());
//				
//				dataset.setValue(visitsSum, StringUtils.EMPTY, monthCategory);
//				
//				visitsSum = visitsAmount;
//				currentMonth = month;
//				currentYear = year;
//			} else {
//				visitsSum += visitsAmount;
//			}
//		}
//		
//		JFreeChart chart = ChartFactory.createBarChart3D(
//				textSource.getText("text.report.dailyvisits.chart.title"),
//				textSource.getText("text.report.dailyvisits.chart.xlabel"),
//				textSource.getText("text.report.dailyvisits.chart.ylabel"),
//				dataset,
//				PlotOrientation.VERTICAL,
//				false,
//				false,
//				false
//		);
//		CategoryPlot plot = (CategoryPlot) chart.getPlot();
//		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		TimeTableXYDataset dataset = new TimeTableXYDataset();
		
		int currentMonth = -1;
		int currentYear = -1;
		long visitsSum = 0;
		
		while (iterator.hasNext()) {
			Object[] rowObj = iterator.next();
			
			int year = (Integer) rowObj[yearIndex];
			int month = (Integer) rowObj[monthIndex];
			long visitsAmount = (Long) rowObj[visitsAmountIndex];
			
			if (currentMonth == -1) {
				currentMonth = month;
				currentYear = year;
				visitsSum = visitsAmount;
			} else if (currentMonth != month || currentYear != year) {
				dataset.add(new Month(currentMonth, currentYear), visitsSum, StringUtils.EMPTY);
				
				visitsSum = visitsAmount;
				currentMonth = month;
				currentYear = year;
			} else {
				visitsSum += visitsAmount;
			}
		}

		JFreeChart chart = ChartUtil.createDailyVisitsChart(
				textSource.getText("text.report.dailyvisits.chart.title"),
				textSource.getText("text.report.dailyvisits.chart.xlabel"),
				textSource.getText("text.report.dailyvisits.chart.ylabel"),
				dataset,
				textSource.getLocale()
		);
		
		return chart;
	}
	
//	private JFreeChart generatePopularStationsChart(NamedColumnsDataSource dataSource,
//	TextSource textSource) {
//		final int eventsAmountColumn = 2;
//		final int stationNameColumn = 0;
//		
//		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		
//		int index = 0;
//		for (Object rowObj : dataSource.getData()) {
//			Object[] row = (Object[]) rowObj;
//			
//			Long eventsAmount = (Long) row[eventsAmountColumn];
//			String stationName = (String) row[stationNameColumn];
//			
//			dataset.setValue(
//					eventsAmount,
//					StringUtils.EMPTY,
//					new IndexedName(index, stationName));
//			index++;
//			
//			if (index >= popularStationsChartMaxColumns) {
//				break;
//			}
//		}
//		
//		JFreeChart chart = ChartFactory.createBarChart3D(
//				textSource.getText("text.report.popularstations.chart.title"),
//				textSource.getText("text.report.popularstations.chart.xlabel"),
//				textSource.getText("text.report.popularstations.chart.ylabel"),
//				dataset,
//				PlotOrientation.VERTICAL,
//				false,
//				false,
//				false
//        );
//		CategoryPlot plot = (CategoryPlot) chart.getPlot();
//		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
//		
//		return chart;
//	}

	private void exportReport(final JasperPrint print, final OutputStream output,
			final ReportOutputFormat outputType) throws JRException {
		JRExporter exporter;
		switch (outputType) {
		case DOCX:
			exporter = new JRDocxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.exportReport();
			break;
		case CSV:
			exporter = new JRCsvExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.exportReport();
			break;
		case ODS:
			exporter = new JROdsExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.exportReport();
			break;
		case ODT:
			exporter = new JROdtExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.exportReport();
			break;
		case PDF:
			JasperExportManager.exportReportToPdfStream(print, output);
			break;
		case RTF:
			exporter = new JRRtfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.exportReport();
			break;
		case XLS:
			exporter = new JRXlsExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);

			exporter.exportReport();
			break;
		case XLSX:
			exporter = new JRXlsxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);

			exporter.exportReport();
			break;
		case XML:
			JasperExportManager.exportReportToXmlStream(print, output);
			break;
		default:
			return;
		}
	}
	
	private void putParameters(final Map<String, Object> parametersMap,
			final ReportParameters reportParameters) {
		parametersMap.put(JRStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT, "true");
		parametersMap.put(styleTemplateInputParam, reportParameters.getStyleTemplateInput());
		parametersMap.put(textSourceParam, reportParameters.getTextSource());
		parametersMap.put(timeFormatParam, reportParameters.getTimeFormatter());
		
		if (reportParameters.getLogoImageInput() != null) {
			parametersMap.put(logoImageInputParam, reportParameters.getLogoImageInput());
		}
	}
	
	private JasperReport readReport(final Resource reportSchemaSource,
			final Resource compiledReportSource) {
		InputStream input = null;
		JasperReport report = null;
		
		try {
			input = compiledReportSource.getInputStream();
			report = (JasperReport) JRLoader.loadObject(input);
		} catch (IOException e) {
			logger.warn("Cannot read compiled report template.");
		} catch (JRException e) {
			logger.error("Invalid compiled report template.");
		} catch (ClassCastException e) {
			logger.error("Invalid compiled report template.");
		} finally {
			IOUtils.closeQuietly(input);
		}
		
		if (report == null) {
			logger.warn("Reading report source...");
			
			input = null;
			
			try {
				input = reportSchemaSource.getInputStream();
				report = JasperCompileManager.compileReport(input);
			} catch (IOException e) {
				throw new ReportException("Cannot read report.", e);
			} catch (JRException e) {
				throw new ReportException("Cannot read report.", e);
			} finally {
				IOUtils.closeQuietly(input);
			}
		}
		
		return report;
	}
	
	private String getEventTypeLabel(EventType eventType, TextSource textSource) {
		if (eventType == null) {
			return StringUtils.EMPTY;
		} else {
			return textSource.getText("text.eventtype." + eventType.name());
		}
	}

}
