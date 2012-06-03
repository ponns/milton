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

package com.ettrema.httpclient.calsync;

import java.io.IOException;

/**
 * A "delta" here refers to some difference between the client and server
 * file systems.
 * 
 *
 * @author brad
 */
public interface DeltaListener {

    /**
     * Called when a local deletion has been detected, which means we should
     * delete the event from the server
     * 
     * @param remote
     * @param remoteStore
     * @return - true if the event was deleted
     */
    boolean onLocalDeletion(CalSyncEvent remote, CalendarStore remoteStore);
    
    /**
     * Called when there is a remotely new or remotely modified calendar event
     * 
     * @param remote - the remote calendar event
     * @param local - the local CalendarStore
     * @return true if the events were syncronised
     * @throws IOException 
     */
    boolean onRemoteChange(CalSyncEvent remote, CalendarStore remoteStore, CalSyncEvent localEvent, CalendarStore localStore);
    
    /**
     * Called when an event has been deleted from the server, but is still locally present
     * 
     * @param localRes
     * @param localStore 
     * @return true if the event was deleted
     */
    boolean onRemoteDelete(CalSyncEvent localRes, CalendarStore localStore);
        
    /**
     * Called when a local change has been detected, so should update remote server
     * 
     * @param localRes
     * @param localStore
     * @param remoteRes
     * @param remoteStore 
     * @return null if no changes were made, otherwise return the new etag for the remotely updated or created event
     */
    String onLocalChange(CalSyncEvent localRes,CalendarStore localStore, CalSyncEvent remoteRes, CalendarStore remoteStore);
     
}
