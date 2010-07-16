package org.gpdviz.gwt.server.ss;


import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.gpdviz.ss.event.IvEventListener;
import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.SensorSystemInfo;
import org.gpdviz.ss.event.IvCommand;
import org.gpdviz.ss.event.IvEvent;
import org.gpdviz.ss.event.SensorSystemRegisteredEvent;
import org.gpdviz.ss.event.SensorSystemUnregisteredEvent;
import org.icepush.gwt.server.ServerPushCommandContext;

/**
 * Gpdviz manager.
 * 
 * @author Carlos Rueda
 */
public class GpdvizManager  {
	
	private void _log(String msg) {
		System.out.println("XXX " +getClass().getSimpleName()+ ": " + msg);
	}

	/**
	 * Creates the main manager in the back-end.
	 * @param httpServlet
	 */
	public GpdvizManager(HttpServlet httpServlet) {
		this.httpServlet = httpServlet;
		
		_log("INIT");

		// TODO some persistence back-end
		_ssRegistry = new SensorSystemRegistry();
		
		_log("INIT: registered sensor systems: " +_ssRegistry.getRegisteredIds());
	}
	
	
	/**
	 * Connects to a registered sensor system.
	 * @param ssid
	 * @return null if no sensor system registered by the given id.
	 */
	public SensorSystemInfo connect(String ssid) {
		
		SensorSystem ss = _ssRegistry.getSensorSystem(ssid);
		if ( ss == null ) {
			return null;
		}
		
		SensorSystemManager ssMan = SensorSystemManager.createSensorSystemManager(ss);
		
		// add the listener if not already added:
		ssMan.addEventListener(_eventListener);
		
		return ssMan.getSensorSystemInfo();
	}

	
	///////////////////////////////////////////////////////////////
	// private
	///////////////////////////////////////////////////////////////
	
	private final HttpServlet httpServlet;
	
	/** 
	 * This resource is shared via setAttribute. 
	 * See {@link org.gpdviz.gwt.server.restlet.GpdvizRestletApplication}.
	 */
	private SensorSystemRegistry _ssRegistry;
	

	private IvEventListener _eventListener = new IvEventListener() {
		public void eventGenerated(IvEvent event) {
			_pushEvent(event);
		}
	};

	
	private void _pushEvent(IvEvent event) {
		IvCommand ivCmd = new IvCommand(event);
		ServletContext servletContext = httpServlet.getServletContext();
		ServerPushCommandContext spcc = ServerPushCommandContext.getInstance(servletContext);
		if ( spcc != null && servletContext != null ) {
			spcc.pushCommand(ivCmd, servletContext);
			_log("_pushEvent: " +event);
		}
		else {
			_log("_pushEvent: WARNING: couldn't push command. spcc=" +spcc+ ", ctx=" +servletContext);
		}
	}


	public void register(SensorSystem ss) {
		_ssRegistry.register(ss);
		
		SensorSystemManager ssMan = SensorSystemManager.createSensorSystemManager(ss);
		// add the listener if not already added:
		ssMan.addEventListener(_eventListener);

		IvEvent event = new SensorSystemRegisteredEvent(ss.getSsid(), ss.getSensorSystemInfo());
		_pushEvent(event);
	}


	public SensorSystem registerIfAbsent(SensorSystem ss) {
		SensorSystem prev = _ssRegistry.registerIfAbsent(ss);
		
		SensorSystemManager ssMan = SensorSystemManager.createSensorSystemManager(ss);
		// add the listener if not already added:
		ssMan.addEventListener(_eventListener);

		IvEvent event = new SensorSystemRegisteredEvent(ss.getSsid(), ss.getSensorSystemInfo());
		_pushEvent(event);

		return prev;
	}


	public SensorSystem getSensorSystem(String ssid) {
		return _ssRegistry.getSensorSystem(ssid);
	}

	/**
	 * Unregisters a sensor system.
	 * @param ssid
	 * @return A human readable message describing the result of the operation
	 */
	public String unregister(String ssid) {
		SensorSystemManager ssm = SensorSystemManager.getSensorSystemManager(ssid);
		if ( ssm != null ) {
			ssm.deactivate();
			_log(ssid+ ": SensorSystemManager deactivated.");
		}
		
		SensorSystem ss = _ssRegistry.unregister(ssid);
		String msg;
		if ( ss != null ) {
			msg = "Sensor system unregistered";
			IvEvent event = new SensorSystemUnregisteredEvent(ssid, msg);
			ss.notifyEvent(event);
		}
		else {
			msg = "No such sensor system registered";
			IvEvent event = new SensorSystemUnregisteredEvent(ssid, msg);
			event.setEventNo(-1);
			_pushEvent(event);
		}

		return ssid+ ": " +msg;
	}


	public boolean containsKey(String ssid) {
		return _ssRegistry.containsKey(ssid);
	}


	public Set<String> getRegisteredIds() {
		return _ssRegistry.getRegisteredIds();
	}

}
