package org.gpdviz.gwt.client.viz;

import java.util.HashMap;
import java.util.Map;

import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;

/**
 * Maintains the current main panels.
 * @author Carlos Rueda
 */
public class Panels {
	private Panels() {}

	private static Map<String, LocationPanel> locPanels = new HashMap<String, LocationPanel>();
	private static Map<String, SourcePanel> srcPanels = new HashMap<String, SourcePanel>();
	private static Map<String, StreamPanel> strPanels = new HashMap<String, StreamPanel>();

	
	public static void reset() {
		locPanels.clear();
		srcPanels.clear();
		strPanels.clear();
	}
	
	
	public static LocationPanel getLocationPanel(String lat, String lon) {
		String key = lat + "," + lon;
		return locPanels.get(key);
	}

	public static LocationPanel createLocationPanel(String lat, String lon, boolean scroll) {
		LocationPanel panel = new LocationPanel(lat, lon, scroll);
		String key = lat + "," + lon;
		locPanels.put(key, panel);
		return panel;
	}

	public static SourcePanel getSourcePanel(String srcfid) {
		return srcPanels.get(srcfid);
	}
	
	public static SourcePanel createSourcePanel(Source src) {
		SourcePanel panel = new SourcePanel(src);
		String key = src.getFullName();
		srcPanels.put(key, panel);
		return panel;
	}
	
	public static StreamPanel getStreamPanel(String strfid) {
		return strPanels.get(strfid);
	}
	
	public static int getNumberOfStreamPanels() {
		return strPanels.size();
	}
	
	public static StreamPanel createStreamPanel(Stream str) {
		StreamPanel panel = new StreamPanel(str);
		String key = str.getFullName();
		strPanels.put(key, panel);
		return panel;
	}
	
}
