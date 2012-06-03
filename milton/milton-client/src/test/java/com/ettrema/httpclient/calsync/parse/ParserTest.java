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

/*
 * Copyright 2012 McEvoy Software Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ettrema.httpclient.calsync.parse;

import info.ineighborhood.cardme.io.VCardWriter;
import info.ineighborhood.cardme.vcard.VCard;
import info.ineighborhood.cardme.vcard.VCardImpl;
import info.ineighborhood.cardme.vcard.types.*;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class ParserTest extends TestCase{

    BeanPropertyMapper parser;
    
    @Override
    protected void setUp() throws Exception {
        parser = new BeanPropertyMapper(new PropertyAccessor());
    }
    
    
    
    public void testParse() {
        MyBean bean = new MyBean();
        VCard card = new VCardImpl();
        card.setBegin(new BeginType());        
        
        card.setUID(new UIDType("xxx"));
        card.setName(new NameType("smith", "joe"));
        String formattedName = buildFormattedName(bean);
        card.setFormattedName(new FormattedNameType(formattedName));
        card.setEnd(new EndType());
        VCardWriter writer = new VCardWriter();
        writer.setVCard(card);
        String text = writer.buildVCardString();
        
        parser.toBean(bean, text);
        
        assertEquals("xxx", bean.getUid());
        assertEquals("smith", bean.getLastName());
        assertEquals("joe", bean.getFirstName());
    }
    
    public void testFormat() {
        MyBean bean = new MyBean();
        bean.setUid("xxx");
        bean.setFirstName("joe");
        bean.setLastName("smith");
        
        String text = parser.toVCard(bean);
        
        assertTrue(text.contains("xxx"));
        assertTrue(text.contains("joe"));
        assertTrue(text.contains("smith"));
    }    

    private String buildFormattedName(MyBean bean) {
        return bean.getFirstName() + " " + bean.getLastName();
    }
}
