package com.ettrema.restlet;

import com.bradmcevoy.http.Cookie;
import org.restlet.data.CookieSetting;

public class CookieAdapter implements Cookie {

    final protected CookieSetting target;

    public CookieAdapter(String name, String value) {
        target = new CookieSetting(name, value);
    }

    public CookieAdapter(com.bradmcevoy.http.Cookie cookie) {
        target = new CookieSetting(
           cookie.getVersion(),
           cookie.getName(),
           cookie.getValue(),
           cookie.getPath(),
           cookie.getDomain(),
           null,
           cookie.getExpiry(),
           cookie.getSecure(),
           false
        );
    }

    public CookieAdapter(org.restlet.data.Cookie target) {
        this.target = new CookieSetting(
           target.getVersion(),
           target.getName(),
           target.getValue(),
           target.getPath(),
           target.getDomain()
        );
    }

    public CookieSetting getTarget() {
        return target;
    }

    public int getVersion() {
        return target.getVersion();
    }

    public void setVersion(int version) {
        target.setVersion(version);
    }

    public String getName() {
        return target.getName();
    }

    public String getValue() {
        return target.getValue();
    }

    public void setValue(String value) {
        target.setValue(value);
    }

    public boolean getSecure() {
        return target.isSecure();
    }

    public void setSecure(boolean secure) {
        target.setSecure(secure);
    }

    public int getExpiry() {
        return target.getMaxAge();
    }

    public void setExpiry(int expiry) {
        target.setMaxAge(expiry);
    }

    public String getPath() {
        return target.getPath();
    }

    public void setPath(String path) {
        target.setPath(path);
    }

    public String getDomain() {
        return target.getDomain();
    }

    public void setDomain(String domain) {
        target.setDomain(domain);
    }

}
