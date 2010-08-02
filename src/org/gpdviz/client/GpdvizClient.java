package org.gpdviz.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.gpdviz.ss.Observation;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * Client connection to a Gpdviz endpoint.
 * 
 * <p>
 * Note: preliminary.  API subject to change.
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
	 * Sets an ad hoc logger to report the requests and the status of the
	 * responses.
	 * 
	 * @param logger Where the messages are printed out.
	 * @param prefix A string to print as a prefix of every logged message.
	 */
	public void setLogger(Writer logger, String prefix) {
		loggerPrefix = prefix == null ? "" : prefix;
		this.logger = (logger instanceof PrintWriter)
			? (PrintWriter) logger 
			: new PrintWriter(logger);
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
	public Status addSource(String ssid, String srcid, String description, String latitude, String longitude) {
		
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
	 * @param props
	 * @return
	 */
	public Status addStream(String ssid, String srcid, String strid, Map<String,String> props) {
		
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/" +srcid+ "/");
		
		Representation rep = _getStreamRepresentation(strid, null, props);
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
	 * Adds an observation to a stream.
	 * @param ssid    Sensor system ID
	 * @param strid   Stream ID
	 * @param strfid  Full stream ID
	 * @param obs      the observation
	 * @return
	 */
	public Status addObservation(String ssid, String strid, String strfid, Observation obs) {
		ClientResource ssResource = new ClientResource(endpoint + "/" +ssid+ "/" +strfid);
		
		Representation rep = _getStreamRepresentation(strid, obs, null);
		return _put(ssResource, rep);
	}

	
	////////////////////////////////////////////////////////////////////////////////////
	// private
	////////////////////////////////////////////////////////////////////////////////////
	
	private String loggerPrefix;
	private PrintWriter logger;
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
	
	private Representation _getStreamRepresentation(String strid, Observation obs, Map<String,String> props) {
		Form form = new Form();
		form.add("strid", strid);
		
		if ( obs != null ) {
			form.add("timestamp", String.valueOf(obs.getTimestamp()));
			form.add("value", obs.getValue());
		}
		
		if ( props != null ) {
			for ( Entry<String, String> entry : props.entrySet() ) {
				form.add(entry.getKey(), entry.getValue());	
			}
		}
		
		// default values  TODO handle this in a better way
		if ( form.getFirst("period") == null ) {
			form.add("period", "3000");
		}
		
		return form.getWebRepresentation();
	}
	
	private Status _get(ClientResource clientResource, Writer writer) throws IOException {
		final String oper = "GET";
		if ( logger != null ) {
			_logRequest(oper, clientResource, null);
		}

		Status status = Status.SUCCESS_OK;
		
		try {
			clientResource.get();
			
			if ( writer != null ) {
				if (clientResource.getStatus().isSuccess()
				&& clientResource.getResponseEntity().isAvailable()) 
				{
					clientResource.getResponseEntity().write(writer);
				}
			}
		}
		catch (ResourceException e) {
			status = e.getStatus();
		}
		
		if ( logger != null ) {
			_logStatus(oper, status);
		}
		
		return status;
	}

	private Status _post(ClientResource clientResource, Representation rep) {
		final String oper = "POST";
		if ( logger != null ) {
			_logRequest(oper, clientResource, rep);
		}

		Status status = Status.SUCCESS_OK;
				
		try {
			clientResource.post(rep);
		}
		catch (ResourceException e) {
			status = e.getStatus();
		}
		
		if ( logger != null ) {
			_logStatus(oper, status);
		}

		return status;
	}

	private Status _put(ClientResource clientResource, Representation rep) {
		final String oper = "PUT";
		if ( logger != null ) {
			_logRequest(oper, clientResource, rep);
		}

		Status status = Status.SUCCESS_OK;
		
		try {
			clientResource.put(rep);
		}
		catch (ResourceException e) {
			status = e.getStatus();
		}
		
		if ( logger != null ) {
			_logStatus(oper, status);
		}

		return status;
	}

	private Status _delete(ClientResource clientResource) {
		final String oper = "DELETE";
		if ( logger != null ) {
			_logRequest(oper, clientResource, null);
		}

		Status status = Status.SUCCESS_OK;
				
		try {
			clientResource.delete();
		}
		catch (ResourceException e) {
			status = e.getStatus();
		}
		
		if ( logger != null ) {
			_logStatus(oper, status);
		}

		return status;
	}

	private void _logRequest(String operation, ClientResource clientResource, Representation rep) {
		String uri = clientResource.getReference().getIdentifier();
		String repStr = "";
		if ( rep != null ) {
			try {
				repStr = "  {" +rep.getText()+ "}";
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.printf("%s %s.%s: %s%s%n",
				loggerPrefix, getClass().getSimpleName(), operation, uri, repStr);
	}
	private void _logStatus(String operation, Status status) {
		logger.printf("%s %s.%s: status = %s%n%n",
				loggerPrefix, getClass().getSimpleName(), operation, status);
	}
}
