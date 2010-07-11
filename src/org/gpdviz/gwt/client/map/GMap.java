package org.gpdviz.gwt.client.map;


//
// From: http://code.google.com/docreader/#p=gwt-google-apis&s=gwt-google-apis&t=MapsGettingStarted
//

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Carlos Rueda
 */
public class GMap {
	
	private final HorizontalPanel widget = new HorizontalPanel();
	private final MapWidget map = new MapWidget();
	
	
	private Map<String,Marker> markers = new HashMap<String,Marker>();
	

	public GMap() {
		map.setZoomLevel(3);
		map.setSize("1000px", "500px");

		// Add some controls for the zoom level
		map.addControl(new LargeMapControl());

	    MapUIOptions opts = map.getDefaultUI();
	    opts.setDoubleClick(false);
	    map.setUI(opts);
	    
		widget.add(map);
		
	}
	
	
	public void clear() {
		map.closeInfoWindow();
		map.clearOverlays();
	}
	
	private Marker getMarker(String slat, String slon) {
		String key = slat+ "," +slon;
		Marker marker = markers.get(key);
		return marker;
	}
	
	private Marker createMarker(String slat, String slon) {
		String key = slat+ "," +slon;
		
		double lat = Double.parseDouble(slat);
		double lon = Double.parseDouble(slon);
		
		LatLng point = LatLng.newInstance(lat, lon);
		final Marker marker = new Marker(point);

		markers.put(key, marker);
		return marker;
	}
	
	public void addLoc(String slat, String slon, final Widget content) {
		
		if ( getMarker(slat, slon) != null ) {
			return; // location already in the map
		}
		
		final Marker marker = createMarker(slat, slon);
		map.addOverlay(marker);
		
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent e) {
				InfoWindowContent ic = new InfoWindowContent(content);
				map.getInfoWindow().open(marker.getLatLng(), ic);
			}
		});
		
		if ( ! map.getInfoWindow().isVisible() ) {
			map.panTo(marker.getLatLng());
		}
	}

	public void updateLoc(final String slat, final String slon, final Widget content) {
		if ( ! map.getInfoWindow().isVisible() ) {
			return; // no need.
		}
		
		final Marker marker = getMarker(slat, slon);
		assert marker != null;
		
		// Only do the update if the location corresponds to the open infoWindow:
		//
		LatLng infoWindowPoint = map.getInfoWindow().getPoint();
		if ( infoWindowPoint.isEquals(marker.getLatLng()) ) {
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					InfoWindowContent ic = new InfoWindowContent(content);
					map.getInfoWindow().open(marker.getLatLng(), ic);
				}
			});
		}
	}
	
	
	public Widget getWidget() {
		return widget;
	}
}