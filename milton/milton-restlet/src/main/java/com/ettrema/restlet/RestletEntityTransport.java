package com.ettrema.restlet;

import com.bradmcevoy.http.entity.EntityTransport;

/**
 * Don't write the entity during Milton's call handling, let Restlet take care of that later.
 */
public class RestletEntityTransport implements EntityTransport {
    @Override
    public void sendResponseEntity(com.bradmcevoy.http.Response r) throws Exception {
        // Take the Response.Entity from Milton and turn it into a Restlet Representation
        ((ResponseAdapter) r).setTargetEntity();
    }

    @Override
    public void closeResponse(com.bradmcevoy.http.Response response) {
        // Restlet flushes later automatically
    }
}
