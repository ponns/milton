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

package com.ettrema.json;

import com.bradmcevoy.http.webdav.PropFindPropertyBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class JsonPropFindHandlerTest extends TestCase {
    
    public JsonPropFindHandlerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * Test of parseField method, of class JsonPropFindHandler.
     */
    public void testParseField() {
        System.out.println("parseField");
        String field = "foo>bar";
        Set<QName> fields = new HashSet<QName>();
        Map<QName, String> aliases = new HashMap<QName, String>();
        
        JsonPropFindHandler instance = new JsonPropFindHandler((PropFindPropertyBuilder)null);
        instance.parseField(field, fields, aliases);

        QName actualQName = fields.iterator().next();
        assertEquals("foo", actualQName.getLocalPart());
        Entry<QName, String> actualAlias = aliases.entrySet().iterator().next();
        assertSame(actualQName, actualAlias.getKey());
        assertEquals("bar", actualAlias.getValue());
        
    }

}
