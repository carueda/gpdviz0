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
 * Handles a MapWidget.
 * 
 * @author Carlos Rueda
 */
public class GMap {
	
	public GMap() {
		_map.setZoomLevel(3);
		_map.setSize("1000px", "500px");

		// Add some controls for the zoom level
		_map.addControl(new LargeMapControl());

	    MapUIOptions opts = _map.getDefaultUI();
	    opts.setDoubleClick(false);
	    _map.setUI(opts);
	    
		_widget.add(_map);
		
	}
	
	public Widget getWidget() {
		return _widget;
	}
	
	public void clear() {
		_map.closeInfoWindow();
		_map.clearOverlays();
		_markers.clear();
	}
	
	public void addLoc(String slat, String slon, final Widget content) {
		
		if ( getMarker(slat, slon) != null ) {
			return; // location already in the map
		}
		
		final Marker marker = createMarker(slat, slon);
		_map.addOverlay(marker);
		
		final InfoWindowContent ic = new InfoWindowContent(content);
		
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent e) {
				_map.getInfoWindow().open(marker.getLatLng(), ic);
			}
		});
		
		if ( ! _map.getInfoWindow().isVisible() ) {
			// pan to marker
			_map.panTo(marker.getLatLng());
			// and open infoWindow
			_map.getInfoWindow().open(marker.getLatLng(), ic);
		}
	}

	public void updateLoc(final String slat, final String slon, final Widget content) {
		if ( ! _map.getInfoWindow().isVisible() ) {
			return; // no need.
		}
		
		final Marker marker = getMarker(slat, slon);
		assert marker != null;
		
		// Only do the update if the location corresponds to the open infoWindow:
		//
		LatLng infoWindowPoint = _map.getInfoWindow().getPoint();
		if ( infoWindowPoint.isEquals(marker.getLatLng()) ) {
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					InfoWindowContent ic = new InfoWindowContent(content);
					_map.getInfoWindow().open(marker.getLatLng(), ic);
				}
			});
		}
	}
	
	public void removeLoc(String slat, String slon) {
		Marker marker = removeMarker(slat, slon);
		if ( marker != null ) {
			_map.removeOverlay(marker);
			
			// close infoWindow if it's the one associated with this marker:
			LatLng infoWindowPoint = _map.getInfoWindow().getPoint();
			if ( infoWindowPoint.isEquals(marker.getLatLng()) ) {
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						_map.getInfoWindow().close();
					}
				});
			}

		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// private 
	//////////////////////////////////////////////////////////////////////////
	
	/** my actual panel */
	private final HorizontalPanel _widget = new HorizontalPanel();
	
	private final MapWidget _map = new MapWidget();
	
	private Map<String,Marker> _markers = new HashMap<String,Marker>();

	
	private Marker getMarker(String slat, String slon) {
		String key = slat+ "," +slon;
		Marker marker = _markers.get(key);
		return marker;
	}
	
	private Marker removeMarker(String slat, String slon) {
		String key = slat+ "," +slon;
		Marker marker = _markers.remove(key);
		return marker;
	}
	
	private Marker createMarker(String slat, String slon) {
		String key = slat+ "," +slon;
		
		double lat = Double.parseDouble(slat);
		double lon = Double.parseDouble(slon);
		
		LatLng point = LatLng.newInstance(lat, lon);
		final Marker marker = new Marker(point);

		_markers.put(key, marker);
		return marker;
	}
	

}