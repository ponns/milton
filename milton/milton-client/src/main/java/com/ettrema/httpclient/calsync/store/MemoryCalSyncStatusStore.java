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
package com.ettrema.httpclient.calsync.store;

import com.ettrema.httpclient.calsync.CalSyncStatusStore;
import com.ettrema.httpclient.calsync.CalendarStore;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brad
 */
public class MemoryCalSyncStatusStore implements CalSyncStatusStore{

    private List<ETagStatus> etags = new ArrayList<ETagStatus>();
    private List<CTagStatus> ctags = new ArrayList<CTagStatus>();
    
    @Override
    public void setLastSyncedEtag(CalendarStore local, CalendarStore remote, String resourceName, String etag) {
        ETagStatus s = getETagStatus(local, remote, resourceName);
        if( s == null ) {
            s = new ETagStatus();
            s.local = local;
            s.remote = remote;
            s.resourceName = resourceName;
            etags.add(s);
        }
        if( etag != null ) {
            s.etag = etag;
        } else {
            etags.remove(s);
        }
    }

    @Override
    public String getLastSyncedCtag(CalendarStore local, CalendarStore remote) {
        CTagStatus c = getCTagStatus(local, remote);
        if( c != null ) {
            return c.ctag;
        }
        return null;
    }

    @Override
    public String getLastSyncedEtag(CalendarStore local, CalendarStore remote, String resourceName) {
        ETagStatus s = getETagStatus(local, remote, resourceName);
        if( s != null ) {
            return s.etag;
        }
        return null;
    }
    
    private CTagStatus getCTagStatus(CalendarStore local, CalendarStore remote) {
        for( CTagStatus c : ctags ) {
            if( c.local.getId().equals(local.getId()) && c.remote.getId().equals(remote.getId())) {
                return c;
            }
        }
        return null;        
    }
    
    private ETagStatus getETagStatus(CalendarStore local, CalendarStore remote, String resourceName) {
        for( ETagStatus s : etags ) {
            if( s.local.getId().equals(local.getId()) && s.remote.getId().equals(remote.getId()) && s.resourceName.equals(resourceName) ) {
                return s;
            }
        }
        return null;
    }

    @Override
    public void setLastSyncedCtag(CalendarStore local, CalendarStore remote, String remoteCtag) {
        CTagStatus s = getCTagStatus(local, remote);
        if( s == null ) {
            s = new CTagStatus();
            s.local = local;
            s.remote = remote;            
            ctags.add(s);
        }
        s.ctag = remoteCtag;
    }
    
    private class ETagStatus {
        CalendarStore local;
        CalendarStore remote;
        String resourceName;
        String etag;
    }
    
    private class CTagStatus {
        CalendarStore local;
        CalendarStore remote;
        String ctag;
    }

    public List<CTagStatus> getCtags() {
        return ctags;
    }

    public List<ETagStatus> getEtags() {
        return etags;
    }
    
    
}
