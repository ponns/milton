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
package com.ettrema.httpclient.calsync.store;

import com.ettrema.httpclient.annotation.Etag;
import com.ettrema.httpclient.annotation.Name;
import com.ettrema.httpclient.calsync.parse.annotation.GivenName;
import com.ettrema.httpclient.calsync.parse.annotation.LastName;
import com.ettrema.httpclient.calsync.parse.annotation.Uid;
import java.util.*;

/**
 * Implementation of CalendarEventFactory which holds simple annotated beans in
 * memory.
 *
 * Useful only for testing and debugging. Real implementations should persist to
 * a database or other repository.
 *
 * @author brad
 */
public class MemoryCalendarEventFactory implements CalendarEventFactory {

    private Map<String, MemoryCalendar> map = new HashMap<String, MemoryCalendar>();

    @Override
    public String getCtag(String storeId) {
        MemoryCalendar cal = map.get(storeId);
        if (cal != null) {
            return cal.ctag;
        }
        return null;
    }

    @Override
    public void deleteEvent(String storeId, String name) {
        MemoryCalendar cal = map.get(storeId);
        if (cal != null) {
            Iterator<EventBean> it = cal.beans.iterator();
            while( it.hasNext() ) {
                EventBean b = it.next();
                if( b.getName().endsWith(name)){
                    it.remove();
                }
            }
        }
        cal.updateCtag();
    }

    @Override
    public Object create(String storeId, String name) {
        MemoryCalendar cal = map.get(storeId);
        if( cal == null ) {
            cal = new MemoryCalendar();
            map.put(storeId, cal);
        }
        EventBean b = new EventBean();
        b.setName(name);
        cal.beans.add(b);
        return b;
    }

    @Override
    public void saveAndUpdateTags(String storeId, Object newEvent) {
        MemoryCalendar cal = map.get(storeId);
        if( cal == null ) {
            throw new RuntimeException("No calendar for: " + storeId);
        }
        EventBean b = (EventBean) newEvent;
        String etag = (long)(Math.random() * 1000000000) + "";
        b.setEtag(etag);
        
        cal.ctag = (long)(Math.random() * 1000000000) + "";
        
    }

    @Override
    public List getChildren(String storeId) {
        MemoryCalendar cal = map.get(storeId);
        if( cal == null ) {
            return null;
        }
        return cal.beans;
    }

    public EventBean add(String storeId, String name) {
        MemoryCalendar cal = map.get(storeId);
        if( cal == null ) {
            cal = new MemoryCalendar();
            map.put(storeId, cal);
        }
        EventBean b = new EventBean();
        b.setUid(UUID.randomUUID().toString());
        b.setName(name);
        cal.beans.add(b);
        return b;
    }

    public class MemoryCalendar {

        private String ctag;
        private List<EventBean> beans = new ArrayList<EventBean>();

        private void updateCtag() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public class EventBean {

        private String name;
        private String uid;
        private String etag;
        private String firstName;
        private String lastName;

        @Uid
        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @Etag
        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        @GivenName
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @LastName
        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
