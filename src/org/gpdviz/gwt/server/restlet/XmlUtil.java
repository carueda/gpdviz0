package org.gpdviz.gwt.server.restlet;

import java.util.List;

import org.gpdviz.gwt.server.ss.GpdvizManager;
import org.gpdviz.ss.Observation;
import org.gpdviz.ss.SensorSystem;
import org.gpdviz.ss.Source;
import org.gpdviz.ss.Stream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper to create XML elements.
 * 
 * @author Carlos Rueda
 */
class XmlUtil {

	static Element toXmlSensorSystems(GpdvizManager gpdvizManager, Document d) {
		Element eltItem = d.createElement("sensorSystems");
		
		for (String ssid : gpdvizManager.getRegisteredIds() ) {
			SensorSystem ss = gpdvizManager.getSensorSystem(ssid);
			Element ssItem = toXmlSensorSystem(d, ss);  
			eltItem.appendChild(ssItem);  
		}  

		return eltItem;
	}
	
	static Element toXmlSensorSystem(Document d, SensorSystem ss) {
		Element eltItem = d.createElement("sensorSystem");
		
		Element eltName = d.createElement("ssid");
		eltName.appendChild(d.createTextNode(ss.getSsid()));
		eltItem.appendChild(eltName);

		Element eltDescription = d.createElement("description");
		eltDescription.appendChild(d.createTextNode(ss.getDescription()));
		eltItem.appendChild(eltDescription);

		Element strsElt = d.createElement("sources");
		eltItem.appendChild(strsElt);
		for ( Source src : ss.getSources().values() ) {
			Element srcElt = toXmlSource(d, ss, src);
			strsElt.appendChild(srcElt);
		}
		
		return eltItem;
	}
	
	static Element toXmlSource(Document d, SensorSystem ss, Source src) {
		Element eltItem = d.createElement("source");
		
		Element elt;
		
		elt = d.createElement("ssid");
		elt.appendChild(d.createTextNode(ss.getSsid()));
		eltItem.appendChild(elt);

		elt = d.createElement("srcid");
		elt.appendChild(d.createTextNode(src.getName()));
		eltItem.appendChild(elt);
		
		elt = d.createElement("description");
		elt.appendChild(d.createTextNode(src.getStringAttribute("location")));
		eltItem.appendChild(elt);

		elt = d.createElement("latitude");
		elt.appendChild(d.createTextNode(src.getStringAttribute("latitude")));
		eltItem.appendChild(elt);
		
		elt = d.createElement("longitude");
		elt.appendChild(d.createTextNode(src.getStringAttribute("longitude")));
		eltItem.appendChild(elt);

		
		Element strsElt = d.createElement("streams");
		eltItem.appendChild(strsElt);
		for ( Stream str : ss.getStreams(src.getFullName()) ) {
			Element strElt = toXmlStream(d, ss, src, str);
			strsElt.appendChild(strElt);
		}
		
		return eltItem;
	}
	
	static Element toXmlStream(Document d, SensorSystem ss, Source src, Stream str) {
		Element eltItem = d.createElement("stream");

		Element elt;

		elt = d.createElement("ssid");
		elt.appendChild(d.createTextNode(ss.getSsid()));
		eltItem.appendChild(elt);

		elt = d.createElement("srcid");
		elt.appendChild(d.createTextNode(src.getName()));
		eltItem.appendChild(elt);

		elt = d.createElement("strid");
		elt.appendChild(d.createTextNode(str.getName()));
		eltItem.appendChild(elt);

		String strfid = str.getFullName();
		assert strfid != null;

		Element observsItem = d.createElement("observations");
		eltItem.appendChild(observsItem);
		
		List<Observation> dps = ss.getSensorSystemInfo().getObservations(strfid);
		if ( dps != null ) {
			
			for ( Observation value : dps ) {
				
				Element observItem = d.createElement("observation");
				observsItem.appendChild(observItem);
				
				elt = d.createElement("timestamp");
				elt.appendChild(d.createTextNode(String.valueOf(value.getTimestamp())));
				observItem.appendChild(elt);
				
				elt = d.createElement("value");
				elt.appendChild(d.createTextNode(value.getValue()));
				observItem.appendChild(elt);
			}
		}

		return eltItem;
	}

}
