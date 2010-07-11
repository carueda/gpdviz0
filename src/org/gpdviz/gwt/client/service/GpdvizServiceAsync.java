package org.gpdviz.gwt.client.service;

import org.gpdviz.ss.SensorSystemInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface.
 * @author Carlos Rueda
 */
public interface GpdvizServiceAsync {
	
	void  connect(String ssid, AsyncCallback<SensorSystemInfo> callback);
	

}
