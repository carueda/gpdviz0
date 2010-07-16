package org.gpdviz.ss.event;


public class SensorSystemUnregisteredEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	private String msg;
	
	SensorSystemUnregisteredEvent() {
	}
	
	public SensorSystemUnregisteredEvent(String ssid, String msg) {
		super(ssid);
		this.msg = msg;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSensorSystemUnregisteredEvent(this);
	}
	
	public String getMessage() {
		return msg;
	}

	protected String getSuffix() {
		return "+UNREGISTERED";
	}
}
