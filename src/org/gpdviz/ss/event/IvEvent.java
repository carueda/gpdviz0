package org.gpdviz.ss.event;

import java.io.Serializable;

/**
 * 
 * @author Carlos Rueda
 */
public abstract class IvEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String ssid;
	
	protected int eventNo;
	
	
	IvEvent() {
	}
	
	public IvEvent(String ssid) {
		this.ssid = ssid;
	}
	
	
	public abstract void accept(IvEventDispatcher ed);

	
	public String getSsid() {
		return ssid;
	}


	public void setEventNo(int eventNo) {
		this.eventNo = eventNo;
	}

	public int getEventNo() {
		return eventNo;
	}
	
	public String toString() {
		return "[" +ssid+ ":" +eventNo+ "] " +getSuffix();
	}

	protected String getSuffix() {
		return "";
	}
	
//	public abstract IvEvent clone() ;
}
