package com.ettrema.httpclient;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.methods.HttpRequestBase;

/**
 *
 * @author mcevoyb
 */
public class MkColMethod extends HttpRequestBase {

    public MkColMethod( String uri ) throws URISyntaxException {
        setURI(new URI(uri));
    }

    @Override
    public String getMethod() {
        return "MKCOL";
    }
}
