package org.gpdviz.gwt.server.restlet;

import java.util.Map;

import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
  
/** 
 * 
 * @author Carlos Rueda
 */
public class SourcesResource extends BaseResource {  
  
	private String ssid;
	private SensorSystem ss;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ssid = (String) getRequest().getAttributes().get("ssid");
		ss = gpdvizManager.getSensorSystem(ssid);
		setExisting(ss != null);
	}
	
	/**
	 * Handle POST requests: registers a new source.
	 */
	@Post
	public Representation acceptItem(Representation entity) {
		Representation result = null;
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.
		Form form = new Form(entity);
		String srcid = form.getFirstValue("srcid");
		String description = form.getFirstValue("description");

		String latitude = form.getFirstValue("latitude");
		String longitude = form.getFirstValue("longitude");

		SensorSystem ss = (SensorSystem) gpdvizManager.getSensorSystem(ssid);
		Source src = ss.getSensorSystemInfo().getSource(srcid);
		

		// TODO synchronize to prevent concurrent update
		if (src == null) {
			src = new Source(srcid, srcid, description, latitude, longitude);
			ss.addSource(src);
			setStatus(Status.SUCCESS_CREATED);
			
			Representation rep = new StringRepresentation("source added",
					MediaType.TEXT_PLAIN);
			// Indicates where is located the new resource.
			rep.setLocationRef(getRequest().getResourceRef().getIdentifier()
					+ "/" + srcid);
			result = rep;
		}
		else { // Item is already registered.
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			result = RestletUtil.generateErrorRepresentation("Source " +srcid+ " for sensor system " +ssid
					+ " already exists.", "1");
		}
		
		return result;
	} 


	 
	/**
	 * Returns a listing of all registered sources. 
	 * @return
	 */
    @Get("xml")
    public Representation represent() {  
    	
		try {
			SensorSystem ss = gpdvizManager.getSensorSystem(ssid);
			
			DomRepresentation representation = new DomRepresentation( MediaType.TEXT_XML);  
			representation.setIndenting(true);
			
			Document d = representation.getDocument();  
			Element r = d.createElement("sources");  
			d.appendChild(r);  
			Map<String, Source> srcs = ss.getSensorSystemInfo().getSources();
			
			for ( String srcid : ss.getSensorSystemInfo().getSourceFullIds() ) {
				Source src = srcs.get(srcid);
				Element eltItem = XmlUtil.toXmlSource(d, ss, src);  
				r.appendChild(eltItem);  
			}  
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