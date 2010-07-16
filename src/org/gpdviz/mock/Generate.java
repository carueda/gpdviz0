package org.gpdviz.mock;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;
import org.gpdviz.ss.event.IvEvent;
import org.gpdviz.ss.event.IvEventDispatcher;
import org.gpdviz.ss.event.IvEventListener;
import org.gpdviz.ss.event.NewSourceEvent;
import org.gpdviz.ss.event.NewStreamEvent;
import org.gpdviz.ss.event.NewValueEvent;
import org.gpdviz.ss.event.SensorSystemRegisteredEvent;
import org.gpdviz.ss.event.SensorSystemResetEvent;
import org.gpdviz.ss.event.SensorSystemUnregisteredEvent;
import org.gpdviz.ss.event.SourceRemovedEvent;
import org.gpdviz.ss.event.StreamRemovedEvent;
import org.restlet.data.Status;

/**
 * Dispatchs events to the given endpoint as the MockGenerator generates them.
 * 
 * @author Carlos Rueda
 */
public class Generate extends MockProvider {
	/** the Generate program will automatically exit after this duration in minutes */
	static final int EXIT_MINUTES = 2;

	public static void main(String[] args) throws Exception {
		new Generate(args);
	}
	
	private IvEventDispatcher myDispatcher = new MyDispatcher();
	
	private MockGenerator mock;
	
	
	Generate(String[] args) throws Exception {
		super(args);

		// create sensor system:
		mock = new MockGenerator(MOCK_SSID, MOCK_DESCRIPTION);
		mock.setMaxSources(MOCK_MAX_SOURCES);
		mock.setMaxStreamsPerSource(MOCK_MAX_STREAMS_PER_SOURCE);
		mock.setPeriod(MOCK_PERIOD);
		mock.setResetPeriod(MOCK_RESET_PERIOD);
		
		
		StringWriter writer = new StringWriter();
		Status status = gpdvizClient.getSensorSystem(MOCK_SSID, writer);
		
		_log("GET: " +MOCK_SSID+ ": " +status);
		if ( status.equals(Status.SUCCESS_OK) ) {
			_log(writer.toString());
			
			_log("ALREADY registered.  Resetting...");
			
			status = gpdvizClient.resetSensorSystem(MOCK_SSID, mock.getDescription());
			
			_log("RESET: " +MOCK_SSID+ ": " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				return;
			}
		}
		else {
			// Not yet registered. Register:
			status = gpdvizClient.registerSensorSystem(MOCK_SSID , mock.getDescription());
			_log("REGISTER: " +MOCK_SSID+ ": " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				return;
			}
		}
		
		_log("Activating provider...");
		
		mock.addEventListener(new IvEventListener() {
			public void eventGenerated(IvEvent event) {
				event.accept(myDispatcher);
			}
		});
		
		_startTimerToExitProgram(EXIT_MINUTES);
		
		mock.activate();
	}
	
	private void _startTimerToExitProgram(int minutes) {
		long delay = minutes * 60 * 1000;
		_log("Starting timer to exit this program in " +minutes+ " minutes");
		Timer terminate = new Timer("terminate demo");
		terminate.schedule(new TimerTask() {
			public void run() {
				_log("Time to stop the demo");
				try {
					Status status = gpdvizClient.unregisterSensorSystem(MOCK_SSID);
					_log("UNREGISTER" + ": " +status);
					
					// allow some time before exiting the program:
					Thread.sleep(2000);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}, delay);
	}
	
	/**
	 * Reacts to updates in the sensor system to notify the Gpdviz endpoint.
	 */
	private class MyDispatcher implements IvEventDispatcher {
		
		public void dispatchNewSourceEvent(NewSourceEvent event) {
			
			String srcid = event.getSource().getName();
			String latitude = event.getLat();
			String longitude = event.getLon();
			String description = latitude+ ", " +longitude;
			
			Status status = gpdvizClient.addNewSource(MOCK_SSID, srcid, description, latitude, longitude);
			
			_log(event.getClass().getSimpleName()+ ": " +MOCK_SSID+ "/" +srcid+ ": " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				mock.deactivate();
			}
		}
		
		public void dispatchSourceRemovedEvent(SourceRemovedEvent event) {
			String srcfid = event.getSrcfid();
			Status status = gpdvizClient.removeSource(MOCK_SSID, srcfid);
			
			_log(event.getClass().getSimpleName()+ ": " +MOCK_SSID+ "/" +srcfid+ ": " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				mock.deactivate();
			}			
		}
		
		public void dispatchNewStreamEvent(NewStreamEvent event) {
			
			Source src = event.getSource();
			Stream str = event.getStream();
			String srcid = src.getName();
			String strid = str.getName();
			
			Status status = gpdvizClient.addNewStream(MOCK_SSID, srcid, strid);
			
			_log(event.getClass().getSimpleName()+ ": " +MOCK_SSID+ "/" +srcid+ "/" +strid+ ": " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				mock.deactivate();
			}
			
		}
		
		public void dispatchStreamRemovedEvent(StreamRemovedEvent event) {
			
			String srcfid = event.getSrcfid();
			String strid = event.getStrid();
			
			Status status = gpdvizClient.removeStream(MOCK_SSID, srcfid, strid);
			
			_log(event.getClass().getSimpleName()+ ": " +MOCK_SSID+ "/" +srcfid+ "/" +strid+ ": " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				mock.deactivate();
			}
		}
		
		public void dispatchNewValueEvent(NewValueEvent event) {
			String strid = event.getStrid();
			String strfid = event.getStrfid();
			String value = event.getValue();

			Status status = gpdvizClient.addNewValue(MOCK_SSID, strid, strfid, value);
			
			_log(event.getClass().getSimpleName()+ ": " +MOCK_SSID+ ": " +strfid+ ": " +value+ " : " +status);
			if ( ! status.equals(Status.SUCCESS_OK) ) {
				mock.deactivate();
			}
		}
		
		public void dispatchSensorSystemResetEvent(SensorSystemResetEvent event) {
			try {
				Status status;
				status = gpdvizClient.resetSensorSystem(MOCK_SSID, mock.getDescription());
				_log(event.getClass().getSimpleName()+ ": " +MOCK_SSID+ ": " +status);
				if ( ! status.equals(Status.SUCCESS_OK) ) {
					mock.deactivate();
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void dispatchSensorSystemRegisteredEvent(SensorSystemRegisteredEvent event) {
			// Not meaningful here
		}
		
		public void dispatchSensorSystemUnregisteredEvent(SensorSystemUnregisteredEvent event) {
			// Not meaningful here
		}

	}
}

