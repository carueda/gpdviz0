package org.gpdviz.gwt.client.viz;

import org.gpdviz.gwt.client.Gpdviz;
import org.gpdviz.gwt.client.util.MessagesPopup;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;
import org.gpdviz.ss.event.IvCommand;
import org.gpdviz.ss.event.IvEvent;
import org.gpdviz.ss.event.IvEventDispatcher;
import org.gpdviz.ss.event.NewSourceEvent;
import org.gpdviz.ss.event.NewStreamEvent;
import org.gpdviz.ss.event.NewValueEvent;
import org.gpdviz.ss.event.SensorSystemRegisteredEvent;
import org.gpdviz.ss.event.SensorSystemResetEvent;
import org.gpdviz.ss.event.SensorSystemUnregisteredEvent;
import org.gpdviz.ss.event.SourceRemovedEvent;
import org.gpdviz.ss.event.StreamRemovedEvent;
import org.icepush.gwt.client.command.ICommand;
import org.icepush.gwt.client.command.ICommandExecuter;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;


class EventCommandHandler implements ICommandExecuter, IvEventDispatcher {

	private final String ssid;
	private  VizPanel vizPanel;
	private MessagesPopup popup;
	
	public EventCommandHandler(String ssid, MessagesPopup popup){
		this.ssid = ssid;
		this.popup = popup;
	}

	public void setVizPanel(VizPanel vizPanel) {
		this.vizPanel = vizPanel;
	}
	
	public void execute(ICommand command) {
		IvCommand ivCmd = (IvCommand) command;
		IvEvent event = ivCmd.getEvent();
		String event_ssid = event.getSsid();
		if ( ssid.equals(event_ssid) ) {
			event.accept(this);
		}
		else {
			Gpdviz.log("IGNORING " +event);
		}
	}

	private void _debugEvent(final IvEvent event) {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				popup.setName(event.toString());
				popup.show();
				Gpdviz.log("EVENT: " +event);
			}
		});
	}
	
	public void dispatchNewSourceEvent(final NewSourceEvent event) {
		_debugEvent(event);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				Source src = event.getSource();
				vizPanel.addSource(src);
			}
		});
	}

	public void dispatchSourceRemovedEvent(final SourceRemovedEvent event) {
		_debugEvent(event);
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				String srcfid = event.getSrcfid();
				vizPanel.removeSource(srcfid);
			}
		});
	}

	public void dispatchNewStreamEvent(final NewStreamEvent event) {
		_debugEvent(event);
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				Stream str = event.getStream();
				vizPanel.addStream(str);
			}
		});
	}
	
	public void dispatchStreamRemovedEvent(final StreamRemovedEvent event) {
		_debugEvent(event);
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				String srcfid = event.getSrcfid();
				String strid = event.getStrid();
				vizPanel.removeStream(srcfid, strid);
			}
		});
	}
		

	public void dispatchNewValueEvent(final NewValueEvent event) {
		_debugEvent(event);
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				String strfid = event.getStrfid();
				String value = event.getValue();
				vizPanel.addNewValue(strfid, value);
			}
		});
	}

	
	public void dispatchSensorSystemRegisteredEvent(final SensorSystemRegisteredEvent event) {
		_debugEvent(event);
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				vizPanel.setSensorSystemInfo(event.getSensorSystemInfo());
			}
		});
	}

	public void dispatchSensorSystemUnregisteredEvent(final SensorSystemUnregisteredEvent event) {
		_debugEvent(event);
		
		// TODO for now, only doing 'reset'
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				vizPanel.unregister();
			}
		});
	}

	public void dispatchSensorSystemResetEvent(final SensorSystemResetEvent event) {
		_debugEvent(event);
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				vizPanel.reset();
			}
		});
	}
	
}