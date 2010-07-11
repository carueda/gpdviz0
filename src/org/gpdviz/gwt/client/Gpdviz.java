package org.gpdviz.gwt.client;

import java.util.Map;

import org.gpdviz.gwt.client.admin.AdminPanel;
import org.gpdviz.gwt.client.chart.ChartCreator;
import org.gpdviz.gwt.client.chart.IChart;
import org.gpdviz.gwt.client.service.GpdvizAdminService;
import org.gpdviz.gwt.client.service.GpdvizAdminServiceAsync;
import org.gpdviz.gwt.client.service.GpdvizService;
import org.gpdviz.gwt.client.service.GpdvizServiceAsync;
import org.gpdviz.gwt.client.util.GizUtil;
import org.gpdviz.gwt.client.viz.VizPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Gpdviz implements EntryPoint {

	private static final boolean TEST_CHART = false;

	private String ssid = "mock";
	
	private GpdvizAdminServiceAsync adminService = null;
	private GpdvizServiceAsync gpdvizService = null;

	
	private void log(String msg) {
		RootPanel.get().add(new Label(msg));
		GWT.log(msg);
	}
	
	
	public void onModuleLoad() {
		
		boolean useGmap = true;
		
		Map<String,String> params = GizUtil.getParams();
		if ( params != null ) {
			String ssid = (String) params.get("ssid");
			if ( ssid != null ) {
				this.ssid = ssid;
			}
			String gmap = (String) params.get("gmap");
			if ( "no".equals(gmap) ) {
				useGmap = false;
			}
		}
		
		log("ssid: " +ssid);
		
//		log("getHostPageBaseURL: " +GWT.getHostPageBaseURL());
//		log("getModuleBaseURL: " +GWT.getModuleBaseURL());
//		log("getLocationHost: " +GizUtil.getLocationHost());
//		log("getLocationProtocol: " +GizUtil.getLocationProtocol());
//		log("getLocationSearch: " +GizUtil.getLocationSearch());
		
        
		if ( DOM.getElementById("adminPanelContainer") != null ) {
			adminService = GWT.create(GpdvizAdminService.class);
			AdminPanel adminPanel = new AdminPanel(adminService);
			RootPanel.get("adminPanelContainer").add(adminPanel);
			adminPanel.init();
			return;
		}
		
		// Else:
		
		if ( TEST_CHART ) {
			VerticalPanel vp = new VerticalPanel();
			vp.setBorderWidth(1);
			IChart chart = ChartCreator._testChart();
			vp.add(chart.asWidget());
			RootPanel.get("mainPanelContainer").add(vp);
			chart.update();
			return;
		}

		gpdvizService = GWT.create(GpdvizService.class);
		VizPanel vizPanel = new VizPanel(gpdvizService, ssid, useGmap);
		RootPanel.get("mainPanelContainer").add(vizPanel);
		vizPanel.init();
		
	}
}
