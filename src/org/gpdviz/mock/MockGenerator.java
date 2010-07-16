package org.gpdviz.mock;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;
import org.gpdviz.ss.event.IvEventListener;
import org.gpdviz.ss.event.SensorSystemResetEvent;


/**
 * Generator of a mock sensor system
 * @author Carlos Rueda
 */
class MockGenerator {
	
	// Not yet testing removals
	private static final boolean DO_REMOVALS = false;

	// timer attributes:
	private static final long DELAY = 2000;
	private static final long DEFAULT_PERIOD = 3000;
	
	// for random selections
	private static final int DEFAULT_MAX_SOURCES = 8;
	private static final int DEFAULT_MAX_STREAMS_PER_SOURCE = 8;
	private static final int DEFAULT_MAX_LATS = 5;
	private static final int DEFAULT_MAX_LONS = 5;
	
	// no reset by default
	private static final int DEFAULT_RESET_PERIOD = 0;
	
	
	private Timer timer;
	
	private int maxSources = DEFAULT_MAX_SOURCES;
	private int maxStreamsPerSource = DEFAULT_MAX_STREAMS_PER_SOURCE;

	private int maxLats = DEFAULT_MAX_LATS;
	private int maxLons = DEFAULT_MAX_LONS;

	private long period = DEFAULT_PERIOD;
	
	private int resetPeriod = DEFAULT_RESET_PERIOD;

	private Random random = new Random();

	
	private SensorSystem ss;
	
	public MockGenerator(String ssid, String description) {
		ss = new SensorSystem(ssid);
		ss.setDescription(description);
	}
	
	public String getDescription() {
		return ss.getDescription();
	}

	public void addEventListener(IvEventListener ivEventListener) {
		ss.addEventListener(ivEventListener);
	}

	
	/**
	 * Sets the period in ms to generate the events.
	 */
	public void setPeriod(int period) {
		this.period = period;
	}
	
	/**
	 * Sets the reset period, ie, every resetPeriod events, a 
	 * {@link SensorSystemResetEvent} is generated.
	 * 
	 * @param resetPeriod If 0, no reset event is generated.
	 */
	public void setResetPeriod(int resetPeriod) {
		this.resetPeriod = resetPeriod;
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

	public boolean isActive() {
		return timer != null ;
	}
	
	public synchronized void activate() {
		if ( timer != null ) {
			return;
		}
		
		TimerTask task = new TimerTask() {
			public void run() {
				refreshState();
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
	 * Runs the scanning of information eventually
	 * generating events and updating the current state.
	 */
	protected void refreshState() {
		
		int _nextEventNo = ss.getNumEvents();
		
		if ( resetPeriod > 0 && _nextEventNo > 0 && _nextEventNo % resetPeriod == 0 ) {
			ss.reset();
			return;
		}
		
		double rdm = random.nextDouble();
		if ( rdm < 0.1 ) {
			return; // no event
		}
		
		Map<String, Source> srcs = ss.getSources();
		int numSrcs = srcs.size();
		
		if ( numSrcs == 0 ) {
			// start with a source of course
			_addSource();
			return;
		}
		
		if ( numSrcs < maxSources ) {
			// randomly choose between generating a new source or
			// generating a stream for one of the sources
			if ( random.nextDouble() < 0.2 ) {
				// generate new source
				_addSource();
				return;
			}
		}
		else if ( DO_REMOVALS ) {
			// randomly choose between removing a source or
			// generating a stream for one of the sources
			if ( random.nextDouble() < 0.5 ) {
				// generate new source
				_removeSource();
				return;
			}
		}
		// pick a random source:
		String srcid = "src_" +random.nextInt(numSrcs);
		Source src = srcs.get(srcid);
		assert src != null;

		refreshStateSource(src);
	}

	private void refreshStateSource(Source src) {
		
		String srcfid = src.getFullName();
		List<Stream> strs = ss.getStreams(srcfid);
		int numStrs = strs.size();
		if ( numStrs == 0 ) {
			// start this source with a stream of course
			_addStream(src);
			return;
		}

		if ( numStrs < maxStreamsPerSource ) {
			// randomly choose between generating a new stream or
			// generating a value for one of the streams
			if ( random.nextDouble() < 0.2 ) {
				// generate new stream
				_addStream(src);
				return;
			}
		}
		else if ( DO_REMOVALS ) {
			// randomly choose between removing a stream or
			// generating a value for one of the streams
			if ( random.nextDouble() < 0.5 ) {
				// generate new stream
				_removeStream(src);
				return;
			}

		}
		
		// pick a random stream and generate a value event for it
		Stream str = strs.get(random.nextInt(numStrs));
		_addValue(src, str);
	}

	private void _addSource() {
		
		Map<String, Source> srcs = ss.getSources();
		int numSrcs = srcs.size();
		if ( numSrcs == maxSources ) {
			return;
		}
		
		String srcid = "src_" +numSrcs;
		String lat = "" +( 10 * random.nextInt(maxLats));
		String lon = "" +( 10 * random.nextInt(maxLons));
		Source src = new Source(srcid, srcid, "TODO: descriptive location", lat, lon);
		ss.addSource(src);
	}

	private void _removeSource() {
		
		Map<String, Source> srcs = ss.getSources();
		int numSrcs = srcs.size();
		if ( numSrcs == 0 ) {
			return;
		}
		
		int id = numSrcs - 1;
		String srcfid = "src_" +id;
		ss.removeSource(srcfid);
	}
	
	private void _addStream(Source src) {
		
		String srcfid = src.getFullName();
		List<Stream> strs = ss.getStreams(srcfid);
		int numStrs = strs.size();
		if ( numStrs == maxStreamsPerSource ) {
			return;
		}
		
		String strid = "str_" +numStrs;
		String strfid = srcfid+ "/" +strid;
		Stream str = new Stream(strid, strfid, 1000, "UNITS");
		ss.addStream(src, str);
	}
	
	private void _removeStream(Source src) {
		
		String srcfid = src.getFullName();
		List<Stream> strs = ss.getStreams(srcfid);
		int numStrs = strs.size();
		if ( numStrs == 0 ) {
			return;
		}
		
		int id = numStrs - 1;
		
		String strid = "str_" +id;
		ss.removeStream(srcfid, strid);
	}
	
	private void _addValue(Source src, Stream str) {
		String value = String.valueOf(random.nextInt(1000));
		ss.addValue(src, str, value);
	}


}
