package org.gpdviz.ss.event;


public class SensorSystemRegisteredEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	SensorSystemRegisteredEvent() {
	}
	
	public SensorSystemRegisteredEvent(String ssid) {
		super(ssid);
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSensorSystemRegisteredEvent(this);
	}
	

	protected String getSuffix() {
		return "+REGISTERED";
	}
	
//	@Override
//	public IvEvent clone() {
//		return new NewSourceEvent(ssid, src);
//	}

}
