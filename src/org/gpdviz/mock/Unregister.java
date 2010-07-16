package org.gpdviz.mock;

import org.restlet.data.Status;

/**
 * convenience program to unregister the sensor system at the Gpdviz endpoint.
 * @author Carlos Rueda
 */
public class Unregister extends MockProvider {
	
	public static void main(String[] args) throws Exception {
		new Unregister(args);
	}

	Unregister(String[] args) throws Exception {
		super(args);
		
		Status status = gpdvizClient.unregisterSensorSystem(MOCK_SSID);
		_log("UNREGISTER"+ ": " +MOCK_SSID+ ": " +status);
	}
}
