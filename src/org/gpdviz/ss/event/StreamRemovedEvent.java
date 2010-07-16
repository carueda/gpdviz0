package org.gpdviz.ss.event;


public class StreamRemovedEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	private String srcfid;
	private String strid;
	private String strfid;
	
	StreamRemovedEvent() {
	}
	
	public StreamRemovedEvent(String ssid, String srcfid, String strid) {
		super(ssid);
		this.srcfid = srcfid;
		this.strid = strid;
		this.strfid = srcfid+ "/" +strid;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchStreamRemovedEvent(this);
	}
	

	public String getSrcfid() {
		return srcfid;
	}

	public String getStrid() {
		return strid;
	}

	protected String getSuffix() {
		return "-Str: " +strfid;
	}
	
}
