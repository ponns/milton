package com.ettrema.httpclient;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.methods.HttpRequestBase;

/**
 *
 * @author mcevoyb
 */
class CopyMethod extends HttpRequestBase {

    final String newUri;

    public CopyMethod( String uri, String newUri ) throws URISyntaxException {
        setURI(new URI(uri));
        this.newUri = newUri;
        addHeader( "Destination", newUri );
    }

    @Override
    public String getMethod() {
        return "COPY";
    }

    public String getNewUri() {
        return newUri;
    }
}
