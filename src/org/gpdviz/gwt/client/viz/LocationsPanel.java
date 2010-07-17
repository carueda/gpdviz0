package org.gpdviz.gwt.client.viz;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An ad hoc panel for the various locations for when the Google Maps is not used.
 * @author Carlos Rueda
 */
public class LocationsPanel {

	Widget getWidget() {
    	return panel;
    }

	void addLoc(String lat, String lon, Widget widget) {
		CellPanel elem = _getElement(lat, lon);
		if ( elem == null ) {
			elem = _createElement(lat, lon, widget);
			panel.add(elem);
		}
		else {
			elem.clear();
		}
		elem.setBorderWidth(1);
		elem.add(new Label("Location: lat,lon=" +lat+ "," +lon));
		elem.add(widget);
	}

	void removeLoc(String lat, String lon) {
		CellPanel elem = _removeElement(lat, lon);
		if ( elem != null ) {
			panel.remove(elem);
		}
	}

	void clear() {
		panel.clear();
	}

	
	//////////////////////////////////////////////////////////////////////
	// private
	//////////////////////////////////////////////////////////////////////
	
	private HorizontalPanel panel = new HorizontalPanel();
	private Map<String,CellPanel> elements = new HashMap<String,CellPanel>();
	
	
	private CellPanel _createElement(String lat, String lon, Widget widget) {
		String key = lat+ "," +lon;
		CellPanel elem = new VerticalPanel();
		elements.put(key, elem);
		return elem;
	}
	
	private CellPanel _getElement(String slat, String slon) {
		String key = slat+ "," +slon;
		CellPanel elem = elements.get(key);
		return elem;
	}
	
	private CellPanel _removeElement(String slat, String slon) {
		String key = slat+ "," +slon;
		CellPanel elem = elements.remove(key);
		return elem;
	}
}
