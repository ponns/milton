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
    
    List<CalSyncEvent> getChildren();
}
