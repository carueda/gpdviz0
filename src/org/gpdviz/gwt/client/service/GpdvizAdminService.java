package org.gpdviz.gwt.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Admin operations.
 * 
 * TODO: very preliminary
 * 
 * @author Carlos Rueda
 */
@RemoteServiceRelativePath("admin")
public interface GpdvizAdminService extends RemoteService {

	Set<String> getRegisteredSsids();
	
	String unregister(String ssid);
}
