package com.ettrema.ldap;

import java.io.IOException;

/**
 * Does not do anything
 *
 * @author brad
 */
public class NullLdapTransactionManager implements LdapTransactionManager{

    @Override
    public void tx(Runnable r) throws IOException {
        r.run();
    }
    
}
