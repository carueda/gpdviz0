package org.gpdviz.gwt.client.service;

import org.gpdviz.ss.SensorSystemInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Services for the visualizer.
 * @author Carlos Rueda
 */
@RemoteServiceRelativePath("gpdviz")
public interface GpdvizService extends RemoteService {

	/**
	 * Connects the client to the sensor system by the given ID.
	 * @param ssid
	 * @return
	 */
	SensorSystemInfo connect(String ssid);
	
}
