package org.gpdviz.gwt.server.restlet;

import java.util.List;

import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;
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
public class StreamsResource extends BaseResource {  
  
	private String ssid;
	private String srcid;
	private SensorSystem ss;
	private Source src;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ssid = (String) getRequest().getAttributes().get("ssid");
		srcid = (String) getRequest().getAttributes().get("srcid");
		ss = gpdvizManager.getSensorSystem(ssid);
		if ( ss != null ) {
			src = ss.getSensorSystemInfo().getSource(srcid);
		}
		setExisting(ss != null && src != null);
	}
	
	/**
	 * Handle POST requests: registers a new stream.
	 */
	@Post
	public Representation acceptItem(Representation entity) {
		Representation result = null;
		// Parse the given representation and retrieve pairs of
		// "name=value" tokens.
		Form form = new Form(entity);
		String strid = form.getFirstValue("strid");
		
		String period = form.getFirstValue("period");
		String units = form.getFirstValue("units");

		SensorSystem ss = (SensorSystem) gpdvizManager.getSensorSystem(ssid);
		Source src = ss.getSensorSystemInfo().getSource(srcid);
		
		// TODO assuming only first level srcid (so srcfid == srcid):
		String srcfid = srcid;
		
		String strfid = srcfid+ "/" +strid;
		
		Stream str = ss.getSensorSystemInfo().getStream(strfid);
		
		// TODO synchronize to prevent concurrent update
		if (str == null) {
			
			str = new Stream(strid, strfid, period, units);
			_setAttributeIfGiven(str, form, "title");
			_setAttributeIfGiven(str, form, "legend");
			_setAttributeIfGiven(str, form, "xlabel");

			ss.addStream(src, str);
			setStatus(Status.SUCCESS_CREATED);
			
			Representation rep = new StringRepresentation("stream added",
					MediaType.TEXT_PLAIN);
			// Indicates where is located the new resource.
			rep.setLocationRef(getRequest().getResourceRef().getIdentifier()
					+ "/" + strid);
			result = rep;
		}
		else { // Item is already registered.
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			result = RestletUtil.generateErrorRepresentation("Stream " +strid+ " for source " +srcfid+ " " +
					"in sensor system " +ssid
					+ " already exists.", "1");
		}
		
		return result;
	} 


	 
	private void _setAttributeIfGiven(Stream str, Form form, String name) {
		String value = form.getFirstValue(name);
		if ( value != null ) {
			str.setStringAttribute(name, value);
		}
	}

	/**
	 * Returns a listing of all registered streams for the source. 
	 * @return
	 */
    @Get("xml")
    public Representation represent() {  
    	
		try {
			System.out.println(this.getClass().getSimpleName()+ " : ssid: " +ssid+ " srcid=" +srcid);
			
			SensorSystem ss = gpdvizManager.getSensorSystem(ssid);
			
			DomRepresentation representation = new DomRepresentation( MediaType.TEXT_XML);  
			representation.setIndenting(true);
			
			Document d = representation.getDocument();  
			Element r = d.createElement("streams");  
			d.appendChild(r);  
			
			// TODO see comment related with fullname
			String srcfid = srcid;
			List<Stream> strs = ss.getSensorSystemInfo().getStreams(srcfid);
			for (Stream str : strs ) {
				Element eltItem = XmlUtil.toXmlStream(d, ss, src, str);  
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