package com.bradmcevoy.http.http11.auth;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.DigestResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.http11.auth.NonceProvider.NonceValidity;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class DigestAuthenticationHandler implements AuthenticationHandler {

    private static final Logger log = LoggerFactory.getLogger( DigestAuthenticationHandler.class );
    private final NonceProvider nonceProvider;


    public DigestAuthenticationHandler( NonceProvider nonceProvider ) {
        this.nonceProvider = nonceProvider;
    }

    public DigestAuthenticationHandler() {
        this.nonceProvider = new SimpleMemoryNonceProvider( 300 );
    }

    public boolean supports( Resource r, Auth auth ) {
        boolean b;
        if( r instanceof DigestResource ) {
            b = Auth.Scheme.DIGEST.equals( auth.getScheme() );
        } else {
            log.debug( "resource is not an instanceof " + DigestResource.class );
            b = false;
        }
        return b;
    }

    public Object authenticate( Resource r, Request request ) {
        DigestResource digestResource = (DigestResource) r;
        Auth auth = request.getAuthorization();
        // Check all required parameters were supplied (ie RFC 2069)
        if( ( auth.getUser() == null ) || ( auth.getRealm() == null ) || ( auth.getNonce() == null ) || ( auth.getUri() == null ) ) {
            log.debug( "missing params" );
            return null;
        }

        // Check all required parameters for an "auth" qop were supplied (ie RFC 2617)
        Long nc;
        if( "auth".equals( auth.getQop() ) ) {
            if( ( auth.getNc() == null ) || ( auth.getCnonce() == null ) ) {
                log.debug( "missing params2" );
                return null;
            }
            nc = Long.parseLong( auth.getNc(), 16); // the nonce-count. hex value, must always increase
        } else {
            nc = null;
        }

        // Check realm name equals what we expected
        if( !r.getRealm().equals( auth.getRealm() ) ) {
            log.debug( "incorrect realm: resource: " + r.getRealm() + " given: " + auth.getRealm() );
            return null;
        }

        // Check nonce was a Base64 encoded (as sent by DigestProcessingFilterEntryPoint)
        if( !Base64.isArrayByteBase64( auth.getNonce().getBytes() ) ) {
            log.debug( "nonce not base64 encoded" );
            return null;
        }

        log.debug( "nc: " + auth.getNc());


        // Decode nonce from Base64
        // format of nonce is
        //   base64(expirationTime + "" + md5Hex(expirationTime + "" + key))
        String plainTextNonce = new String( Base64.decodeBase64( auth.getNonce().getBytes() ) );
        NonceValidity validity = nonceProvider.getNonceValidity( plainTextNonce, nc ); 
        if( NonceValidity.INVALID.equals( validity ) ) {
            log.debug( "invalid nonce: " + plainTextNonce );
            return null;
        } else if( NonceValidity.EXPIRED.equals( validity ) ) {
            log.debug( "expired nonce: " + plainTextNonce );
            // make this known so that we can add stale field to challenge
            auth.setNonceStale( true );
            return null;
        }

        DigestResponse resp = toDigestResponse( auth, request );
        Object o = digestResource.authenticate( resp );
        return o;
    }

    public String getChallenge( Resource resource, Request request ) {

        String nonceValue = nonceProvider.createNonce( resource, request );
        String nonceValueBase64 = new String( Base64.encodeBase64( nonceValue.getBytes() ) );

        // qop is quality of protection, as defined by RFC 2617.
        // we do not use opaque due to IE violation of RFC 2617 in not
        // representing opaque on subsequent requests in same session.
        String authenticateHeader = "Digest realm=\"" + resource.getRealm()
            + "\", " + "qop=\"auth\", nonce=\"" + nonceValueBase64
            + "\"";

        if( request.getAuthorization() != null ) {
            if( request.getAuthorization().isNonceStale() ) {
                authenticateHeader = authenticateHeader
                    + ", stale=\"true\"";
            }
        }

        return authenticateHeader;
    }

    public boolean isCompatible( Resource resource ) {
        return ( resource instanceof DigestResource );
    }

    private DigestResponse toDigestResponse( Auth auth, Request request ) {
        DigestResponse dr = new DigestResponse(
            request.getMethod(),
            auth.getUser(),
            auth.getRealm(),
            auth.getNonce(),
            auth.getUri(),
            auth.getResponseDigest(),
            auth.getQop(),
            auth.getNc(),
            auth.getCnonce() );
        return dr;

    }
}
