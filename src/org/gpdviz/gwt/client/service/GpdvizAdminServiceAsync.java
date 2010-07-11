package org.gpdviz.gwt.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface.
 * @author Carlos Rueda
 */
public interface GpdvizAdminServiceAsync {

	void getRegisteredSsids(AsyncCallback<Set<String>> callback);
	
	void unregister(String ssid, AsyncCallback<String> callback);

}
