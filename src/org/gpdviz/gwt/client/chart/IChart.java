//////////////////////////////////////////////////////
// COMET Project
// Module: Remote access & visualization of BML data 
// Author: Carlos Rueda
//
// $LastChangedRevision: 245 $
// $LastChangedDate: 2008-06-30 23:35:12 -0700 (Mon, 30 Jun 2008) $
// $LastChangedBy: crueda $
//////////////////////////////////////////////////////

package org.gpdviz.gwt.client.chart;

import com.google.gwt.user.client.ui.Widget;

/**
 * All chart manipulation is done through this interface.
 * 
 * <p>
 * Note: {@link comet.bml.gwt.client.chart.Chart} was actually the
 * base implementation (based on {@link com.googlecode.gchart.client.GChart}),
 * but then this interface was extracted to allow other implementations,
 * currently {@link comet.bml.gwt.client.chart.ImageChart}. 
 * 
 * @author Carlos Rueda
 * @version $Id: IChart.java 245 2008-07-01 06:35:12Z crueda $
 */
public interface IChart {

    /**
     * Sets the title for this chart.
     */
    public void setGivenTitle();

    /**
     * Puts random values for this chart.
     * It calls <code>addPoint(d + e * Math.random())</code> for the
     * maximum number of points associated.
     * @param d  a central value
     * @param e  some variance around the central value.
     */
    public void putRandomData(double d, double e);

    /**
     * Removes all data from this chart.
     */
    public void reset();

    /**
     * Gets the maimum number of points to show in this chart.
     */
    public int getMaxPoints();

    /**
     * Adds a data point returning the index used.
     * The index is internally incremented.
     */
    public int addPoint(double d);

    /**
     * Sets the label text for the X axis.
     */
    public void setAxisLabel(String string);
    
    /** specifically for times, hh:mm:ss */
    public void setYTickLabelFormat(String string);
    
    public void setXTickLabelFormat(String string);
    
    /**
     * Sets the title for this chart.
     */
    public void setChartTitle(String string);

    /**
     * Reflects all changes made to this chart.
     */
    public void update();

    /**
     * Sets the size of this chart.
     */
    public void setChartSize(int w, int h);

    /**
     * Gets the widget used to display this chart.
     */
    public Widget asWidget();

    public void setXChartSize(int width);

    public void setYChartSize(int height);

    public void setWidth(String string);

    public void setHeight(String string);

    public void clearCurves();

    public void removeFromParent();

    /**
     * Gets the base font size used to create the various texts. 
     */
    public int getFontSize();

    /**
     * Sets the title for this chart with some coloring depending
     * on the error flag.
     */
    public void setChartTitle(boolean error, String string);

    /**
     * Sets whether abscisa values should be displayed.
     */
    public void setShowValuesX(boolean showValuesX);
    
    
    public interface IHoverManager {
    	public String getHovertextTemplate();
    	public String getHoverParameter(String paramName, double x, double y);
    }
    public void setHoverManager(IHoverManager hoverMan);
    public IHoverManager getHoverManager();

    
    //////////////////////////////////////////
    
	public String getHeight();

	public String getWidth();
	
	
	public int getYChartSizeDecorated();
}