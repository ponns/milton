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

/**
 * A DeltaListener which updates the stores to bring them into sync
 *
 * @author brad
 */
public class SyncingDeltaListener implements DeltaListener{

    @Override
    public boolean onLocalDeletion(CalSyncEvent remote, CalendarStore remoteStore) {
        remoteStore.deleteEvent(remote); 
        return true;
    }

    @Override
    public boolean onRemoteChange(CalSyncEvent remote, CalendarStore remoteStore, CalSyncEvent localEvent, CalendarStore localStore) {
        String icalText = remoteStore.getICalData(remote);
        if( localEvent == null ) {
            localStore.createICalEvent(remote.getName(), icalText);
        } else {
            localStore.setICalData(localEvent, icalText);
        }
        return true;
    }

    @Override
    public boolean onRemoteDelete(CalSyncEvent localRes, CalendarStore localStore) {
        localStore.deleteEvent(localRes); 
        return true;
    }

    @Override
    public String onLocalChange(CalSyncEvent localRes,CalendarStore localStore, CalSyncEvent remoteRes, CalendarStore remoteStore) {
        String s = localStore.getICalData(localRes);
        String newRemoteEtag;
        if( remoteRes == null ) {
            newRemoteEtag = remoteStore.createICalEvent(localRes.getName(), s);
        } else {
            newRemoteEtag = remoteStore.setICalData(remoteRes, s);
        }   
        return newRemoteEtag;
    }
    
}
