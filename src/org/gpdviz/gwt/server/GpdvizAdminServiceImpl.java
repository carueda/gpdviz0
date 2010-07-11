package org.gpdviz.gwt.server;

import java.util.Set;

import org.gpdviz.gwt.client.service.GpdvizAdminService;
import org.gpdviz.gwt.server.ss.GpdvizManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the admin service.
 */
@SuppressWarnings("serial")
public class GpdvizAdminServiceImpl extends RemoteServiceServlet implements GpdvizAdminService {


	/** Just logs a message */
	public void init() {
		System.out.println(getClass().getName()+ " INIT");
	}
	
	public Set<String> getRegisteredSsids() {
		_getGpdvizManager();
		return gpdvizManager.getRegisteredIds();
	}

	public String unregister(String ssid) {
		_getGpdvizManager();
		return gpdvizManager.unregister(ssid);
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
