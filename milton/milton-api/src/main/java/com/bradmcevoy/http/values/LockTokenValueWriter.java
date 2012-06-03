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

package com.bradmcevoy.http.values;

import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockToken;
import com.bradmcevoy.http.XmlWriter;
import com.bradmcevoy.http.XmlWriter.Element;
import com.bradmcevoy.http.webdav.LockWriterHelper;
import com.bradmcevoy.http.webdav.WebDavProtocol;
import java.util.Map;

public class LockTokenValueWriter implements ValueWriter {

    private LockWriterHelper lockWriterHelper = new LockWriterHelper();

    public LockWriterHelper getLockWriterHelper() {
        return lockWriterHelper;
    }

    public void setLockWriterHelper( LockWriterHelper lockWriterHelper ) {
        this.lockWriterHelper = lockWriterHelper;
    }

	@Override
    public boolean supports( String nsUri, String localName, Class c ) {
        return LockToken.class.isAssignableFrom( c );
    }

	@Override
    public void writeValue( XmlWriter writer, String nsUri, String prefix, String localName, Object val, String href, Map<String, String> nsPrefixes ) {
        LockToken token = (LockToken) val;
		String d = WebDavProtocol.DAV_PREFIX;
        Element lockDiscovery = writer.begin( d + ":lockdiscovery" ).open();		
        if( token != null ) {
			Element activeLock = writer.begin( d + ":activelock" ).open();
            LockInfo info = token.info;
            lockWriterHelper.appendType( writer, info.type );
            lockWriterHelper.appendScope( writer, info.scope );
            lockWriterHelper.appendDepth( writer, info.depth );
            lockWriterHelper.appendOwner( writer, info.lockedByUser );
            lockWriterHelper.appendTimeout( writer, token.timeout.getSeconds() );
            lockWriterHelper.appendTokenId( writer, token.tokenId );
            lockWriterHelper.appendRoot( writer, href );
			activeLock.close();
        }		
        lockDiscovery.close();
    }

	@Override
    public Object parse( String namespaceURI, String localPart, String value ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
