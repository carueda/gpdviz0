package org.gpdviz.ss.event;

import org.gpdviz.ss.SensorSystemInfo;


public class SensorSystemRegisteredEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	private SensorSystemInfo ssi;
	
	SensorSystemRegisteredEvent() {
	}
	
	public SensorSystemRegisteredEvent(String ssid, SensorSystemInfo ssi) {
		super(ssid);
		this.ssi = ssi;
	}

	
	public SensorSystemInfo getSensorSystemInfo() {
		return ssi;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSensorSystemRegisteredEvent(this);
	}
	

	protected String getSuffix() {
		return "+REGISTERED";
	}
	
}
