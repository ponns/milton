package com.ettrema.httpclient;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class ReportMethod extends HttpEntityEnclosingRequestBase {

    private static final Logger log = LoggerFactory.getLogger( PropFindMethod.class );

    public ReportMethod( String uri ) throws URISyntaxException {
        setURI(new URI(uri));
    }

    @Override
    public String getMethod() {
        return "REPORT";
    }

    public Document getResponseAsDocument(HttpClient client) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Utils.executeHttpWithStatus(client, this, out);
        String xml = out.toString();
        try {
            Document document = RespUtils.getJDomDocument( new ByteArrayInputStream( xml.getBytes() ) );
            return document;
        } catch( JDOMException ex ) {
            throw new RuntimeException(xml, ex );
        }
    }
}
