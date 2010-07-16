package org.gpdviz.gwt.server.restlet;

import java.io.IOException;

import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;
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
public class StreamResource extends BaseResource {

	private String ssid;
	private String srcid;
	private String srcfid;
	
	private String strid;
	private String strfid;
	
	
	private SensorSystem ss;
	private Source src;
	private Stream str;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ssid = (String) getRequest().getAttributes().get("ssid");
		srcid = (String) getRequest().getAttributes().get("srcid");
		strid = (String) getRequest().getAttributes().get("strid");
		
		// TODO
		srcfid = srcid;
		strfid = srcfid+ "/" +strid;
		
		ss = gpdvizManager.getSensorSystem(ssid);
		if ( ss != null ) {
			src = ss.getSensorSystemInfo().getSource(srcid);
			str = ss.getSensorSystemInfo().getStream(strfid);
		}
		setExisting(ss != null && src != null && str != null);
	}

	/**
	 * Handle DELETE requests.
	 */
	@Delete
	public void removeItem() {
		if (ss != null) {
			ss.removeStream(srcfid, strid);
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
		
		String value = form.getFirstValue("value");
		
		
		// The PUT request updates or creates the resource.
		// TODO synchronize to prevent concurrent update
		if (str == null) {
			// TODO other attributes for stream
			Stream str = new Stream(strid, strfid, 1000, "UNITS");

			ss.addStream(src, str);
			
			if ( value != null ) {
				ss.addValue(src, str, value);
			}
			
			setStatus(Status.SUCCESS_CREATED);
		}
		else {
			// TODO
			str.setStringAttribute("period", String.valueOf("1000"));
			str.setStringAttribute("units", "UNITS");

			if ( value != null ) {
				ss.addValue(src, str, value);
			}

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
			Element eltItem = XmlUtil.toXmlStream(d, ss, src, str);
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