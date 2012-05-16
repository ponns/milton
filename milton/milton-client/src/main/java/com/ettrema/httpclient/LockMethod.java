package com.ettrema.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author mcevoyb
 */
public class LockMethod extends HttpEntityEnclosingRequestBase {

    public LockMethod(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    @Override
    public String getMethod() {
        return "LOCK";
    }

    public String getLockToken(HttpResponse response) {
        try {
            Document document = getResponseAsDocument(response);
            if (document == null) {
                throw new RuntimeException("Got empty response to LOCK request");
            }
            Element root = document.getRootElement();
            List<Element> lockTokenEls = RespUtils.getElements(root, "locktoken");
            for (Element el : lockTokenEls) {
                String token = RespUtils.asString(el, "href");
                if (token == null) {
                    throw new RuntimeException("No href element in locktoken");
                }
                return token;
            }
            throw new RuntimeException("Didnt find a locktoken/href element in LOCK response");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Document getResponseAsDocument(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        InputStream in = null;
        try {
            in = entity.getContent();
//        IOUtils.copy( in, out );
//        String xml = out.toString();
            Document document = RespUtils.getJDomDocument(in);
            return document;
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
