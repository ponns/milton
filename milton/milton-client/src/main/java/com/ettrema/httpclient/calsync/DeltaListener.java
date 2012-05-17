package com.ettrema.httpclient.calsync;

import com.bradmcevoy.common.Path;
import java.io.IOException;

/**
 * A "delta" here refers to some difference between the client and server
 * file systems.
 * 
 *
 * @author brad
 */
public interface DeltaListener {

    
    void onLocalDeletion(CalSyncEvent remote, CalendarStore remoteStore);
    
    /**
     * Called when there is a remotely new or remotely modified calendar event
     * 
     * @param remote - the remote calendar event
     * @param local - the local CalendarStore
     * @throws IOException 
     */
    void onRemoteChange(CalSyncEvent remote, CalendarStore localStore);
    
    /**
     * Called when an event has been deleted from the server, but is still locally present
     * 
     * @param localRes
     * @param localStore 
     */
    void onRemoteDelete(CalSyncEvent localRes, CalendarStore localStore);
        
    void onLocalChange(CalSyncEvent localRes, CalSyncEvent remoteRes, CalendarStore remote);
     
}
