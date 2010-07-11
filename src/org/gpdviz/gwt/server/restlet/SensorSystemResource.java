package org.gpdviz.gwt.server.restlet;

import java.io.IOException;

import org.gpdviz.ss.SensorSystem;
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
public class SensorSystemResource extends BaseResource {

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
	 * Handle DELETE requests.
	 */
	@Delete
	public void removeItem() {
		if (ss != null) {
			gpdvizManager.unregister(ss.getSsid());
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
		// The PUT request updates or creates the resource.
		if (ss == null) {
			ss = new SensorSystem(ssid);
			gpdvizManager.register(ss);
		}
		else {
			ss.reset();
		}

		// Update the description.
		Form form = new Form(entity);
		ss.setDescription(form.getFirstValue("description"));

		if ( gpdvizManager.registerIfAbsent(ss) == null) {
			setStatus(Status.SUCCESS_CREATED);
		}
		else {
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
			Element eltItem = XmlUtil.toXmlSensorSystem(d, ss);
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