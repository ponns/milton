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

import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.httpclient.calsync.ConflictManager.ConflictAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Compares two calendars, usually local and remote, and generates events when
 * differences (aka deltas) are found
 *
 * @author brad
 */
public class CalendarDeltaGenerator {

    private final CalendarStore local;
    private final CalendarStore remote;
    private final CalSyncStatusStore statusStore;
    private final DeltaListener deltaListener;
    private final ConflictManager conflictManager;

    public CalendarDeltaGenerator(CalendarStore local, CalendarStore remote, CalSyncStatusStore statusStore, DeltaListener deltaListener, ConflictManager conflictManager) {
        this.local = local;
        this.remote = remote;
        this.statusStore = statusStore;
        this.deltaListener = deltaListener;
        this.conflictManager = conflictManager;
    }

    public void compareCalendars() throws NotAuthorizedException, BadRequestException {

        // Check ctags
        String remoteCtag = remote.getCtag();
        String lastSyncedCTag = statusStore.getLastSyncedCtag(local, remote);
        if (remoteCtag != null && remoteCtag.equals(lastSyncedCTag)) {
            // no changes, we're done
            return;
        }

        List<CalSyncEvent> remoteChildren = new ArrayList<CalSyncEvent>(remote.getChildren());
        Map<String, CalSyncEvent> remoteMap = SyncUtils.toMap(remoteChildren);
        List<CalSyncEvent> localChildren = new ArrayList<CalSyncEvent>(local.getChildren());
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
        for (CalSyncEvent localRes : localChildren) {
            CalSyncEvent remoteRes = remoteMap.get(localRes.getName());
            if (remoteRes == null) {
                doMissingRemote(localRes);
            }
        }
        
        statusStore.setLastSyncedCtag(local, remote, remoteCtag);
    }

    private void doMissingLocal(CalSyncEvent remoteRes) {
        // There is a resource in the remote store, but no corresponding local resource
        // Need to determine if it has been locally deleted or remotely created
        String lastSyncedEtag = statusStore.getLastSyncedEtag(local, remote, remoteRes.getName());
        if (lastSyncedEtag == null) {
            // we've never seen it before, so it must be remotely new
            if( deltaListener.onRemoteChange(remoteRes, remote, null, local) ) {
                statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), remoteRes.getEtag());
            }
        } else {
            // we have previously synced this item, it no longer exists, so must have been deleted
            if( deltaListener.onLocalDeletion(remoteRes, remote) ) {
                statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), null);
            }
        }
    }

    /**
     * There is a local event with no corresponding remote event, so either
     * remotely deleted or locall created. Check sync status to find out which
     *
     * @param localRes
     */
    private void doMissingRemote(CalSyncEvent localRes) {
        String lastSyncedEtag = statusStore.getLastSyncedEtag(local, remote, localRes.getName());
        if (lastSyncedEtag == null) {
            // never before synced, so is locally new
            String newRemoteEtag = deltaListener.onLocalChange(localRes, local, null, remote);
            if( newRemoteEtag != null ) {
                statusStore.setLastSyncedEtag(local, remote, localRes.getName(), newRemoteEtag);
            }
        } else {
            // has been synced before, so was on server and not now = rmotely deleted
            if( deltaListener.onRemoteDelete(localRes, local) ) {
                statusStore.setLastSyncedEtag(local, remote, localRes.getName(), null);
            }
        }
    }

    /**
     * so we know the events differ, but which is most up to date? we check the
     * last synced etag for this local and remote store if current remote etag
     * is different to last synced etag, then remote is modified if current
     * local etag is different then local is modified it both modified then its
     * a CONFLICT
     */
    private void doDifferentETags(CalSyncEvent remoteRes, CalSyncEvent localRes) {
        String lastSyncedEtag = statusStore.getLastSyncedEtag(local, remote, remoteRes.getName());
        if (lastSyncedEtag == null) {
            onConflict(remoteRes, localRes);
        } else {
            if (!lastSyncedEtag.equals(remoteRes.getEtag()) && !lastSyncedEtag.equals(localRes.getEtag())) {
                onConflict(remoteRes, localRes);
            } else if (!lastSyncedEtag.equals(remoteRes.getEtag())) {
                if( deltaListener.onRemoteChange(remoteRes, remote, localRes, local) ) {
                    statusStore.setLastSyncedEtag(local, remote, localRes.getName(), remoteRes.getEtag());
                }
            } else if (!lastSyncedEtag.equals(localRes.getEtag())) {
                String newRemoteEtag = deltaListener.onLocalChange(localRes, local, remoteRes, remote);
                if( newRemoteEtag != null ) {
                    statusStore.setLastSyncedEtag(local, remote, localRes.getName(), remoteRes.getEtag());
                }
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
                String newRemoteEtag = deltaListener.onLocalChange(localRes, local, remoteRes, remote);
                if( newRemoteEtag != null ) {
                    statusStore.setLastSyncedEtag(local, remote, localRes.getName(), remoteRes.getEtag());
                }
                return true;
            case USE_REMOTE:
                if( deltaListener.onRemoteChange(remoteRes, remote, localRes, local) ) {
                    statusStore.setLastSyncedEtag(local, remote, remoteRes.getName(), remoteRes.getEtag());
                }
        }
        return false;
    }
}
