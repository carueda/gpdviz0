package org.gpdviz.ss;

import java.io.Serializable;

/**
 * A value and corresponding timestamp.
 * 
 * @author Carlos Rueda
 */
@SuppressWarnings("serial")
public class Observation implements Serializable {
	
	// no-arg ctr.
	Observation() {
	}
	
	public Observation(long timestamp, String value) {
		this.setTimestamp(timestamp);
		this.setValue(value);
	}
	
	public void setTimestamp(long timestamp) {
		this._timestamp = timestamp;
	}

	public long getTimestamp() {
		return _timestamp;
	}
	
	public void setValue(String value) {
		this._value = value;
	}
	
	public String getValue() {
		return _value;
	}
	
	private long _timestamp;
	private String _value;
}
