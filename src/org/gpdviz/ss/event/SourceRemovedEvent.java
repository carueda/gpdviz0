package org.gpdviz.ss.event;


public class SourceRemovedEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	private String srcfid;
	
	SourceRemovedEvent() {
	}
	
	public SourceRemovedEvent(String ssid, String srcfid) {
		super(ssid);
		this.srcfid = srcfid;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSourceRemovedEvent(this);
	}
	

	public String getSrcfid() {
		return srcfid;
	}


	protected String getSuffix() {
		return "-Src: " +srcfid;
	}
	
}
