package org.gpdviz.gwt.server.ss;


import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.gpdviz.gwt.server.ss.SensorSystemRegistry.SsrListener;
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

	/**
	 * Creates the main manager in the back-end.
	 * @param httpServlet
	 */
	public GpdvizManager(HttpServlet httpServlet) {
		this.httpServlet = httpServlet;
		
		System.out.println(getClass().getName()+ " INIT");

		// TODO some persistence back-end
		_ssRegistry = new SensorSystemRegistry();
		
		System.out.println(getClass().getName()+ " INIT: registered sensor systems: "
				+_ssRegistry.getRegisteredIds()
		);
		
		_ssRegistry.addSsrListener(_ssrListener );
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
		
		SensorSystemManager ssMan = SensorSystemManager.getSensorSystemManager(ss);
		
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
	
	/** Listener of registration/unregistration of sensor systems */
	private SsrListener _ssrListener = new SensorSystemRegistry.SsrListener() {
		public void sensorSystemRegistered(SensorSystem ss) {
			IvEvent event = new SensorSystemRegisteredEvent(ss.getSsid());
			_eventGenerated(event);
		}

		public void sensorSystemUnregistered(SensorSystem ss) {
			IvEvent event = new SensorSystemUnregisteredEvent(ss.getSsid());
			_eventGenerated(event);
		} 
	};
	

	private IvEventListener _eventListener = new IvEventListener() {
		public void eventGenerated(IvEvent event) {
			_eventGenerated(event);
		}
	};

	
	private void _eventGenerated(IvEvent event) {
		
		IvCommand ivCmd = new IvCommand(event);
		ServletContext servletContext = httpServlet.getServletContext();
		ServerPushCommandContext spcc = ServerPushCommandContext.getInstance(servletContext);
		if ( spcc != null && servletContext != null ) {
			spcc.pushCommand(ivCmd, servletContext);
		}
		
		System.out.println(this.getClass().getName()+ ": " +event);
	}


	public void register(SensorSystem ss) {
		_ssRegistry.register(ss);
	}


	public SensorSystem registerIfAbsent(SensorSystem ss) {
		return _ssRegistry.registerIfAbsent(ss);
	}


	public SensorSystem getSensorSystem(String ssid) {
		return _ssRegistry.getSensorSystem(ssid);
	}


	public String unregister(String ssid) {
		return _ssRegistry.unregister(ssid);
	}


	public boolean containsKey(String ssid) {
		return _ssRegistry.containsKey(ssid);
	}


	public Set<String> getRegisteredIds() {
		return _ssRegistry.getRegisteredIds();
	}

}
