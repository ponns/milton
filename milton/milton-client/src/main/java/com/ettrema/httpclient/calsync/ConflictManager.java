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
