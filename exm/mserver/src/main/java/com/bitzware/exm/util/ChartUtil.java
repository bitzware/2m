package com.bitzware.exm.util;

import java.util.Locale;
import java.util.TimeZone;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public final class ChartUtil {

	private ChartUtil() {
	}
	
	public static JFreeChart createDailyVisitsChart(
			final String title,
			final String timeAxisLabel,
			final String valueAxisLabel,
			final XYDataset dataset,
			final Locale locale) {

		final double margin = 0.02;
		
		ValueAxis timeAxis = new DateAxis(timeAxisLabel, TimeZone.getDefault(), locale);
		timeAxis.setLowerMargin(margin);  // reduce the default margins
		timeAxis.setUpperMargin(margin);
		NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
		valueAxis.setAutoRangeIncludesZero(false);  // override default
		XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		renderer.setBaseToolTipGenerator(null);
		renderer.setURLGenerator(null);
		plot.setRenderer(renderer);

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, false);
		return chart;

	}

}
