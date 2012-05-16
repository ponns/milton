package com.ettrema.httpclient;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.methods.HttpRequestBase;

/**
 *
 * @author mcevoyb
 */
class MoveMethod extends HttpRequestBase {

    final String newUri;

    public MoveMethod( String uri, String newUri ) throws URISyntaxException {
        setURI(new URI(uri));
        this.newUri = newUri;
        addHeader( "Destination", newUri );
    }

    @Override
    public String getMethod() {
        return "MOVE";
    }

    public String getNewUri() {
        return newUri;
    }
}
