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

package com.bradmcevoy.http.http11;

import com.bradmcevoy.http.*;
import com.bradmcevoy.http.exceptions.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Response.Status;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

public class DeleteHandler implements ExistingEntityHandler {

    private Logger log = LoggerFactory.getLogger(DeleteHandler.class);
    private final Http11ResponseHandler responseHandler;
    private final HandlerHelper handlerHelper;
    private final ResourceHandlerHelper resourceHandlerHelper;
    private DeleteHelper deleteHelper;

    public DeleteHandler(Http11ResponseHandler responseHandler, HandlerHelper handlerHelper) {
        this.responseHandler = responseHandler;
        this.handlerHelper = handlerHelper;
        this.resourceHandlerHelper = new ResourceHandlerHelper(handlerHelper, responseHandler);
        deleteHelper = new DeleteHelperImpl(handlerHelper);
    }

	@Override
    public String[] getMethods() {
        return new String[]{Method.DELETE.code};
    }

    @Override
    public boolean isCompatible(Resource handler) {
        return (handler instanceof DeletableResource);
    }

    @Override
    public void process(HttpManager manager, Request request, Response response) throws NotAuthorizedException, ConflictException, BadRequestException {
        String url = request.getAbsoluteUrl();
        if( url.contains("#")) {
            // See http://www.ettrema.com:8080/browse/MIL-88
            // Litmus test thinks this is unsafe
            throw new BadRequestException(null, "Can't delete a resource with a # in the url");
        }
        resourceHandlerHelper.process(manager, request, response, this);
    }

	@Override
    public void processResource(HttpManager manager, Request request, Response response, Resource r) throws NotAuthorizedException, ConflictException, BadRequestException {
        resourceHandlerHelper.processResource(manager, request, response, r, this);
    }

	@Override
    public void processExistingResource(HttpManager manager, Request request, Response response, Resource resource) throws NotAuthorizedException, BadRequestException, ConflictException {
        log.debug("DELETE: " + request.getAbsoluteUrl());

        DeletableResource r = (DeletableResource) resource;

        if (deleteHelper.isLockedOut(request, r)) {
            log.info("Could not delete. Is locked");
            responseHandler.respondDeleteFailed(request, response, r, Status.SC_LOCKED);
            return;
        }

        deleteHelper.delete(r, manager.getEventManager());
        log.debug("deleted ok");
        responseHandler.respondNoContent(resource, response, request);

    }

    public DeleteHelper getDeleteHelper() {
        return deleteHelper;
    }

    public void setDeleteHelper(DeleteHelper deleteHelper) {
        this.deleteHelper = deleteHelper;
    }   
}
