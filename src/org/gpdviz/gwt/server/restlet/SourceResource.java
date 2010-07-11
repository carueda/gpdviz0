package org.gpdviz.gwt.server.restlet;

import java.io.IOException;

import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Carlos Rueda
 */
public class SourceResource extends BaseResource {

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
	 * Handle DELETE requests.
	 */
	@Delete
	public void removeItem() {
		if (ss != null) {
			ss.getSensorSystemInfo().getSources().remove(srcid);
		}

		setStatus(Status.SUCCESS_NO_CONTENT);
	}

	/**
	 * Handle PUT requests.
	 * 
	 * @throws IOException
	 */
	@Put
	public void storeItem(Representation entity) throws IOException {
		if ( ss == null ) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}
		
		Form form = new Form(entity);
		
		String lat = form.getFirstValue("lat");
		String lon = form.getFirstValue("lon");
		String description = form.getFirstValue("description");
		
		// The PUT request updates or creates the resource.
		// TODO synchronize to prevent concurrent update
		if (src == null) {
			src = new Source(srcid, srcid, description, lat, lon);
			ss.getSensorSystemInfo().addSource(src);
			setStatus(Status.SUCCESS_CREATED);
		}
		else {
			src.setStringAttribute("location", description);
			src.setStringAttribute("latitude", lat);
			src.setStringAttribute("longitude", lon);
			setStatus(Status.SUCCESS_OK);
		}
	}

	@Get("xml")
	public Representation toXml() {
		try {
			DomRepresentation representation = new DomRepresentation(
					MediaType.TEXT_XML);
			representation.setIndenting(true);
			
			Document d = representation.getDocument();
			Element eltItem = XmlUtil.toXmlSource(d, ss, src);
			d.appendChild(eltItem);
			d.normalizeDocument();
			return representation;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}