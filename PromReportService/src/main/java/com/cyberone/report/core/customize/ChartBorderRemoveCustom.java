package com.cyberone.report.core.customize;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
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
		
		if (plot instanceof CategoryPlot) {
			//LineChart Y축의 값의 소수점표시 안돼도록 처리
			ValueAxis axis = ((CategoryPlot)plot).getRangeAxis();
			Range range = axis.getRange();
			if (range.getLowerBound() < 0) {
				axis.setRange(new Range(-5, 5));
			}
			
			CategoryItemRenderer renderer = ((CategoryPlot)plot).getRenderer();
			if (renderer instanceof BarRenderer) {
				((BarRenderer)renderer).setSeriesPaint(0, new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray));
			}
		} else if (plot instanceof PiePlot) {
			((PiePlot)plot).setLabelBackgroundPaint(Color.WHITE);
			((PiePlot)plot).setLabelOutlinePaint(Color.WHITE);
			((PiePlot)plot).setLabelShadowPaint(Color.WHITE);
		}
		
		
	}
	
}

