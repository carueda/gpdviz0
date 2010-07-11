package org.gpdviz.gwt.client.viz;

import org.gpdviz.gwt.client.util.MessagesPopup;
import org.gpdviz.ss.event.IvCommand;
import org.icepush.gwt.client.command.ClientPushCommandContext;


class EventCommandHandlers {

	static private  String _ssid;
	static private  VizPanel _mainPanel;
	static private MessagesPopup _popup;
	
	static void setHandlers(String ssid, VizPanel vizPanel, MessagesPopup popup) {
		_ssid = ssid;
		_mainPanel = vizPanel;
		_popup = popup;
		ClientPushCommandContext context = ClientPushCommandContext.getInstance();
		EventCommandHandler cmdHandler = new EventCommandHandler(_ssid, _popup);
		cmdHandler.setVizPanel(_mainPanel);
		context.registerExecuter(IvCommand.class, cmdHandler);
	}


}