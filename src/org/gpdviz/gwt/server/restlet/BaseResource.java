package org.gpdviz.gwt.server.restlet;

import org.gpdviz.gwt.server.ss.GpdvizManager;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
  
/** 
 * A base class for the served resources.
 * @author Carlos Rueda
 */
abstract class BaseResource extends ServerResource {  
  
	protected GpdvizManager gpdvizManager;

	private GpdvizManager getSensorSystemRegistry() throws Exception {
		return ((GpdvizRestletApplication) getApplication()).getSensorSystemRegistry();
	}
	
	@Override
	protected void doInit() throws ResourceException {
		try {
			gpdvizManager = getSensorSystemRegistry();
		}
		catch (Exception e) {
			throw new ResourceException(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e);
		}
	}

}
