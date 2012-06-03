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

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bradmcevoy.http.LockInfo.LockScope;
import com.bradmcevoy.http.LockInfo.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockInfoSaxHandler extends DefaultHandler {

    private static final Logger log = LoggerFactory.getLogger( LockInfo.class );

    private LockInfo info = new LockInfo();
    private StringBuilder owner;
    private Stack<String> elementPath = new Stack<String>();

    @Override
    public void startElement( String uri, String localName, String name, Attributes attributes ) throws SAXException {
        elementPath.push( localName );
        if( localName.equals( "owner" ) ) {
            owner = new StringBuilder();
        }
        super.startElement( uri, localName, name, attributes );
    }

    @Override
    public void characters( char[] ch, int start, int length ) throws SAXException {
        if( owner != null ) {
            owner.append( ch, start, length );
        }
    }

    @Override
    public void endElement( String uri, String localName, String name ) throws SAXException {
        elementPath.pop();
        if( localName.equals( "owner" ) ) {
            log.debug( "owner: " + owner.toString());
            getInfo().lockedByUser = owner.toString();
        }
        if( elementPath.size() > 1 ) {
            if( elementPath.get( 1 ).equals( "lockscope" ) ) {
                if( localName.equals( "exclusive" ) ) {
                    getInfo().scope = LockScope.EXCLUSIVE;
                } else if( localName.equals( "shared" ) ) {
                    getInfo().scope = LockScope.SHARED;
                } else {
                    getInfo().scope = LockScope.NONE;
                }
            } else if( elementPath.get( 1 ).equals( "locktype" ) ) {
                if( localName.equals( "read" ) ) {
                    getInfo().type = LockType.READ;
                } else if( localName.equals( "write" ) ) {
                    getInfo().type = LockType.WRITE;
                } else {
                    getInfo().type = LockType.WRITE;
                }
            }

        }
        super.endElement( uri, localName, name );
    }

    public LockInfo getInfo() {
        return info;
    }
}
