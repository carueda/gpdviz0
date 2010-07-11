package org.gpdviz.gwt.server;

import org.gpdviz.gwt.client.service.GpdvizService;
import org.gpdviz.gwt.server.ss.GpdvizManager;
import org.gpdviz.ss.SensorSystemInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the visualizer service.
 */
@SuppressWarnings("serial")
public class GpdvizServiceImpl extends RemoteServiceServlet implements GpdvizService {

	/** Just logs a message */
	public void init() {
		System.out.println(getClass().getName()+ " INIT");
	}
		
	public SensorSystemInfo connect(String ssid) {
		_getGpdvizManager();
		return gpdvizManager.connect(ssid);
	}

	///////////////////////////////////////////////////////////////
	// private
	///////////////////////////////////////////////////////////////

	/** 
	 * Lazily initialized to point to the resource "exported" by 
	 * {@link CoreServlet}.
	 */
	private GpdvizManager gpdvizManager = null;
	

	/**
	 * Initializes the pointer to accesss the GpdvizManager. 
	 */
	private void _getGpdvizManager() {
		if ( gpdvizManager == null ) {
			gpdvizManager = (GpdvizManager) getServletContext().getAttribute(GpdvizManager.class.getName());

			if ( gpdvizManager == null ) {
				String error = "Could not retrieve GpdvizManager!";
				System.out.println(getClass().getName()+ " ERROR: " +error);
				throw new RuntimeException(error);
			}
		}
	}
}
