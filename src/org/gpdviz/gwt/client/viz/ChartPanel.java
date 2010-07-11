package org.gpdviz.gwt.client.viz;

import org.gpdviz.gwt.client.chart.IChart;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel for a chart.
 * @author Carlos Rueda
 */
public class ChartPanel extends VerticalPanel {
 
    private IChart ichart;

	public ChartPanel(IChart ichart) {
        this.ichart = ichart;
        setBorderWidth(1);
        add(ichart.asWidget());
        
//        setHeight(ichart.getHeight());
//        setWidth(ichart.getWidth());
    }
    
    public IChart getIchart() {
		return ichart;
	}

}

