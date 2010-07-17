package org.gpdviz.ss.event;

import org.gpdviz.ss.Source;

public class SourceAddedEvent extends LocationEvent {
	private static final long serialVersionUID = 1L;
	
	
	private Source src;
	
	SourceAddedEvent() {
	}
	
	public SourceAddedEvent(String ssid, Source src) {
		super(ssid, src.getStringAttribute("latitude"), src.getStringAttribute("longitude"));
		this.src = src;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchSourceAddedEvent(this);
	}

	
	public Source getSource() {
		return src;
	}

	protected String getSuffix() {
		return "+Src: " +src.getFullName()+ "@" +lat+ "," +lon;
	}
	
//	@Override
//	public IvEvent clone() {
//		return new NewSourceEvent(ssid, src);
//	}

}
