package org.gpdviz.ieee1451;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import org.gpdviz.client.GpdvizClient;
import org.gpdviz.ieee1451.client.Ieee1451Client;
import org.gpdviz.ieee1451.client.Ieee1451Client.DataPoint;
import org.gpdviz.ieee1451.client.Ieee1451Client.TransducerInfo;
import org.restlet.data.Status;


/** 
 * A test client that "pulls" data from an IEEE1451 server and pushes is to
 * a Gpdviz endpoint.
 * 
 * @author Carlos Rueda
 */
public class Ieee1451Main {

	private static final long PROGRAM_DURATION_MINS = 2;
	

// From the old code in sosplot:
//	private static Oidemo1451ContentProvider[] ncaps = {
//		new Oidemo1451ContentProvider("1", "http://oidemo.mbari.org:1451/1451"),
//		//new Oidemo1451ContentProvider("2", "http://www.comsys.informatik.uni-kiel.de:1451/1451"),
//		//new Oidemo1451ContentProvider("3", "http://esonet.bremen.wdc-mare.org:1451/1451"),
//		new Oidemo1451ContentProvider("4", "http://134.89.13.150:1451/1451")		//new Oidemo1451ContentProvider("5", "http://147.83.140.20:1451/1451"),
//		//new Oidemo1451ContentProvider("4", "http://147.83.140.20:1451/1451"),
//		//new Oidemo1451ContentProvider("5", "http://147.83.140.10:1451/1451"),
//	};
	
	public static void main(String[] args) throws Exception {
		new Ieee1451Main(args);
	}


	private GpdvizClient gpdvizClient;
	private Ieee1451Client ieee1451Client;
	
	private volatile boolean ncontinueRunning = true;
	
	private void _usage(String msg) {
		if ( msg != null ) {
			System.err.println(
					getClass().getName()+ ": " +msg+ "\n" +
					"  Try --help\n"
			);
			System.exit(1);
		}
		else {
			System.out.println(
					"USAGE: " +getClass().getName()+ " params\n" +
					"  params:\n" +
					"    --endpoint <url>    The Gpdviz REST endpoint.\n" +
					"    --server <url>    IEEE1451 server.\n" +
					"    --ncap <num>      IEEE1451 NCAP ID.\n" +
					"    --unregister      Unregister sensor system and exit\n" +
					""
			);
			System.exit(0);
		}

	}

	Ieee1451Main(String[] args) throws Exception {
		String endPoint = null;
		String ieee1451server = null;
		String ieee1451ncap = null;
		boolean unregister = false;
		
		if ( args.length == 0 || args[0].matches("-*help.*") ) {
			_usage(null);
		}
		
		for ( int arg = 0; arg < args.length; arg++ ) {
			if ( args[arg].equals("--endpoint") ) {
				endPoint = args[++arg];
			}
			else if ( args[arg].equals("--server") ) {
				ieee1451server = args[++arg];
			}
			else if ( args[arg].equals("--ncap") ) {
				ieee1451ncap = args[++arg];
			}
			else if ( args[arg].equals("--unregister") ) {
				unregister = true;
			}
		}
		
		if( endPoint == null ) {
			_usage("missing endPoint");
		}
		if( ieee1451server == null ) {
			_usage("missing ieee1451 server");
		}
		if( ieee1451ncap == null ) {
			_usage("missing ieee1451 ncap");
		}

		final String ssid = "ieee1451." +ieee1451ncap;
		String ssDescription = "some description";
		
		gpdvizClient = new GpdvizClient(endPoint);
		gpdvizClient.setLogger(new PrintWriter(System.out, true), "%%%%% ");
		
		
		if ( unregister ) {
			gpdvizClient.unregisterSensorSystem(ssid);
			return;
		}
		
		ieee1451Client = new Ieee1451Client(ieee1451ncap, ieee1451server);
		
		String[] tims = ieee1451Client.getTims();
		if ( tims.length == 0 ) {
			_log("No TIMs reported");
			return;
		}

		_prepareSensorSystem(ssid, ssDescription);

		for ( final String timId : tims ) {
			_log(" tim = " +timId);

			String[] latlon = ieee1451Client.getGeolocation(timId);
			String timDescription = ieee1451Client.getTimDescription(timId);
			
			_pause();
			final String srcid = "tim_" +timId;
			gpdvizClient.addNewSource(ssid, srcid, timDescription, latlon[0], latlon[1]);
			

			TransducerInfo[] transducerInfos = ieee1451Client.getTransducerInfos(timId);
			for (final TransducerInfo ti : transducerInfos) {
				
				// not used yet
//				String chDescription = ieee1451Client.getChannelDescription(timId, ti.channelId);
				
				final String strid = "ch_" +ti.getChannelId();
				gpdvizClient.addNewStream(ssid, srcid, strid);
				_pause();
				
				final Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if ( ! ncontinueRunning ) {
							timer.cancel();
							return;
						}
						
						try {
							DataPoint dp = ieee1451Client.getDataPoint(timId, ti.getChannelId());
							// millis not used yet
//							long millis = dp.getMillis();
							gpdvizClient.addNewValue(ssid, strid, srcid+ "/" +strid, dp.getValue());
						}
						catch (Exception e) {
							e.printStackTrace();
							timer.cancel();
						}
					}
					
				}, 1000, 10000);
				
			}
			
		}
			
		// timer to terminate this demo
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				ncontinueRunning = false;

				_log("Time to terminate this demo.");
				try {
					gpdvizClient.unregisterSensorSystem(ssid);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				System.exit(0);
			}
			
		}, PROGRAM_DURATION_MINS * 60 * 1000);
		
		_log("This demo will exit in " +PROGRAM_DURATION_MINS+ " minutes.");
	}

	/**
	 * Registers the sensor system if not already registered; otherwise, resets it.
	 * @param ssid
	 * @param description
	 * @throws Exception
	 */
	private void _prepareSensorSystem(String ssid, String description) throws Exception {
		StringWriter writer = new StringWriter();
		Status status = gpdvizClient.getSensorSystem(ssid, writer);

		if ( status.equals(Status.SUCCESS_OK) ) {
			_log(writer.toString());

			_log("ALREADY registered.  Resetting...");

			status = gpdvizClient.resetSensorSystem(ssid, description);
			_log("RESET" + ": " +status);
		}
		else {
			status = gpdvizClient.registerSensorSystem(ssid, description);
			_log("registerSensorSystem"+ ": " +ssid+ ": " +status);
		}
	}


	void _log(String msg) {
		System.out.println("=== " +msg);
	}

	void _pause() throws Exception {
//		_log("Press Enter to continue");
//		System.in.read();
		Thread.sleep(1000);
	}
	
}
