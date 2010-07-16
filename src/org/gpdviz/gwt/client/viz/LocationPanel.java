package org.gpdviz.gwt.client.viz;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Carlos Rueda
 */
public class LocationPanel {
	
	private VerticalPanel widget = new VerticalPanel();
	
	private Panel sources = new HorizontalPanel();
	private ScrollPanel sourcesScrollPanel = new ScrollPanel(sources);
 
    public LocationPanel(String lat, String lon, boolean scroll) {
        HorizontalPanel hp = new HorizontalPanel();
        widget.add(hp);
        hp.add(new HTML("lat,lon=" +lat+ ", " +lon));
        if ( scroll ) {
        	widget.add(sourcesScrollPanel);
        	sourcesScrollPanel.setHeight("300px");
        	sourcesScrollPanel.setWidth("500px");
        }
        else {
        	widget.add(sources);
        }
    }
    
    public Widget getWidget() {
    	return widget;
    }

	public void addSourcePanel(SourcePanel srcPanel) {
		sources.add(srcPanel.getWidget());
	}
    
	public void removeSourcePanel(SourcePanel srcPanel) {
		sources.remove(srcPanel.getWidget());
	}
	
}

