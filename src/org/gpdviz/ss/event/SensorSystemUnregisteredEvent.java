package org.gpdviz.ss.event;


public class SensorSystemUnregisteredEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	SensorSystemUnregisteredEvent() {
	}
	
	public SensorSystemUnregisteredEvent(String ssid) {
		super(ssid);
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSensorSystemUnregisteredEvent(this);
	}
	

	protected String getSuffix() {
		return "+UNREGISTERED";
	}
	
//	@Override
//	public IvEvent clone() {
//		return new NewSourceEvent(ssid, src);
//	}

}
