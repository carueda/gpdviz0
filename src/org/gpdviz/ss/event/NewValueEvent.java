package org.gpdviz.ss.event;

public class NewValueEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	private String strid;
	private String strfid;
	private String value;
	
	NewValueEvent() {
	}
	
	public NewValueEvent(String ssid, String strid, String strfid, String value) {
		super(ssid);
		this.strid = strid;
		this.strfid = strfid;
		this.value = value;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchNewValueEvent(this);
	}

	protected String getSuffix() {
		return "+Val: " +value;
	}
	
//	@Override
//	public IvEvent clone() {
//		return new NewPoint(ssid, value);
//	}

	
	public String getValue() {
		return value;
	}

	public String getStrid() {
		return strid;
	}

	public String getStrfid() {
		return strfid;
	}
	
}
