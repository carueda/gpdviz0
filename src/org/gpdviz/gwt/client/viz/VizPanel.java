package org.gpdviz.gwt.client.viz;


import java.util.List;

import org.gpdviz.gwt.client.Gpdviz;
import org.gpdviz.gwt.client.map.GMap;
import org.gpdviz.gwt.client.service.GpdvizServiceAsync;
import org.gpdviz.gwt.client.util.MessagesPopup;
import org.gpdviz.ss.SensorSystemInfo;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main viz panel.
 * @author Carlos Rueda
 */
public class VizPanel extends VerticalPanel {
 
	private final GpdvizServiceAsync gpdvizService;
	private final String ssid;
	private Label descriptionLabel = new Label("");
	private HTML statusLabel = new HTML("event");
	
	private HorizontalPanel gmapPanel = new HorizontalPanel();
	private GMap gmap;
    
	// used when no gmap:
	private final LocationsPanel locsPanel = new LocationsPanel();
	
	private final MessagesPopup messages = new MessagesPopup();


    /**
     * Creates the main panel.
     * @param metadata
     * @param params
     */
	public VizPanel(GpdvizServiceAsync gpdvizService, String ssid, boolean useGmap) {
		
		this.gpdvizService = gpdvizService;
		this.ssid = ssid;
		
//		setBorderWidth(1);
		
		VerticalPanel statusPanel = new VerticalPanel();
		
		// TODO remove this temporary filler
		statusPanel.add(new HTML("<br/><br/>"));
		
		statusPanel.add(descriptionLabel);
		statusPanel.add(statusLabel);
		add(statusPanel);
		
    	add(gmapPanel);
    	add(locsPanel.getWidget());
    	
		// map:
    	if ( useGmap ) {
    		try {
    			gmap = new GMap();
    			gmapPanel.add(gmap.getWidget());
    		}
    		catch ( Throwable thr ) {
    			// may happen if no internet connection, or invalid API code, etc.
    			gmapPanel.add(new Label("Error while creating GMap: " +thr.getMessage()));
    		}
    	}
	}
	
	public void reset() {
		if ( gmap != null ) {
			gmap.clear();
		}
		else if ( locsPanel != null ) {
			locsPanel.clear();
		}
		Panels.reset();
		setStatus("RESET");
	}
	
	public void unregister() {
		reset();
		refreshState(null);
		Gpdviz.log(this.getClass().getName()+ ": unregister: " +ssid);
	}
	
	private void setStatus(String string) {
		statusLabel.setHTML(string);
	}
	
	public void addSource(Source src) {
		String lat = src.getStringAttribute("latitude");
		String lon = src.getStringAttribute("longitude");
		
		SourcePanel srcPanel = Panels.getSourcePanel(src.getFullName());
		if ( srcPanel == null ) {
			srcPanel = Panels.createSourcePanel(src);
		}

		boolean addLoc = false;
		LocationPanel locPanel = Panels.getLocationPanel(lat, lon);
		if ( locPanel == null ) {
			locPanel = Panels.createLocationPanel(lat, lon, gmap != null);
			addLoc = true;
		}
		locPanel.addSourcePanel(srcPanel);
		
		if ( addLoc ) {
			_addLoc(lat, lon, locPanel.getWidget());
		}
		else {
			_updateLoc(lat, lon, locPanel.getWidget());
		}

	}

	public void removeSource(String srcfid) {
		SourcePanel srcPanel = Panels.getSourcePanel(srcfid);
		if ( srcPanel == null ) {
			return;
		}
		
		Source src = srcPanel.getSource();
		String lat = src.getStringAttribute("latitude");
		String lon = src.getStringAttribute("longitude");
		
		LocationPanel locPanel = Panels.getLocationPanel(lat, lon);
		if ( locPanel == null ) {
			return;
		}
		locPanel.removeSourcePanel(srcPanel);
		
		if ( locPanel.getNumberOfSourcePanels() == 0 ) {
			// remove the location panel
			Panels.removeLocationPanel(lat, lon);
			_removeLoc(lat, lon);
		}
	}
	
	private void _addLoc(String lat, String lon, Widget widget) {
		if ( gmap != null ) {
			gmap.addLoc(lat, lon, widget);
		}
		else {
			locsPanel.addLoc(lat, lon, widget);
		}
	}

	private void _updateLoc(String lat, String lon, Widget widget) {
		if ( gmap != null ) {
			gmap.updateLoc(lat, lon, widget);
		}
		else {
			// nothing to do.
		}
	}

	private void _removeLoc(String lat, String lon) {
		if ( gmap != null ) {
			gmap.removeLoc(lat, lon);
		}
		else {
			locsPanel.removeLoc(lat, lon);
		}
	}


	public void addStream(Stream str) {
		
		String srcfid = str.getParentFullName();
		assert srcfid != null;  // all streams must have a parent
		
		SourcePanel srcPanel = Panels.getSourcePanel(srcfid);
		assert srcPanel != null; // the parent (source) must have been inserted already.

		// now create the stream panel
		StreamPanel strPanel = Panels.getStreamPanel(str.getFullName());
		if ( strPanel == null ) {
			strPanel = Panels.createStreamPanel(str);
		}
		
		srcPanel.addStreamPanel(strPanel);

        strPanel.update();
        
        // open chart immediately if we are using the ad hoc panel OR if first stream: 
        if ( locsPanel != null || Panels.getNumberOfStreamPanels() == 1 ) {
        	strPanel.openChart();	
        }
        
        Source src = srcPanel.getSource();
        String lat = src.getStringAttribute("latitude");
        String lon = src.getStringAttribute("longitude");
		LocationPanel locPanel = Panels.getLocationPanel(lat, lon);
		assert locPanel != null ;
		_updateLoc(lat, lon, locPanel.getWidget());

	}
	
	public void removeStream(String srcfid, String strid) {
		
		SourcePanel srcPanel = Panels.getSourcePanel(srcfid);
		if ( srcPanel == null ) {
			return;
		}
		
		// TODO
		String strfid = srcfid+ "/" +strid;
		
		// now create the stream panel
		StreamPanel strPanel = Panels.getStreamPanel(strfid);
		if ( strPanel == null ) {
			return;
		}
		
		srcPanel.removeStreamPanel(strPanel);
		
		Source src = srcPanel.getSource();
		String lat = src.getStringAttribute("latitude");
		String lon = src.getStringAttribute("longitude");
		LocationPanel locPanel = Panels.getLocationPanel(lat, lon);
		assert locPanel != null ;
		_updateLoc(lat, lon, locPanel.getWidget());
		
	}
	
	public void addNewValue(String strfid, String value) {
		StreamPanel strPanel = Panels.getStreamPanel(strfid);
//		Stream str = strPanel.getStr();
		strPanel.addPoint(Double.parseDouble(value));
		strPanel.update();
	}
	
	
	/** performs the init sequence */
	public void init() {
		gpdvizService.connect(ssid, new AsyncCallback<SensorSystemInfo>() {

			public void onFailure(Throwable caught) {
				setStatus("<font color=\"red\">" +"ERROR" +":</font> " +caught.getClass().getName()+ ": " +caught.getMessage());
			}

			public void onSuccess(SensorSystemInfo ssi) {
				refreshState(ssi);
				
				// and prepare to receive events:
				// Note, this is done even if no such sensor system is currently registered
				EventCommandHandlers.setHandlers(ssid, VizPanel.this, messages);
			}
		});
		
	}


	private void refreshState(final SensorSystemInfo ssi) {
		
		Gpdviz.log(this.getClass().getName()+ ": refreshState: " +ssid+ ": " +ssi);

		if ( ssi == null ) {
			descriptionLabel.setText("?");
			setStatus("<font color=\"red\">" +ssid+ "</font>: sensor system not currently registered. Waiting for registration event...");
			return;
		}

		descriptionLabel.setText("Sensor system: " +ssid+ ": " +ssi.getDescription());
		setStatus("<b>" +ssid+ "</b>: waiting for updates...");
		
		// initialize interface with obtained sensor system info:
		
		// add sources:
		for ( String srcfid : ssi.getSourceFullIds() ) { 
			final Source src = ssi.getSource(srcfid);
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					addSource(src);
				}
			});
		}
		
		// for each, source, add its streams, 
		// each with its last known value, if any:
		for ( String srcfid : ssi.getSourceFullIds() ) { 
			List<Stream> strs = ssi.getStreams(srcfid );
			for ( final Stream str : strs ) {
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						addStream(str);
						
						String strfid = str.getFullName();
						List<String> values = ssi.getLastValue(strfid);
						if ( values != null ) {
							for ( String value : values ) {
								addNewValue(strfid, value);
							}
						}
					}
				});
			}
		}
	}

	public String getSsid() {
		return ssid;
	}

	public void setSensorSystemInfo(SensorSystemInfo ssi) {
		refreshState(ssi);		
	}

}

