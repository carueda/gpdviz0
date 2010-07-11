package org.gpdviz.ss.event;


public class SensorSystemResetEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	SensorSystemResetEvent() {
	}
	
	public SensorSystemResetEvent(String ssid) {
		super(ssid);
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSensorSystemResetEvent(this);
	}
	

	protected String getSuffix() {
		return "*RESET";
	}
	
//	@Override
//	public IvEvent clone() {
//		return new NewSourceEvent(ssid, src);
//	}

}
