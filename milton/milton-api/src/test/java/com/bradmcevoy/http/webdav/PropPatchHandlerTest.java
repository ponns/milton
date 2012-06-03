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

package com.bradmcevoy.http.webdav;

import com.bradmcevoy.http.PropPatchableResource;
import com.bradmcevoy.http.*;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.webdav.PropPatchHandler.Fields;
import org.apache.commons.io.output.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

/**
 *
 */
public class PropPatchHandlerTest extends TestCase {

    public void testDummy() {
        
    }

//    public void testParseContent_MSOffice() throws Exception  {
//        InputStream in = this.getClass().getResourceAsStream("proppatch_request_msoffice.xml");
//        assertNotNull(in);
//        PropPatchHandler.Fields fields = PropPatchHandler.parseContent(in);
//        assertNotNull(fields);
//        assertEquals(3, fields.setFields.size());
//        assertEquals(0, fields.removeFields.size());
//        assertEquals("Win32LastAccessTime",fields.setFields.get(0).name);
//        assertEquals("Win32LastModifiedTime",fields.setFields.get(1).name);
//        assertEquals("Win32FileAttributes",fields.setFields.get(2).name);
//
//        assertEquals("Wed, 10 Dec 2008 21:55:22 GMT",fields.setFields.get(0).value);
//        assertEquals("Wed, 10 Dec 2008 21:55:22 GMT",fields.setFields.get(1).value);
//        assertEquals("00000020",fields.setFields.get(2).value);
//
//    }
//
//    public void testParseContent_Spec() throws Exception  {
//        InputStream in = this.getClass().getResourceAsStream("proppatch_request_spec.xml");
//        assertNotNull(in);
//        PropPatchHandler.Fields fields = PropPatchHandler.parseContent(in);
//        assertNotNull(fields);
//        assertEquals(2, fields.setFields.size());
//        assertEquals(1, fields.removeFields.size());
//    }
//
//    public void testProcess() throws IOException, NotAuthorizedException, BadRequestException, ConflictException {
//        System.out.println( "start testProcess" );
//        InputStream in = this.getClass().getResourceAsStream("proppatch_request_vista.http");
//        assertNotNull( in );
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        PropPatchHandler handler = new PropPatchHandler( null );
//        PropPatchableResource res = createMock( PropPatchableResource.class);
//        res.setProperties( (Fields) anyObject());
//        expectLastCall();
//
//        Request request = createMock( Request.class);
//        expect(request.getInputStream()).andReturn( in ).atLeastOnce();
//        expect(request.getAbsoluteUrl()).andReturn( "http://test/abc");
//
//        Response response = createMock( Response.class);
//        expect(response.getOutputStream()).andReturn( out );
//        response.setStatus( Response.Status.SC_OK );
//        expectLastCall();
//
//        replay(res, request, response);
//
//        handler.processExistingResource(null, request, response, res);
//
//        System.out.println( out.toString() );
//
//
//        System.out.println( "finish testProcess" );
//    }
}
