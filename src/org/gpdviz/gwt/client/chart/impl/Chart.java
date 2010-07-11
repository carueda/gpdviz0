package org.gpdviz.gwt.client.chart.impl;

import org.gpdviz.gwt.client.chart.IChart;
import org.gpdviz.gwt.client.util.GizUtil;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.googlecode.gchart.client.GChart;
import com.googlecode.gchart.client.GChartCanvasFactory;
import com.googlecode.gchart.client.GChartCanvasLite;
import com.googlecode.gchart.client.HoverParameterInterpreter;
import com.googlecode.gchart.client.GChart.Curve.Point;

/**
 * An implementation of IChart based on GChart.
 * 
 * @author Carlos Rueda
 */
public class Chart extends GChart implements IChart {

	////////////////////////////////////////////////////////////////////////////
	// //////////
	// Note: the following section copied verbatim from:
	//http://gchart.googlecode.com/svn/trunk/live-demo/v2_3/com.googlecode.gchart
	// .gchartdemoapp.GChartDemoApp/GChartExample17.txt

	// GChart requires GWT Widgets that implement GChartCanvasLite.
	// Since GWTCanvas extends Widget, we meet the Widget requirement.
	public static class GWTCanvasBasedCanvasLite extends GWTCanvas implements
			GChartCanvasLite {
		// GChartCanvasLite requires CSS color strings, but
		// GWTCanvas uses its own Color class instead, so we wrap:
		public void setStrokeStyle(String cssColor) {
			setStrokeStyle(new Color(cssColor));
		}

		public void setFillStyle(String cssColor) {
			setFillStyle(new Color(cssColor));
		}

		public void setBackgroundColor(String cssColor) {
			setBackgroundColor(new Color(cssColor));
		}
		// Note: all other GChart GChartCanvasLite methods (lineTo, moveTo,
		// pie, etc.) are directly inherited from GWTCanvas: no
		// wrapper methods required.
	}

	// Machine GChart needs to create these GChartCanvasLite Widgets
	public static class GWTCanvasBasedCanvasFactory implements
			GChartCanvasFactory {

		public GChartCanvasLite create() {
			GChartCanvasLite result = new GWTCanvasBasedCanvasLite();
			return result;
		}

	}

	// this line "teaches" GChart how to create the canvas
	// widgets it needs to render LINE_CANVAS based curves
	// (you only need to do this once per GWT application).

	static {
		GChart.setCanvasFactory(new GWTCanvasBasedCanvasFactory());
	}
	////////////////////////////////////////////////////////////////////////////
	// //////////

	int maxPoints;
	int fontSize;
	String title;
	private int index = 0;

	public Chart() {
		super();
		setBackgroundColor("#DDF");
	}

	public Chart(int maxPoints, int fontSize, String title, String legend,
			String xLabel, String yLabel) {
		super();

		this.maxPoints = maxPoints;
		this.fontSize = fontSize;
		this.title = title;

		setBackgroundColor("#DDF");

		addCurve();

		setGivenTitle();
		if (legend != null) {
			getCurve().setLegendLabel(legend);
		}
		else {
			setLegendVisible(false);
		}

		setChartSize(maxPoints * 15, (int) (0.65 * maxPoints * 15));

		_setXAxis(xLabel);
		_setYAxis(yLabel);

		// getCurve().getSymbol().setSymbolType(SymbolType.HBAR_PREV);
		// getCurve().getSymbol().setSymbolType(SymbolType.LINE_CANVAS); //
		// LINE_CANVAS deprecated
		getCurve().getSymbol().setSymbolType(SymbolType.LINE);
		getCurve().getSymbol().setBorderColor("red");
		getCurve().getSymbol().setBackgroundColor("red");

		// start with NaN values
		for (int i = 0; i < maxPoints; i++) {
			getCurve().addPoint(i, Double.NaN);
		}

		reset();
		update();
	}

	private void _setXAxis(String xLabel) {
		Axis xAxis = getXAxis();
		xAxis.setTickLabelFontSize(fontSize - 2);
		// always set the x-label; otherwise undesirable height may result
		if (xLabel == null) {
			xLabel = "";
		}
		xAxis.setAxisLabel(GizUtil.createHtml(xLabel, fontSize - 2));
//		xAxis.setAxisLabel(xLabel);

		xAxis.setTickCount(maxPoints);
		xAxis.setTickLabelFontSize((int) Math.round(0.75 * xAxis.getTickLabelFontSize()));
	}

	private void _setYAxis(String yLabel) {
		Axis yAxis = getYAxis();
		
		int actualFontSize = fontSize;
//		int actualFontSize = (int) Math.round(0.75 * fontSize);

//		yAxis.setTickLabelFontSize(fontSize);
		yAxis.setTickLabelFontSize(actualFontSize);

		if (yLabel == null || yLabel.trim().length() == 0 ) {
			yLabel = "&nbsp;";
		}
//		yAxis.setAxisLabel(yLabel);
		yAxis.setAxisLabel(GizUtil.createHtml(yLabel, actualFontSize));

//-		yAxis.setTickLabelFontSize((int) Math.round(actualFontSize));

		yAxis.setTickCount(6);

		// yAxis.setTickLabelFormat("0.000E000");
//		 yAxis.setTickLabelFormat("0.############");

	}

	private IHoverManager hoverMan;

	public void setHoverManager(final IHoverManager hoverMan) {
		this.hoverMan = hoverMan;
		getCurve().getSymbol().setHovertextTemplate(
		// GChart.formatAsHovertext(
				"<html>" + hoverMan.getHovertextTemplate() + "</html>"
		// )
				);

		getY2Axis().setTickLabelFormat("+0.000;-0.000");
		getCurve().getSymbol().setHoverSelectionWidth(1);
		getCurve().getSymbol().setHoverSelectionBackgroundColor("gray");
		// use a vertical line for the selection cursor
		getCurve().getSymbol()
				.setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
		// tall brush so it touches independent of mouse y position
		getCurve().getSymbol().setBrushSize(25, 200);
		// so only point-to-mouse x-distance matters for hit testing
		getCurve().getSymbol().setDistanceMetric(1, 0);

		getCurve().getSymbol().setHoverAnnotationSymbolType(
				SymbolType.ANCHOR_SOUTHEAST);
		getCurve().getSymbol().setHoverYShift(-45);
		//getCurve().getSymbol().setHoverLocation(AnnotationLocation.SOUTHEAST);
		setHoverParameterInterpreter(new HoverParameterInterpreter() {
			public String getHoverParameter(String paramName, Point hoveredOver) {
//				Curve curve = hoveredOver.getParent();
//				int idx = curve.getPointIndex(hoveredOver);
//				curve.getNPoints();
//				Main.log(" idx = " +idx+ " maxPoints = " +maxPoints);
//				int base = maxPoints/2;
//				// 14 is rather arbitrary
//				if ( idx < 14 || idx <= base ) {
//					curve.getSymbol().setHoverLocation(AnnotationLocation.SOUTHEAST
//					);
//				}
//				else {
//					curve.getSymbol().setHoverLocation(AnnotationLocation.SOUTHWEST
//					);
//				}
				return hoverMan.getHoverParameter(paramName,
						hoveredOver.getX(), hoveredOver.getY());
			}
		});
	}

	public IHoverManager getHoverManager() {
		return hoverMan;
	}

	public void setGivenTitle() {
		if (title != null) {
			setChartTitle(GizUtil.createLabel(title, fontSize));
		}
	}

	public void reset() {
		Curve curve = getCurve();
		for (int i = 0; i < maxPoints; i++) {
			Point p = curve.getPoint(i);
			p.setX(i);
			p.setY(Double.NaN);
		}
		index = 0;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public int addPoint(double y) {
		addPoint(index, y);
		return index++;
	}

	void addPoint(double x, double y) {
		Curve curve = getCurve();
		Point p = curve.getPoint(0);
		for (int i = 1; i < maxPoints; i++) {
			Point q = curve.getPoint(i);
			p.setX(q.getX());
			p.setY(q.getY());
			p = q;
		}
		p.setX(x);
		p.setY(y);
	}

	public void putRandomData(double d, double e) {
		reset();
		for (int i = 0; i < maxPoints; i++) {
			addPoint(d + e * Math.random());
		}
	}

	public void setAxisLabel(String string) {
		getXAxis().setAxisLabel(GizUtil.createLabel(string, fontSize));
	}

	public void setYTickLabelFormat(String string) {
		getYAxis().setTickLabelFormat(string);
	}

	public void setXTickLabelFormat(String string) {
		getXAxis().setTickLabelFormat(string);
	}

	public void setChartTitle(String string) {
		setChartTitle(GizUtil.createHtml(string, 10));
	}

	public void setChartTitle(boolean error, String string) {
		if (error) {
			setChartTitle("<font color=red>ERROR</font>: " + string);
		}
		else {
			setChartTitle(string);
		}
	}

	public Widget asWidget() {
		return this;
	}

	public int getFontSize() {
		return fontSize;
	}

	/**
	 * Does nothing (not implemented in this class)
	 */
	public void setShowValuesX(boolean showValuesX) {
		// TODO Auto-generated method stub
	}

	public String getWidth() {
		String width = DOM.getStyleAttribute(getElement(), "width");
		return width;
	}

	public String getHeight() {
		String height = DOM.getStyleAttribute(getElement(), "height");
		return height;
	}
}
