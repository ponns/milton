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

import com.ettrema.httpclient.calsync.CalendarStore;

/**
 *
 * @author brad
 */
public interface CalSyncStatusStore {
    
    /**
     * Set the last synced etag for the named resource when syncing the given local
     * and remote stores
     * 
     * @param local
     * @param remote
     * @param resourceName
     * @param etag 
     */
    void setLastSyncedEtag(CalendarStore local, CalendarStore remote, String resourceName, String etag);
    
    /**
     * Return the last CTag seen from the remote server when syncing with the local
     * store. Note that the persisted ctag must have been obtained no later then
     * the last event to be synced. In other words, don't update and then get the ctag - 
     * get the ctag then update!
     * 
     * @param local
     * @param remote
     * @return 
     */
    String getLastSyncedCtag(CalendarStore local, CalendarStore remote);

    /**
     * Get the last seen etag for the given remote resource name, from the given remote
     * store when syncing the given local store
     * 
     * @param local
     * @param remote
     * @param resourceName
     * @return 
     */
    String getLastSyncedEtag(CalendarStore local, CalendarStore remote, String resourceName);

    public void setLastSyncedCtag(CalendarStore local, CalendarStore remote, String remoteCtag);
}
