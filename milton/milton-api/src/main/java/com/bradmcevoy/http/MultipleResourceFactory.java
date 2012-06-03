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

package com.bradmcevoy.http;

import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleResourceFactory implements ResourceFactory {

    private Logger log = LoggerFactory.getLogger( MultipleResourceFactory.class );
    protected final List<ResourceFactory> factories;
    protected Map<String, ResourceFactory> mapOfFactoriesByHost;

    public MultipleResourceFactory() {
        factories = new ArrayList<ResourceFactory>();
    }

    public MultipleResourceFactory( List<ResourceFactory> factories ) {
        this.factories = factories;
    }

	@Override
    public Resource getResource( String host, String url ) throws NotAuthorizedException, BadRequestException {
        if( log.isTraceEnabled() ) {
            log.trace( "getResource: " + url );
        }
        ResourceFactory hostRf = null;
        if( mapOfFactoriesByHost != null ) {
            hostRf = mapOfFactoriesByHost.get( host );
        }
        Resource theResource;
        if( hostRf != null ) {
            theResource = hostRf.getResource( host, url );
        } else {
            theResource = findFromFactories( host, url );
        }
        if( theResource == null ) {
            log.debug( "no resource factory supplied a resouce" );
        } else {
			
		}
        return theResource;
    }

    /**
     * When set will always be used exclusively for any matching hosts
     * 
     * @return
     */
    public Map<String, ResourceFactory> getMapOfFactoriesByHost() {
        return mapOfFactoriesByHost;
    }

    public void setMapOfFactoriesByHost( Map<String, ResourceFactory> mapOfFactoriesByHost ) {
        this.mapOfFactoriesByHost = mapOfFactoriesByHost;
    }

    private Resource findFromFactories( String host, String url ) throws NotAuthorizedException, BadRequestException {
        for( ResourceFactory rf : factories ) {
            Resource r = rf.getResource( host, url );
            if( r != null ) {
                return r;
            }
        }
        return null;
    }
}
