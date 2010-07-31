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
    	return _panel;
    }

	void addLoc(String lat, String lon, Widget widget) {
		CellPanel elem = _getElement(lat, lon);
		if ( elem == null ) {
			elem = _createElement(lat, lon, widget);
			_panel.add(elem);
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
			_panel.remove(elem);
		}
	}

	void clear() {
		_panel.clear();
		_elements.clear();
	}

	
	//////////////////////////////////////////////////////////////////////
	// private
	//////////////////////////////////////////////////////////////////////
	
	private HorizontalPanel _panel = new HorizontalPanel();
	private Map<String,CellPanel> _elements = new HashMap<String,CellPanel>();
	
	
	private CellPanel _createElement(String lat, String lon, Widget widget) {
		String key = lat+ "," +lon;
		CellPanel elem = new VerticalPanel();
		_elements.put(key, elem);
		return elem;
	}
	
	private CellPanel _getElement(String slat, String slon) {
		String key = slat+ "," +slon;
		CellPanel elem = _elements.get(key);
		return elem;
	}
	
	private CellPanel _removeElement(String slat, String slon) {
		String key = slat+ "," +slon;
		CellPanel elem = _elements.remove(key);
		return elem;
	}
}
