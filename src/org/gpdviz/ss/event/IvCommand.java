package org.gpdviz.ss.event;

import java.io.Serializable;

import org.icepush.gwt.client.command.ICommand;

@SuppressWarnings("serial")
public class IvCommand implements ICommand, Serializable {
	
	private IvEvent event;

	// no-arg ctr
	IvCommand() { }
	
	public IvCommand(IvEvent event) {
		super();
		this.event = event;
	}

	public IvEvent getEvent() {
		return event;
	}

	public void setEvent(IvEvent event) {
		this.event = event;
	}

}
