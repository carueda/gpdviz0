package org.gpdviz.ieee1451.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.gpdviz.ss.Observation;


/**
 * Provides data from an IEEE1451 server.
 * <p>
 * Note: implementation only intended for demonstration purposes.
 * 
 * @author Carlos Rueda
 */
public class Ieee1451Client {
	
	/** timeout for connect and read: 20 secs */
	private static final int CONNECT_AND_READ_TIMEOUT = 20 * 1000;

	
	/////////////////////////////////////////////
	// Instance.
	
	private String ncapId;

	private String server;
	
	public Ieee1451Client(String ncapId, String server) {
		super();
		this.ncapId = ncapId;
		this.server = server;
	}
	
	public String getNcapId() {
		return ncapId;
	}

	public String toString() {
		return ncapId+ ":" +server;
	}

	public Observation getObservation(String timId, String channelId) throws IOException, Exception {
		String getDataUrl = getDataUrl(timId, channelId); 
		
        String response = getResponse("getObservation", getDataUrl);
        String[] toks = parseResponse(response); 

        long seconds;
        long nanosec;
        String svalue;

        try {
	//        int ncapId = Integer.parseInt(toks[1]);
	        int timIdRecd = Integer.parseInt(toks[2]);
	        assert Integer.parseInt(timId) == timIdRecd;
	        int channelIdRecd = Integer.parseInt(toks[3]);
	        assert Integer.parseInt(channelId) == channelIdRecd;
	        seconds = Long.parseLong(toks[4]);
	        nanosec = Long.parseLong(toks[5]);
	        svalue = toks[6];
		}
		catch (RuntimeException ex) {
			throw new Exception("Cannot parse the data response: " +response, ex);
		}
        
        long timestamp = seconds*1000 + nanosec/1000000;
        
        Observation obs = new Observation(timestamp, svalue);
        return obs;
	}
	
	private String getDataUrl(String timId, String channelId) {
        String url = server + "/TransducerAccess/ReadData?ncapId=" +ncapId+ "&" +
					"timId=" +timId+ "&channelId=" +channelId+ "&samplingMode=5&timeoutSec=10&timeoutNsec=0&responseFormat=text";
        return url;
    }
    
	public String[] getTims() throws IOException {
        String timDiscoveryUrl = getTimDiscoveryUrl();
        String[] toks = getAndParseResponse("getTims", timDiscoveryUrl); 
//        int ncapId = Integer.parseInt(toks[1]);
        int numTims = toks.length - 2;
        
        // 2009-11-13: however, TIM=0 means NO TIM, so check for this case and
        // break the loop upon the first occurrence of TIM=0:
        List<String> tims = new ArrayList<String>();
        for ( int i = 0; i < numTims; i++ ) {
        	String tim = toks[2 + i];
        	if ( tim.equals("0") ) {
        		break;
        	}
        	else {
        		tims.add(tim);
        	}
        }
        return tims.toArray(new String[tims.size()]);
	}
	
	private String getTimDiscoveryUrl() {
    	String url = server + "/Discovery/TIMDiscovery?ncapId=" +ncapId+ "&responseFormat=text";
    	return url;
    }

	
	public static class TransducerInfo {
		private String channelId;
		String name;
		
		TransducerInfo() {
		}
		
		private void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getChannelId() {
			return channelId;
		}
	}
	
	public TransducerInfo[] getTransducerInfos(String timId) throws IOException {
    	String transducerDiscoveryUrl = getTransducerDiscoveryUrl(timId);
    	String[] toks = getAndParseResponse("getTransducerInfos", transducerDiscoveryUrl);
//        ncapId = Integer.parseInt(toks[1]);
        String timIdRecd = toks[2];
        assert timId.equals(timIdRecd);
        int numChannels = Integer.parseInt(toks[3]);
        TransducerInfo[] transducerInfos = new TransducerInfo[numChannels];
        for ( int j = 0; j < numChannels; j++ ) {
        	transducerInfos[j] = new TransducerInfo();
        	transducerInfos[j].setChannelId(toks[4 + j]);
        	transducerInfos[j].name = toks[4 + numChannels + j]; 
        }
        return transducerInfos;
	}
	
	private String getTransducerDiscoveryUrl(String timId) {
    	String url = server + "/Discovery/TransducerDiscovery?ncapId=" +ncapId+ "&timId=" +timId+ "&responseFormat=text";
    	return url;
    }

	
	
	public String getTimDescription(String timId) throws IOException {
//		if ( true ) return "TIM-" +timId;
        String timMetadataUrl = getTimMetadataUrl("" +timId);
        String[] toks = getAndParseResponse("getTimDescription", timMetadataUrl); 
    	// NOTE: taking last token as the channel description
    	String timDescription = toks[toks.length - 1]; 
    	return timDescription;
	}
	private String getTimMetadataUrl(String timId) {
    	// note: channelId is 0.
        String url = server + "/TEDSManager/ReadTeds?ncapId=" +ncapId+ "&" +
					"timId=" +timId+ "&channelId=0&tedsType=2&timeoutSec=10&timeoutNsec=0&responseFormat=text";
        return url;
    }
    
	
	public String[] getGeolocation(String timId) throws IOException {
        String geolocationUrl = getGeolocationUrl(timId); 
    	String[] toks = getAndParseResponse("getGeolocation", geolocationUrl);
//        assert contentProvider.getNcapId().equals(toks[1]);
        assert timId.equals(toks[2]);
        int channelIdRecd = Integer.parseInt(toks[3]);
        assert channelIdRecd == 0;
        int tedsType = Integer.parseInt(toks[4]);
        assert tedsType == 14;
        
        if ( toks.length < 7 ) {
        	System.out.println(getClass().getName()+ "Error: expecting lat and lon tokens. " +
        			"NCAP=" +getNcapId()+ " timId=" +timId+ ".  Continuing."
        	);
        	return null;
        }
        return new String[] { toks[6], toks[5] };
    	// elevation = toks[7];
	}
	
	private String getGeolocationUrl(String timId) {
    	// note: channelId is 0.
        String url = server + "/TEDSManager/ReadTeds?ncapId=" +ncapId+ "&" +
					"timId=" +timId+ "&channelId=0&tedsType=14&timeoutSec=10&timeoutNsec=0&responseFormat=text";
        return url;
    }
    
	public String getChannelDescription(String timId, String channelId) throws IOException {
//		if ( true ) return "CH-" +timId+ "-" + channelId;
    	String channelIdUrl = getChannelIdUrl(""+ timId, ""+ channelId);
    	String[] toks = getAndParseResponse("getChannelDescription", channelIdUrl);
    	// NOTE: taking last token as the channel description
    	String channelDescription = toks[toks.length - 1]; 
    	return channelDescription;
	}
	private String getChannelIdUrl(String timId, String channelId) {
        String url = server + "/TEDSManager/ReadTeds?ncapId=" +ncapId+ "&" +
    				"timId=" +timId+ "&channelId=" +channelId+ "&tedsType=4&timeoutSec=10&timeoutNsec=0&responseFormat=text";
        return url;
    }
	
	
	
	static String[] getAndParseResponse(String prefixMsg, String request) throws IOException {
        String response = getResponse(prefixMsg, request);
        String[] toks = parseResponse(response); 
        return toks;
    }

	private static String getResponse(String prefixMsg, String request) throws IOException {
		System.out.println("getResponse{" +prefixMsg+ "}(timeout=" +CONNECT_AND_READ_TIMEOUT+ " ms): request:" +request);
		URL url = new URL(request);
		// set connect and read timeouts:
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(CONNECT_AND_READ_TIMEOUT);
		conn.setReadTimeout(CONNECT_AND_READ_TIMEOUT);
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ( (line = br.readLine()) != null ) {
			sb.append(line + "\n");
		}
		String response = sb.toString();
		System.out.println("getResponse: response:" +response);
		return response;
	}

	/** parses the response using ' ' and ',' as token separators and respecting quoted strings, which are however
	 * returned without the quotes
	 */
	private static String[] parseResponse(String response) {
		List<String> toks = new ArrayList<String>();
		
		StringBuffer currTok = new  StringBuffer();
		
		boolean inQuote = false;
		
		for ( int i = 0; i < response.length(); i++ ) {
			char chr = response.charAt(i);
			switch (chr) {
			
			case ',': case ' ': case '\n': case '\r': case '\t': case '\f':
				if ( inQuote ) {
					currTok.append(chr);
				}
				else {
					while ( i < response.length() - 1 && Character.isWhitespace(response.charAt(i + 1)) ) {
						i++;
					}
					// token completed.
					if ( currTok.length() > 0 ) {
						// previous token under construction: add it
						toks.add(currTok.toString().replaceAll("^\"+|\"+$", ""));
						currTok.setLength(0);
					}
				}
				break;

			case '"':
				inQuote = !inQuote; 
				currTok.append(chr);
				break;
			
			default:
				currTok.append(chr);
			}
		}
		
		// pending token?
		if ( currTok.length() > 0 ) {
			// previous token under construction: added (with no quotes)
			toks.add(currTok.toString().replaceAll("^\"+|\"+$", ""));
			currTok.setLength(0);
		}

		System.out.println("parseResponse: " +toks);
		
        int errCode = Integer.parseInt(toks.get(0));
        assert errCode == 0;

		return toks.toArray(new String[toks.size()]);
	}


}
