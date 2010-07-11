package org.gpdviz.gwt.server.restlet;

import javax.servlet.ServletContext;

import org.gpdviz.gwt.server.ss.GpdvizManager;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * 
 * @author Carlos Rueda
 */
public class GpdvizRestletApplication extends Application {  
  
	/** gotten on demand */
	private GpdvizManager gpdvizManager = null;
	
    @Override  
    public synchronized Restlet createInboundRoot() {  

    	Router router = new Router(getContext());  
    	
//        router.attach("/ss", SensorSystemsResource.class);  
//        router.attach("/ss/{ssid}", SensorSystemResource.class);  
//        router.attach("/ss/{ssid}/src", SourcesResource.class);  
//        router.attach("/ss/{ssid}/src/{srcid}", SourceResource.class);  
        
        router.attach("/",       SensorSystemsResource.class);  
        router.attach("/{ssid}", SensorSystemResource.class);  
        
        router.attach("/{ssid}/",        SourcesResource.class);  
        router.attach("/{ssid}/{srcid}", SourceResource.class);
        
        router.attach("/{ssid}/{srcid}/",        StreamsResource.class);  
        router.attach("/{ssid}/{srcid}/{strid}", StreamResource.class);  
        
        return router;  
    }  
    
    GpdvizManager getSensorSystemRegistry() throws Exception {
		if ( gpdvizManager == null ) {
			Context context = getContext();
			ServletContext servletContext = (ServletContext) context.getAttributes().get(
					"org.restlet.ext.servlet.ServletContext");

			if ( servletContext == null ) {
				throw new Exception("Could not retrieve the ServletContext!");
			}

			gpdvizManager = (GpdvizManager) servletContext.getAttribute(
					GpdvizManager.class.getName());

			if ( gpdvizManager == null ) {
				throw new Exception("Could not retrieve GpdvizManager!");
			}
		}
    	return gpdvizManager;
	}
  
} 