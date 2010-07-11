//////////////////////////////////////////////////////
// COMET Project
// Module: Remote access & visualization of BML data 
// Author: Carlos Rueda
//
// $LastChangedRevision: 245 $
// $LastChangedDate: 2008-06-30 23:35:12 -0700 (Mon, 30 Jun 2008) $
// $LastChangedBy: crueda $
//////////////////////////////////////////////////////

package org.gpdviz.ss;

import java.io.Serializable;


/**
 * Information about a stream, that is, values for a certain
 * variable (sometimes called "channel").
 * This information includes a period that tells how often the observations
 * are generated, and a string representation of the units.
 * 
 * @author Carlos Rueda
 */
public class Stream extends BaseNode implements Serializable {
	private static final long serialVersionUID = 1L;

	// no-arg ctr
    Stream() {
    }
    
    public Stream(String name, String fullname, long period, String units) {
        super(name, fullname);
        setStringAttribute("period", String.valueOf(period));
        setStringAttribute("units", units);
    }

    public long getPeriod() {
        String period = getStringAttribute("period");
        return period != null ? Long.parseLong(period) : 0;
    }
}
