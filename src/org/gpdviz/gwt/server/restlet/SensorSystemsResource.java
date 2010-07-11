package org.gpdviz.gwt.server.restlet;

import org.gpdviz.ss.SensorSystem;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
  
/** 
 * 
 * @author Carlos Rueda
 */
public class SensorSystemsResource extends BaseResource {  

	/**
	 * Handle POST requests: registers a new sensor system.
	 */
	@Post
	public Representation acceptItem(Representation entity) {
		Representation result = null;
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.
		Form form = new Form(entity);
		String ssid = form.getFirstValue("ssid");
		String description = form.getFirstValue("description");

		SensorSystem ss = new SensorSystem(ssid);
		ss.setDescription(description);
		
		// Register the new item if one is not already registered.
		if ( ! gpdvizManager.containsKey(ssid)
		&&  gpdvizManager.registerIfAbsent(ss) == null ) {
			// Set the response's status and entity
			setStatus(Status.SUCCESS_CREATED);
			Representation rep = new StringRepresentation("sensor system registered",
					MediaType.TEXT_PLAIN);
			// Indicates where is located the new resource.
			rep.setLocationRef(getRequest().getResourceRef().getIdentifier()
					+ "/" + ssid);
			result = rep;
		}
		else { // Item is already registered.
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			result = RestletUtil.generateErrorRepresentation("Sensor system ID " + ssid
					+ " already exists.", "1");
		}

		return result;
	} 

	
	/**
	 * Returns a listing of all registered sensor systems. 
	 * @return
	 */
    @Get("xml")
    public Representation represent() {  
    	
		try {
			DomRepresentation representation = new DomRepresentation( MediaType.TEXT_XML);  
			representation.setIndenting(true);
			
			Document d = representation.getDocument();  
			Element r = XmlUtil.toXmlSensorSystems(gpdvizManager, d);  
			d.appendChild(r);  
			d.normalizeDocument();  
			return representation;  
			
		}
		catch (Exception e) {
			// TODO: error handling
			e.printStackTrace();
			return null;
		}
		
    }  
  
} 