package org.gpdviz.simpleclient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.gpdviz.client.GpdvizClient;
import org.gpdviz.ss.Observation;
import org.restlet.data.Status;

/** 
 * A test client for the REST interface.
 * @author Carlos Rueda
 */
public class SimpleClient {

	private static final String DEFAULT_ENDPOINT = "http://127.0.0.1:8888/rest";

	
	public static void main(String[] args) throws Exception {
		new SimpleClient(args);
	}


	private GpdvizClient gpdvizClient;
	
	private final String SS_ID = "test1";
	private final String SS_DESCRIPTION = "description of " +SS_ID;

	private final String SRC_ID = "srcX";
	private final String SRC_DESCRIPTION = "Description of source " +SRC_ID;

	private final String STR_ID = "strA";
	
	private double x = 0;
	private double X_INC = Math.PI / 5;


	SimpleClient(String[] args) throws Exception {
		String endPoint = DEFAULT_ENDPOINT;
		if ( args.length > 0 ) {
			if ( args[0].matches("-*help.*") ) {
				System.out.println(
						"USAGE: " +getClass().getName()+ " [ <endpoint> ]\n" +
						" <endpoint>  The Gpdviz REST endpoint. By default, " +DEFAULT_ENDPOINT
				);
				System.exit(0);
			}
			endPoint = args[0];
		}

		gpdvizClient = new GpdvizClient(endPoint);
		gpdvizClient.setLogger(new PrintWriter(System.out, true), "%%%%% ");
		
		_pause();
		
		_prepareSensorSystem();   _pause();
		_addSource();             _pause();
		_addStream("my title");   _pause();

		// values
		while ( x < 2 * Math.PI  &&  _addValue() ) {
			_pause();
		}
		
		_removeStream();             _pause();
		_removeSource();             _pause();
		_resetSensorSystem();        _pause();
		
//		if ( false ) 
		_unregisterSensorSystem();
	}

	
	void _log(String msg) {
		System.out.println("=== " +msg);
	}

	void _pause() throws Exception {
		_log("Press Enter to continue");
		System.in.read();
	}
	
	
	private void _prepareSensorSystem() throws Exception {
		StringWriter writer = new StringWriter();
		Status status = gpdvizClient.getSensorSystem(SS_ID, writer);

		if ( status.equals(Status.SUCCESS_OK) ) {
			_log(writer.toString());

			_log("ALREADY registered.  Resetting...");

			status = gpdvizClient.resetSensorSystem(SS_ID, SS_DESCRIPTION);
			_log("RESET" + ": " +status);
		}
		else {
			status = gpdvizClient.registerSensorSystem(SS_ID, SS_DESCRIPTION);
			_log("registerSensorSystem"+ ": " +SS_ID+ ": " +status);
		}
	}

	private void _addSource() throws Exception {
		Status status = gpdvizClient.addSource(SS_ID, SRC_ID, SRC_DESCRIPTION, "32", "-120" );
		_log("addNewSource"+ ": " +SS_ID+ "/" + SRC_ID+ ": " +status);
	}
	
	private void _removeSource() throws Exception {
		Status status = gpdvizClient.removeSource(SS_ID, SRC_ID);
		_log("removeSource"+ ": " +SS_ID+ "/" + SRC_ID+ ": " +status);
	}
	
	private void _addStream(String title) throws Exception {
		Map<String,String> props = new HashMap<String,String>();
		props.put("title", title);
		Status status = gpdvizClient.addStream(SS_ID, SRC_ID, STR_ID, props);
		_log("addNewStream"+ ": " +SS_ID+ "/" + SRC_ID+  "/" + STR_ID+ ": " +status);
	}
	
	private void _removeStream() throws Exception {
		Status status = gpdvizClient.removeStream(SS_ID, SRC_ID, STR_ID);
		_log("removeStream"+ ": " +SS_ID+ "/" + SRC_ID+  "/" + STR_ID+ ": " +status);
	}
	
	private boolean _addValue() throws Exception {
		double val = Math.sin(x);
		Observation obs = new Observation(new Date().getTime(), String.valueOf(val));
		
		String strfid = SRC_ID+ "/" +STR_ID;
		Status status = gpdvizClient.addObservation(SS_ID, STR_ID, strfid , obs);
		
		_log("x=" +x+ " VALUE at " +obs.getTimestamp()+ " : " +obs.getValue()+ " : " +status);
		x += X_INC;
		
		return status.equals(Status.SUCCESS_OK);
	}
	
	private void _resetSensorSystem() throws Exception {
		Status status = gpdvizClient.resetSensorSystem(SS_ID, SS_DESCRIPTION);
		_log("RESET" + ": " +status);
	}
	
	private void _unregisterSensorSystem() throws Exception {
		Status status = gpdvizClient.unregisterSensorSystem(SS_ID);
		_log("UNREGISTER" + ": " +status);
	}
	
}
