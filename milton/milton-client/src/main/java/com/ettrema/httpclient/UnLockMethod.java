package com.ettrema.httpclient;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.methods.HttpRequestBase;

/**
 *
 * @author mcevoyb
 */
public class UnLockMethod extends HttpRequestBase {

	private final String lockToken;
	
    public UnLockMethod( String uri, String lockToken ) throws URISyntaxException {
        setURI(new URI(uri));
		this.lockToken = lockToken;
		addHeader("Lock-Token", lockToken);
    }

    @Override
    public String getMethod() {
        return "UNLOCK";
    }

	public String getLockToken() {
		return lockToken;
	}
	
	
}
