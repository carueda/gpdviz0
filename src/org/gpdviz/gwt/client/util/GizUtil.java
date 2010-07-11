package org.gpdviz.gwt.client.util;


import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Misc utilities.
 * 
 * <p>
 * NOTE: source code does not use Java 1.5 features because of GWT limitations.
 * 
 * @author Carlos Rueda
 * @version $Id: Util.java 245 2008-07-01 06:35:12Z crueda $
 */
public class GizUtil {
	public static native String getLocationSearch() /*-{
        return $wnd.location.search;
    }-*/ ;

	public static native String getLocationHost() /*-{
        return $wnd.location.host;
    }-*/ ;

	public static native String getLocationProtocol() /*-{
        return $wnd.location.protocol;
    }-*/ ;
    
    
    public static Map<String,String> getParams() {
        Map<String,String> params = null;
        String locSearch = URL.decode(getLocationSearch());
//        Main.log("getParams: locSearch=" +locSearch);
        if ( locSearch != null && locSearch.trim().length() > 0 ) {
            // skip ? and get &-separated chunks:
            locSearch = locSearch.substring(1);
            String[] chunks = locSearch.split("&");
            if ( chunks.length > 0 ) {
                params = new HashMap<String,String>();
            }
            for (int i = 0; i < chunks.length; i++) {
                String chunk = chunks[i];
//                Main.log("getParams: " +i+ ": chunk=" +chunk);
                String[] toks = chunk.split("=");
                if ( toks.length == 2 ) {
                    params.put(toks[0], toks[1]);
                }
            }
        }
        return params;
    }

    /** Ad hoc utility */
    public static HTML createHtml(String str, int fontFize) {
        HTML obj = new HTML(str);
        setFontSize(obj, fontFize);
        return obj;
    }

    /** Copied from GChart and made public */
    static void setFontSize(UIObject uio, int fontSize) {
           DOM.setIntStyleAttribute(
              uio.getElement(), "fontSize", fontSize);
    }

    /** Ad hoc utility */
    public static Label createLabel(String str, int fontFize) {
        Label obj = new Label(str);
        setFontSize(obj, fontFize);
        return obj;
    }
    

}
