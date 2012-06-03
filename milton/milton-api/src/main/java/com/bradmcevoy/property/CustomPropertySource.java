/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.bradmcevoy.property;

import com.bradmcevoy.http.CustomProperty;
import com.bradmcevoy.http.CustomPropertyResource;
import com.bradmcevoy.http.Resource;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author brad
 */
public class CustomPropertySource implements PropertySource {

    public Object getProperty( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        if( prop != null ) {
            return prop.getTypedValue();
        } else {
            return null;
        }
    }

    public PropertyMetaData getPropertyMetaData( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        if( prop != null ) {
            return new PropertyMetaData( PropertyAccessibility.WRITABLE, prop.getValueClass());
        } else {
            return PropertyMetaData.UNKNOWN;
        }

    }


    public void setProperty( QName name, Object value, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        if( prop != null ) {
            prop.setFormattedValue( value.toString() );
        } else {
            throw new RuntimeException( "property not found: " + name.getLocalPart() );
        }
    }

    public boolean hasProperty( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        return prop != null;
    }

    public void clearProperty( QName name, Resource r ) {
        CustomProperty prop = lookupProperty( name, r );
        prop.setFormattedValue( null );
    }

    private CustomProperty lookupProperty( QName name, Resource r ) {
        if( name == null) throw new IllegalArgumentException( "name is null");
        if( r instanceof CustomPropertyResource ) {
            CustomPropertyResource cpr = (CustomPropertyResource) r;
            if( cpr.getNameSpaceURI() == null ) throw new IllegalArgumentException( "namespace uri is null on CPR");
            if( cpr.getNameSpaceURI().equals( name.getNamespaceURI() ) ) {
                return cpr.getProperty( name.getLocalPart() );
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public List<QName> getAllPropertyNames( Resource r ) {
        List<QName> list = new ArrayList<QName>();
        if( r instanceof CustomPropertyResource ) {
            CustomPropertyResource cpr = (CustomPropertyResource) r;
            for( String n : cpr.getAllPropertyNames() ) {
                QName qname = new QName( cpr.getNameSpaceURI(), n);
                list.add( qname );
            }
        }
        return list;
    }
    
}
