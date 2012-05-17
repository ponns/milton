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
 *
 * @author brad
 */
public interface ConflictManager {
    enum ConflictAction {
        USE_REMOTE,
        USE_LOCAL,
        NO_CHANGE
    }
    
    
    /**
     * Called when the system detects a conflict, ie when either we know that both
     * the local and remote resources have changed, OR we know they are different
     * but we can't tell which one has changed
     * 
     * The implementation can choose to use either local or remote instances, overwriting
     * the other, or ir can return a NO_CHANGE instruction which tells the system
     * to just carry on without resolving this conflict
     * 
     * @param remoteRes
     * @param localRes
     * @param remote
     * @param local
     * @return 
     */
    ConflictAction resolveConflict(CalSyncEvent remoteRes, CalSyncEvent localRes, CalendarStore remote, CalendarStore local);
}
