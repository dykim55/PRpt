package com.cyberone.report.core.customize;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;

public class LineChartCustom implements JRChartCustomizer, Serializable
{
	private static final long serialVersionUID = -8493880774698206000L;
	
	public void customize(JFreeChart chart, JRChart jasperChart)
	{
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) chart.getCategoryPlot().getRenderer();

		CategorySeriesLabelGenerator  generator = new myCategorySeriesLabelGenerator();
		renderer.setLegendItemLabelGenerator(generator);
	}
	
	@SuppressWarnings("serial")
	class myCategorySeriesLabelGenerator implements CategorySeriesLabelGenerator, Serializable {
		@Override
        public String generateLabel(CategoryDataset dataset, int i) {
			String rowKey = dataset.getRowKey(i).toString();
			return rowKey.substring(rowKey.indexOf("=")+1); 
        }
	}
	
}

