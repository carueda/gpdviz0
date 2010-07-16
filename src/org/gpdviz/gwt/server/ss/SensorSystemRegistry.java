package org.gpdviz.gwt.server.ss;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.gpdviz.ss.SensorSystem;


/**
 * Registry of sensor systems.
 * @author Carlos Rueda
 */
class SensorSystemRegistry {
	
	private void _log(String msg) {
		System.out.println("XXX " +getClass().getSimpleName()+ ": " + msg);
	}

	private ConcurrentMap<String,SensorSystem> regSss = new ConcurrentHashMap<String,SensorSystem>();
	
	
	public SensorSystemRegistry() {
	}
	
	
	public void register(SensorSystem ss) {
		regSss.put(ss.getSsid(), ss);
		_log(": register: " +ss.getSsid());
	}

	public SensorSystem registerIfAbsent(SensorSystem ss) {
		SensorSystem prev = regSss.putIfAbsent(ss.getSsid(), ss);
		_log(": registerIfAbsent: " +ss.getSsid());
		return prev;
	}
	
	
	public Set<String> getRegisteredIds() {
		return regSss.keySet();
	}
	
	public SensorSystem getSensorSystem(String ssid) {
		return regSss.get(ssid);
	}


	public SensorSystem unregister(String ssid) {
		SensorSystem ss = regSss.remove(ssid);
		return ss;
	}

	public boolean containsKey(String ssid) {
		return regSss.containsKey(ssid);
	}
}
