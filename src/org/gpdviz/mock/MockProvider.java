package org.gpdviz.mock;

import java.io.PrintWriter;

import org.gpdviz.client.GpdvizClient;


/**
 * Base class for stand-alone programs
 * 
 * @author Carlos Rueda
 */
public class MockProvider  {

	static final String DEFAULT_GPDVIZ_ENDPOINT = "http://127.0.0.1:8888/rest";
	
	static final int MOCK_MAX_SOURCES = 3;
	static final int MOCK_MAX_STREAMS_PER_SOURCE = 2;
	static final int MOCK_PERIOD = 1000;
	
	// sensor system id and description
	static final String MOCK_SSID = "mock1";
	static final String MOCK_DESCRIPTION = "a mock data provider";
	
	
	GpdvizClient gpdvizClient;

	void _log(String msg) {
		System.out.println("%%% " +getClass().getSimpleName()+ " %%% " +msg);
	}
	
	MockProvider(String[] args) {
		String program = getClass().getSimpleName();
		String endPoint = DEFAULT_GPDVIZ_ENDPOINT;
		if ( args.length > 0 ) {
			if ( args[0].matches("-*help.*") ) {
				System.out.println(
						"USAGE: " +program+ " [ <endpoint> ]\n" +
						" <endpoint>  The Gpdviz REST endpoint. By default, " +DEFAULT_GPDVIZ_ENDPOINT
				);
				System.exit(0);
			}
			endPoint = args[0];
		}
		gpdvizClient = new GpdvizClient(endPoint);
		gpdvizClient.setLogger(new PrintWriter(System.out, true), "%%%%% ");
	}
	
}
