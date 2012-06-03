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

import java.util.List;

/**
 * Implement this to use the AnnotationCalendarStore. The implementation provides
 * methods for creating and removing events, and indicating the state of the
 * calendar store as a whole, while allowing annotations to deal with properties
 * of events
 *
 * @author brad
 */
public interface CalendarEventFactory {

    /**
     * Get the CTag for this calendar in the given calendar store
     * 
     * @param storeId
     * @return 
     */
    String getCtag(String storeId);

    /**
     * Remove the named event from the given store
     * 
     * @param name 
     */
    void deleteEvent(String storeId, String name);

    /**
     * Create an instance, with initially empty fields.
     * 
     * @param id
     * @param name
     * @return 
     */
    Object create(String id, String name);

    /**
     * Called after any changes to the event, including initial population when created
     * 
     * The implementation should immediately calculate new values for the etag of the
     * event and the ctag for the calendar
     * 
     * @param newEvent 
     */
    void saveAndUpdateTags(String storeId, Object newEvent);

    /**
     * Return the children, as annotation beans, of the given calendar store
     * 
     * @param storeId
     * @return 
     */
    List getChildren(String storeId);
    
}
