package org.gpdviz.gwt.client.viz;

import org.gpdviz.ss.Source;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class SourcePanel {

	// TODO
	
	private Source src;
	private VerticalPanel streams = new VerticalPanel();
	private DisclosurePanel widget;
	
	SourcePanel(Source src) {
		this.src = src;
		widget = new DisclosurePanel(src.toString());
		// set open so addition of streams are immediately reflected:
		widget.setOpen(true);
		widget.add(streams);
	}
	
	
    public Widget getWidget() {
    	return widget;
    }

	public void addStreamPanel(StreamPanel strPanel) {
		streams.add(strPanel.getWidget());
	}


	public Source getSource() {
		return src;
	}

}
