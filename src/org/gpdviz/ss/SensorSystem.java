package org.gpdviz.ss;

import java.util.List;
import java.util.Map;

import org.gpdviz.ss.event.IvEvent;
import org.gpdviz.ss.event.IvEventListener;
import org.gpdviz.ss.event.SourceAddedEvent;
import org.gpdviz.ss.event.StreamAddedEvent;
import org.gpdviz.ss.event.ValueAddedEvent;
import org.gpdviz.ss.event.SensorSystemResetEvent;
import org.gpdviz.ss.event.SourceRemovedEvent;
import org.gpdviz.ss.event.StreamRemovedEvent;


/**
 * Sensor system with event generation.
 * @author Carlos Rueda
 */
public class SensorSystem {

	
	/** The sensor system info.
	 *  While refreshing information, this state is used to determine
	 *  new events, for example that a new stream has come up.
	 */
	protected SensorSystemInfo sensorSystemInfo = new SensorSystemInfo();
	
	private IvEventListener evListener;
	
	protected int _nextEventNo = 1;
	
	
	protected String ssid;
	
	
	public SensorSystem(String ssid) {
		this.ssid = ssid;
	}

	
	public String getSsid() {
		return ssid;
	}
	
	/** removes all sources/streams */
	public void reset() {
		sensorSystemInfo.reset();
		IvEvent event = new SensorSystemResetEvent(ssid);
		notifyEvent(event);
	}

	public void addSource(Source src) {
		sensorSystemInfo.addSource(src);
		IvEvent event = new SourceAddedEvent(ssid, src);
		notifyEvent(event);
	}
	
	public void removeSource(String srcfid) {
		sensorSystemInfo.removeSource(srcfid);
		IvEvent event = new SourceRemovedEvent(ssid, srcfid);
		notifyEvent(event);
	}
	
	public void addStream(Source src, Stream str) {
		sensorSystemInfo.addStream(str);
		IvEvent event = new StreamAddedEvent(ssid, src, str);
		notifyEvent(event);
	}
	
	public void removeStream(String srcid, String strid) {
		// TODO
		String srcfid = srcid;
		String strfid = srcfid+ "/" +strid;
		sensorSystemInfo.getStreams().remove(strfid);
		IvEvent event = new StreamRemovedEvent(ssid, srcfid, strid);
		notifyEvent(event);
	}
	
	public void addValue(Source src, Stream str, String value) {
		String strid = str.getName();
		String strfid = str.getFullName();
		sensorSystemInfo.addValue(strfid, value);
		IvEvent event = new ValueAddedEvent(ssid, strid, strfid, value);
		notifyEvent(event);
	}
	

    /** Gets a brief description of the sensor system. */
	public String getDescription() {
		return sensorSystemInfo.getDescription();
	}

	/** Sets a brief description of the sensor system. */
	public void setDescription(String description) {
		sensorSystemInfo.setDescription(description);
	}



	/** TODO Note: Only keeps one listener. */
	public void addEventListener(IvEventListener evListener) {
		this.evListener = evListener;
	}
	
	/**
	 * Notifies listeners about the given event.
	 * @param event
	 */
	public void notifyEvent(IvEvent event) {
		event.setEventNo(_nextEventNo++);
		if ( evListener != null ) {
			evListener.eventGenerated(event);
		}
	}
	
	/** Gets the current state of the sensor system */
	public SensorSystemInfo getSensorSystemInfo() {
		return sensorSystemInfo;
	}


	public int getNumEvents() {
		return _nextEventNo;
	}


	public Map<String, Source> getSources() {
		return sensorSystemInfo.getSources();
	}


	public List<Stream> getStreams(String srcfid) {
		return sensorSystemInfo.getStreams(srcfid);
	}

}
