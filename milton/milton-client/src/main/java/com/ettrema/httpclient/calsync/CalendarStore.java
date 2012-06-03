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
package com.ettrema.httpclient.calsync;

import java.util.Date;
import java.util.List;

/**
 *
 * @author brad
 */
public interface CalendarStore {
    /**
     * Return an identifier for this store. This will be used to identify sync status
     * records
     * 
     * @return 
     */
    String getId();
    
    /**
     * Return the current CTag for this calendar. If this represents a remote server
     * then it should query the server for the current value. If this represents a 
     * local CalendarStore then it will be a value which simply changes when any resource 
     * in the local store changes.
     * 
     * THIS IS NOT REQUIRED TO MATCH THE SERVER CTAG
     * 
     * And, in fact, it cant. Because it might be synced with multiple remote stores
     * 
     * @return 
     */
    String getCtag();       
    
    /**
     * Get the items within this store. The object returned only needs sufficient
     * information to identify the resource and its state. It might not be efficient
     * to retrieve all of the data associated with the event, especially for remote stores
     * 
     * @return 
     */
    List<CalSyncEvent> getChildren();

    /**
     * Remove the event with the given name. The etag of the resource is provided,
     * as it was when information about this event was retrieved. It might be
     * desirable to ensure the etag has not changed
     * 
     * @param event
     */
    void deleteEvent(CalSyncEvent event);
    
    /**
     * Get the ical data for the given event. If the ical data has already been
     * retrieved just return it from the event. Remote stores will usually make
     * a seperate call to retrieve this information
     * 
     * 
     * @param event
     * @return 
     */
    String getICalData(CalSyncEvent event);
    
    /**
     * Update the given event with the icaldata
     * 
     * @param event
     * @param icalData 
     * @return the etag of the modified event
     */
    String setICalData(CalSyncEvent event, String icalData);
    
    /**
     * Find the modified date for the given event
     * 
     * @param event
     * @return 
     */
    Date getModifiedDate(CalSyncEvent event);

    /**
     * Crete a new event in this store with the given name and ical data and return
     * its etag
     * 
     * @param name
     * @param icalText
     * @return the etag for the newly created resource
     */
    String createICalEvent(String name, String icalText);
}
