package org.gpdviz.gwt.client.viz;


import java.util.List;

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
import com.google.gwt.user.client.ui.Panel;
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
	private Label statusLabel = new Label("event");
	
	private HorizontalPanel gmapPanel = new HorizontalPanel();
	private GMap gmap;
    
	// used when no gmap:
	private final Panel adhocPanel = new HorizontalPanel();
	
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
    	add(adhocPanel);
    	
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
		else if ( adhocPanel != null ) {
			adhocPanel.clear();
		}
		Panels.reset();
		setStatus("RESET");
	}

	void setStatus(String string) {
		statusLabel.setText(string);
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

	private void _addLoc(String lat, String lon, Widget widget) {
		if ( gmap != null ) {
			gmap.addLoc(lat, lon, widget);
		}
		else {
			VerticalPanel vp = new VerticalPanel();
			vp.setBorderWidth(1);
			adhocPanel.add(vp);
			vp.add(new Label("Add location: lat,lon=" +lat+ "," +lon));
			vp.add(widget);
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
        if ( adhocPanel != null || Panels.getNumberOfStreamPanels() == 1 ) {
        	strPanel.openChart();	
        }
        
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
				setStatus("ERROR: " +caught.getClass().getName()+ ": " +caught.getMessage());
			}

			public void onSuccess(SensorSystemInfo ss) {
				if ( ss == null ) {
					descriptionLabel.setText("?");
					setStatus("NOTE: " + "No sensor system by the given ID: " +ssid);
					return;
				}
				descriptionLabel.setText("Sensor system: " +ssid+ ": " +ss.getDescription());
				refreshState(ss);
			}
		});
		
	}


	private void refreshState(final SensorSystemInfo ss) {
		
		// initialize interface with obtained sensor system info:
		
		// add sources:
		for ( String srcfid : ss.getSourceFullIds() ) { 
			final Source src = ss.getSource(srcfid);
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					addSource(src);
				}
			});
		}
		
		// for each, source, add its streams, 
		// each with its last known value, if any:
		for ( String srcfid : ss.getSourceFullIds() ) { 
			List<Stream> strs = ss.getStreams(srcfid );
			for ( final Stream str : strs ) {
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						addStream(str);
						
						String strfid = str.getFullName();
						List<String> values = ss.getLastValue(strfid);
						if ( values != null ) {
							for ( String value : values ) {
								addNewValue(strfid, value);
							}
						}
					}
				});
			}
		}
		
		// TODO add latest values for each stream.
		// ...
		
		// and prepare to receive events:
		setStatus(ssid+ ": waiting for updates...");
		EventCommandHandlers.setHandlers(ssid, this, messages);
	}

}

