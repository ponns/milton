package com.ettrema.httpclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mcevoyb
 */
public class PropFindMethod extends HttpEntityEnclosingRequestBase {

    private static final Logger log = LoggerFactory.getLogger(PropFindMethod.class);
    private static final Namespace NS_CLYDE = Namespace.getNamespace("ns1", "clyde");

    public PropFindMethod(String sUri) {
        URI uri;
        try {
            uri = new URI(sUri);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        this.setURI(uri);
    }

    @Override
    public String getMethod() {
        return "PROPFIND";
    }
}
