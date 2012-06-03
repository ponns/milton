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
package com.ettrema.httpclient.calsync.conflict;

import com.bradmcevoy.http.HttpManager;
import com.ettrema.httpclient.calsync.CalSyncEvent;
import com.ettrema.httpclient.calsync.CalendarStore;
import com.ettrema.httpclient.calsync.ConflictManager;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class PolicyConflictManager implements ConflictManager{

    private static final Logger log = LoggerFactory.getLogger(PolicyConflictManager.class);
    
    public enum ConflictPolicy {
        SERVER_WINS,
        CLIENT_WINS,
        NEWEST_WINS
    }
    
    private ConflictPolicy conflictPolicy;
    
    private long numConflicts;
    
    @Override
    public ConflictAction resolveConflict(CalSyncEvent remoteRes, CalSyncEvent localRes, CalendarStore remote, CalendarStore local) {
        log.info("resolveConflict: " + localRes.getName());
        numConflicts++;
        if( conflictPolicy == ConflictPolicy.CLIENT_WINS) {
            return ConflictAction.USE_LOCAL;
        } else if( conflictPolicy == ConflictPolicy.SERVER_WINS) {
            return ConflictAction.USE_REMOTE;
        } else if( conflictPolicy == ConflictPolicy.NEWEST_WINS) {
            Date localMod = local.getModifiedDate(localRes);            
            if( localMod != null ) {
                Date remoteMod = remote.getModifiedDate(remoteRes);
                if( remoteMod != null ) {
                    if( localMod.after(remoteMod)) {
                        return ConflictAction.USE_LOCAL;
                    } else {
                        return ConflictAction.USE_REMOTE;
                    }
                } else {
                    log.warn("conflict policy is use newest, but no modified date is available for event: " + remoteRes.getName() + " in store: " + remote.getId());
                    return null;
                }                    
            } else {
                log.warn("conflict policy is use newest, but no modified date is available for event: " + localRes.getName() + " in store: " + local.getId());
                return null;
            }            
        } else {
            throw new RuntimeException("Unknown policy type: " + conflictPolicy);
        }
    }

    public ConflictPolicy getConflictPolicy() {
        return conflictPolicy;
    }

    public void setConflictPolicy(ConflictPolicy conflictPolicy) {
        this.conflictPolicy = conflictPolicy;
    }

    public long getNumConflicts() {
        return numConflicts;
    }
        
    
}
