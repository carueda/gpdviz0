package org.gpdviz.gwt.server.ss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.gpdviz.ss.SensorSystem;


/**
 * Registry of sensor systems.
 * @author Carlos Rueda
 */
class SensorSystemRegistry {
	
	interface SsrListener {
		
		public void sensorSystemRegistered(SensorSystem ss);
		
		public void sensorSystemUnregistered(SensorSystem ss);
	}

	
	private List<SsrListener> listeners = Collections.synchronizedList(
			new ArrayList<SsrListener>()
	);
	
	private ConcurrentMap<String,SensorSystem> regSss = new ConcurrentHashMap<String,SensorSystem>();
	
	
	public SensorSystemRegistry() {
	}
	
	
	public void addSsrListener(SsrListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeSsrListener(SsrListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	public void register(SensorSystem ss) {
		regSss.put(ss.getSsid(), ss);
		System.out.println(this.getClass().getName()+ ": register: " +ss.getSsid());
		_notifySensorSystemRegistered(ss);
	}

	public SensorSystem registerIfAbsent(SensorSystem ss) {
		SensorSystem prev = regSss.putIfAbsent(ss.getSsid(), ss);
		System.out.println(this.getClass().getName()+ ": register: " +ss.getSsid());
		_notifySensorSystemRegistered(ss);
		return prev;
	}
	
	
	public Set<String> getRegisteredIds() {
		return regSss.keySet();
	}
	
	public SensorSystem getSensorSystem(String ssid) {
		return regSss.get(ssid);
	}


	public String unregister(String ssid) {
		SensorSystem ss = regSss.remove(ssid);
		if ( ss != null ) {
			_notifySensorSystemUnregistered(ss);
			return ssid+ ": should be unregistered now.";
		}
		else {
			return ssid+ ": no such sensor system registered.";
		}
	}

	public boolean containsKey(String ssid) {
		return regSss.containsKey(ssid);
	}
	
	
	private void _notifySensorSystemRegistered(SensorSystem ss) {
		synchronized (listeners) {
			for ( SsrListener listener : listeners ) {
				listener.sensorSystemRegistered(ss);
			}
		}
	}
	
	private void _notifySensorSystemUnregistered(SensorSystem ss) {
		synchronized (listeners) {
			for ( SsrListener listener : listeners ) {
				listener.sensorSystemUnregistered(ss);
			}
		}
	}
	

}
