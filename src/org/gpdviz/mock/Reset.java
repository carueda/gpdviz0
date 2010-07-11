package org.gpdviz.mock;

import org.restlet.data.Status;

/**
 * convenience program to reset the sensor system at the Gpdviz endpoint.
 * @author Carlos Rueda
 */
public class Reset extends MockProvider {
	
	public static void main(String[] args) throws Exception {
		new Reset(args);
	}

	Reset(String[] args) throws Exception {
		super(args);
		
		Status status = gpdvizClient.resetSensorSystem(MOCK_SSID, MOCK_DESCRIPTION);
		_log("REST"+ ": " +MOCK_SSID+ ": " +status);
	}
}
