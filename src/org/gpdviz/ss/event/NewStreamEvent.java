package org.gpdviz.ss.event;

import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;

public class NewStreamEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	
	private Source src;
	private Stream str;
	
	NewStreamEvent() {
	}
	
	public NewStreamEvent(String ssid, Source src, Stream str) {
		super(ssid);
		this.src = src;
		this.str = str;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchNewStreamEvent(this);
	}
	

	// TODO probably not an operation here.
	public Source getSource() {
		return src;
	}
	
	public Stream getStream() {
		return str;
	}

	protected String getSuffix() {
		return "+Str: " +str.getFullName();
	}
	
//	@Override
//	public IvEvent clone() {
//		return new NewSourceEvent(ssid, src);
//	}

}
