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

    public Document getResponseAsDocument(InputStream in) throws IOException {
//        IOUtils.copy( in, out );
//        String xml = out.toString();
        try {
            Document document = RespUtils.getJDomDocument(in);
            return document;
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @return - child responses only, not the requested url
     */
    public void buildResponses(HttpResponse response, List<PropFindMethod.Response> responses) {
        try {
            Header serverDateHeader = response.getFirstHeader("Date");
            String serverDate = null;
            if (serverDateHeader != null) {
                serverDate = serverDateHeader.getValue();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return;
            }
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            entity.writeTo(bout);
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            Document document = getResponseAsDocument(bin);
            if (document == null) {
                return ;
            }
            Element root = document.getRootElement();
            List<Element> responseEls = RespUtils.getElements(root, "response");
            boolean isFirst = true;
            for (Element el : responseEls) {
                if( !isFirst ) {
                    Response resp = new Response(serverDate, el);
                    responses.add(resp);
                } else {
                    isFirst = false;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class Response {

        final String name;
        final String parentHref;
        final String displayName;
        final String href;
        final String modifiedDate;
        final String createdDate;
        final String serverDate;
        final String contentType;
        final Long contentLength;
        final Long quotaAvailableBytes;
        final Long quotaUsedBytes;
        final Long crc;
        final boolean isCollection;
        final String lockOwner;
        final String lockToken;

        public Response(String serverDate, Element elResponse) {
            this.serverDate = serverDate;
            href = RespUtils.asString(elResponse, "href").trim();
            int pos = href.lastIndexOf("/", 8);
            if (pos > 0) {
                parentHref = href.substring(0, pos - 1);
            } else {
                parentHref = null;
            }

            Element el = elResponse.getChild("propstat", RespUtils.NS_DAV).getChild("prop", RespUtils.NS_DAV);
            if (href.contains("/")) {
                String[] arr = href.split("[/]");
                if (arr.length > 0) {
                    name = arr[arr.length - 1];
                } else {
                    name = "";
                }
            } else {
                name = href;
            }
            String dn = RespUtils.asString(el, "displayname");
            displayName = (dn == null) ? name : dn;
            createdDate = RespUtils.asString(el, "creationdate");
            modifiedDate = RespUtils.asString(el, "getlastmodified");

            contentType = RespUtils.asString(el, "getcontenttype");
            contentLength = RespUtils.asLong(el, "getcontentlength");
            quotaAvailableBytes = RespUtils.asLong(el, "quota-available-bytes");
            quotaUsedBytes = RespUtils.asLong(el, "quota-used-bytes");
            crc = RespUtils.asLong(el, "crc", NS_CLYDE);
            isCollection = RespUtils.hasChild(el, "collection");

            Element elLockDisc = el.getChild("lockdiscovery", RespUtils.NS_DAV);
            if (elLockDisc != null) {
                Element elActiveLock = elLockDisc.getChild("activelock", RespUtils.NS_DAV);
                if (elActiveLock != null) {
                    lockOwner = RespUtils.asString(elActiveLock, "owner");
                    Element elToken = elActiveLock.getChild("locktoken", RespUtils.NS_DAV);
                    if (elToken != null) {
                        String t = RespUtils.asString(elToken, "href");
                        if (t != null && t.contains(":")) {
                            t = t.substring(t.indexOf(":"));
                        }
                        lockToken = t;
                    } else {
                        lockToken = null;
                    }
                } else {
                    lockOwner = null;
                    lockToken = null;
                }
            } else {
                lockOwner = null;
                lockToken = null;
            }
        }
    }
}
