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


package com.ettrema.console;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.event.EventManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConsoleCommand implements ConsoleCommand{
    private static final Logger log = LoggerFactory.getLogger(AbstractConsoleCommand.class);
    
    protected final List<String> args;
    protected final Cursor cursor;
    protected final ResourceFactory resourceFactory;
    protected EventManager eventManager;

    AbstractConsoleCommand(List<String> args, String host, String currentDir, ResourceFactory resourceFactory) {
        this.args = args;
        cursor = new Cursor( resourceFactory, host, currentDir );
        this.resourceFactory = resourceFactory;
    }    
    
    /**
     * The current resource must be a collection
     *
     * @return
     */
    protected CollectionResource currentResource() throws NotAuthorizedException, BadRequestException {
        return (CollectionResource) cursor.getResource();
    }
    
    protected Result result(String msg) {
        return new Result(cursor.getPath().toString(),msg);
    }    
    
    protected Resource host() throws NotAuthorizedException, BadRequestException {
        return cursor.host();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager( EventManager eventManager ) {
        this.eventManager = eventManager;
    }
}
