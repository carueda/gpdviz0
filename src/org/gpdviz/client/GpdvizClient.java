package org.gpdviz.client;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * Client connection to a Gpdviz endpoint.
 * 
 * @author Carlos Rueda
 */
public class GpdvizClient {
	
	/** 
	 * Creates a new Gpdviz client.
	 * @param endpoint URL of the Gpdviz endpoint
	 */
	public GpdvizClient(String endpoint) {
		super();
		this.endpoint = endpoint;
		this.sensorSystemsResource = new ClientResource(endpoint + "/");
	}

	/**
	 * Prints a basic description of the registered sensor systems
	 * @param writer
	 * @return
	 * @throws IOException
	 */
	public Status getSensorSystems(Writer writer) throws IOException {

		return _get(sensorSystemsResource, writer);
	}
	

	/**
	 * Prints a basic description of a registered sensor system.
	 * @param ssid
	 * @param writer
	 * @return
	 * @throws IOException
	 */
	public Status getSensorSystem(String ssid, Writer writer) throws IOException {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" + ssid);
		
		return _get(ssResource, writer);
	}
	
	/**
	 * Prints the list of sources of a given sensor system.
	 * @param ssid
	 * @param writer
	 * @return
	 * @throws IOException
	 */
	public Status getSources(String ssid, Writer writer) throws IOException {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" + ssid+ "/");
		
		return _get(ssResource, writer);
	}
	
	/**
	 * Registers a sensor system.
	 * @param ssid
	 * @param description
	 * @return
	 * @throws IOException
	 */
	public Status registerSensorSystem(String ssid, String description) throws IOException {
		Representation rep = _getSsRepresentation(ssid, description);
		return _post(sensorSystemsResource, rep);
	}

	/**
	 * Resets a sensor system.
	 * (this should probably be called updateSensorSystem, but the current effect is that all
	 * sources, if any, are removed)
	 * 
	 * @param ssid  
	 *             ID of the sensor system to update
	 * @param description  
	 *             the new description
	 * @return
	 * @throws IOException
	 */
	public Status resetSensorSystem(String ssid, String description) throws IOException {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" + ssid);
		
		Representation rep = _getSsRepresentation(ssid, description);
		return _put(ssResource, rep);
	}

	/**
	 * Unregisters a sensor system.
	 * @param ssid
	 * @return
	 * @throws IOException
	 */
	public Status unregisterSensorSystem(String ssid) throws IOException {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" + ssid);
		
		try {
			ssResource.delete();
		}
		catch (ResourceException e) {
			Status status = e.getStatus();
			return status;
		}
		
		return Status.SUCCESS_OK;
	}

	
	/**
	 * Adds a source to a sensor system.
	 * @param ssid
	 * @param srcid
	 * @param description
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Status addNewSource(String ssid, String srcid, String description, String latitude, String longitude) {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/");
		
		Representation rep = _getSourceRepresentation(srcid, description, latitude, longitude);
		return _post(ssResource, rep);
		
	}

	/**
	 * Removes a source.
	 * @param ssid
	 * @param srcfid
	 * @return
	 */
	public Status removeSource(String ssid, String srcfid) {
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/" +srcfid);
		return _delete(ssResource);
	}
	
	/**
	 * Add a stream to a sensor system.
	 * @param ssid
	 * @param srcid
	 * @param strid
	 * @return
	 */
	public Status addNewStream(String ssid, String srcid, String strid) {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/" +srcid+ "/");
		
		Representation rep = _getStreamRepresentation(strid, null);
		return _post(ssResource, rep);
		
	}
	
	/**
	 * Removes a stream from a sensor system.
	 * @param ssid
	 * @param srcfid
	 * @param strid
	 * @return
	 */
	public Status removeStream(String ssid, String srcfid, String strid) {
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/" +srcfid+ "/" + strid);
		return _delete(ssResource);
	}
	
	/**
	 * Adds a value to a stream.
	 * @param ssid    Sensor system ID
	 * @param strid   Stream ID
	 * @param strfid  Full stream ID
	 * @param value   the value
	 * @return
	 */
	public Status addNewValue(String ssid, String strid, String strfid, String value) {
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/" +strfid);
		
		Representation rep = _getStreamRepresentation(strid, value);
		return _put(ssResource, rep);
	}

	
	////////////////////////////////////////////////////////////////////////////////////
	// private
	////////////////////////////////////////////////////////////////////////////////////
	
	private final String endpoint;
	private final ClientResource sensorSystemsResource;
	
	private Representation _getSsRepresentation(String ssid, String description) throws IOException {
		Form form = new Form();
		form.add("ssid", ssid);
		form.add("description", description);
		return form.getWebRepresentation();
	}

	private Representation _getSourceRepresentation(String srcid, String description, String latitude, String longitude) {
		Form form = new Form();
		form.add("srcid", srcid);
		form.add("description", description);
		form.add("latitude", latitude);
		form.add("longitude", longitude);
		return form.getWebRepresentation();
	}
	
	private Representation _getStreamRepresentation(String strid, String value) {
		Form form = new Form();
		form.add("strid", strid);
		if ( value != null ) {
			form.add("value", value);
		}
		return form.getWebRepresentation();
	}
	
	private Status _get(ClientResource clientResource, Writer writer) throws IOException {
		
		try {
			clientResource.get();
		}
		catch (ResourceException e) {
			Status status = e.getStatus();
			return status;
		}
		
		if ( writer != null ) {
			if (clientResource.getStatus().isSuccess()
			&& clientResource.getResponseEntity().isAvailable()) 
			{
				clientResource.getResponseEntity().write(writer);
			}
		}
		
		return Status.SUCCESS_OK;
	}

	private Status _post(ClientResource clientResource, Representation rep) {
		
		try {
			clientResource.post(rep);
		}
		catch (ResourceException e) {
			Status status = e.getStatus();
			return status;
		}
		
		return Status.SUCCESS_OK;
	}

	private Status _put(ClientResource clientResource, Representation rep) {
		
		try {
			clientResource.put(rep);
		}
		catch (ResourceException e) {
			Status status = e.getStatus();
			return status;
		}
		
		return Status.SUCCESS_OK;
	}

	private Status _delete(ClientResource clientResource) {
		
		try {
			clientResource.delete();
		}
		catch (ResourceException e) {
			Status status = e.getStatus();
			return status;
		}
		
		return Status.SUCCESS_OK;
	}
	
}
