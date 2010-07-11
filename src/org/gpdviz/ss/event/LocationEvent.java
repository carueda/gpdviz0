package org.gpdviz.ss.event;

public abstract class LocationEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	protected String lat;
	protected String lon;
	
	
	LocationEvent() {
	}
	
	public LocationEvent(String ssid, String lat, String lon) {
		super(ssid);
		this.lat = lat;
		this.lon = lon;
	}
	
	
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLat() {
		return lat;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLon() {
		return lon;
	}
	
	protected String getSuffix() {
		return "Loc: " +lat+ ", " +lon;
	}
}
