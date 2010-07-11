package org.gpdviz.gwt.server;


import javax.servlet.http.HttpServlet;

import org.gpdviz.gwt.server.ss.GpdvizManager;

/**
 * The core servlet.
 * Sets up the GpdvizManager at init() time. 
 * This should have load-on-startup=1
 * 
 * @author Carlos Rueda
 */
@SuppressWarnings("serial")
public class CoreServlet extends HttpServlet {

	public void init() {
		System.out.println(getClass().getName()+ " INIT");

		// This should be the only call to the GpdvizManager constructor.
		gpdvizManager = new GpdvizManager(this);
		
		// sets an attribute to share the registry:
		getServletContext().setAttribute(GpdvizManager.class.getName(), gpdvizManager);

		System.out.println(getClass().getName()+ " INIT: GpdvizManager created.");
	}
	
	
	///////////////////////////////////////////////////////////////
	// private
	///////////////////////////////////////////////////////////////
	
	/** 
	 * This resource is created here and shared via setAttribute. 
	 * See {@link org.gpdviz.gwt.server.restlet.GpdvizRestletApplication}.
	 */
	private GpdvizManager gpdvizManager;

}
