package org.gpdviz.ss;


/**
 * Information about a stream source. A source is a "site" or "station"
 * comprising a number of streams or other sources, so sources are
 * organized hierarchically.
 * 
 * @author Carlos Rueda
 */
public class Source extends BaseNode {
	private static final long serialVersionUID = 1L;

    // no-arg ctr
	Source() {
    }
    
    public Source(String name, String fullname, String location, String latitude, String longitude) {
    	super(name, fullname);
        setStringAttribute("location", location);
        setStringAttribute("latitude", latitude);
        setStringAttribute("longitude", longitude);
    }
    
	public String toString() {
		return getFullName()+ ":lat,lon=" +getStringAttribute("latitude")+
			"," +getStringAttribute("longitude");
	}

}
