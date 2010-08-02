package org.gpdviz.ss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information associated to a sensor system.
 * 
 * @author Carlos Rueda
 */
@SuppressWarnings("serial")
public class SensorSystemInfo implements Serializable {
   
	/** Gets a brief description of the sensor system. */
	public String getDescription() {
		return description;
	}

	/** Sets a brief description of the sensor system. */
	public void setDescription(String description) {
		this.description = description;
	}

	public Source getSource(String srcfid) {
		return _srcs.get(srcfid);
	}
	
	public Stream getStream(String strfid) {
		return _strs.get(strfid);
	}
 

	/**
	 * @param src
	 * @throw IllegalArgumentException if src.getFullName returns null
	 */
	public void addSource(Source src) { 
		String srcfid = src.getFullName();
		if ( srcfid == null ) {
			throw new IllegalArgumentException("src.getFullName returned null");
		}
		_srcs.put(srcfid, src);
		System.out.println(this.getClass().getName()+ ": addSource " +srcfid) ;
	}

	/**
	 * @param srcfid
	 */
	public void removeSource(String srcfid) { 
		_srcs.remove(srcfid);
		// FIXME remove all streams belonging to this source
		System.out.println(this.getClass().getName()+ ": removeSource " +srcfid) ;
	}
	
	
	/**
	 * Removes all sources/streams.
	 */
	public void reset() {
		_srcs.clear();
		_strs.clear();
		_latestObs.clear();
	}
	
	public Map<String, Source> getSources() {
		return _srcs;
	}
	
	/**
	 * Returns a sorted list of the source IDs. 
	 * @return
	 */
	public List<String> getSourceFullIds() {
		List<String> list = new ArrayList<String>(); 
		list.addAll(_srcs.keySet());
		Collections.sort(list);
		return list;
	}
	
	public Map<String, Stream> getStreams() {
		return _strs;
	}
	
	/**
	 * @param str
	 * @throw IllegalArgumentException if str.getFullName returns null
	 */
	public void addStream(Stream str) {
		String strfid = str.getFullName();
		if ( strfid == null ) {
			throw new IllegalArgumentException("str.getFullName returned null");
		}
		_strs.put(strfid, str);
		System.out.println(this.getClass().getName()+ ": addStream " +strfid+ " : " +str) ;
	}
	

	// never null
	public List<Stream> getStreams(String srcfid) {
		List<Stream> list = new ArrayList<Stream>();
		for ( Stream str : _strs.values() ) {
			String strid = str.getName();
			String strfid = str.getFullName();
			if ( (srcfid+ "/" +strid).equals(strfid) ) {
				list.add(str);
			}
		}
		if ( list.size() > 0 ) {
			Collections.sort(list);
		}
		return list;
	}
	
    
	/** adds value in a given stream -- preliminary
	 * @param strfid
	 * @param value
	 */
	public void addValue(String strfid, Observation obs) {
		List<Observation> values = _latestObs.get(strfid);
		if ( values == null ) {
			values = new ArrayList<Observation>();
			_latestObs.put(strfid, values);
		}
		_addValue(values, obs);
	}


	/** gets last known values in a given stream -- preliminary
	 * @param strfid
	 * @return
	 */
	public List<Observation> getObservations(String strfid) {
		return _latestObs.get(strfid);
	}
	
	public String toString() {
		return description;
	}
	
	
	
	//////////////////////////////////////////
	// private
	//////////////////////////////////////////

	private static final int MAX_NO_OBSERVATIONS = 30;


	/**
	 * Adds an Observation to the list making sure that the size of the list is at most
	 * {@link #MAX_NO_OBSERVATIONS} elements.
	 */
	private void _addValue(List<Observation> values, Observation obs) {
		while ( values.size() >= MAX_NO_OBSERVATIONS ) {
			values.remove(0);
		}
		values.add(obs);
	}

	
	/** A brief description of the sensor system. */
	private String description;
	
    /**
     * All the sources in this sensor system.
     * srcfid -> Source
     */
    private Map<String,Source> _srcs = new HashMap<String,Source>();
    
    /**
     * All the streams in this sensor system.
     * strfid -> Stream
     */
    private Map<String,Stream> _strs = new HashMap<String,Stream>();

    /**
     * Preliminary.
     * The latest known observations per stream.
     * strfid -> List&lt;Observation>
     */
    private Map<String,List<Observation>> _latestObs = new HashMap<String,List<Observation>>();
    

}
