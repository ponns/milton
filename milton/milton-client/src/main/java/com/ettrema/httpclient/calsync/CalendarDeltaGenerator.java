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

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.httpclient.calsync.ConflictManager.ConflictAction;
import java.util.List;
import java.util.Map;

/**
 * Compares two calendars, usually local and remote, and generates events when
 * differences (aka deltas) are found
 *
 * @author brad
 */
public class CalendarDeltaGenerator {

    private CalendarStore local;
    private CalendarStore remote;
    private CalSyncStatusStore statusStore;
    private DeltaListener deltaListener;
    private ConflictManager conflictManager;

    public void compareCalendars() throws NotAuthorizedException, BadRequestException {

        // Check ctags
        String remoteCtag = remote.getCtag();
        String lastSyncedCTag = statusStore.getLastSyncedCtag(local, remote);
        if (remoteCtag != null && remoteCtag.equals(lastSyncedCTag)) {
            // no changes, we're done
            return;
        }

        List<CalSyncEvent> remoteChildren = remote.getChildren();
        Map<String, CalSyncEvent> remoteMap = SyncUtils.toMap(remoteChildren);
        List<CalSyncEvent> localChildren = local.getChildren();
        Map<String, CalSyncEvent> localMap = SyncUtils.toMap(localChildren);

        for (CalSyncEvent remoteRes : remoteChildren) {
            CalSyncEvent localRes = localMap.get(remoteRes.getName());
            if (localRes == null) {
                doMissingLocal(remoteRes);
            } else {
                if (localRes.getEtag() != null && localRes.getEtag().equals(remoteRes.getEtag())) {
                    statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), remoteRes.getEtag());
                } else {
                    doDifferentETags(remoteRes, localRes);
                }
            }
        }
        
        // Now check for remote deletes by iterating over local children looking for missing remotes
        for( CalSyncEvent localRes : localChildren ) {
            CalSyncEvent remoteRes = remoteMap.get(localRes.getName());
            if( remoteRes == null ) {
                doMissingRemote(localRes);
            }
        }
    }

    private void doMissingLocal(CalSyncEvent remoteRes) {
        // There is a resource in the remote store, but no corresponding local resource
        // Need to determine if it has been locally deleted or remotely created
        String lastSyncedEtag = statusStore.getLastSyncedEtag(local, remote, remoteRes.getName());
        if (lastSyncedEtag == null) {
            // we've never seen it before, so it must be remotely new
            deltaListener.onRemoteChange(remoteRes, local);
        } else {
            // we have previously synced this item, it no longer exists, so must have been deleted
            deltaListener.onLocalDeletion(remoteRes, remote);
        }
    }
    
    /**
     * There is a local event with no corresponding remote event, so either remotely
     * deleted or locall created. Check sync status to find out which
     * 
     * @param localRes 
     */
    private void doMissingRemote(CalSyncEvent localRes) {
        String lastSyncedEtag = statusStore.getLastSyncedEtag(local, remote, localRes.getName());
        if( lastSyncedEtag == null ) {
            // never before synced, so is locally new
            deltaListener.onLocalChange(localRes, null, remote);
        } else {
            // has been synced before, so was on server and not now = rmotely deleted
            deltaListener.onRemoteDelete(localRes, local);
        }
    }    

    /** so we know the events differ, but which is most up to date?
    we check the last synced etag for this local and remote store
    if current remote etag is different to last synced etag, then remote is modified
    if current local etag is different then local is modified
    it both modified then its a CONFLICT    
    */
    private void doDifferentETags(CalSyncEvent remoteRes, CalSyncEvent localRes) {
        String lastSyncedEtag = statusStore.getLastSyncedEtag(local, remote, remoteRes.getName());
        if( lastSyncedEtag == null ) {
            onConflict(remoteRes, localRes);
        } else {
            if( !lastSyncedEtag.equals(remoteRes.getEtag()) && !lastSyncedEtag.equals(localRes.getEtag()) ) {
                onConflict(remoteRes, localRes);
            } else if( !lastSyncedEtag.equals(remoteRes.getEtag()) ) {
                deltaListener.onRemoteChange(remoteRes, local);
            } else if( !lastSyncedEtag.equals(localRes.getEtag())) {
                deltaListener.onLocalChange(localRes, remoteRes, remote);
            }
        }
    }

    private boolean onConflict(CalSyncEvent remoteRes, CalSyncEvent localRes) {
        // local and remote are different, but we don't have sync status to tell us which is
        // we should use. This can happen if the user has removed the sync status database
        // or has just installed this sync software.
        // So we delegate to the conflict handler to decide what to do
        ConflictAction action = conflictManager.resolveConflict(remoteRes, localRes, remote, local);
        switch (action) {
            case NO_CHANGE:
                return true;
            case USE_LOCAL:
                deltaListener.onLocalChange(localRes, remoteRes, remote);
                return true;
        case USE_REMOTE:
            deltaListener.onRemoteChange(remoteRes, local);
        }
        return false;
    }


}
