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

import info.ineighborhood.cardme.vcard.types.BeginType;
import info.ineighborhood.cardme.vcard.types.EndType;
import java.lang.reflect.Method;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class TypeConverterTest extends TestCase {
    
    PropertyAccessor typeConverter;

    @Override
    protected void setUp() throws Exception {
        typeConverter = new PropertyAccessor();
    }
    
        
    public void test_GetString() throws Exception{
        MyBean bean = new MyBean();
        bean.setLastName("XXX");
        Method readMethod = bean.getClass().getMethod("getLastName");
        String v = typeConverter.get(bean, readMethod, String.class);
        assertEquals("XXX", v);
    }
    
    public void test_SetString() throws Exception{
        MyBean bean = new MyBean();
        bean.setLastName("XXX");
        Method writeMethod = bean.getClass().getMethod("setLastName", String.class);
        typeConverter.set(bean, writeMethod, "YYY");
        assertEquals("YYY", bean.getLastName());
    }    
}
