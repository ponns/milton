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

import com.ettrema.httpclient.calsync.CalSyncEvent;
import com.ettrema.httpclient.calsync.CalendarStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author brad
 */
public class MemoryCalendarStore implements CalendarStore{

    private final String id;
    
    private long ctag;
    
    private List<CalSyncEvent> children = new ArrayList<CalSyncEvent>();
    
    private boolean readonly;

    public MemoryCalendarStore(String id) {
        this.id = id;
    }        
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCtag() {
        return ctag+"";
    }

    @Override
    public List<CalSyncEvent> getChildren() {
        return children;
    }

    @Override
    public void deleteEvent(CalSyncEvent event) {
        if( readonly ) {
            throw new RuntimeException("Attempt to modify when readonly");
        }
        children.remove(event);
        ctag = System.currentTimeMillis();
    }

    @Override
    public String createICalEvent(String name, String icalText) {
        if( readonly ) {
            throw new RuntimeException("Attempt to modify when readonly");
        }
        ctag = System.currentTimeMillis();
        MemoryCalSyncEvent e = new MemoryCalSyncEvent(1, name, icalText, new Date());
        children.add(e);
        return e.getEtag();
    }

    
    
    @Override
    public Date getModifiedDate(CalSyncEvent event) {
        MemoryCalSyncEvent e = (MemoryCalSyncEvent) event;
        return e.modDate;
    }
    
    

    @Override
    public String getICalData(CalSyncEvent event) {
        MemoryCalSyncEvent e = (MemoryCalSyncEvent) event;
        return e.getIcalText();
    }

    @Override
    public String setICalData(CalSyncEvent event, String icalData) {
        if( readonly ) {
            throw new RuntimeException("Attempt to modify when readonly");
        }
        
        MemoryCalSyncEvent e = (MemoryCalSyncEvent) event;
        e.setIcalText(icalData);
        ctag = System.currentTimeMillis();
        return e.getEtag();
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
    
    
    
    public static class MemoryCalSyncEvent implements CalSyncEvent {

        private long etag;
        private String name;
        private String icalText;
        private Date modDate;

        public MemoryCalSyncEvent() {
        }

        public MemoryCalSyncEvent(long etag, String name, String icalText, Date modDate) {
            this.etag = etag;
            this.name = name;
            this.icalText = icalText;
            this.modDate = modDate;
        }
                       
        @Override
        public String getEtag() {
            return etag + "";
        }

        @Override
        public String getName() {
            return name;
        }

        public String getIcalText() {
            return icalText;
        }

        public void setIcalText(String icalText) {
            this.icalText = icalText;
            etag = (long)(Math.random() * 1000000000);
            modDate = new Date();
        }                        
    }
}
