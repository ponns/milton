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
import com.ettrema.httpclient.calsync.conflict.PolicyConflictManager;
import com.ettrema.httpclient.calsync.store.MemoryCalSyncStatusStore;
import com.ettrema.httpclient.calsync.store.MemoryCalendarStore;
import com.ettrema.httpclient.calsync.store.MemoryCalendarStore.MemoryCalSyncEvent;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class CalSyncMemoryTest extends TestCase{

        MemoryCalendarStore localStore;
        MemoryCalendarStore remoteStore;
        MemoryCalSyncStatusStore statusStore;
        DeltaListener deltaListener;
        PolicyConflictManager conflictManager;
        CalendarDeltaGenerator deltaGenerator;
    
    
    @Override
    protected void setUp() throws Exception {
        localStore = new MemoryCalendarStore("local");
        remoteStore = new MemoryCalendarStore("remote");
        statusStore = new MemoryCalSyncStatusStore();
        deltaListener = new SyncingDeltaListener();
        conflictManager = new PolicyConflictManager();
        conflictManager.setConflictPolicy(PolicyConflictManager.ConflictPolicy.SERVER_WINS);
        deltaGenerator = new CalendarDeltaGenerator(localStore, remoteStore, statusStore, deltaListener, conflictManager);

    }
    
    
    
    public void test_BothEmpty() throws NotAuthorizedException, BadRequestException {        
        assertEquals(0, localStore.getChildren().size());        
        assertEquals(0, remoteStore.getChildren().size());
        
        deltaGenerator.compareCalendars();
        
        assertEquals(0, localStore.getChildren().size());        
        assertEquals(0, remoteStore.getChildren().size());        
    }
    
    public void test_RemotelyNew() throws NotAuthorizedException, BadRequestException {        
        remoteStore.getChildren().add(new MemoryCalendarStore.MemoryCalSyncEvent(1, "a","ical",new Date() ));

        assertEquals(1, remoteStore.getChildren().size());
        assertEquals(0, localStore.getChildren().size());        
        
        deltaGenerator.compareCalendars();
        
        assertEquals(1, remoteStore.getChildren().size());
        assertEquals(1, localStore.getChildren().size());
        assertEquals(1, statusStore.getEtags().size());
        assertEquals(1, statusStore.getCtags().size());
        
        // run it again to ensure nothing changes
        localStore.setReadonly(true);
        remoteStore.setReadonly(true);
        deltaGenerator.compareCalendars();
        
    }    
    
    public void test_LocallyNew() throws NotAuthorizedException, BadRequestException {        
        localStore.getChildren().add(new MemoryCalendarStore.MemoryCalSyncEvent(1, "a","ical",new Date() ));

        assertEquals(0, remoteStore.getChildren().size());
        assertEquals(1, localStore.getChildren().size());        
        
        deltaGenerator.compareCalendars();
        
        assertEquals(1, remoteStore.getChildren().size());
        assertEquals(1, localStore.getChildren().size());
        assertEquals(1, statusStore.getEtags().size());
        assertEquals(1, statusStore.getCtags().size());
        
        // run it again to ensure nothing changes
        localStore.setReadonly(true);
        remoteStore.setReadonly(true);
        deltaGenerator.compareCalendars();
        
    }        
    
    public void test_LocallyUpdated() throws NotAuthorizedException, BadRequestException {        
        // First prime the sync status
        MemoryCalSyncEvent localEvent = new MemoryCalSyncEvent(1, "a","ical",new Date() );
        localStore.getChildren().add(localEvent);
        deltaGenerator.compareCalendars();
        
        // check sync status and remote store were primed
        assertEquals(1, remoteStore.getChildren().size());
        assertEquals(1, statusStore.getEtags().size());
        MemoryCalSyncEvent remoteEvent = (MemoryCalSyncEvent) remoteStore.getChildren().get(0);
        assertEquals("ical", remoteEvent.getIcalText());
        
        // Now update local event
        localStore.setICalData(localEvent, "XXXX");
        
        // Perform the update
        deltaGenerator.compareCalendars();
        
        // And check remote event was updated
        remoteEvent = (MemoryCalSyncEvent) remoteStore.getChildren().get(0);
        assertEquals("XXXX", remoteEvent.getIcalText());
    }        
    
    public void test_RemotelyUpdated() throws NotAuthorizedException, BadRequestException {        
        // First prime the sync status
        MemoryCalSyncEvent remoteEvent = new MemoryCalSyncEvent(1, "a","ical",new Date() );
        remoteStore.getChildren().add(remoteEvent);
        deltaGenerator.compareCalendars();
        
        // check sync status and remote store were primed
        assertEquals(1, localStore.getChildren().size());
        assertEquals(1, statusStore.getEtags().size());
        MemoryCalSyncEvent localEvent = (MemoryCalSyncEvent) localStore.getChildren().get(0);
        assertEquals("ical", remoteEvent.getIcalText());
        
        // Now update remote event
        remoteStore.setICalData(localEvent, "XXXX");
        
        // Perform the update
        deltaGenerator.compareCalendars();
        
        // And check remote event was updated
        localEvent = (MemoryCalSyncEvent) localStore.getChildren().get(0);
        assertEquals("XXXX", localEvent.getIcalText());
    }     
    
    public void test_LocallyDeleted() throws NotAuthorizedException, BadRequestException {        
        // First prime the sync status
        MemoryCalSyncEvent localEvent = new MemoryCalSyncEvent(1, "a","ical",new Date() );
        localStore.getChildren().add(localEvent);
        deltaGenerator.compareCalendars();
        assertEquals(1, remoteStore.getChildren().size());
                
        // Now delete local event
        localStore.deleteEvent(localEvent);
        
        // Perform the update
        deltaGenerator.compareCalendars();
        
        // And check remote event was deleted
        assertEquals(0, remoteStore.getChildren().size());
        assertEquals(0, statusStore.getEtags().size());
    }     
    
    public void test_RemotelyDeleted() throws NotAuthorizedException, BadRequestException {        
        // First prime the sync status
        MemoryCalSyncEvent remoteEvent = new MemoryCalSyncEvent(1, "a","ical",new Date() );
        remoteStore.getChildren().add(remoteEvent);
        deltaGenerator.compareCalendars();
        assertEquals(1, localStore.getChildren().size());
                
        // Now delete local event
        remoteStore.deleteEvent(remoteEvent);
        
        // Perform the update
        deltaGenerator.compareCalendars();
        
        // And check remote event was deleted
        assertEquals(0, localStore.getChildren().size());
        assertEquals(0, statusStore.getEtags().size());
    }     
    
    public void test_Conflict() throws NotAuthorizedException, BadRequestException {        
        // First prime the sync status
        MemoryCalSyncEvent remoteEvent = new MemoryCalSyncEvent(1, "a","ical",new Date() );
        remoteStore.getChildren().add(remoteEvent);
        deltaGenerator.compareCalendars();
        assertEquals(1, localStore.getChildren().size());
        MemoryCalSyncEvent localEvent = (MemoryCalSyncEvent) localStore.getChildren().get(0);
                
        // Now update both local and remote events
        remoteStore.setICalData(remoteEvent, "remoteMod");
        localStore.setICalData(localEvent, "localMod");
        
        // Perform the update
        deltaGenerator.compareCalendars();

        // make sure there was a conflict detected
        assertEquals(1, conflictManager.getNumConflicts());
        
        // check we havent added or removed anything
        assertEquals(1, localStore.getChildren().size());
        assertEquals(1, remoteStore.getChildren().size());
        assertEquals(1, statusStore.getEtags().size());
                
        // we're usign server wins, so local event should have server change
        localEvent = (MemoryCalSyncEvent) localStore.getChildren().get(0);
        assertEquals("remoteMod", localEvent.getIcalText());
    }      
}
