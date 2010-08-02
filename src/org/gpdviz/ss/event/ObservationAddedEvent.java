package org.gpdviz.ss.event;

import org.gpdviz.ss.Observation;

/**
 * Event for the addition of observations to a stream.
 * @author Carlos Rueda
 */
public class ObservationAddedEvent extends IvEvent {
	private static final long serialVersionUID = 1L;
	
	private String strid;
	private String strfid;
	
	private Observation obs;
	

	ObservationAddedEvent() {
	}
	
	public ObservationAddedEvent(String ssid, String strid, String strfid, Observation obs) {
		super(ssid);
		if ( obs == null ) {
			throw new IllegalArgumentException("observation cannot be null");
		}
		this.strid = strid;
		this.strfid = strfid;
		this.obs = obs;
	}

	@Override
	public void accept(IvEventDispatcher ed) {
		ed.dispatchObservationAddedEvent(this);
	}

	protected String getSuffix() {
		String timestamp = obs == null ? null : String.valueOf(obs.getTimestamp());
		String value = obs == null ? null : obs.getValue();
		return "+Val: " +value+ " at " +timestamp;
	}
	
	public Observation getObservation() {
		return obs;
	}

	public String getStrid() {
		return strid;
	}

	public String getStrfid() {
		return strfid;
	}

}
