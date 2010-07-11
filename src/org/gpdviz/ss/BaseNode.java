package org.gpdviz.ss;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A base class for objects having attributes.
 * Special attributes:
 * - name: eg. "foo"
 * - fullname: eg., "/baz/foo"
 * 
 * <p>
 * Comparison based on {@link #getFullName()}.
 * 
 * @author Carlos Rueda
 */
public class BaseNode implements Serializable, Comparable<BaseNode> {
	private static final long serialVersionUID = 1L;

	protected Map<String,String> attrs = new HashMap<String,String>();

	// no-arg ctr.
	BaseNode() {
    }
    
    public BaseNode(String name, String fullname) {
        if ( name == null || name.trim().length() == 0 || name.indexOf('/') >= 0 ) {
        	throw new IllegalArgumentException("bad name: " +name);
        }
        if ( fullname == null || fullname.trim().length() == 0 || fullname.endsWith("/") ) {
        	throw new IllegalArgumentException("bad fullname: " +fullname);
        }
        setStringAttribute("name", name);
        setStringAttribute("fullname", fullname);
    }
    
    public String getName() {
        return getStringAttribute("name");
    }
    
    public String getFullName() {
    	return getStringAttribute("fullname");
    }
    
    /** eg.: parentFullName("foo/bar") returns "foo"
     */
    public String getParentFullName() {
    	String fullName = getFullName();
    	int idx = fullName.lastIndexOf('/');
    	return idx >= 0 ? fullName.substring(0, idx) : null;
    }
    
    public String getStringAttribute(String attrName) {
        return (String) attrs.get(attrName);
    }
    
     
    public final void setStringAttribute(String attrName, String attrValue) {
        attrs.put(attrName, attrValue);
    }

	public String toString() {
		return getFullName();
	}

	/**
	 * Comparison based on {@link #getFullName()}.
	 */
	public int compareTo(BaseNode o) {
		if ( o == null ) {
			throw new NullPointerException();
		}
		String my = getFullName();
		String other = o.getFullName();
		if ( my == null ) {
			return other == null ? 0 : -1;
		}
		return my.compareTo(other);
	}
}
