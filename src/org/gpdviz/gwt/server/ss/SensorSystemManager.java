package org.gpdviz.gwt.server.ss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gpdviz.ss.event.IvEventListener;
import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.SensorSystemInfo;
import org.gpdviz.ss.event.IvEvent;

/**
 * 
 * @author Carlos Rueda
 */
public class SensorSystemManager {
	
	////////////////////////////////////////////////////////////////////////////////
	// static
	////////////////////////////////////////////////////////////////////////////////
	
	
	// sensorSystemId -> SensorSystemManager
	private static Map<String,SensorSystemManager> ssManagers = new 
		HashMap<String,SensorSystemManager>();
	
	
	public static synchronized SensorSystemManager createSensorSystemManager(SensorSystem ss) {
		String ssid = ss.getSsid();
		SensorSystemManager ssManager = ssManagers.get(ssid);
		if ( ssManager == null ) {
			ssManager = new SensorSystemManager(ssid, ss);
			ssManagers.put(ssid, ssManager);
		}
		return ssManager;
	}

	public static synchronized SensorSystemManager getSensorSystemManager(String ssid) {
		SensorSystemManager ssManager = ssManagers.get(ssid);
		return ssManager;
	}
	
	private static synchronized void removeSensorSystemManager(SensorSystemManager ssMan) {
		String ssid = ssMan.sensorSystem.getSsid();
		ssManagers.remove(ssid);
	}

	
	////////////////////////////////////////////////////////////////////////////////
	// Instance.
	////////////////////////////////////////////////////////////////////////////////
	
	
//	private final Log log = LogFactory.getLog(SensorSystemManager.class);

	
	/** the managed sensor system */
	private SensorSystem sensorSystem;
	
	private List<IvEventListener> listeners = 
		Collections.synchronizedList(
				new ArrayList<IvEventListener>()
		);

	
	private SensorSystemManager(String sensorSystemId, SensorSystem ss) {
//		Util.debug(log, "Creating sensor system instance");
		
		sensorSystem = ss;
		sensorSystem.addEventListener(new IvEventListener() {
			public void eventGenerated(IvEvent event) {
				notifyListeners(event);
			}
		});
	}
	
	
	public SensorSystemInfo getSensorSystemInfo() {
		return sensorSystem.getSensorSystemInfo();
	}
	
	private void notifyListeners(IvEvent event) {
		synchronized (listeners) {
			for ( IvEventListener listener : listeners ) {
				listener.eventGenerated(event);
			}
		}
	}

	/**
	 * Adds the given listener if not already added.
	 * @param listener
	 */
	public void addEventListener(IvEventListener listener) {
		if ( ! listeners.contains(listener) ) {
			listeners.add(listener);
		}
	}
	
	public void removeEventListener(IvEventListener listener) {
		listeners.remove(listener);
	}
	
	// TODO In general, review release of unused resources
	public void deactivate() {
		removeSensorSystemManager(this);
	}
}
