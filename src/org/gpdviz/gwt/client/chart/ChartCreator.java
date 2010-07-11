package org.gpdviz.gwt.client.chart;

import org.gpdviz.gwt.client.chart.impl.Chart;
import org.gpdviz.gwt.client.chart.impl.ImageChart;




/**
 * Factory of IChart objects.
 * 
 * <p>
 * Note: during this prototyping phase, a private-package visible flag 
 * determines what IChart implementation is used.
 * 
 * @author Carlos Rueda
 */
public class ChartCreator {
	// for computing chart width acording to number of points
    private static final int POINT_X_SEPARATION = 16;//9;
    
    private static class Style {
    	int numPoints;
    	int chartWidth;
    	int chartHeight;
    	int fontSize;
    	
		Style(int chartHeight, int fontSize, int numPoints) {
			this.chartWidth = numPoints * POINT_X_SEPARATION;
			this.chartHeight = chartHeight;
			this.fontSize = fontSize;
			this.numPoints = numPoints;
		}
    }
    
    private static Style style1 = new Style(60, 8, 20);
    
    private static Style style2 = new Style(80, 10, 30);
    

    /**
     * a private-package visible flag that will determine what concrete class
     * implementing {@link IChart} is instantiated.
     */
    static boolean useGChart = true;

    public static IChart create(int numPoints, int fontSize, String title, String legend, String xLabel, String yLabel) {
    	IChart chart;
        if ( useGChart ) { 
        	chart = new Chart(numPoints, fontSize, title, legend, xLabel, yLabel);
        }
        else {
            chart = new ImageChart(numPoints, fontSize, title, legend, xLabel, yLabel);
        }
		return chart;
    }
    

	private static IChart createChartStyle(
			Style style,
			String title,
			String legend,
			String xLabel,
			String yLabel
	) {
		
        IChart chart = create( 
        		style.numPoints,
        		style.fontSize, 
        		title, legend, xLabel, yLabel
        );
        
        chart.setChartSize(style.chartWidth, style.chartHeight);
        
        return chart;
    }

	public static IChart createChartStyle1(
			String title,
			String legend,
			String xLabel,
			String yLabel
	) {
		return createChartStyle(style1, title, legend, xLabel, yLabel);
    }

	public static IChart createChartStyle2(
			String title,
			String legend,
			String xLabel,
			String yLabel
	) {
		return createChartStyle(style2, title, legend, xLabel, yLabel);
	}
	

	public static IChart _testChart() {
		IChart chart = createChartStyle1("title", null, "", "");
		chart.putRandomData(-50, 100);
		return chart;

	}
}
