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

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.http.CalendarCollection;
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

    public void compareCalendars() throws NotAuthorizedException, BadRequestException {

        // Check ctags
        String remoteCtag = remote.getCtag();
        String localCtag = local.getCtag();
        if (remoteCtag != null && remoteCtag.equals(localCtag)) {
            // no changes, we're done
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
                    //syncStatusStore.setBackedupHash(childPath, localTriplet.getHash());
                } else {
                    doDifferentHashes(remoteRes, localRes);
                }
            }
        }
    }

    private void doMissingLocal(CalSyncEvent remoteRes) {
        // There is a resource in the remote store, but no corresponding local resource
        // Need to determine if it has been locally deleted or remotely created
        
    }

    private void doDifferentHashes(CalSyncEvent remoteRes, CalSyncEvent localRes) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
