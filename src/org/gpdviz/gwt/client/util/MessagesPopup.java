package org.gpdviz.gwt.client.util;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class MessagesPopup extends PopupPanel {
	
	private static final int HIDE_TIME = 1500;
	
	private HideTimer timer = new HideTimer(this);
	private Label messageLabel = new Label();
	public MessagesPopup(){
		
		messageLabel.setStylePrimaryName("messagesLabel");

		this.add(messageLabel);
		this.setStylePrimaryName("messagesPanel");
//		this.setAnimationEnabled(true);
		this.hide();
	}
	
	public void setName(String name){
		this.messageLabel.setText(name);
	}

	public void show(){
//		super.center(); <- kill the client?
		super.show();
		this.timer.schedule(HIDE_TIME);
	}
	
	public class HideTimer extends Timer{
		private MessagesPopup target;
		public HideTimer(MessagesPopup target){
			this.target = target;
		}
		@Override
		public void run() {
			this.target.hide();
		}
	}
}