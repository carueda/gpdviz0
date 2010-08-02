package org.gpdviz.mock;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.gpdviz.ss.Observation;
import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;


/**
 * Generates a sensor system with random sources, streams, and values.
 * @author Carlos Rueda
 */
class MockGenerator {
	
	// probability to do removals
	private static final double PROB_REMOVALS = 0.05;

	// timer attributes:
	private static final long DELAY = 2000;
	private static final long DEFAULT_PERIOD = 3000;
	
	// for random selections
	private static final int DEFAULT_MAX_SOURCES = 8;
	private static final int DEFAULT_MAX_STREAMS_PER_SOURCE = 8;
	private static final int DEFAULT_MAX_LATS = 5;
	private static final int DEFAULT_MAX_LONS = 5;
	
	
	private Timer timer;
	
	private int maxSources = DEFAULT_MAX_SOURCES;
	private int maxStreamsPerSource = DEFAULT_MAX_STREAMS_PER_SOURCE;

	private int maxLats = DEFAULT_MAX_LATS;
	private int maxLons = DEFAULT_MAX_LONS;

	private long period = DEFAULT_PERIOD;
	
	private Random random = new Random();

	
	private SensorSystem ss;

	private int _nextSrcNum = 0;
	private int _nextStrNum = 0;
	
	
	public MockGenerator(String ssid, String description) {
		ss = new SensorSystem(ssid);
		ss.setDescription(description);
	}
	
	public SensorSystem getSensorSystem() {
		return ss;
	}

	
	/**
	 * Sets the period in ms to generate the events.
	 */
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public void setMaxSources(int maxSources) {
		this.maxSources = maxSources;
	}
	
	public void setMaxStreamsPerSource(int maxStreamsPerSource) {
		this.maxStreamsPerSource = maxStreamsPerSource;
	}

	public void setMaxLatsLons(int maxLats, int maxLons) {
		this.maxLats = maxLats;
		this.maxLons = maxLons;
	}

	public synchronized void activate() {
		if ( timer != null ) {
			return;
		}
		
		TimerTask task = new TimerTask() {
			public void run() {
				_generateEvent();
			}
		};
		timer = new Timer(this.getClass().getSimpleName()+ " timer");
		timer.schedule(task, DELAY , period);

	}
	
	public synchronized void deactivate() {
		if ( timer != null ) {
			timer.cancel();
			timer = null;
		}
	}
	

	/**
	 * Called by the timer to generate an event.
	 */
	private void _generateEvent() {
		
		Map<String, Source> srcs = ss.getSources();
		int numSrcs = srcs.size();
		
		if ( numSrcs == 0 ) {
			// start with a source of course
			_addSource();
			return;
		}
		
		else if ( numSrcs < maxSources ) {
			// randomly choose between generating a new source or
			// generating a stream for one of the sources
			if ( random.nextDouble() < 0.2 ) {
				// generate new source
				_addSource();
				return;
			}
		}
		else if ( PROB_REMOVALS > 0.0 ) {
			// randomly choose between removing a source or
			// generating an event for a randomly chosen source:
			if ( random.nextDouble() < PROB_REMOVALS ) {
				// generate new source
				_removeSource();
				return;
			}
		}
		
		// pick a random source and generate an event for it:
		int idx = random.nextInt(numSrcs);
		Source src = (Source) srcs.values().toArray()[idx];
		_generateEventForSource(src);
	}

	/**
	 * Generates an event for the given source.
	 */
	private void _generateEventForSource(Source src) {
		
		String srcfid = src.getFullName();
		List<Stream> strs = ss.getStreams(srcfid);
		int numStrs = strs.size();
		if ( numStrs == 0 ) {
			// start this source with a stream:
			_addStream(src);
			return;
		}

		else if ( numStrs < maxStreamsPerSource ) {
			// randomly choose between generating a new stream or
			// generating a value for one of the streams
			if ( random.nextDouble() < 0.2 ) {
				// generate new stream
				_addStream(src);
				return;
			}
		}
		else if ( PROB_REMOVALS > 0.0 ) {
			// randomly choose between removing a stream or
			// generating a value for one of the streams
			if ( random.nextDouble() < PROB_REMOVALS ) {
				// generate new stream
				_removeStream(src);
				return;
			}

		}
		
		// pick a random stream and generate a value event for it
		Stream str = strs.get(random.nextInt(numStrs));
		_addValue(src, str);
	}

	/**
	 * Adds a source to the sensor system.
	 */
	private void _addSource() {
		Map<String, Source> srcs = ss.getSources();
		int numSrcs = srcs.size();
		if ( numSrcs == maxSources ) {
			return;
		}
		
		String srcid = _getNextSourceId();
		String lat = "" +( 10 * random.nextInt(maxLats));
		String lon = "" +( 10 * random.nextInt(maxLons));
		Source src = new Source(srcid, srcid, "TODO: descriptive location", lat, lon);
		ss.addSource(src);
	}

	private String _getNextSourceId() {
		String srcid = "src_" +_nextSrcNum ;
		_nextSrcNum++;
		return srcid;
	}

	private String _getNextStreamId() {
		String strid = "str_" +_nextStrNum ;
		_nextStrNum++;
		return strid;
	}
	
	/**
	 * Removes a source.
	 */
	private void _removeSource() {
		Map<String, Source> srcs = ss.getSources();
		int numSrcs = srcs.size();
		if ( numSrcs == 0 ) {
			return;
		}
		
		int idx = random.nextInt(numSrcs);
		Source src = (Source) srcs.values().toArray()[idx];
		String srcfid = src.getFullName();
		ss.removeSource(srcfid);
	}
	
	/**
	 * Adds a stream to the given source.
	 */
	private void _addStream(Source src) {
		String srcfid = src.getFullName();
		List<Stream> strs = ss.getStreams(srcfid);
		int numStrs = strs.size();
		if ( numStrs == maxStreamsPerSource ) {
			return;
		}
		
		String strid = _getNextStreamId();
		String strfid = srcfid+ "/" +strid;
		Stream str = new Stream(strid, strfid, "1000", "UNITS");
		str.setStringAttribute("title", "title for stream " +strfid);
		ss.addStream(src, str);
	}
	
	/**
	 * Removes a stream from the given source.
	 */
	private void _removeStream(Source src) {
		
		String srcfid = src.getFullName();
		List<Stream> strs = ss.getStreams(srcfid);
		int numStrs = strs.size();
		if ( numStrs == 0 ) {
			return;
		}
		
		int idx = random.nextInt(numStrs);
		Stream str = strs.get(idx);
		String strid = str.getName();
		ss.removeStream(srcfid, strid);
	}
	
	/**
	 * Adds a value to the given stream.
	 */
	private void _addValue(Source src, Stream str) {
		String value = String.valueOf(random.nextInt(1000));
		Observation obs = new Observation(new Date().getTime(), value);
		ss.addObservation(src, str, obs);
	}
}
