package com.cyberone.report.core.customize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import net.sf.jasperreports.engine.JRPropertiesMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;

public class ChartBorderRemoveCustom implements JRChartCustomizer, Serializable
{
	private static final long serialVersionUID = -8493880774698206000L;
	
	public void customize(JFreeChart chart, JRChart jasperChart)
	{
		Plot plot = chart.getPlot();

		if (chart.getLegend() != null) {
			chart.getLegend().setBorder(0.0, 0.0, 0.0, 0.0);
		}
		
		JRPropertiesMap prop = jasperChart.getPropertiesMap();
		
		if (plot instanceof CategoryPlot) {
			//LineChart Y축의 값의 소수점표시 안돼도록 처리
			ValueAxis axis = ((CategoryPlot)plot).getRangeAxis();
			Range range = axis.getRange();
			if (range.getLowerBound() < 0) {
				axis.setRange(new Range(-5, 5));
			} else if (range.getUpperBound() < 10) {
				axis.setRange(new Range(0, 10));
			}

			if (prop != null && prop.getProperty("fileFormat").equals("docx")) {
				Font font = new Font("SansSerif", Font.PLAIN, 12);
				((CategoryPlot)plot).getDomainAxis().setLabelFont(font);
				((CategoryPlot)plot).getRangeAxis().setLabelFont(font);
				
				LegendTitle legend = (LegendTitle)chart.getLegend();
				if (legend != null) legend.setItemFont(font);
				
				TextTitle text = (TextTitle)chart.getTitle();
				if (text != null) text.setFont(font);
				
				if (((CategoryPlot)plot).getRenderer() instanceof BarRenderer) {
					font = new Font("SansSerif", Font.PLAIN, 12);
					((CategoryPlot)plot).getDomainAxis().setTickLabelFont(font);
					font = new Font("SansSerif", Font.PLAIN, 10);
					((CategoryPlot)plot).getRangeAxis().setTickLabelFont(font);
					((CategoryPlot)plot).getRenderer().setBaseItemLabelFont(font);
				} else {
					font = new Font("SansSerif", Font.PLAIN, 10);
					((CategoryPlot)plot).getDomainAxis().setTickLabelFont(font);
					((CategoryPlot)plot).getRangeAxis().setTickLabelFont(font);
				}
			} else if (prop != null && prop.getProperty("fileFormat").equals("pdf")) {
				Font font = new Font("SansSerif", Font.PLAIN, 8);
				((CategoryPlot)plot).getDomainAxis().setLabelFont(font);
				((CategoryPlot)plot).getRangeAxis().setLabelFont(font);

				LegendTitle legend = (LegendTitle)chart.getLegend();
				if (legend != null) legend.setItemFont(font);

				TextTitle text = (TextTitle)chart.getTitle();
				if (text != null) text.setFont(font);
				
				font = new Font("SansSerif", Font.PLAIN, 8);
				((CategoryPlot)plot).getDomainAxis().setTickLabelFont(font);
				((CategoryPlot)plot).getRangeAxis().setTickLabelFont(font);
			}
			
			CategoryItemRenderer renderer = ((CategoryPlot)plot).getRenderer();
			if (renderer instanceof BarRenderer) {
				((BarRenderer)renderer).setSeriesPaint(0, new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray));
			} else if (renderer instanceof LineAndShapeRenderer && ((CategoryPlot) plot).getDataset() != null) {
				for (int i = 0; i < ((CategoryPlot) plot).getDataset().getRowCount(); i++) {
					((LineAndShapeRenderer)renderer).setSeriesStroke(i, new BasicStroke(2f));
				}
			}
		} else if (plot instanceof PiePlot) {
			((PiePlot)plot).setLabelBackgroundPaint(Color.WHITE);
			((PiePlot)plot).setLabelOutlinePaint(Color.WHITE);
			((PiePlot)plot).setLabelShadowPaint(Color.WHITE);
			
			if (prop != null && prop.getProperty("fileFormat").equals("docx")) {
				Font font = new Font("SansSerif", Font.PLAIN, 12);
				((PiePlot)plot).setLabelFont(font);

				LegendTitle legend = (LegendTitle)chart.getLegend();
				legend.setItemFont(font);
			} else if (prop != null && prop.getProperty("fileFormat").equals("pdf")) {
				
			}
		}
		
		
	}
	
}

