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

package com.ettrema.http.caldav;

import com.bradmcevoy.http.*;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.webdav.MkColHandler;
import com.bradmcevoy.http.webdav.PropPatchHandler;
import com.ettrema.http.MakeCalendarResource;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class MkCalendarHandler implements Handler, MkColHandler.CollectionResourceCreator {

    private static final Logger log = LoggerFactory.getLogger(MkCalendarHandler.class);
    private final MkColHandler mkColHandler;
    private final PropPatchHandler propPatchHandler;

    public MkCalendarHandler(MkColHandler mkColHandler, PropPatchHandler propPatchHandler) {
        this.mkColHandler = mkColHandler;
        this.propPatchHandler = propPatchHandler;
    }

    @Override
    public String[] getMethods() {
        return new String[]{Method.MKCALENDAR.code};
    }

    @Override
    public boolean isCompatible(Resource handler) {
        return (handler instanceof MakeCalendarResource);
    }

    @Override
    public void process(HttpManager manager, Request request, Response response) throws ConflictException, NotAuthorizedException, BadRequestException {
        try {
            mkColHandler.process(manager, request, response, this);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public CollectionResource createResource(MakeCollectionableResource existingCol, String newName, Request request) throws ConflictException, NotAuthorizedException, BadRequestException, IOException {
        MakeCalendarResource mkcal = (MakeCalendarResource) existingCol;
        CollectionResource newCal = mkcal.createCalendar(newName);
        propPatchHandler.doPropPatch(request, newCal);
        return newCal;

    }
}

