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
package com.ettrema.httpclient.calsync.store;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.ettrema.http.caldav.CalDavProtocol;
import com.ettrema.httpclient.*;
import com.ettrema.httpclient.calsync.CalSyncEvent;
import com.ettrema.httpclient.calsync.CalendarStore;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Implementation of CalendarStore for remote Caldav servers
 *
 * @author brad
 */
public class HttpCalendarStore implements CalendarStore {

    private static final QName icalName = new QName(CalDavProtocol.CALDAV_NS, "calendar-data");
    private static final QName ctagName = new QName(CalDavProtocol.CALDAV_NS, "getctag");
    private static final QName etagName = RespUtils.davName("etag");
    private static final QName nameName = RespUtils.davName("name");
    private static final QName modDateName = RespUtils.davName("getlastmodified");
    private String id;
    private final Host host;

    /**
     * The host object should have its root path set to the calendar path
     *
     * @param host
     */
    public HttpCalendarStore(Host host) {
        this.host = host;
        this.id = host.server + "/" + host.rootPath;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getCtag() {
        try {
            List<PropFindResponse> list = host.propFind(Path.root, 0, ctagName);
            if (list.isEmpty()) {
                return null;
            } else {
                PropFindResponse r = list.get(0);
                return (String) r.getProperties().get(ctagName);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotAuthorizedException ex) {
            throw new RuntimeException(ex);
        } catch (BadRequestException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public List<CalSyncEvent> getChildren() {
        try {
            List<PropFindResponse> list = host.propFind(Path.root, 1, etagName, nameName);
            return buildEvents(list);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotAuthorizedException ex) {
            throw new RuntimeException(ex);
        } catch (BadRequestException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteEvent(CalSyncEvent event) {
        Path p = Path.path(event.getName());
        try {
            host.doDelete(p);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotAuthorizedException ex) {
            throw new RuntimeException(ex);
        } catch (ConflictException ex) {
            throw new RuntimeException(ex);
        } catch (BadRequestException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getICalData(CalSyncEvent event) {
        // can either do a GET or a PROPFIND on calendar-data
        Path p = Path.path(event.getName());
        try {
            byte[] arr = host.get(p);
            try {
                return new String(arr, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotAuthorizedException ex) {
            throw new RuntimeException(ex);
        } catch (BadRequestException ex) {
            throw new RuntimeException(ex);
        } catch (ConflictException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String setICalData(CalSyncEvent event, String icalData) {
        try {
            Path p = Path.path(event.getName());
            byte[] bytes = icalData.getBytes("UTF-8");
            HttpResult result = host.doPut(p, bytes, "text/calendar");
            String etag = result.getHeaders().get("Etag");
            return etag;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Date getModifiedDate(CalSyncEvent event) {
        try {
            List<PropFindResponse> list = host.propFind(Path.root, 0, modDateName);
            if (list.isEmpty()) {
                return null;
            } else {
                PropFindResponse r = list.get(0);
                return r.getModifiedDate();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotAuthorizedException ex) {
            throw new RuntimeException(ex);
        } catch (BadRequestException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String createICalEvent(String name, String icalData) {
        try {
            Path p = Path.path(name);
            byte[] bytes = icalData.getBytes("UTF-8");
            HttpResult result = host.doPut(p, bytes, "text/calendar");
            String etag = result.getHeaders().get("Etag");
            return etag;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<CalSyncEvent> buildEvents(List<PropFindResponse> responses) {
        List<CalSyncEvent> list = new ArrayList<CalSyncEvent>();
        for (PropFindResponse r : responses) {
            list.add(new HttpCalSyncEvent(r));
        }
        return list;
    }

    public static class HttpCalSyncEvent implements CalSyncEvent {

        private final PropFindResponse resp;

        public HttpCalSyncEvent(PropFindResponse resp) {
            this.resp = resp;
        }

        @Override
        public String getEtag() {
            return (String) resp.getDavProperty("etag");
        }

        @Override
        public String getName() {
            return (String) resp.getDavProperty("name");
        }
    }
}
