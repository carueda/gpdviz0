package org.gpdviz.gwt.client.viz;

import org.gpdviz.gwt.client.chart.ChartCreator;
import org.gpdviz.gwt.client.chart.IChart;
import org.gpdviz.ss.Stream;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StreamPanel {

	private Stream str;
	private DisclosurePanel widget;
	
	private ChartPanel chartPanel;
	
	private Label lastValue = new Label("lastValue: ");
	private IChart chart;
	
	
	StreamPanel(Stream str) {
		this.str = str;
		widget = new DisclosurePanel("stream: " +str.getFullName());
		
//		VerticalPanel header = new VerticalPanel();
//		header.add(new Label("stream: " +str.getFullName()));
//		header.add(new Label("click here to open chart--TODO"));
//		header.add(lastValue);
//		widget.setHeader(header);
		
		
        // create Chart on demand
		widget.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				openChart();
			}
		});

//		widget.addCloseHandler(new CloseHandler<DisclosurePanel>() {
//			public void onClose(CloseEvent<DisclosurePanel> event) {
//				chart = null;
//			}
//		});
	}

	
	
    public Stream getStr() {
		return str;
	}



	public Widget getWidget() {
    	return widget;
    }


	
	private IChart _createChart() {
		
		// TODO capture all these attrs appropriately
        String title = "myTitle";
        String legend = null;
        String xLabel = null; 
        String yLabel = "myUnits"; //stream.getStringAttribute("units");
        
        IChart chart = ChartCreator.createChartStyle1(title, legend, xLabel, yLabel); 

//		chart.putRandomData(-50, 100);

		return chart;
	}

	public void openChart() {
		if ( chart == null ) {
			chart = _createChart();
			chart.update();
			chartPanel = new ChartPanel(chart);
			widget.add(chartPanel);
		}
		widget.setOpen(true);
	}

	public void update() {
		if ( chart != null ) {
			chart.update();
		}
	}


	public void addPoint(double value) {
		lastValue.setText(String.valueOf(value));
		if ( chart != null ) {
			chart.addPoint(value);
		}
	}

}
