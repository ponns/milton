package com.bradmcevoy.http.webdav;

import com.bradmcevoy.property.PropertySource;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class PropertySourcesList extends ArrayList<PropertySource> {

	private static final Logger log = LoggerFactory.getLogger( PropertySourcesList.class );
	
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an empty list
     */
    public PropertySourcesList() {
		log.info("Created PropertySourcesList");
    }

    /**
     * Adds all default property sources
     * 
     * @param resourceTypeHelper
     */
    public PropertySourcesList( ResourceTypeHelper resourceTypeHelper ) {
        this.addAll( PropertySourceUtil.createDefaultSources( resourceTypeHelper ) );
		log.info("Created propertysourceslist, hashcode: " + hashCode() + " size: " + size());		
    }

	@Override
	public boolean add(PropertySource e) {
		log.info("adding property source: " + e.getClass() + " to PropertySourcesList: " + hashCode());
		return super.add(e);
	}

	
	
    /**
     * Allows you to add an extra source to the default list
     *
     * @param source
     */
    public void setExtraSource( PropertySource source ) {
        this.add( source );
    }

    public void setSources( List<PropertySource> sources ) {
        this.clear();
        this.addAll( sources );
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( PropertySource l : this ) {
			sb.append(l.getClass()).append(",");
		}
		return sb.toString();
	}
	
	
}
