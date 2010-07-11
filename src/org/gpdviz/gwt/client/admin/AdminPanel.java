package org.gpdviz.gwt.client.admin;


import java.util.Set;

import org.gpdviz.gwt.client.service.GpdvizAdminServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * TODO adjust after several changes in other places.
 * 
 * @author Carlos Rueda
 */
public class AdminPanel extends VerticalPanel {
 
	private final GpdvizAdminServiceAsync adminService;
	
	// TODO capture this from appropriate UI
	private final String ssid = "mock1";
	
	private final Button refreshButton = new Button("Refresh");
	private final Button unregisterButton = new Button("Unregister");
	private final Label errorLabel = new Label();
    
    /**
     * Creates the main panel.
     * @param metadata
     * @param params
     */
	public AdminPanel(final GpdvizAdminServiceAsync adminService) {
		
		this.adminService = adminService;
		
		refreshButton.addStyleName("sendButton");
		unregisterButton.addStyleName("sendButton");

		add(refreshButton);
		add(unregisterButton);
		add(errorLabel);

//		nameField.setFocus(true);
//		nameField.selectAll();

		refreshButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				errorLabel.setText("");
				init();
			}
		});

		unregisterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				unregister();
			}

			private void unregister() {
				errorLabel.setText("");

				refreshButton.setEnabled(false);
				adminService.unregister(ssid,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								errorLabel.setText("ERROR: " +caught.getMessage());
							}

							public void onSuccess(String result) {
								errorLabel.setText("OK: " +result);
							}
						});
			}
		});
		
		refreshButton.setEnabled(false);
		unregisterButton.setEnabled(false);

	}
	
	public void init() {
		errorLabel.setText("Getting registered systems...");
		adminService.getRegisteredSsids(new AsyncCallback<Set<String>>() {

			public void onFailure(Throwable caught) {
				errorLabel.setText("ERROR: " +caught.getClass().getName()+ ": " +caught.getMessage());
			}

			public void onSuccess(Set<String> result) {
				errorLabel.setText("Registered: " +result);
			}
		});
	}
}

