package org.gpdviz.gwt.client.chart.impl;


import org.gpdviz.gwt.client.chart.IChart;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * An implementation of IChart based on generating an image for the
 * chart.
 * In this version, an embedded img element is used to obtain
 * the chart using the Google Chart API. 
 * 
 * @author Carlos Rueda
 */
public class ImageChart implements IChart {
    private final Point[] points;
    
    private int fontSize;
    private int index;

    private String xLabel;
    private String yLabel;

    private int width;
    private int height;

    private String title;

    private String h_string;
    private String w_string;

//    private int xSize;
//    private int ySize;
    

    private boolean errorTitle = false;
    
    private boolean showValuesX = true;

    private Widget widget;

    // testing option;  TODO remove when tests completed.
    // true => widget in HTML with "<img src=..." fragment
    // false=> widget is a Grid with Image component
    public static boolean useImgFragment = false;
    
    // Only used iff useImage == true.
    private Image gImg;
    

    
    public ImageChart(int maxPoints, int fontSize, String title, String legend, String xLabel, String yLabel) {
        this.fontSize = fontSize;
        
        // drop tags:
        if ( title != null ) {
            this.title = removeTags(title);
        }
        if ( xLabel != null ) {
            this.xLabel = removeTags(xLabel);
        }
        if ( yLabel != null ) {
            this.yLabel = removeTags(yLabel);
        }
        
        points = new Point[maxPoints];
        index = 0;
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(i, Double.NaN);
        }
        
        if ( useImgFragment ) {
            widget = new HTML();
        }
        else {
            widget = new Grid(1,1);
            Grid gTable = (Grid) widget;
            gTable.setStylePrimaryName("inline");
            gImg = new Image();
            gTable.setWidget(0, 0, gImg);
        }
        
        update();
    }
    
    private static String removeTags(String str) {
        return str.replaceAll("(<[^>]*>)", "");
    }
    
    
    public int addPoint(double y) {
        addPoint(index, y);
        return index++;
    }

    private void addPoint(int x, double y) {
        Point p = points[0];
        for (int i = 1; i < points.length; i++) {
            points[i-1] = points[i];
        }
        p.x = x;
        p.y = y;
        points[points.length - 1] = p;
    }
    
    public Widget asWidget() {
        return widget;
    }

    public void clearCurves() {
        index = 0;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getMaxPoints() {
        return points.length;
    }

    public void putRandomData(double d, double e) {
        reset();
        for (int i = 0; i < points.length; i++) {
            addPoint(d + e * Math.random());
        }
    }

    public void removeFromParent() {
        widget.removeFromParent();
    }

    public void reset() {
        index = 0;
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            p.x = i;
            p.y = Double.NaN;
        }
    }

    public void setAxisLabel(String string) {
        xLabel = string;
    }

    /** Ignored in this class for now */
    public void setYTickLabelFormat(String string) {
        // TODO
    }
    
    
    public void setChartSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void setChartTitle(String string) {
        title = string;
    }
    
    public void setChartTitle(boolean error, String string) {
        errorTitle = error;
        setChartTitle(string);
    }

    public void setGivenTitle() {
        setChartTitle(title);
    }

    public void setHeight(String string) {
        this.h_string = string;
    }

    public void setWidth(String string) {
        this.w_string = string;
    }

    public void setXChartSize(int width) {
    	// never read locally
//        this.xSize = width;
    }

    public void setYChartSize(int height) {
    	// never read locally
//        this.ySize = height;
    }

    public void update() {
        String imgUrl = updateImageUrl();
        
        if ( useImgFragment ) {
            HTML gHtml = (HTML) widget;
            
            imgUrl.replaceAll("\\&", "\\&amp;");

            StringBuffer sb = new StringBuffer();
            sb.append("<img src=\"" +imgUrl+ "\"")  ;
            
//            if ( title2 != null ) {
//                sb.append(" alt=\"" +title2+ "\"")  ;
//            }
            
            sb.append(" />");
            
            String html = sb.toString();

//            Main.log("ImageChart.update. html= " + html);
            
            gHtml.setHTML(
                    "<table class=\"inline\" width=\"" +(width+10)+ "\" height=\"" +(height+10)+ "\"" + ">" +
                    "<tr><td>" +
                    html +
                    "</td></tr>" +
                    "</table>"
            );
        }
        else {
            Image.prefetch(imgUrl);
            Grid gTable = (Grid) widget;
            gTable.setWidth("" +(width+10));
            gTable.setHeight("" +(height+10));
            gImg.setUrl(imgUrl);
        }
    }
    
    private String updateImageUrl() {
        double max = Double.NaN, min = Double.NaN;
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            if ( !Double.isNaN(p.y) ) {
                if ( i == 0 || max < p.y ) {
                    max = p.y;
                }
                if ( i == 0 || min > p.y ) {
                    min = p.y;
                }
            }
        }

//        Main.log("ImageChart.updateImageUrl:  points.length=" +points.length+ " max=" +max+ "  min=" +min);
        
        String col1 = "aaaaaa" ;
        String size = width+ "x" +height; // w_string+ "x" + h_string;

        StringBuffer imgUrl = new StringBuffer();
        
        imgUrl.append("http://chart.apis.google.com/chart?") ;
        imgUrl.append("chs=" +size) ;
//        sb.append("&chdl=Real-time|WRF generated") ;

        imgUrl.append("&chco=" +col1) ;
        imgUrl.append("&chls=1,1,0") ;
        imgUrl.append("&cht=lc")  ;
        
        //////// <axis>
        imgUrl.append("&chxt=y,x")  ;
        if ( xLabel != null ) {
            imgUrl.append(",x")  ;
        }
        // y axis:
        int axis = 0;
        StringBuffer chxs = new StringBuffer();
        chxs.append(axis+ ",000000," +(fontSize-2)+ ",1");
        imgUrl.append("&chxl=" +axis+ ":||" +"v"+ "|")  ;
        axis++;
        imgUrl.append(axis+ ":|")  ; 
        for (int i = 0; i < points.length; i++) {
            if ( showValuesX ) {
                Point p = points[i];
                imgUrl.append(p.x+ "|");
            }
            else {
                imgUrl.append("|"); // empty labels
                // Note: I put these empty labels so the dots are not cut in half
                // at the bottom of the chart.
            }                    
        }
        chxs.append("|" +axis+ ",000000," +(fontSize-2)+ ",0");
        axis++;
        if ( xLabel != null ) {
            imgUrl.append(axis+ ":|" +xLabel+ "|")  ;
            imgUrl.append("&chxp=" +axis+ ",50")  ;
            chxs.append("|" +axis+ ",000000," +(fontSize-2)+ ",0");
            axis++;
        }
        imgUrl.append("&chxs=" +chxs.toString());
        
        //////// </axis>
        
        String title2 = null;
        if ( title != null ) {
            title2 = title.replaceAll("\\s", "+");
        }
        
        if ( !errorTitle && yLabel != null ) { 
            //sb.append("1:||" +yLabel+ "|")  ;
            // use the title:
            if ( title2 != null ) {
                title2 += (" (" +yLabel+ ")").replaceAll("\\s", "+");
            }
            else {
                title2 = yLabel;
            }
        }
        
        
        if ( title2 != null ) {
            imgUrl.append("&chtt=" +title2) ;
            String titleColor  = errorTitle ? "ff0000" : "000000";
            imgUrl.append("&chts=" +titleColor+ "," +(fontSize+ (errorTitle? 0 : 2)));
        }
                
        imgUrl.append("&chd=t:") ;
        
        String c = "";
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            int y = -1;
            if ( !Double.isNaN(p.y) && !Double.isNaN(max) && !Double.isNaN(min) ) {
                double val = p.y - min + 0.0001;                
                y = (int) (height * val / (max-min + 0.0001));
            }
//            Main.log(" ImageChart:  p.y = " +p.y+ "   y = " +y);
            imgUrl.append(c+ y);
            c = ",";
        }

        imgUrl.append("&chm=") ;
        c = "";
        for (int i = 0; i < points.length; i++) {
            imgUrl.append(c + "s," +"ff0000"+ ",0," +i+ ",4.0")  ;
            c = "|";
        }
        
        return imgUrl.toString();
    }
    

    public static class Point {
        private int x;
        private double y;

        public Point(int x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public void setShowValuesX(boolean showValuesX) {
        this.showValuesX = showValuesX;
    }
    
    /** Not implemented */
    public void setHoverManager(final IHoverManager hoverMan) {
    	// Not implemented.
    }
    public IHoverManager getHoverManager() {
    	return null;
    }

	public String getHeight() {
		return h_string;
	}

	public String getWidth() {
		return w_string;
	}
	
	/** Not implemented. Returns zero */
	public int getYChartSizeDecorated() {
		return 0;
	}

	public void setXTickLabelFormat(String string) {
		// TODO Auto-generated method stub
		
	}
}
